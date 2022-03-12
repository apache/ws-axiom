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

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link OutputStream} implementation that compares the data written to it with another character
 * sequence specified by an {@link InputStream}.
 */
public class ByteStreamComparator extends OutputStream {
    private final InputStream in;
    private final String name1;
    private final String name2;
    private final byte[] compareBuffer = new byte[1024];
    private int position;
    
    /**
     * Constructor.
     * 
     * @param in
     *            the stream to compare to
     * @param name1
     *            the name of the stream passed as argument; used in error messages
     * @param name2
     *            a name for the stream represented by the data written to this instance; used in
     *            error messages
     */
    public ByteStreamComparator(InputStream in, String name1, String name2) {
        this.in = in;
        this.name1 = name1;
        this.name2 = name2;
    }

    @Deprecated
    public ByteStreamComparator(InputStream in) {
        this(in, "s1", "s2");
    }

    @Override
    public void write(byte[] buffer, int off, int len) throws IOException {
        while (len > 0) {
            int c = in.read(compareBuffer, 0, Math.min(compareBuffer.length, len));
            if (c == -1) {
                fail("The two streams have different lengths: len(" + name1 + ") = " + position + " < len(" + name2 + ")");
            }
            for (int i=0; i<c; i++) {
                if (buffer[off] != compareBuffer[i]) {
                    fail("Byte mismatch: " + name1 + "[" + position + "] = " + compareBuffer[i] + " != " + name2 + "[" + position + "] = " + buffer[off]);
                }
                off++;
                len--;
                position++;
            }
        }
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
        if (in.read() != -1) {
            fail("The two streams have different lengths: len(" + name1 + ") > len(" + name2 + ") = " + position);
        }
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[] { (byte)b });
    }
}
