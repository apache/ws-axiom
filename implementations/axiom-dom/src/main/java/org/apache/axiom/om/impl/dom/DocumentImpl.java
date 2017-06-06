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

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import java.util.Hashtable;

import org.apache.axiom.dom.DOMDocument;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public class DocumentImpl extends ParentNode implements DOMDocument, AxiomDocument {
    protected Hashtable identifiers;
    
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
                        ((ElementImpl)newElement).coreAppendAttribute((AttrImpl)importNode(sourceAttrs.item(index), true));
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
            
            case Node.PROCESSING_INSTRUCTION_NODE: {
                ProcessingInstruction pi = (ProcessingInstruction)importedNode;
                newNode = createProcessingInstruction(pi.getTarget(), pi.getData());
                break;
            }
            case Node.ENTITY_REFERENCE_NODE:
            case Node.ENTITY_NODE:
            case Node.NOTATION_NODE:
                throw new UnsupportedOperationException("TODO : Implement handling of org.w3c.dom.Node type == " + type );

            case Node.DOCUMENT_NODE: // Can't import document nodes
            case Node.DOCUMENT_TYPE_NODE:
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
}
