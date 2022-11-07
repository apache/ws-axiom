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
package org.apache.axiom.testutils.blob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.ext.io.StreamCopyException;

public class RandomBlob implements Blob {
    private final long seed;
    private final int rangeStart;
    private final int rangeEnd;
    private final long length;

    public RandomBlob(long seed, int rangeStart, int rangeEnd, long length) {
        this.seed = seed;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.length = length;
    }

    public RandomBlob(long seed, long length) {
        this(seed, 0, 256, length);
    }

    public RandomBlob(long length) {
        this(System.currentTimeMillis(), length);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        Random random = new Random(seed);
        return new InputStream() {
            private long position;

            @Override
            public int read() throws IOException {
                if (position == length) {
                    return -1;
                } else {
                    position++;
                    return random.nextInt(rangeEnd - rangeStart) + rangeStart;
                }
            }
        };
    }

    @Override
    public void writeTo(OutputStream out) throws StreamCopyException {
        Random random = new Random(seed);
        for (long i = 0; i < length; i++) {
            try {
                out.write(random.nextInt(rangeEnd - rangeStart) + rangeStart);
            } catch (IOException ex) {
                throw new StreamCopyException(StreamCopyException.WRITE, ex);
            }
        }
    }

    @Override
    public long getSize() {
        return length;
    }
}
