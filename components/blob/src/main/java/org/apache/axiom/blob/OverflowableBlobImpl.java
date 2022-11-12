/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.axiom.blob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axiom.ext.io.ReadFromSupport;
import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.util.io.IOUtils;

final class OverflowableBlobImpl implements OverflowableBlob {
    class OutputStreamImpl extends OutputStream implements ReadFromSupport {
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (state != State.UNCOMMITTED) {
                throw new IllegalStateException();
            }
            if (overflowOutputStream != null) {
                overflowOutputStream.write(b, off, len);
            } else if (len > (chunks.length - chunkIndex) * chunkSize - chunkOffset) {

                // The buffer will overflow. Switch to a temporary file.
                switchToOverflowBlob();

                // Write the new data to the temporary file.
                overflowOutputStream.write(b, off, len);

            } else {

                // The data will fit into the buffer.
                while (len > 0) {

                    byte[] chunk = getCurrentChunk();

                    // Determine number of bytes that can be copied to the current chunk.
                    int c = Math.min(len, chunkSize - chunkOffset);
                    // Copy data to the chunk.
                    System.arraycopy(b, off, chunk, chunkOffset, c);

                    // Update variables.
                    len -= c;
                    off += c;
                    chunkOffset += c;
                    if (chunkOffset == chunkSize) {
                        chunkIndex++;
                        chunkOffset = 0;
                    }
                }
            }
        }

        @Override
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override
        public void write(int b) throws IOException {
            write(new byte[] {(byte) b}, 0, 1);
        }

        @Override
        public void close() throws IOException {
            if (overflowOutputStream != null) {
                overflowOutputStream.close();
            }
            state = State.COMMITTED;
        }

