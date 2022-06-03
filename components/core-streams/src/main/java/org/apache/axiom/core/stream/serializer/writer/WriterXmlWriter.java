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
import java.io.Writer;

import org.apache.axiom.util.base64.AbstractBase64EncodingOutputStream;
import org.apache.axiom.util.base64.Base64EncodingWriterOutputStream;

public final class WriterXmlWriter extends XmlWriter {
    private final Writer out;

    public WriterXmlWriter(Writer out) {
        this.out = out;
    }

    @Override
    public void setUnmappableCharacterHandler(
            UnmappableCharacterHandler unmappableCharacterHandler) {}

    @Override
    public void write(char c) throws IOException {
        out.write(c);
    }

    @Override
    public void write(String s) throws IOException {
        out.write(s);
    }

    @Override
    public void write(char[] chars, int start, int length) throws IOException {
        out.write(chars, start, length);
    }

    @Override
    public AbstractBase64EncodingOutputStream getBase64EncodingOutputStream() {
        return new Base64EncodingWriterOutputStream(out);
    }

    @Override
    public void flushBuffer() throws IOException {}
}
