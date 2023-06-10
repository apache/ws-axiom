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
package org.apache.axiom.truth.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.truth.xml.spi.XML;
import org.apache.axiom.truth.xml.spi.XMLFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.SimpleSubjectBuilder;
import com.google.common.truth.Subject;
import com.google.common.truth.Truth;

/** Google Truth extension for XML. */
public final class XMLTruth {
    private static final Subject.Factory<XMLSubject, Object> SUBJECT_FACTORY =
            new Subject.Factory<XMLSubject, Object>() {
                @Override
                public XMLSubject createSubject(FailureMetadata metadata, Object that) {
                    return new XMLSubject(metadata, that);
                }
            };

    private static final List<XMLFactory<?>> factories;

    static {
        factories = new ArrayList<>();
        factories.add(
                new XMLFactory<XML>() {
                    @Override
                    public Class<XML> getExpectedType() {
                        return XML.class;
                    }

                    @Override
                    public XML createXML(XML xml) {
                        return xml;
                    }
                });
        factories.add(
                new XMLFactory<InputStream>() {
                    @Override
                    public Class<InputStream> getExpectedType() {
                        return InputStream.class;
                    }

                    @Override
                    public XML createXML(final InputStream in) {
                        return new StAXXML(
                                new Parsable() {
                                    @Override
                                    XMLStreamReader createXMLStreamReader(XMLInputFactory factory)
                                            throws XMLStreamException {
                                        return factory.createXMLStreamReader(in);
                                    }
                                });
                    }
                });
        factories.add(
                new XMLFactory<Reader>() {
                    @Override
                    public Class<Reader> getExpectedType() {
                        return Reader.class;
                    }

                    @Override
                    public XML createXML(final Reader reader) {
                        return new StAXXML(
                                new Parsable() {
                                    @Override
                                    XMLStreamReader createXMLStreamReader(XMLInputFactory factory)
                                            throws XMLStreamException {
                                        return factory.createXMLStreamReader(reader);
                                    }
                                });
                    }
                });
        factories.add(
                new XMLFactory<StreamSource>() {
                    @Override
                    public Class<StreamSource> getExpectedType() {
                        return StreamSource.class;
                    }

                    @Override
                    public XML createXML(final StreamSource source) {
                        return new StAXXML(
                                new Parsable() {
                                    @Override
                                    XMLStreamReader createXMLStreamReader(XMLInputFactory factory)
                                            throws XMLStreamException {
                                        return factory.createXMLStreamReader(source);
                                    }
                                });
                    }
                });
        factories.add(
                new XMLFactory<InputSource>() {
                    @Override
                    public Class<InputSource> getExpectedType() {
                        return InputSource.class;
                    }

                    @Override
                    public XML createXML(InputSource is) {
                        StreamSource source = new StreamSource();
                        source.setInputStream(is.getByteStream());
                        source.setReader(is.getCharacterStream());
                        source.setPublicId(is.getPublicId());
                        source.setSystemId(is.getSystemId());
                        return xml(source);
                    }
                });
        factories.add(
                new XMLFactory<URL>() {
                    @Override
                    public Class<URL> getExpectedType() {
                        return URL.class;
                    }

                    @Override
                    public XML createXML(URL url) {
                        return xml(new StreamSource(url.toString()));
                    }
                });
        factories.add(
                new XMLFactory<File>() {
                    @Override
                    public Class<File> getExpectedType() {
                        return File.class;
                    }

                    @Override
                    public XML createXML(File file) {
                        return xml(new StreamSource(file.toURI().toString()));
                    }
                });
        factories.add(
                new XMLFactory<String>() {
                    @Override
                    public Class<String> getExpectedType() {
                        return String.class;
                    }

                    @Override
                    public XML createXML(String xml) {
                        return xml(new StringReader(xml));
                    }
                });
        factories.add(
                new XMLFactory<byte[]>() {
                    @Override
                    public Class<byte[]> getExpectedType() {
                        return byte[].class;
                    }

                    @Override
                    public XML createXML(byte[] bytes) {
                        return xml(new ByteArrayInputStream(bytes));
                    }
                });
        factories.add(
                new XMLFactory<Document>() {
                    @Override
                    public Class<Document> getExpectedType() {
                        return Document.class;
                    }

                    @Override
                    public XML createXML(Document document) {
                        return new DOMXML(document);
                    }
                });
        factories.add(
                new XMLFactory<Element>() {
                    @Override
                    public Class<Element> getExpectedType() {
                        return Element.class;
                    }

                    @Override
                    public XML createXML(Element element) {
                        return new DOMXML(element);
                    }
                });
        factories.add(
                new XMLFactory<XMLStreamReader>() {
                    @Override
                    public Class<XMLStreamReader> getExpectedType() {
                        return XMLStreamReader.class;
                    }

                    @Override
                    public XML createXML(final XMLStreamReader source) {
                        return new StAXXML(
                                new XMLStreamReaderProvider() {
                                    @Override
                                    XMLStreamReader getXMLStreamReader(
                                            boolean expandEntityReferences)
                                            throws XMLStreamException {
                                        if (expandEntityReferences) {
                                            throw new UnsupportedOperationException(
                                                    "Can't expand entity references on a user supplied XMLStreamReader");
                                        }
                                        return source;
                                    }
                                });
                    }
                });
        factories.add(
                new XMLFactory<StAXSource>() {
                    @Override
                    public Class<StAXSource> getExpectedType() {
                        return StAXSource.class;
                    }

                    @Override
                    public XML createXML(StAXSource source) {
                        return xml(source.getXMLStreamReader());
                    }
                });
        for (XMLFactory<?> factory :
                ServiceLoader.load(XMLFactory.class, XMLTruth.class.getClassLoader())) {
            factories.add(factory);
        }
    }

    private XMLTruth() {}

    /**
     * Get the {@link Subject.Factory} to be used with {@link Truth#assertAbout(Subject.Factory)}.
     *
     * @return a {@link Subject.Factory} for {@link XMLSubject} instances
     */
    public static Subject.Factory<XMLSubject, Object> xml() {
        return SUBJECT_FACTORY;
    }

    /**
     * Prepare XML data so that it will be accessed through a particular API. Use this method for
     * objects that represent XML data, but that implement more than one API supported by the
     * factory returned by {@link #xml()} (e.g. DOM and the Axiom API).
     *
     * @param type the API to use (e.g. {@link Document}
     * @param object an object implementing that API
     * @return an object that can be passed to {@link SimpleSubjectBuilder#that(Object)} or {@link
     *     XMLSubject#hasSameContentAs(Object)}
     */
    public static <T> Object xml(Class<T> type, T object) {
        return createXML(type, object);
    }

    private static <T> XML createXML(Class<T> type, T object) {
        XMLFactory<?> factory = null;
        for (XMLFactory<?> candidate : factories) {
            Class<?> expectedType = candidate.getExpectedType();
            if ((type == null || expectedType.isAssignableFrom(type))
                    && expectedType.isInstance(object)) {
                if (factory != null) {
                    throw new IllegalArgumentException(
                            "Multiple matching XMLFactory instances found");
                } else {
                    factory = candidate;
                }
            }
        }
        if (factory == null) {
            throw new IllegalArgumentException(
                    "No XMLFactory found for type " + object.getClass().getName());
        } else {
            return createXML0(factory, object);
        }
    }

    static XML xml(Object object) {
        return createXML(null, object);
    }

    private static <T> XML createXML0(XMLFactory<T> factory, Object object) {
        return factory.createXML(factory.getExpectedType().cast(object));
    }
}
