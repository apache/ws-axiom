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

import java.util.NoSuchElementException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.util.stax.AbstractXMLStreamReader;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

class DOMXMLStreamReader extends AbstractXMLStreamReader implements DTDReader {
    /**
     * The root node for which StAX events are synthesized. This may be a {@link Document} or an
     * {@link Element}.
     */
    private final Node root;
    
    private final boolean expandEntityReferences;
    
    /**
     * The DOM node corresponding to the current StAX event. This is <code>null</code> if the
     * current event is {@link XMLStreamConstants#START_DOCUMENT} or
     * {@link XMLStreamConstants#END_DOCUMENT} and {@link #root} is an {@link Element}.
     */
    private Node node;
    
    /**
     * The current StAX event type;
     */
    private int event;
    
    private boolean attributesLoaded;
    private int attributeCount;
    private Attr[] attributes = new Attr[8];
    private int namespaceCount;
    private Attr[] namespaces = new Attr[8];
    private NamespaceContext nsContext;

    DOMXMLStreamReader(Node node, boolean expandEntityReferences) {
        root = node;
        this.node = node.getNodeType() == Node.DOCUMENT_NODE ? node : null;
        this.expandEntityReferences = expandEntityReferences;
        event = START_DOCUMENT;
    }

    Node currentNode() {
        return node;
    }
    
    public Object getProperty(String name) throws IllegalArgumentException {
        if (DTDReader.PROPERTY.equals(name)) {
            return this;
        } else {
            return null;
        }
    }

    public boolean hasNext() throws XMLStreamException {
        return event != END_DOCUMENT;
    }

