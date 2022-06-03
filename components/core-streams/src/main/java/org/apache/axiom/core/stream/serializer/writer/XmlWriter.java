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
import java.nio.charset.Charset;

import org.apache.axiom.util.base64.AbstractBase64EncodingOutputStream;

public abstract class XmlWriter {
    public static XmlWriter create(OutputStream out, String encoding) {
        Charset charset = Charset.forName(encoding);
        String name = charset.name();
        if (name.equals("UTF-8")) {
            return new UTF8XmlWriter(out);
        } else if (name.equals("US-ASCII")) {
            return new Latin1XmlWriter(out, 127);
        } else if (name.equals("ISO-8859-1")) {
            return new Latin1XmlWriter(out, 255);
        } else {
            return new OutputStreamXmlWriter(out, charset);
        }
    }

    public abstract void setUnmappableCharacterHandler(
            UnmappableCharacterHandler unmappableCharacterHandler) throws IOException;

    public abstract void write(char c) throws IOException;

    public abstract void write(String s) throws IOException;

    public abstract void write(char chars[], int start, int length) throws IOException;

    public abstract AbstractBase64EncodingOutputStream getBase64EncodingOutputStream();

    /**
     * Write any pending data to the underlying stream, without flushing the stream itself.
     *
     * @throws IOException
     */
    public abstract void flushBuffer() throws IOException;

    public final void writeCharacterReference(int codePoint) throws IOException {
        write("&#");
        // TODO: optimize this
        write(Integer.toString(codePoint));
        write(';');
    }
}
