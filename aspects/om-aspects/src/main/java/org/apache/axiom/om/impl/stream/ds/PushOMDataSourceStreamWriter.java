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
package org.apache.axiom.om.impl.stream.ds;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.util.stax.wrapper.XMLStreamWriterWrapper;

/**
 * {@link XMLStreamWriter} wrapper that throws exceptions for invocations of methods that
 * {@link OMDataSource#serialize(XMLStreamWriter)} is not allowed to call.
 */
public final class PushOMDataSourceStreamWriter extends XMLStreamWriterWrapper {
    PushOMDataSourceStreamWriter(XMLStreamWriter parent) {
        super(parent);
    }

    @Override
    public XMLStreamWriter getParent() {
        return super.getParent();
    }

    @Override
    public void writeStartDocument() {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartDocument()");
    }

    @Override
    public void writeStartDocument(String encoding, String version) {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartDocument(String, String)");
    }

    @Override
    public void writeStartDocument(String version) {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartDocument(String)");
    }

    @Override
    public void writeEndDocument() {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeEndDocument()");
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException {
        throw new XMLStreamException("A DTD must not appear in element content");
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartElement(String)");
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeEmptyElement(String)");
    }

    @Override
    public void flush() throws XMLStreamException {
        // Do nothing
    }

    @Override
    public void close() throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT call XMLStreamWriter#close()");
    }
}
