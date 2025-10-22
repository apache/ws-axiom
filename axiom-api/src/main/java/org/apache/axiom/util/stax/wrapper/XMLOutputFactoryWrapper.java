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

package org.apache.axiom.util.stax.wrapper;

import java.io.OutputStream;
import java.io.Writer;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

/**
 * Base class for {@link XMLOutputFactory} wrappers. The class provides default implementations for
 * all methods. Each of them calls the corresponding method in the parent factory.
 */
public class XMLOutputFactoryWrapper extends XMLOutputFactory {
    private final XMLOutputFactory parent;

    /**
     * Constructor.
     *
     * @param parent the parent factory
     */
    public XMLOutputFactoryWrapper(XMLOutputFactory parent) {
        this.parent = parent;
    }

    @Override
    public XMLEventWriter createXMLEventWriter(OutputStream stream, String encoding)
            throws XMLStreamException {
        return parent.createXMLEventWriter(stream, encoding);
    }

    @Override
    public XMLEventWriter createXMLEventWriter(OutputStream stream) throws XMLStreamException {
        return parent.createXMLEventWriter(stream);
    }

    @Override
    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return parent.createXMLEventWriter(result);
    }

    @Override
    public XMLEventWriter createXMLEventWriter(Writer stream) throws XMLStreamException {
        return parent.createXMLEventWriter(stream);
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(OutputStream stream, String encoding)
            throws XMLStreamException {
        return parent.createXMLStreamWriter(stream, encoding);
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(OutputStream stream) throws XMLStreamException {
        return parent.createXMLStreamWriter(stream);
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        return parent.createXMLStreamWriter(result);
    }

    @Override
    public XMLStreamWriter createXMLStreamWriter(Writer stream) throws XMLStreamException {
        return parent.createXMLStreamWriter(stream);
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return parent.getProperty(name);
    }

    @Override
    public boolean isPropertySupported(String name) {
        return parent.isPropertySupported(name);
    }

    @Override
    public void setProperty(String name, Object value) throws IllegalArgumentException {
        parent.setProperty(name, value);
    }
}
