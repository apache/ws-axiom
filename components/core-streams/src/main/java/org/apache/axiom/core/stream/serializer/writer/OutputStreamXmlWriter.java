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
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import org.apache.axiom.util.base64.AbstractBase64EncodingOutputStream;

final class OutputStreamXmlWriter extends XmlWriter {
    private final OutputStream out;
    private final CharBuffer encoderIn;
    private final ByteBuffer encoderOut;
    private final CharsetEncoder encoder;
    private UnmappableCharacterHandler unmappableCharacterHandler =
            UnmappableCharacterHandler.THROW_EXCEPTION;
    private boolean processingUnmappableCharacter;
    private CharBuffer encoderInAlt;

    OutputStreamXmlWriter(OutputStream out, Charset charset) {
        this.out = out;
        encoderIn = CharBuffer.allocate(4096);
        encoderOut = ByteBuffer.allocate(4096);
        encoder = charset.newEncoder();
    }

    private void flushEncodingOut() throws IOException {
        out.write(encoderOut.array(), 0, encoderOut.position());
        // Cast ensures compatibility with Java 8.
        ((Buffer) encoderOut).clear();
    }

    private CharBuffer getEncoderIn() throws IOException {
        if (processingUnmappableCharacter) {
            if (encoderInAlt == null) {
                encoderInAlt = CharBuffer.allocate(64);
            }
            return encoderInAlt;
        } else {
            return encoderIn;
        }
    }

    private void flush(CharBuffer encoderIn) throws IOException {
        // Cast ensures compatibility with Java 8.
        ((Buffer) encoderIn).flip();
        while (true) {
            CoderResult coderResult = encoder.encode(encoderIn, encoderOut, false);
            if (coderResult.isUnderflow()) {
                encoderIn.compact();
                break;
            } else if (coderResult.isOverflow()) {
                flushEncodingOut();
            } else if (coderResult.isUnmappable()) {
                if (processingUnmappableCharacter) {
                    throw new IllegalStateException();
                }
                processingUnmappableCharacter = true;
                try {
                    switch (coderResult.length()) {
                        case 1:
                            unmappableCharacterHandler.processUnmappableCharacter(
                                    encoderIn.get(), this);
                            break;
                        case 2:
                            throw new UnsupportedOperationException("TODO");
                        default:
                            throw new IllegalStateException();
                    }
                    flush(encoderInAlt);
                } finally {
                    processingUnmappableCharacter = false;
                }
            } else {
                throw new IOException("Malformed character sequence");
            }
        }
    }

    @Override
    public void setUnmappableCharacterHandler(UnmappableCharacterHandler unmappableCharacterHandler)
            throws IOException {
        if (unmappableCharacterHandler != this.unmappableCharacterHandler) {
            flush(encoderIn);
            this.unmappableCharacterHandler = unmappableCharacterHandler;
        }
    }

    @Override
    public void write(char c) throws IOException {
        CharBuffer encoderIn = getEncoderIn();
        if (!encoderIn.hasRemaining()) {
            flush(encoderIn);
        }
        encoderIn.put(c);
    }

    @Override
    public void write(String src) throws IOException {
        CharBuffer encoderIn = getEncoderIn();
        int offset = 0;
        int length = src.length();
        while (length > 0) {
            if (!encoderIn.hasRemaining()) {
                flush(encoderIn);
            }
            int c = Math.min(length, encoderIn.remaining());
            encoderIn.put(src, offset, length);
            offset += c;
            length -= c;
        }
    }

    @Override
    public void write(char[] src, int offset, int length) throws IOException {
        CharBuffer encoderIn = getEncoderIn();
        while (length > 0) {
            if (!encoderIn.hasRemaining()) {
                flush(encoderIn);
            }
            int c = Math.min(length, encoderIn.remaining());
            encoderIn.put(src, offset, length);
            offset += c;
            length -= c;
        }
    }

    @Override
    public AbstractBase64EncodingOutputStream getBase64EncodingOutputStream() {
        return new AbstractBase64EncodingOutputStream() {
            @Override
            protected void doWrite(byte[] b) throws IOException {
                CharBuffer encoderIn = getEncoderIn();
                if (encoderIn.remaining() < 4) {
                    OutputStreamXmlWriter.this.flush(encoderIn);
                }
                for (int i = 0; i < 4; i++) {
                    encoderIn.put((char) (b[i] & 0xFF));
                }
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
    public void flushBuffer() throws IOException {
        flush(encoderIn);
        flushEncodingOut();
    }
}
