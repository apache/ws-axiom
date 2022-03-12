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

package org.apache.axiom.testutils.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.input.ProxyInputStream;

/**
 * {@link InputStream} wrapper that throws an exception after a configurable number of bytes have
 * been read from the stream or when the end of the stream is reached.
 */
public class ExceptionInputStream extends ProxyInputStream {
    private int remaining;
    private IOException exception;
    
    public ExceptionInputStream(InputStream in) {
        this(in, Integer.MAX_VALUE);
    }

    public ExceptionInputStream(InputStream in, int maxBytes) {
        super(in);
        remaining = maxBytes;
    }

    @Override
    public int read() throws IOException {
        if (remaining == 0) {
            throw exception = new IOException("Maximum number of bytes read");
        }
        int b = super.read();
        if (b == -1) {
            throw exception = new IOException("End of stream reached");
        }
        remaining--;
        return b;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (remaining == 0) {
            throw exception = new IOException("Maximum number of bytes read");
        }
        // Note: We use a sort of throttling mechanism here where we reduce the
        //       number of bytes read if we approach the point where we throw an
        //       exception. This is useful when testing consumers that tend to
        //       read too much in advance.
        int c = super.read(b, off, Math.min(Math.max(1, remaining/2), len));
        if (c == -1) {
            throw exception = new IOException("End of stream reached");
        }
        remaining -= c;
        return c;
    }

    public IOException getException() {
        return exception;
    }
}
