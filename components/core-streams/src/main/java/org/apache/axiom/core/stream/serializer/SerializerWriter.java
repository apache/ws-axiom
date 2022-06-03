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
import java.io.Writer;

import org.apache.axiom.core.stream.StreamException;

/**
 * {@link Writer} that writes character data as {@link ToXMLStream#characters(char[], int, int)} to
 * a {@link ToXMLStream}.
 */
final class SerializerWriter extends Writer {
    private final Serializer serializer;

    public SerializerWriter(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        try {
            serializer.characters(cbuf, off, len);
        } catch (StreamException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        try {
            serializer.characters(str, off, len);
        } catch (StreamException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void close() throws IOException {}

    @Override
    public void flush() throws IOException {}
}
