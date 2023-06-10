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
package org.apache.axiom.truth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.truth.xml.spi.Event;
import org.apache.axiom.truth.xml.spi.Traverser;
import org.apache.axiom.truth.xml.spi.TraverserException;

final class AxiomTraverser implements Traverser {
    private final OMContainer root;
    private final boolean expandEntityReferences;
    private OMNode node;
    private boolean visited;

    AxiomTraverser(OMContainer root, boolean expandEntityReferences) {
        this.root = root;
        this.expandEntityReferences = expandEntityReferences;
    }

    @Override
    public Event next() throws TraverserException {
        if (node == null) {
            if (root instanceof OMDocument) {
                node = ((OMDocument) root).getFirstOMChild();
            } else {
                node = (OMElement) root;
            }
        } else if (!visited && node instanceof OMElement) {
            OMNode firstChild = ((OMElement) node).getFirstOMChild();
            if (firstChild != null) {
                node = firstChild;
            } else {
                visited = true;
            }
        } else {
            OMNode nextSibling = node.getNextOMSibling();
            if (node == root) {
                return null;
            } else if (nextSibling != null) {
                node = nextSibling;
                visited = false;
            } else {
                OMContainer parent = node.getParent();
                if (parent instanceof OMDocument) {
                    return null;
                } else {
                    node = (OMElement) parent;
                    visited = true;
                }
            }
        }
        switch (node.getType()) {
            case OMNode.DTD_NODE:
                return Event.DOCUMENT_TYPE;
            case OMNode.ELEMENT_NODE:
                return visited ? Event.END_ELEMENT : Event.START_ELEMENT;
            case OMNode.TEXT_NODE:
                return Event.TEXT;
            case OMNode.SPACE_NODE:
                return Event.WHITESPACE;
            case OMNode.ENTITY_REFERENCE_NODE:
                if (expandEntityReferences) {
                    throw new UnsupportedOperationException();
                } else {
                    return Event.ENTITY_REFERENCE;
                }
            case OMNode.COMMENT_NODE:
                return Event.COMMENT;
            case OMNode.CDATA_SECTION_NODE:
                return Event.CDATA_SECTION;
            case OMNode.PI_NODE:
                return Event.PROCESSING_INSTRUCTION;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String getRootName() {
        return ((OMDocType) node).getRootName();
    }

    @Override
    public String getPublicId() {
        return ((OMDocType) node).getPublicId();
    }

    @Override
    public String getSystemId() {
        return ((OMDocType) node).getSystemId();
    }

    @Override
    public QName getQName() {
        return ((OMElement) node).getQName();
    }

    @Override
    public Map<QName, String> getAttributes() {
        Map<QName, String> attributes = null;
        for (Iterator<OMAttribute> it = ((OMElement) node).getAllAttributes(); it.hasNext(); ) {
            OMAttribute attr = it.next();
            if (attributes == null) {
                attributes = new HashMap<QName, String>();
            }
            attributes.put(attr.getQName(), attr.getAttributeValue());
        }
        return attributes;
    }

    @Override
    public Map<String, String> getNamespaces() {
        Map<String, String> namespaces = null;
        for (Iterator<OMNamespace> it = ((OMElement) node).getAllDeclaredNamespaces();
                it.hasNext(); ) {
            OMNamespace ns = it.next();
            if (namespaces == null) {
                namespaces = new HashMap<String, String>();
            }
            namespaces.put(ns.getPrefix(), ns.getNamespaceURI());
        }
        return namespaces;
    }

    @Override
    public String getText() {
        switch (node.getType()) {
            case OMNode.TEXT_NODE:
            case OMNode.SPACE_NODE:
            case OMNode.CDATA_SECTION_NODE:
                return ((OMText) node).getText();
            case OMNode.COMMENT_NODE:
                return ((OMComment) node).getValue();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String getEntityName() {
        return ((OMEntityReference) node).getName();
    }

    @Override
    public String getPITarget() {
        return ((OMProcessingInstruction) node).getTarget();
    }

    @Override
    public String getPIData() {
        return ((OMProcessingInstruction) node).getValue();
    }
}
