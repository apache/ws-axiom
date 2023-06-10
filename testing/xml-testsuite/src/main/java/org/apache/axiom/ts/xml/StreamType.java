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
package org.apache.axiom.ts.xml;

import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.io.InstrumentedInputStream;
import org.apache.axiom.testutils.io.InstrumentedReader;
import org.apache.axiom.testutils.io.InstrumentedStream;

public abstract class StreamType extends Multiton {
    public static final StreamType BYTE_STREAM =
            new StreamType(InputStream.class) {
                @Override
                public Closeable getStream(XMLSample sample) {
                    return sample.getInputStream();
                }

                @Override
                public InstrumentedStream instrumentStream(Closeable stream) {
                    return new InstrumentedInputStream((InputStream) stream);
                }

                @Override
                public XMLStreamReader createXMLStreamReader(
                        XMLInputFactory factory, Closeable stream) throws XMLStreamException {
                    return factory.createXMLStreamReader((InputStream) stream);
                }

                @Override
                public StreamSource createStreamSource(Closeable stream) {
                    return new StreamSource((InputStream) stream);
                }
            };

    public static final StreamType CHARACTER_STREAM =
            new StreamType(Reader.class) {
                @Override
                public Closeable getStream(XMLSample sample) {
                    return new InputStreamReader(
                            sample.getInputStream(), Charset.forName(sample.getEncoding()));
                }

                @Override
                public InstrumentedStream instrumentStream(Closeable stream) {
                    return new InstrumentedReader((Reader) stream);
                }

                @Override
                public XMLStreamReader createXMLStreamReader(
                        XMLInputFactory factory, Closeable stream) throws XMLStreamException {
                    return factory.createXMLStreamReader((Reader) stream);
                }

                @Override
                public StreamSource createStreamSource(Closeable stream) {
                    return new StreamSource((Reader) stream);
                }
            };

    private final Class<? extends Closeable> type;

    private StreamType(Class<? extends Closeable> type) {
        this.type = type;
    }

    public final Class<? extends Closeable> getType() {
        return type;
    }

    public abstract Closeable getStream(XMLSample sample);

    public abstract InstrumentedStream instrumentStream(Closeable stream);

    public abstract XMLStreamReader createXMLStreamReader(XMLInputFactory factory, Closeable stream)
            throws XMLStreamException;

    public abstract StreamSource createStreamSource(Closeable stream);
}
