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
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ServiceLoader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.SubjectFactory;

public final class XMLTruth {
    private static final SubjectFactory<XMLSubject,XML> SUBJECT_FACTORY = new SubjectFactory<XMLSubject,XML>() {
        @Override
        public XMLSubject getSubject(FailureStrategy fs, XML that) {
            return new XMLSubject(fs, that);
        }
        
    };
    
    @SuppressWarnings("rawtypes")
    private static final ServiceLoader<XMLFactory> factoryLoader = ServiceLoader.load(
            XMLFactory.class, XMLTruth.class.getClassLoader());
    
    private XMLTruth() {}

    public static SubjectFactory<XMLSubject,XML> xml() {
        return SUBJECT_FACTORY;
    }

    public static XML xml(Document document) {
        return new DOMXML(document);
    }

    public static XML xml(Element element) {
        return new DOMXML(element);
    }

    public static XML xml(final InputSource is) {
        final StreamSource source = new StreamSource();
        source.setInputStream(is.getByteStream());
        source.setReader(is.getCharacterStream());
        source.setPublicId(is.getPublicId());
        source.setSystemId(is.getSystemId());
        return new StAXXML() {
            @Override
            XMLStreamReader createXMLStreamReader(XMLInputFactory factory) throws XMLStreamException {
                return factory.createXMLStreamReader(source);
            }
        };
    }
    
    public static XML xml(final InputStream in) {
        return new StAXXML() {
            @Override
            XMLStreamReader createXMLStreamReader(XMLInputFactory factory) throws XMLStreamException {
                return factory.createXMLStreamReader(in);
            }
        };
    }

    public static XML xml(final Reader reader) {
        return new StAXXML() {
            @Override
            XMLStreamReader createXMLStreamReader(XMLInputFactory factory) throws XMLStreamException {
                return factory.createXMLStreamReader(reader);
            }
        };
    }

    public static XML xml(final URL url) {
        return new StAXXML() {
            @Override
            XMLStreamReader createXMLStreamReader(XMLInputFactory factory) throws XMLStreamException {
                return factory.createXMLStreamReader(new StreamSource(url.toString()));
            }
        };
    }

    public static XML xml(String xml) {
        return xml(new StringReader(xml));
    }
    
    public static XML xml(byte[] bytes) {
        return xml(new ByteArrayInputStream(bytes));
    }
    
    public static XML xml(Object object) {
        for (XMLFactory<?> factory : factoryLoader) {
            if (factory.getExpectedType().isInstance(object)) {
                return xml(factory, object);
            }
        }
        throw new IllegalArgumentException();
    }
    
    private static <T> XML xml(XMLFactory<T> factory, Object object) {
        return factory.createXML(factory.getExpectedType().cast(object));
    }
}
