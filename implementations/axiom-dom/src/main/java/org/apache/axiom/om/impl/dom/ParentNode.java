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

import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreDocumentFragment;
import org.apache.axiom.dom.DOMParentNode;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class ParentNode extends NodeImpl implements DOMParentNode {
    protected ParentNode(OMFactory factory) {
        super(factory);
    }

    // /
    // /DOM Node methods
    // /

    public final Node appendChild(Node newChild) throws DOMException {
        checkNewChild(newChild);
        if (newChild instanceof CoreChildNode) {
            coreAppendChild((CoreChildNode)newChild, false);
        } else if (newChild instanceof CoreDocumentFragment) {
            coreAppendChildren((CoreDocumentFragment)newChild);
        } else {
            throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
        }
        return newChild;
    }

    private void checkNewChild(Node newChild) {
        NodeImpl newDomChild = (NodeImpl) newChild;
        
        checkSameOwnerDocument(newDomChild);

        if (isAncestorOrSelf(newChild)) {
            throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
        }

        if (this instanceof Document) {
            if (newDomChild instanceof ElementImpl) {
                if (((DocumentImpl) this).getOMDocumentElement() != null) {
                    // Throw exception since there cannot be two document elements
                    throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
                }
                if (newDomChild.parentNode() == null) {
                    newDomChild.setParent(this);
                }
            } else if (!(newDomChild instanceof CommentImpl
                    || newDomChild instanceof ProcessingInstructionImpl
                    || newDomChild instanceof DocumentFragmentImpl
                    || newDomChild instanceof DocumentTypeImpl)) {
                throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
            }
        }
        
    }
    
    /**
     * Inserts newChild before the refChild. If the refChild is null then the newChild is made the
     * last child.
     */
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        if (refChild == null) {
            return appendChild(newChild);
        } else {
            if (!(refChild instanceof CoreChildNode && ((CoreChildNode)refChild).coreGetParent() == this)) {
                throw newDOMException(DOMException.NOT_FOUND_ERR);
            }
            checkNewChild(newChild);
            if (newChild instanceof CoreChildNode) {
                ((CoreChildNode)refChild).coreInsertSiblingBefore((CoreChildNode)newChild);
            } else if (newChild instanceof CoreDocumentFragment) {
                ((CoreChildNode)refChild).coreInsertSiblingsBefore((CoreDocumentFragment)newChild);
            } else {
                throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
            }
            return newChild;
        }
    }

    /** Replaces the oldChild with the newChild. */
    public final Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        NodeImpl newDomChild = (NodeImpl) newChild;
        NodeImpl oldDomChild = (NodeImpl) oldChild;

        if (newChild == null) {
            throw new IllegalArgumentException("newChild can't be null");
        }

        if (isAncestorOrSelf(newChild)) {
            throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
        }

        checkSameOwnerDocument(newDomChild);

        NodeImpl tempNode = (NodeImpl)getFirstChild();
        boolean found = false;
        while (!found && tempNode != null) {
            if (tempNode == oldChild) {
                NodeImpl head; // The first child to insert
                NodeImpl tail; // The last child to insert
                
                if (newChild instanceof DocumentFragmentImpl) {
                    DocumentFragmentImpl docFrag =
                            (DocumentFragmentImpl) newDomChild;
                    head = (NodeImpl)docFrag.getFirstChild();
                    tail = (NodeImpl)docFrag.getLastChild();
                    
                    NodeImpl child = (NodeImpl) docFrag.getFirstChild();
                    //set the parent of all kids to me
                    while(child != null) {
                        child.setParent(this);
                        child = child.internalGetNextSibling();
                    }

                    docFrag.coreSetFirstChild(null);
                    docFrag.coreSetLastChild(null);
                } else {
                    head = newDomChild;
                    tail = newDomChild;
                    newDomChild.setParent(this);
                }
                
                // We use getNextSibling here to force bulding the node if necessary
                NodeImpl nextSibling = (NodeImpl)oldDomChild.getNextSibling();
                NodeImpl previousSibling = oldDomChild.internalGetPreviousSibling();
                
                tail.internalSetNextSibling(nextSibling);
                head.internalSetPreviousSibling(previousSibling);
                
                if (previousSibling != null) {
                    previousSibling.internalSetNextSibling(head);
                } else {
                    coreSetFirstChild((CoreChildNode)head);
                }

                if (nextSibling != null) {
                    nextSibling.internalSetPreviousSibling(tail);
                } else {
                    coreSetLastChild((CoreChildNode)tail);
                }
                
                found = true;

                // remove the old child's references to this tree
                oldDomChild.internalSetNextSibling(null);
                oldDomChild.internalSetPreviousSibling(null);
                oldDomChild.setParent(null);
            }
            tempNode = (NodeImpl)tempNode.getNextSibling();
        }

        if (!found)
            throw newDOMException(DOMException.NOT_FOUND_ERR);

        return oldChild;
    }

    /**
     * Checks if the given node is an ancestor (or identical) to this node.
     * 
     * @param node
     *            the node to check
     * @return <code>true</code> if the node is an ancestor or indentical to this node
     */
    private boolean isAncestorOrSelf(Node node) {
        Node currentNode = this;
        do {
            if (currentNode == node) {
                return true;
            }
            currentNode = currentNode.getParentNode();
        } while (currentNode != null);
        return false;
    }

    final NodeImpl clone(OMCloneOptions options, ParentNode targetParent, boolean deep, boolean namespaceRepairing) {
        ParentNode clone = shallowClone(options, targetParent, namespaceRepairing);
        if (deep) {
            for (Node child = getFirstChild(); child != null; child = child.getNextSibling()) {
                ((NodeImpl)child).clone(options, clone, true, namespaceRepairing);
            }
        }
        return clone;
    }
    
    abstract ParentNode shallowClone(OMCloneOptions options, ParentNode targetParent, boolean namespaceRepairing);

    public String getTextContent() throws DOMException {
        Node child = getFirstChild();
        if (child != null) {
            Node next = child.getNextSibling();
            if (next == null) {
                return hasTextContent(child) ? ((NodeImpl)child).getTextContent() : "";
            }
            StringBuffer buf = new StringBuffer();
            getTextContent(buf);
            return buf.toString();
        } else {
            return "";
        }
    }

    void getTextContent(StringBuffer buf) throws DOMException {
        Node child = getFirstChild();
        while (child != null) {
            if (hasTextContent(child)) {
                ((NodeImpl)child).getTextContent(buf);
            }
            child = child.getNextSibling();
        }
    }
    
    // internal method returning whether to take the given node's text content
    private static boolean hasTextContent(Node child) {
        return child.getNodeType() != Node.COMMENT_NODE &&
            child.getNodeType() != Node.PROCESSING_INSTRUCTION_NODE /* &&
            (child.getNodeType() != Node.TEXT_NODE ||
             ((TextImpl) child).isIgnorableWhitespace() == false)*/;
    }
    
    public void setTextContent(String textContent) throws DOMException {
        // get rid of any existing children
        coreRemoveChildren(coreGetOwnerDocument(true));
        // create a Text node to hold the given content
        if (textContent != null && textContent.length() != 0) {
            coreAppendChild((CoreChildNode)getOMFactory().createOMText(textContent), false);
        }
    }
}