        @Override
        public long readFrom(InputStream in, long length) throws StreamCopyException {
            return OverflowableBlobImpl.this.readFrom(in, length, false);
        }
    }

    class InputStreamImpl extends InputStream {

        private int currentChunkIndex;
        private int currentChunkOffset;
        private int markChunkIndex;
        private int markChunkOffset;

        @Override
        public int available() throws IOException {
            return (chunkIndex - currentChunkIndex) * chunkSize + chunkOffset - currentChunkOffset;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {

            if (len == 0) {
                return 0;
            }

            int read = 0;
            while (len > 0
                    && !(currentChunkIndex == chunkIndex && currentChunkOffset == chunkOffset)) {

                int c;
                if (currentChunkIndex == chunkIndex) {
                    // The current chunk is the last one => take into account the offset
                    c = Math.min(len, chunkOffset - currentChunkOffset);
                } else {
                    c = Math.min(len, chunkSize - currentChunkOffset);
                }

                // Copy the data.
                System.arraycopy(chunks[currentChunkIndex], currentChunkOffset, b, off, c);

                // Update variables
                len -= c;
                off += c;
                currentChunkOffset += c;
                read += c;
                if (currentChunkOffset == chunkSize) {
                    currentChunkIndex++;
                    currentChunkOffset = 0;
                }
            }

            if (read == 0) {
                // We didn't read anything (and the len argument was not 0) => we reached the end of
                // the buffer.
                return -1;
            } else {
                return read;
            }
        }

        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read() throws IOException {
            byte[] b = new byte[1];
            return read(b) == -1 ? -1 : b[0] & 0xFF;
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark(int readlimit) {
            markChunkIndex = currentChunkIndex;
            markChunkOffset = currentChunkOffset;
        }

        @Override
        public void reset() throws IOException {
            currentChunkIndex = markChunkIndex;
            currentChunkOffset = markChunkOffset;
        }

        @Override
        public long skip(long n) throws IOException {

            int available = available();
            int c = n < available ? (int) n : available;
            int newOffset = currentChunkOffset + c;
            int chunkDelta = newOffset / chunkSize;
            currentChunkIndex += chunkDelta;
            currentChunkOffset = newOffset - (chunkDelta * chunkSize);
            return c;
        }

        @Override
        public void close() throws IOException {}
    }

    /** Size of the chunks that will be allocated in the buffer. */
    final int chunkSize;

    final WritableBlobFactory<?> overflowBlobFactory;

    /**
     * Array of <code>byte[]</code> representing the chunks of the buffer. A chunk is only allocated
     * when the first byte is written to it. This attribute is set to <code>null</code> when the
     * buffer overflows and is written out to a temporary file.
     */
    byte[][] chunks;

    /** Index of the chunk the next byte will be written to. */
    int chunkIndex;

    /** Offset into the chunk where the next byte will be written. */
    int chunkOffset;

    /**
     * The overflow blob. This is only set when the memory buffer overflows and is written to a
     * different blob.
     */
    WritableBlob overflowBlob;

    /** The state of the blob. */
    State state = State.NEW;

    OutputStream overflowOutputStream;

    OverflowableBlobImpl(
            int numberOfChunks, int chunkSize, WritableBlobFactory<?> overflowBlobFactory) {
        this.chunkSize = chunkSize;
        this.overflowBlobFactory = overflowBlobFactory;
        chunks = new byte[numberOfChunks][];
    }

    /**
     * Get the current chunk to write to, allocating it if necessary.
     *
     * @return the current chunk to write to (never null)
     */
    byte[] getCurrentChunk() {
        if (chunkOffset == 0) {
            // We will write the first byte to the current chunk. Allocate it.
            byte[] chunk = new byte[chunkSize];
            chunks[chunkIndex] = chunk;
            return chunk;
        } else {
            // The chunk has already been allocated.
            return chunks[chunkIndex];
        }
    }

    /**
     * Create a temporary file and write the existing in memory data to it.
     *
     * @return an open FileOutputStream to the temporary file
     * @throws IOException
     */
    void switchToOverflowBlob() throws IOException {
        overflowBlob = overflowBlobFactory.createBlob();

        overflowOutputStream = overflowBlob.getOutputStream();
        // Write the buffer to the temporary file.
        for (int i = 0; i < chunkIndex; i++) {
            overflowOutputStream.write(chunks[i]);
        }

        if (chunkOffset > 0) {
            overflowOutputStream.write(chunks[chunkIndex], 0, chunkOffset);
        }

        // Release references to the buffer so that it can be garbage collected.
        chunks = null;
    }

    @Override
    public OutputStream getOutputStream() {
        if (state != State.NEW) {
            throw new IllegalStateException();
        } else {
            state = State.UNCOMMITTED;
            return new OutputStreamImpl();
        }
    }

    long readFrom(InputStream in, long length, boolean commit) throws StreamCopyException {
        if (state == State.COMMITTED) {
            throw new IllegalStateException();
        }
        long read = 0;
        long toRead = length == -1 ? Long.MAX_VALUE : length;
        while (toRead > 0) {
            if (overflowOutputStream != null) {
                read += IOUtils.copy(in, overflowOutputStream, toRead);
                break;
            } else if (chunkIndex == chunks.length) {
                try {
                    switchToOverflowBlob();
                } catch (IOException ex) {
                    throw new StreamCopyException(StreamCopyException.WRITE, ex);
                }
            } else {
                int c;
                try {
                    int len = chunkSize - chunkOffset;
                    if (len > toRead) {
                        len = (int) toRead;
                    }
                    c = in.read(getCurrentChunk(), chunkOffset, len);
                } catch (IOException ex) {
                    throw new StreamCopyException(StreamCopyException.READ, ex);
                }
                if (c == -1) {
                    break;
                }
                read += c;
                toRead -= c;
                chunkOffset += c;
                if (chunkOffset == chunkSize) {
                    chunkIndex++;
                    chunkOffset = 0;
                }
            }
        }
        if (commit && overflowOutputStream != null) {
            try {
                overflowOutputStream.close();
            } catch (IOException ex) {
                throw new StreamCopyException(StreamCopyException.WRITE, ex);
            }
        }
        state = commit ? State.COMMITTED : State.UNCOMMITTED;
        return read;
    }

    @Override
    public long readFrom(InputStream in) throws StreamCopyException {
        if (state != State.NEW) {
            throw new IllegalStateException();
        }
        return readFrom(in, -1, true);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (state != State.COMMITTED) {
            throw new IllegalStateException();
        } else if (overflowBlob != null) {
            return overflowBlob.getInputStream();
        } else {
            return new InputStreamImpl();
        }
    }

    @Override
    public void writeTo(OutputStream out) throws StreamCopyException {
        if (state != State.COMMITTED) {
            throw new IllegalStateException();
        }
        if (overflowBlob != null) {
            overflowBlob.writeTo(out);
        } else {
            try {
                for (int i = 0; i < chunkIndex; i++) {
                    out.write(chunks[i]);
                }
                if (chunkOffset > 0) {
                    out.write(chunks[chunkIndex], 0, chunkOffset);
                }
            } catch (IOException ex) {
                throw new StreamCopyException(StreamCopyException.WRITE, ex);
            }
        }
    }

    @Override
    public long getSize() {
        if (state != State.COMMITTED) {
            throw new IllegalStateException();
        }
        if (overflowBlob != null) {
            return overflowBlob.getSize();
        } else {
            return chunkIndex * chunkSize + chunkOffset;
        }
    }

    @Override
    public void release() throws IOException {
        if (overflowBlob != null) {
            overflowBlob.release();
            overflowBlob = null;
        }
        state = State.RELEASED;
    }

    @Override
    public WritableBlob getOverflowBlob() {
        return overflowBlob;
    }
}
