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

import org.apache.axiom.ext.io.StreamCopyException;

final class MemoryBlobOutputStreamImpl extends MemoryBlobOutputStream {
    private final MemoryBlobImpl blob;
    private MemoryBlobChunk chunk;

    MemoryBlobOutputStreamImpl(MemoryBlobImpl blob, MemoryBlobChunk firstChunk) {
        this.blob = blob;
        chunk = firstChunk;
    }

    private void updateChunk() {
        if (chunk.size == chunk.buffer.length) {
            chunk = chunk.allocateNextChunk();
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        if (chunk == null) {
            throw new IllegalStateException();
        }
        int total = 0;
        while (total < len) {
            updateChunk();
            int c = Math.min(len - total, chunk.buffer.length - chunk.size);
            System.arraycopy(b, off, chunk.buffer, chunk.size, c);
            chunk.size += c;
            total += c;
            off += c;
        }
    }

    @Override
    public void write(byte[] b) {
        write(b, 0, b.length);
    }

    @Override
    public void write(int b) {
        if (chunk == null) {
            throw new IllegalStateException();
        }
        updateChunk();
        chunk.buffer[chunk.size++] = (byte) b;
    }

    @Override
    public long readFrom(InputStream in, long length) throws StreamCopyException {
        if (chunk == null) {
            throw new IllegalStateException();
        }
        long read = 0;
        long toRead = length == -1 ? Long.MAX_VALUE : length;
        while (toRead > 0) {
            updateChunk();
            int c;
            try {
                c =
                        in.read(
                                chunk.buffer,
                                chunk.size,
                                (int) Math.min(toRead, chunk.buffer.length - chunk.size));
            } catch (IOException ex) {
                throw new StreamCopyException(StreamCopyException.READ, ex);
            }
            if (c == -1) {
                break;
            }
            chunk.size += c;
            read += c;
            toRead -= c;
        }
        return read;
    }

    @Override
    public void close() {
        blob.commit();
        chunk = null;
    }
}
