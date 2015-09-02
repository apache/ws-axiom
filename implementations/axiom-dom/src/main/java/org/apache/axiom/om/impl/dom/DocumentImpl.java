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

package org.apache.axiom.om.impl.dom;

import static org.apache.axiom.dom.DOMExceptionTranslator.newDOMException;

import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.NodeMigrationPolicy;
import org.apache.axiom.dom.DOMDocument;
import org.apache.axiom.dom.DOMExceptionTranslator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.common.AxiomDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class DocumentImpl extends ParentNode implements DOMDocument, AxiomDocument {
    private Vector idAttrs;

    protected Hashtable identifiers;
    
    public DocumentImpl(OMFactory factory) {
        super(factory);
    }

    // /org.w3c.dom.Document methods
    // /

    public DocumentType getDoctype() {
        Iterator it = getChildren();
        while (it.hasNext()) {
            Object child = it.next();
            if (child instanceof DocumentType) {
                return (DocumentType)child;
            } else if (child instanceof Element) {
                // A doctype declaration can only appear before the root element. Stop here.
                return null;
            }
        }
        return null;
    }

    public Element getElementById(String elementId) {

        //If there are no id attrs
        if (this.idAttrs == null) {
            return null;
        }

        Enumeration attrEnum = this.idAttrs.elements();
        while (attrEnum.hasMoreElements()) {
            Attr tempAttr = (Attr) attrEnum.nextElement();
            if (tempAttr.getValue().equals(elementId)) {
                return tempAttr.getOwnerElement();
            }
        }

        //If we reach this point then, there's no such attr 
        return null;
    }

    public Node importNode(Node importedNode, boolean deep) throws DOMException {

        short type = importedNode.getNodeType();
        Node newNode = null;
        switch (type) {
            case Node.ELEMENT_NODE: {
                Element newElement;
                if (importedNode.getLocalName() == null) {
                    newElement = this.createElement(importedNode.getNodeName());
                } else {
                    
                    String ns = importedNode.getNamespaceURI();
                    ns = (ns != null) ? ns.intern() : null;
                    newElement = createElementNS(ns, importedNode.getNodeName());
                }

                // Copy element's attributes, if any.
                NamedNodeMap sourceAttrs = importedNode.getAttributes();
                if (sourceAttrs != null) {
                    int length = sourceAttrs.getLength();
                    for (int index = 0; index < length; index++) {
                        try {
                            ((ElementImpl)newElement).coreAppendAttribute((AttrImpl)importNode(sourceAttrs.item(index), true), NodeMigrationPolicy.MOVE_ALWAYS);
                        } catch (CoreModelException ex) {
                            throw DOMExceptionTranslator.translate(ex);
                        }
                    }
                }
                newNode = newElement;
                break;
            }

            case Node.ATTRIBUTE_NODE: {
                if (importedNode.getLocalName() == null) {
                    newNode = createAttribute(importedNode.getNodeName());
                } else {
                    String ns = importedNode.getNamespaceURI();
                    ns = (ns != null) ? ns.intern() : null;
                    newNode = createAttributeNS(ns ,
                                                importedNode.getNodeName());
                }
                ((Attr) newNode).setValue(importedNode.getNodeValue());
                break;
            }

            case Node.TEXT_NODE: {
                newNode = createTextNode(importedNode.getNodeValue());
                break;
            }

            case Node.COMMENT_NODE: {
                newNode = createComment(importedNode.getNodeValue());
                break;
            }
                
            case Node.DOCUMENT_FRAGMENT_NODE: {
                newNode = createDocumentFragment();
                // No name, kids carry value
                break;
            }

            case Node.CDATA_SECTION_NODE:
                newNode = createCDATASection(importedNode.getNodeValue());
                break;
            
            case Node.ENTITY_REFERENCE_NODE:
            case Node.ENTITY_NODE:
            case Node.PROCESSING_INSTRUCTION_NODE:
            case Node.DOCUMENT_TYPE_NODE:
            case Node.NOTATION_NODE:
                throw new UnsupportedOperationException("TODO : Implement handling of org.w3c.dom.Node type == " + type );

            case Node.DOCUMENT_NODE: // Can't import document nodes
            default:
                throw newDOMException(DOMException.NOT_SUPPORTED_ERR);
        }

        // If deep, replicate and attach the kids.
        if (deep && !(importedNode instanceof Attr)) {
            for (Node srckid = importedNode.getFirstChild(); srckid != null;
                 srckid = srckid.getNextSibling()) {
                newNode.appendChild(importNode(srckid, true));
            }
        }

        return newNode;

    }

    protected void addIdAttr(Attr attr) {
        if (this.idAttrs == null) {
            this.idAttrs = new Vector();
        }
        this.idAttrs.add(attr);
    }

    protected void removeIdAttr(Attr attr) {
        if (this.idAttrs != null) {
            this.idAttrs.remove(attr);
        }

    }

    /*
    * DOM-Level 3 methods
    */

    public Node adoptNode(Node node) throws DOMException {
        if (node instanceof NodeImpl) {
            NodeImpl childNode = (NodeImpl)node;
            if (childNode instanceof CoreChildNode && ((CoreChildNode)childNode).coreHasParent()) {
                ((OMNode)childNode).detach();
            }
            childNode.coreSetOwnerDocument(this);
            if (node instanceof AttrImpl) {
                ((AttrImpl)node).coreSetSpecified(true);
            }
            return childNode;
        } else {
            return null;
        }
    }

    public String getDocumentURI() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public boolean getStrictErrorChecking() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void setDocumentURI(String documentURI) {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void setStrictErrorChecking(boolean strictErrorChecking) {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public final void checkChild(OMNode child) {
        if (child instanceof OMElement) {
            if (getOMDocumentElement() != null) {
                throw new OMException("Document element already exists");
            } else {
                checkDocumentElement((OMElement)child);
            }
        }
    }

    protected void checkDocumentElement(OMElement element) {
    }

    public final void setPrefix(String prefix) throws DOMException {
        throw newDOMException(DOMException.NAMESPACE_ERR);
    }
}
