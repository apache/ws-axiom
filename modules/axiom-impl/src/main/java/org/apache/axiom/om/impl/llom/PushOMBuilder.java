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
package org.apache.axiom.om.impl.llom;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.util.stax.AbstractXMLStreamWriter;

// TODO: need to seed the namespace context with the namespace context from the parent!
public class PushOMBuilder extends AbstractXMLStreamWriter {
    private final OMFactory factory;
    private OMElement parent;
    private int depth;
    
    public PushOMBuilder(OMSourcedElementImpl root) {
        factory = root.getOMFactory();
        parent = root;
    }
    
    public Object getProperty(String name) throws IllegalArgumentException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteStartDocument() throws XMLStreamException {
        // Do nothing
    }

    protected void doWriteStartDocument(String encoding, String version) throws XMLStreamException {
        // Do nothing
    }

    protected void doWriteStartDocument(String version) throws XMLStreamException {
        // Do nothing
    }

    protected void doWriteEndDocument() throws XMLStreamException {
        // Do nothing
    }

    protected void doWriteDTD(String dtd) throws XMLStreamException {
        throw new XMLStreamException("A DTD must not appear in element content");
    }

    protected void doWriteStartElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteStartElement(String localName) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteEndElement() throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteEmptyElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException {
        doWriteStartElement(prefix, localName, namespaceURI);
        doWriteEndElement();
    }

    protected void doWriteEmptyElement(String localName) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteAttribute(String prefix, String namespaceURI, String localName,
            String value) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteAttribute(String localName, String value) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteDefaultNamespace(String namespaceURI) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteCharacters(char[] text, int start, int len) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteCharacters(String text) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteCData(String data) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteComment(String data) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteEntityRef(String name) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteProcessingInstruction(String target, String data)
            throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteProcessingInstruction(String target) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void flush() throws XMLStreamException {
        // Do nothing
    }

    public void close() throws XMLStreamException {
        // Do nothing
    }
}
