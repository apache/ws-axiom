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
package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream wrapper that automatically calls {@link PartImpl#releaseContent()} when the content
 * has been consumed.
 */
class ReadOnceInputStreamWrapper extends InputStream {
    private final PartImpl part;
    private InputStream in;
    
    ReadOnceInputStreamWrapper(PartImpl part, InputStream in) {
        this.part = part;
        this.in = in;
    }
    
    public int available() throws IOException {
        return in == null ? 0 : in.available();
    }

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

    public long skip(long n) throws IOException {
        return in == null ? 0 : in.skip(n);
    }

    public void close() throws IOException {
        if (in != null) {
            in.close();
            part.releaseContent();
            in = null;
        }
    }
}
