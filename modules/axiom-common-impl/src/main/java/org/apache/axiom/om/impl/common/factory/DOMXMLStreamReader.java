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
package org.apache.axiom.om.impl.common.factory;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.util.stax.AbstractXMLStreamReader;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

class DOMXMLStreamReader extends AbstractXMLStreamReader implements DTDReader {
    private final Node root;
    private final boolean expandEntityReferences;
    private Node node;
    private int event;
    private boolean attributesLoaded;
    private int attributeCount;
    private Attr[] attributes = new Attr[8];
    private int namespaceCount;
    private Attr[] namespaces = new Attr[8];

    public DOMXMLStreamReader(Node node, boolean expandEntityReferences) {
        root = node;
        this.node = node;
        this.expandEntityReferences = expandEntityReferences;
        event = START_DOCUMENT;
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if (DTDReader.PROPERTY.equals(name)) {
            return this;
        } else {
            return null;
        }
    }

    public int next() throws XMLStreamException {
        boolean forceTraverse = false;
        while (true) {
            boolean visited;
            if (event == START_DOCUMENT || event == START_ELEMENT || forceTraverse) {
                Node firstChild = node.getFirstChild();
                if (firstChild == null) {
                    visited = true;
                } else {
                    node = firstChild;
                    visited = false;
                }
                forceTraverse = false;
            } else {
                Node nextSibling = node.getNextSibling();
                if (nextSibling == null) {
                    node = node.getParentNode();
                    visited = true;
                } else {
                    node = nextSibling;
                    visited = false;
                }
            }
            switch (node.getNodeType()) {
                case Node.DOCUMENT_NODE:
                    event = END_DOCUMENT;
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                    event = DTD;
                    break;
                case Node.ELEMENT_NODE:
                    if (visited) {
                        event = END_ELEMENT;
                    } else {
                        event = START_ELEMENT;
                        attributesLoaded = false;
                    }
                    break;
                case Node.TEXT_NODE:
                    event = CHARACTERS;
                    break;
                case Node.CDATA_SECTION_NODE:
                    event = CDATA;
                    break;
                case Node.COMMENT_NODE:
                    event = COMMENT;
                    break;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    event = PROCESSING_INSTRUCTION;
                    break;
                case Node.ENTITY_REFERENCE_NODE:
                    if (expandEntityReferences) {
                        if (!visited) {
                            forceTraverse = true;
                        }
                        continue;
                    } else {
                        event = ENTITY_REFERENCE;
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected node type " + node.getNodeType());
            }
            return event;
        }
    }

    public int getEventType() {
        return event;
    }

    public String getEncoding() {
        return ((Document)node).getInputEncoding();
    }

    public String getVersion() {
        return ((Document)node).getXmlVersion();
    }

    public String getCharacterEncodingScheme() {
        return ((Document)node).getXmlEncoding();
    }

    public boolean isStandalone() {
        return ((Document)node).getXmlStandalone();
    }

    public String getRootName() {
        return ((DocumentType)node).getName();
    }

    public String getPublicId() {
        return ((DocumentType)node).getPublicId();
    }

    public String getSystemId() {
        return ((DocumentType)node).getSystemId();
    }

    public String getLocalName() {
        switch (event) {
            case START_ELEMENT:
            case END_ELEMENT:
                return node.getLocalName();
            case ENTITY_REFERENCE:
                return node.getNodeName();
            default:
                throw new IllegalStateException();
        }
    }
    
    public String getNamespaceURI() {
        return node.getNamespaceURI();
    }

    public String getPrefix() {
        return node.getPrefix();
    }

    private Attr[] grow(Attr[] array) {
        Attr[] newArray = new Attr[array.length*2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }
    
    private void loadAttributes() {
        if (!attributesLoaded) {
            attributeCount = 0;
            namespaceCount = 0;
            NamedNodeMap attrs = node.getAttributes();
            for (int i=0, l=attrs.getLength(); i<l; i++) {
                Attr attr = (Attr)attrs.item(i);
                if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI())) {
                    if (namespaceCount == namespaces.length) {
                        namespaces = grow(namespaces);
                    }
                    namespaces[namespaceCount++] = attr;
                } else {
                    if (attributeCount == attributes.length) {
                        attributes = grow(attributes);
                    }
                    attributes[attributeCount++] = attr;
                }
            }
            attributesLoaded = true;
        }
    }
    
    public int getAttributeCount() {
        loadAttributes();
        return attributeCount;
    }

    public String getAttributeLocalName(int index) {
        loadAttributes();
        return attributes[index].getLocalName();
    }

    public String getAttributeNamespace(int index) {
        loadAttributes();
        return attributes[index].getNamespaceURI();
    }

    public String getAttributePrefix(int index) {
        loadAttributes();
        return attributes[index].getPrefix();
    }

    public String getAttributeValue(int index) {
        loadAttributes();
        return attributes[index].getValue();
    }

    public String getAttributeType(int index) {
        return "CDATA";
    }

    public int getNamespaceCount() {
        loadAttributes();
        return namespaceCount;
    }
    
    public String getNamespacePrefix(int index) {
        loadAttributes();
        Attr attr = namespaces[index];
        String prefix = attr.getPrefix();
        return prefix == null ? null : attr.getLocalName();
    }

    public String getNamespaceURI(int index) {
        loadAttributes();
        return namespaces[index].getValue();
    }

    public String getText() {
        if (event == DTD) {
            return ((DocumentType)node).getInternalSubset();
        } else {
            return node.getNodeValue();
        }
    }

    public String getPITarget() {
        return ((ProcessingInstruction)node).getTarget();
    }

    public String getPIData() {
        return ((ProcessingInstruction)node).getData();
    }

    public void close() throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public QName getAttributeName(int arg0) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public String getAttributeValue(String arg0, String arg1) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public String getElementText() throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public QName getName() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public NamespaceContext getNamespaceContext() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public String getNamespaceURI(String arg0) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public char[] getTextCharacters() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3)
            throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public int getTextLength() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public int getTextStart() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean isAttributeSpecified(int arg0) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean isWhiteSpace() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean standaloneSet() {
        // TODO
        throw new UnsupportedOperationException();
    }
}
