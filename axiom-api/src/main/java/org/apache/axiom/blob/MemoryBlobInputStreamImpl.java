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

final class MemoryBlobInputStreamImpl extends MemoryBlobInputStream {
    private MemoryBlobChunk chunk;
    private int index;
    private MemoryBlobChunk markChunk;
    private int markIndex;
    
    MemoryBlobInputStreamImpl(MemoryBlobChunk firstChunk) {
        markChunk = chunk = firstChunk;
    }

    private void updateChunk() {
        while (chunk != null && index == chunk.size) {
            chunk = chunk.nextChunk;
            index = 0;
        }
    }
    
    @Override
    public int read(byte[] buffer, int off, int len) {
        int read = 0;
        while (len > 0) {
            updateChunk();
            if (chunk == null) {
                if (read == 0) {
                    return -1;
                } else {
                    break;
                }
            }
            int c = Math.min(len, chunk.size-index);
            System.arraycopy(chunk.buffer, index, buffer, off, c);
            index += c;
            off += c;
            len -= c;
            read += c;
        }
        return read;
    }

    @Override
    public int read(byte[] buffer) {
        return read(buffer, 0, buffer.length);
    }

    @Override
    public int read() {
        updateChunk();
        if (chunk == null) {
            return -1;
        } else {
            return chunk.buffer[index++] & 0xFF;
        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        markChunk = chunk;
        markIndex = index;
    }

    @Override
    public synchronized void reset() {
        chunk = markChunk;
        index = markIndex;
    }

    @Override
    public long skip(long n) {
        long skipped = 0;
        while (n > 0) {
            updateChunk();
            if (chunk == null) {
                break;
            }
            int c = (int)Math.min(n, chunk.size-index);
            index += c;
            skipped += c;
            n -= c;
        }
        return skipped;
    }

    @Override
    public int available() {
        if (chunk == null) {
            return 0;
        } else {
            long available = chunk.size - index;
            MemoryBlobChunk chunk = this.chunk.nextChunk;
            while (chunk != null) {
                available += chunk.size;
                if (available > Integer.MAX_VALUE) {
                    return Integer.MAX_VALUE;
                }
                chunk = chunk.nextChunk;
            }
            return (int)available;
        }
    }

    @Override
    public void close() {
        chunk = null;
    }
}
