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
package org.apache.axiom.core.stream.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

final class OutputStreamXmlWriter extends XmlWriter {
    private final OutputStream out;
    private final CharBuffer encoderIn;
    private final ByteBuffer encoderOut;
    private final CharsetEncoder encoder;
    private CharBuffer characterReferenceBuffer;

    OutputStreamXmlWriter(OutputStream out, Charset charset) {
        this.out = out;
        encoderIn = CharBuffer.allocate(4096);
        encoderOut = ByteBuffer.allocate(4096);
        encoder = charset.newEncoder();
    }

    private void flushEncodingOut() throws IOException {
        out.write(encoderOut.array(), 0, encoderOut.position());
        encoderOut.clear();
    }

    private void flushEncodingIn(boolean force) throws IOException {
        if (force || !encoderIn.hasRemaining()) {
            encoderIn.flip();
            while (true) {
                CoderResult coderResult = encoder.encode(encoderIn, encoderOut, false);
                if (coderResult.isUnderflow()) {
                    encoderIn.compact();
                    break;
                } else if (coderResult.isOverflow()) {
                    flushEncodingOut();
                } else if (coderResult.isUnmappable()) {
                    // Note that we can't use writeCharacterReference here because we are still
                    // processing the encoderIn buffer
                    switch (coderResult.length()) {
                        case 1:
                            insertCharacterReference(encoderIn.get());
                            break;
                        case 2:
                            throw new UnsupportedOperationException("TODO");
                        default:
                            throw new IllegalStateException();
                    }
                } else {
                    throw new IOException("Malformed character sequence");
                }
            }
        }
    }

    private void insertCharacterReference(int codePoint) throws IOException {
        CharBuffer buffer = characterReferenceBuffer;
        if (characterReferenceBuffer == null) {
            buffer = characterReferenceBuffer = CharBuffer.allocate(16);
        } else {
            buffer.clear();
        }
        buffer.put("&#");
        // TODO: optimize this
        buffer.put(Integer.toString(codePoint));
        buffer.put(';');
        buffer.flip();
        while (true) {
            CoderResult coderResult = encoder.encode(buffer, encoderOut, false);
            if (coderResult.isUnderflow()) {
                break;
            } else if (coderResult.isOverflow()) {
                flushEncodingOut();
            } else {
                throw new IllegalStateException();
            }
        }
    }

    @Override
    void write(char c) throws IOException {
        flushEncodingIn(false);
        encoderIn.put(c);
    }

    @Override
    void write(String src) throws IOException {
        int offset = 0;
        int length = src.length();
        while (length > 0) {
            flushEncodingIn(false);
            int c = Math.min(length, encoderIn.remaining());
            encoderIn.put(src, offset, length);
            offset += c;
            length -= c;
        }
    }

    @Override
    void write(char[] src, int offset, int length) throws IOException {
        while (length > 0) {
            flushEncodingIn(false);
            int c = Math.min(length, encoderIn.remaining());
            encoderIn.put(src, offset, length);
            offset += c;
            length -= c;
        }
    }

    @Override
    void flushBuffer() throws IOException {
        flushEncodingIn(true);
        flushEncodingOut();
    }
}
