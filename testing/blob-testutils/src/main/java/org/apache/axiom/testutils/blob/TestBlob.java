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
import java.util.Arrays;

import com.google.common.base.Preconditions;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.ext.io.StreamCopyException;

/**
 * Test blob that produces a byte sequence with specified length and with all bytes equal to a
 * specified value.
 */
public class TestBlob implements Blob {
    final byte value;
    final long length;

    public TestBlob(byte value, long length) {
        this.value = value;
        this.length = length;
    }

    public TestBlob(int value, long length) {
        Preconditions.checkArgument(
                value >= 0 && value < 256, "value must be in the range 0-255, got: %s", value);
        this.value = (byte) value;
        this.length = length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new InputStream() {
            private long position;

            @Override
            public int read() throws IOException {
                if (position == length) {
                    return -1;
                } else {
                    position++;
                    return value & 0xFF;
                }
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                long remaining = length - position;
                if (remaining <= 0) {
                    return -1;
                }
                int toRead = (int) Math.min(len, remaining);
                Arrays.fill(b, off, off + toRead, value);
                position += toRead;
                return toRead;
            }
        };
    }

    @Override
    public void writeTo(OutputStream out) throws StreamCopyException {
        byte[] buf = new byte[(int) Math.min(length, 8192L)];
        Arrays.fill(buf, value);
        long remaining = length;
        while (remaining > 0) {
            int toWrite = (int) Math.min(remaining, buf.length);
            try {
                out.write(buf, 0, toWrite);
            } catch (IOException ex) {
                throw new StreamCopyException(StreamCopyException.WRITE, ex);
            }
            remaining -= toWrite;
        }
    }

    @Override
    public long getSize() {
        return length;
    }
}
