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
package org.apache.axiom.mime;

import java.io.IOException;
import java.io.InputStream;

import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.blob.WritableBlob;
import org.apache.axiom.blob.WritableBlobFactory;

final class PartInputStream extends InputStream {
    private WritableBlob content;
    private InputStream in;
    private WritableBlobFactory<?> blobFactory;

    PartInputStream(WritableBlob content) throws IOException {
        this.content = content;
        in = getInputStream(content);
    }

    PartInputStream(InputStream in, WritableBlobFactory<?> blobFactory) {
        this.in = in;
        this.blobFactory = blobFactory;
    }

    private static InputStream getInputStream(WritableBlob content) throws IOException {
        if (content instanceof MemoryBlob) {
            return ((MemoryBlob) content).readOnce();
        } else {
            return content.getInputStream();
        }
    }

    void detach() throws IOException {
        if (blobFactory == null) {
            throw new IllegalStateException();
        }
        if (in != null) {
            WritableBlob content = blobFactory.createBlob();
            content.readFrom(in);
            this.content = content;
            in = getInputStream(content);
        }
        blobFactory = null;
    }

    @Override
    public int available() throws IOException {
        return in == null ? 0 : in.available();
    }

    @Override
    public int read() throws IOException {
        if (in == null) {
            return -1;
        } else {
            int result = in.read();
            if (result == -1) {
                close();
            }
            return result;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (in == null) {
            return -1;
        } else {
            int result = in.read(b, off, len);
            if (result == -1) {
                close();
            }
            return result;
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (in == null) {
            return -1;
        } else {
            int result = in.read(b);
            if (result == -1) {
                close();
            }
            return result;
        }
    }

    @Override
    public long skip(long n) throws IOException {
        return in == null ? 0 : in.skip(n);
    }

    @Override
    public void close() throws IOException {
        if (in != null) {
            in.close();
            in = null;
        }
        if (content != null) {
            content.release();
            content = null;
        }
    }
}
