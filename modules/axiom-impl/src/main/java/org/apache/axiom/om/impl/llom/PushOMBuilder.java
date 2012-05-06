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
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.BuilderUtil;
import org.apache.axiom.util.stax.AbstractXMLStreamWriter;

// TODO: need to seed the namespace context with the namespace context from the parent!
public class PushOMBuilder extends AbstractXMLStreamWriter {
    private final OMSourcedElementImpl root;
    private final OMFactory factory;
    private OMElement parent;
    
    public PushOMBuilder(OMSourcedElementImpl root) {
        this.root = root;
        factory = root.getOMFactory();
    }
    
    public Object getProperty(String name) throws IllegalArgumentException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteStartDocument() {
        // Do nothing
    }

    protected void doWriteStartDocument(String encoding, String version) {
        // Do nothing
    }

    protected void doWriteStartDocument(String version) {
        // Do nothing
    }

    protected void doWriteEndDocument() {
        // Do nothing
    }

    protected void doWriteDTD(String dtd) throws XMLStreamException {
        throw new XMLStreamException("A DTD must not appear in element content");
    }

    protected void doWriteStartElement(String prefix, String localName, String namespaceURI) {
        if (parent == null) {
            root.validateName(prefix, localName, namespaceURI);
            parent = root;
        } else {
            parent = factory.createOMElement(localName, null, parent);
        }
        BuilderUtil.setNamespace(parent, namespaceURI, prefix, false);
    }

    protected void doWriteStartElement(String localName) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteEndElement() {
        if (parent == root) {
            parent = null;
        } else {
            parent = (OMElement)parent.getParent();
        }
    }

    protected void doWriteEmptyElement(String prefix, String localName, String namespaceURI) {
        doWriteStartElement(prefix, localName, namespaceURI);
        doWriteEndElement();
    }

    protected void doWriteEmptyElement(String localName) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteAttribute(String prefix, String namespaceURI, String localName, String value) {
        BuilderUtil.processAttribute(parent, prefix, namespaceURI, localName, value, null);
    }

    protected void doWriteAttribute(String localName, String value) throws XMLStreamException {
        doWriteAttribute(null, null, localName, value);
    }

    protected void doWriteNamespace(String prefix, String namespaceURI) {
        // Note that the namespace declaration may already have been added automatically by writeStartElement
        // or writeAttribute; we count on declareNamespace to do the necessary checks
        parent.declareNamespace(namespaceURI == null ? "" : namespaceURI, prefix == null ? "" : prefix);
    }

    protected void doWriteDefaultNamespace(String namespaceURI) {
        parent.declareDefaultNamespace(namespaceURI == null ? "" : namespaceURI);
    }

    protected void doWriteCharacters(char[] text, int start, int len) {
        doWriteCharacters(new String(text, start, len));
    }

    protected void doWriteCharacters(String text) {
        factory.createOMText(parent, text);
    }

    protected void doWriteCData(String data) {
        factory.createOMText(parent, data, OMNode.CDATA_SECTION_NODE);
    }

    protected void doWriteComment(String data) {
        factory.createOMComment(parent, data);
    }

    protected void doWriteEntityRef(String name) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    protected void doWriteProcessingInstruction(String target, String data) {
        factory.createOMProcessingInstruction(parent, target, data);
    }

    protected void doWriteProcessingInstruction(String target) {
        doWriteProcessingInstruction(target, "");
    }

    public void flush() throws XMLStreamException {
        // Do nothing
    }

    public void close() throws XMLStreamException {
        // Do nothing
    }
}
