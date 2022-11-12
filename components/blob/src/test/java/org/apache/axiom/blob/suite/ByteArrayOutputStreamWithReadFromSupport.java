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
package org.apache.axiom.blob.suite;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axiom.ext.io.ReadFromSupport;
import org.apache.axiom.ext.io.StreamCopyException;

public class ByteArrayOutputStreamWithReadFromSupport extends OutputStream
        implements ReadFromSupport {
    private byte[] buffer = new byte[4096];
    private int size;
    private boolean readFromCalled;

    @Override
    public void write(int b) throws IOException {
        if (buffer.length == size) {
            byte[] newBuffer = new byte[size * 2];
            System.arraycopy(buffer, 0, newBuffer, 0, size);
            buffer = newBuffer;
        }
        buffer[size++] = (byte) b;
    }

    @Override
    public long readFrom(InputStream inputStream, long length) throws StreamCopyException {
        readFromCalled = true;
        long read = 0;
        while (length < 0 || read < length) {
            int b;
            try {
                b = inputStream.read();
            } catch (IOException ex) {
                throw new StreamCopyException(StreamCopyException.READ, ex);
            }
            if (b == -1) {
                break;
            }
            try {
                write(b);
            } catch (IOException ex) {
                throw new StreamCopyException(StreamCopyException.WRITE, ex);
            }
        }
        return read;
    }

    public byte[] toByteArray() {
        byte[] result = new byte[size];
        System.arraycopy(buffer, 0, result, 0, size);
        return result;
    }

    public boolean isReadFromCalled() {
        return readFromCalled;
    }
}
