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

final class CountingInputStream extends InputStream {
    private final InputStream parent;
    private long count;

    CountingInputStream(InputStream parent) {
        this.parent = parent;
    }

    long getCount() {
        return count;
    }

    @Override
    public int read() throws IOException {
        int b = parent.read();
        if (b != -1) {
            count++;
        }
        return b;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int read = parent.read(b);
        if (read != -1) {
            count += read;
        }
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = parent.read(b, off, len);
        if (read != -1) {
            count += read;
        }
        return read;
    }

    @Override
    public long skip(long n) throws IOException {
        long skipped = parent.skip(n);
        count += skipped;
        return skipped;
    }

    @Override
    public int available() throws IOException {
        return parent.available();
    }

    @Override
    public void close() throws IOException {
        parent.close();
    }
}
