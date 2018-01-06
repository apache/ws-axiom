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

import org.apache.axiom.ext.io.StreamCopyException;

final class MemoryBlobImpl implements MemoryBlob {
    private MemoryBlobChunk firstChunk;
    private boolean committed;
    
    @Override
    public long getSize() {
        if (firstChunk == null || !committed) {
            throw new IllegalStateException();
        } else {
            long size = 0;
            MemoryBlobChunk chunk = firstChunk;
            while (chunk != null) {
                size += chunk.size;
                chunk = chunk.nextChunk;
            }
            return size;
        }
    }

    @Override
    public MemoryBlobOutputStream getOutputStream() {
        return internalGetOutputStream();
    }
    
    private MemoryBlobOutputStream internalGetOutputStream() {
        if (firstChunk != null || committed) {
            throw new IllegalStateException();
        } else {
            return new MemoryBlobOutputStreamImpl(this, firstChunk = new MemoryBlobChunk(4096));
        }
    }

    void commit() {
        committed = true;
    }
    
    @Override
    public long readFrom(InputStream in) throws StreamCopyException {
        MemoryBlobOutputStream out = internalGetOutputStream();
        try {
            return out.readFrom(in, -1);
        } finally {
            out.close();
        }
    }

    @Override
    public MemoryBlobInputStream getInputStream() {
        return getInputStream(true);
    }
    
    @Override
    public MemoryBlobInputStream readOnce() {
        return getInputStream(false);
    }

    private MemoryBlobInputStream getInputStream(boolean preserve) {
        if (firstChunk == null || !committed) {
            throw new IllegalStateException();
        }
        MemoryBlobInputStream in = new MemoryBlobInputStreamImpl(firstChunk);
        if (!preserve) {
            firstChunk = null;
        }
        return in;
    }

    @Override
    public void writeTo(OutputStream os) throws StreamCopyException {
        if (firstChunk == null || !committed) {
            throw new IllegalStateException();
        }
        MemoryBlobChunk chunk = firstChunk;
        try {
            while (chunk != null) {
                if (chunk.size > 0) {
                    os.write(chunk.buffer, 0, chunk.size);
                }
                chunk = chunk.nextChunk;
            }
        } catch (IOException ex) {
            throw new StreamCopyException(StreamCopyException.WRITE, ex);
        }
    }

    @Override
    public void release() {
        firstChunk = null;
    }
}
