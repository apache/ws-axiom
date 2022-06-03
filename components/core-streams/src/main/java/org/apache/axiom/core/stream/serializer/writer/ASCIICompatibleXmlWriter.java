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
package org.apache.axiom.core.stream.serializer.writer;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.axiom.util.base64.AbstractBase64EncodingOutputStream;

abstract class ASCIICompatibleXmlWriter extends XmlWriter {
    private final OutputStream out;
    final byte[] buffer = new byte[4096];
    int bufferPosition;
    private char highSurrogate;

    ASCIICompatibleXmlWriter(OutputStream out) {
        this.out = out;
    }

    protected abstract void writeNonASCIICharacter(int codePoint) throws IOException;

    protected final void writeByte(byte b) throws IOException {
        if (bufferPosition == buffer.length) {
            flushBuffer();
        }
        buffer[bufferPosition++] = b;
    }

    @Override
    public final void write(char c) throws IOException {
        if (c < 128 && highSurrogate == 0) {
            if (bufferPosition == buffer.length) {
                flushBuffer();
            }
            buffer[bufferPosition++] = (byte) c;
        } else {
            internalWrite(c);
        }
    }

    private final void internalWrite(char c) throws IOException {
        if (highSurrogate != 0) {
            if (Character.isLowSurrogate(c)) {
                int codePoint = Character.toCodePoint(highSurrogate, c);
                // Need to reset highSurrogate before writing because the character
                // may be unmappable, resulting in a character reference being written
                // (which means that this method must be reentrant).
                highSurrogate = 0;
                writeNonASCIICharacter(codePoint);
            } else {
                throw new IOException("Invalid surrogate pair");
            }
        } else if (Character.isHighSurrogate(c)) {
            highSurrogate = c;
        } else if (Character.isLowSurrogate(c)) {
            throw new IOException("Invalid surrogate pair");
        } else {
            writeNonASCIICharacter(c);
        }
    }

    @Override
    public final void write(String s) throws IOException {
        final byte[] buffer = this.buffer;
        final int bufferLength = buffer.length;
        int bufferPosition = this.bufferPosition;
        int highSurrogate = this.highSurrogate;
        for (int i = 0, length = s.length(); i < length; i++) {
            char c = s.charAt(i);
            if (c < 128 && highSurrogate == 0) {
                if (bufferPosition == bufferLength) {
                    out.write(buffer, 0, bufferLength);
                    bufferPosition = 0;
                }
                buffer[bufferPosition++] = (byte) c;
            } else {
                this.bufferPosition = bufferPosition;
                internalWrite(c);
                bufferPosition = this.bufferPosition;
                highSurrogate = this.highSurrogate;
            }
        }
        this.bufferPosition = bufferPosition;
    }

    @Override
    public final void write(char[] chars, int start, int length) throws IOException {
        final byte[] buffer = this.buffer;
        final int bufferLength = buffer.length;
        int bufferPosition = this.bufferPosition;
        int highSurrogate = this.highSurrogate;
        for (int i = 0; i < length; i++) {
            char c = chars[start + i];
            if (c < 128 && highSurrogate == 0) {
                if (bufferPosition == bufferLength) {
                    out.write(buffer, 0, bufferLength);
                    bufferPosition = 0;
                }
                buffer[bufferPosition++] = (byte) c;
            } else {
                this.bufferPosition = bufferPosition;
                internalWrite(c);
                bufferPosition = this.bufferPosition;
                highSurrogate = this.highSurrogate;
            }
        }
        this.bufferPosition = bufferPosition;
    }

    @Override
    public AbstractBase64EncodingOutputStream getBase64EncodingOutputStream() {
        return new AbstractBase64EncodingOutputStream() {
            @Override
            protected void doWrite(byte[] b) throws IOException {
                if (buffer.length - bufferPosition < 4) {
                    ASCIICompatibleXmlWriter.this.flushBuffer();
                }
                System.arraycopy(b, 0, buffer, bufferPosition, 4);
                bufferPosition += 4;
            }

            @Override
            protected void flushBuffer() throws IOException {}

            @Override
            protected void doFlush() throws IOException {}

            @Override
            protected void doClose() throws IOException {}
        };
    }

    @Override
    public final void flushBuffer() throws IOException {
        out.write(buffer, 0, bufferPosition);
        bufferPosition = 0;
    }
}