    public int next() throws XMLStreamException {
        if (event == END_DOCUMENT) {
            throw new NoSuchElementException("End of the document reached");
        }
        boolean forceTraverse = false;
        while (true) {
            // Determine the DOM node corresponding to the next event and determine if we
            // have already traversed the descendants of that node
            boolean visited;
            if (node == null) {
                // We get here if the root node is an element (not a document) and we have
                // not yet traversed that element (in which case the current event must
                // be START_DOCUMENT)
                node = root;
                visited = false;
            } else if (event == START_DOCUMENT || event == START_ELEMENT || forceTraverse) {
                Node firstChild = node.getFirstChild();
                if (firstChild == null) {
                    visited = true;
                } else {
                    node = firstChild;
                    visited = false;
                }
                forceTraverse = false;
            } else if (node == root) {
                // We get here if the root node is an element and we have finished traversal
                // of that element
                node = null;
                visited = true;
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
            
            // Determine the event type for the next node
            switch (node == null ? Node.DOCUMENT_NODE : node.getNodeType()) {
                case Node.DOCUMENT_NODE:
                    event = END_DOCUMENT;
                    break;
                case Node.DOCUMENT_TYPE_NODE:
                    event = DTD;
                    break;
                case Node.ELEMENT_NODE:
                    event = visited ? END_ELEMENT : START_ELEMENT;
                    // Namespace declarations can be queried on an END_ELEMENT event; always reset the
                    // attributesLoaded flag
                    attributesLoaded = false;
                    break;
                case Node.TEXT_NODE:
                    event = ((Text)node).isElementContentWhitespace() ? SPACE : CHARACTERS;
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
        if (event == START_DOCUMENT) {
            return node != null ? ((Document)node).getInputEncoding() : null;
        } else {
            throw new IllegalStateException();
        }
    }

    public String getVersion() {
        return node != null ? ((Document)node).getXmlVersion() : "1.0";
    }

    public String getCharacterEncodingScheme() {
        if (event == START_DOCUMENT) {
            return node != null ? ((Document)node).getXmlEncoding() : null;
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean isStandalone() {
        return node != null ? ((Document)node).getXmlStandalone() : true;
    }

    public boolean standaloneSet() {
        return true;
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
        switch (event) {
            case START_ELEMENT:
            case END_ELEMENT:
                return node.getNamespaceURI();
            default:
                throw new IllegalStateException();
        }
    }

    public String getPrefix() {
        switch (event) {
            case START_ELEMENT:
            case END_ELEMENT:
                return node.getPrefix();
            default:
                throw new IllegalStateException();
        }
    }

    public QName getName() {
        switch (event) {
            case START_ELEMENT:
            case END_ELEMENT:
                return getQName(node);
            default:
                throw new IllegalStateException();
        }
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
                if (DOMUtils.isNSDecl(attr)) {
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
        if (event == START_ELEMENT) {
            loadAttributes();
            return attributeCount;
        } else {
            throw new IllegalStateException();
        }
    }

    private Attr getAttribute(int index) {
        if (event == START_ELEMENT) {
            loadAttributes();
            return attributes[index];
        } else {
            throw new IllegalStateException();
        }
    }
    
    public String getAttributeLocalName(int index) {
        return getAttribute(index).getLocalName();
    }

    public String getAttributeNamespace(int index) {
        return getAttribute(index).getNamespaceURI();
    }

    public String getAttributePrefix(int index) {
        return getAttribute(index).getPrefix();
    }

    public QName getAttributeName(int index) {
        return getQName(getAttribute(index));
    }

    public String getAttributeValue(int index) {
        return getAttribute(index).getValue();
    }

    public String getAttributeType(int index) {
        if (event == START_ELEMENT) {
            return "CDATA";
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean isAttributeSpecified(int index) {
        return getAttribute(index).getSpecified();
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        return ((Element)node).getAttributeNS(namespaceURI == null || namespaceURI.length() == 0 ? null : namespaceURI, localName);
    }

    public int getNamespaceCount() {
        switch (event) {
            case START_ELEMENT:
            case END_ELEMENT:
                loadAttributes();
                return namespaceCount;
            default:
                throw new IllegalStateException();
        }
    }
    
    private Attr getNamespace(int index) {
        switch (event) {
            case START_ELEMENT:
            case END_ELEMENT:
                loadAttributes();
                return namespaces[index];
            default:
                throw new IllegalStateException();
        }
    }
    
    public String getNamespacePrefix(int index) {
        return DOMUtils.getNSDeclPrefix(getNamespace(index));
    }

    public String getNamespaceURI(int index) {
        return getNamespace(index).getValue();
    }

    private String internalGetText() {
        switch (event) {
            case CHARACTERS:
            case SPACE:
            case CDATA:
            case COMMENT:
                return node.getNodeValue();
            default:
                throw new IllegalStateException();
        }
    }
    
    public String getText() {
        switch (event) {
            case DTD:
                return ((DocumentType)node).getInternalSubset();
            case ENTITY_REFERENCE:
                // DOM only gives access to the parsed replacement value, but StAX returns
                // the unparsed replacement value
                return null;
            default:
                return internalGetText();
        }
    }

    public int getTextStart() {
        // Call internalGetText to throw an IllegalStateException if appropriate
        internalGetText();
        return 0;
    }

    public int getTextLength() {
        return internalGetText().length();
    }

    public char[] getTextCharacters() {
        return internalGetText().toCharArray();
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        String text = internalGetText();
        int copied = Math.min(length, text.length()-sourceStart);
        text.getChars(sourceStart, sourceStart + copied, target, targetStart);
        return copied;
    }

    public String getPITarget() {
        if (event == PROCESSING_INSTRUCTION) {
            return ((ProcessingInstruction)node).getTarget();
        } else {
            throw new IllegalStateException();
        }
    }

    public String getPIData() {
        if (event == PROCESSING_INSTRUCTION) {
            return ((ProcessingInstruction)node).getData();
        } else {
            throw new IllegalStateException();
        }
    }

    public NamespaceContext getNamespaceContext() {
        if (nsContext == null) {
            nsContext = new DOMNamespaceContext(this);
        }
        return nsContext;
    }

    public String getNamespaceURI(String prefix) {
        Node current = node;
        do {
            NamedNodeMap attributes = current.getAttributes();
            if (attributes != null) {
                for (int i=0, l=attributes.getLength(); i<l; i++) {
                    Attr attr = (Attr)attributes.item(i);
                    if (DOMUtils.isNSDecl(attr)) {
                        String candidatePrefix = DOMUtils.getNSDeclPrefix(attr);
                        if (candidatePrefix == null) {
                            candidatePrefix = "";
                        }
                        if (candidatePrefix.equals(prefix)) {
                            return attr.getValue();
                        }
                    }
                }
            }
            current = current.getParentNode();
        } while (current != null);
        return null;
    }

    public void close() throws XMLStreamException {
    }

    private static QName getQName(Node node) {
        String prefix = node.getPrefix();
        return new QName(node.getNamespaceURI(), node.getLocalName(), prefix == null ? "" : prefix);
    }
}
