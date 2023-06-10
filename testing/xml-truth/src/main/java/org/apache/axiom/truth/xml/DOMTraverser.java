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

import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.axiom.truth.xml.spi.Event;
import org.apache.axiom.truth.xml.spi.Traverser;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import com.google.common.base.Strings;

final class DOMTraverser implements Traverser {
    private final Node root;
    private final boolean dom3;
    private final boolean expandEntityReferences;
    private Node node;
    private boolean descend;

    DOMTraverser(Node root, boolean dom3, boolean expandEntityReferences) {
        this.root = root;
        this.dom3 = dom3;
        this.expandEntityReferences = expandEntityReferences;
        if (root.getNodeType() == Node.DOCUMENT_NODE) {
            node = root;
            descend = true;
        }
    }

    @Override
    public Event next() {
        while (true) {
            boolean visited;
            if (node == null) {
                node = root;
                visited = false;
            } else if (descend) {
                Node firstChild = node.getFirstChild();
                if (firstChild != null) {
                    node = firstChild;
                    visited = false;
                } else {
                    visited = true;
                }
            } else {
                Node nextSibling = node.getNextSibling();
                if (node == root) {
                    return null;
                } else if (nextSibling != null) {
                    node = nextSibling;
                    visited = false;
                } else {
                    node = node.getParentNode();
                    visited = true;
                }
            }
            switch (node.getNodeType()) {
                case Node.DOCUMENT_NODE:
                    return null;
                case Node.DOCUMENT_TYPE_NODE:
                    descend = false;
                    return Event.DOCUMENT_TYPE;
                case Node.ELEMENT_NODE:
                    if (!visited) {
                        descend = true;
                        return Event.START_ELEMENT;
                    } else {
                        descend = false;
                        return Event.END_ELEMENT;
                    }
                case Node.TEXT_NODE:
                    descend = false;
                    return dom3 && ((Text) node).isElementContentWhitespace()
                            ? Event.WHITESPACE
                            : Event.TEXT;
                case Node.ENTITY_REFERENCE_NODE:
                    if (expandEntityReferences) {
                        descend = !visited;
                        break;
                    } else {
                        descend = false;
                        return Event.ENTITY_REFERENCE;
                    }
                case Node.COMMENT_NODE:
                    descend = false;
                    return Event.COMMENT;
                case Node.CDATA_SECTION_NODE:
                    descend = false;
                    return Event.CDATA_SECTION;
                case Node.PROCESSING_INSTRUCTION_NODE:
                    descend = false;
                    return Event.PROCESSING_INSTRUCTION;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    @Override
    public String getRootName() {
        return ((DocumentType) node).getName();
    }

    @Override
    public String getPublicId() {
        return ((DocumentType) node).getPublicId();
    }

    @Override
    public String getSystemId() {
        return ((DocumentType) node).getSystemId();
    }

    private static QName getQName(Node node) {
        String localName = node.getLocalName();
        if (localName == null) {
            return new QName(node.getNodeName());
        } else {
            return new QName(
                    node.getNamespaceURI(),
                    node.getLocalName(),
                    Strings.nullToEmpty(node.getPrefix()));
        }
    }

    @Override
    public QName getQName() {
        return getQName(node);
    }

    @Override
    public Map<QName, String> getAttributes() {
        Map<QName, String> result = null;
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attr = (Attr) attributes.item(i);
            if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI())) {
                if (result == null) {
                    result = new HashMap<>();
                }
                result.put(getQName(attr), attr.getValue());
            }
        }
        return result;
    }

    @Override
    public Map<String, String> getNamespaces() {
        Map<String, String> result = null;
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attr = (Attr) attributes.item(i);
            if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI())) {
                if (result == null) {
                    result = new HashMap<>();
                }
                String prefix = attr.getPrefix();
                result.put(
                        XMLConstants.XMLNS_ATTRIBUTE.equals(prefix) ? attr.getLocalName() : "",
                        attr.getValue());
            }
        }
        return result;
    }

    @Override
    public String getText() {
        return node.getNodeValue();
    }

    @Override
    public String getEntityName() {
        return node.getNodeName();
    }

    @Override
    public String getPITarget() {
        return ((ProcessingInstruction) node).getTarget();
    }

    @Override
    public String getPIData() {
        return ((ProcessingInstruction) node).getData();
    }
}
