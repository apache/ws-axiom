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
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMParentNode;
import org.apache.axiom.dom.DOMSemantics;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class ParentNode extends NodeImpl implements DOMParentNode {
    // /
    // /DOM Node methods
    // /

    public final Node appendChild(Node newChild) throws DOMException {
        try {
            checkNewChild(newChild, null);
            if (newChild instanceof CoreChildNode) {
                coreAppendChild((CoreChildNode)newChild);
            } else if (newChild instanceof CoreDocumentFragment) {
                coreAppendChildren((CoreDocumentFragment)newChild);
            } else {
                throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
            }
            return newChild;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    private void checkNewChild(Node newChild, Node replacedChild) {
        try {
            NodeImpl newDomChild = (NodeImpl) newChild;
            
            checkSameOwnerDocument(newDomChild);
    
            if (isAncestorOrSelf(newChild)) {
                throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
            }
    
            if (this instanceof Document) {
                if (newDomChild instanceof ElementImpl) {
                    if (!(replacedChild instanceof Element) && ((DocumentImpl) this).coreGetDocumentElement() != null) {
                        // Throw exception since there cannot be two document elements
                        throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
                    }
                } else if (!(newDomChild instanceof CommentImpl
                        || newDomChild instanceof ProcessingInstructionImpl
                        || newDomChild instanceof DocumentFragmentImpl
                        || newDomChild instanceof DocumentTypeImpl)) {
                    throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
                }
            }
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }
    
    /**
     * Inserts newChild before the refChild. If the refChild is null then the newChild is made the
     * last child.
     */
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        try {
            if (refChild == null) {
                return appendChild(newChild);
            } else {
                if (!(refChild instanceof CoreChildNode && ((CoreChildNode)refChild).coreGetParent() == this)) {
                    throw newDOMException(DOMException.NOT_FOUND_ERR);
                }
                checkNewChild(newChild, null);
                if (newChild instanceof CoreChildNode) {
                    ((CoreChildNode)refChild).coreInsertSiblingBefore((CoreChildNode)newChild);
                } else if (newChild instanceof CoreDocumentFragment) {
                    ((CoreChildNode)refChild).coreInsertSiblingsBefore((CoreDocumentFragment)newChild);
                } else {
                    throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
                }
                return newChild;
            }
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    /** Replaces the oldChild with the newChild. */
    public final Node replaceChild(Node newChild, Node _oldChild) throws DOMException {
        try {
            if (!(_oldChild instanceof CoreChildNode)) {
                throw newDOMException(DOMException.NOT_FOUND_ERR);
            }
            CoreChildNode oldChild = (CoreChildNode)_oldChild;
            if (oldChild.coreGetParent() != this) {
                throw newDOMException(DOMException.NOT_FOUND_ERR);
            }
            checkNewChild(newChild, _oldChild);
            CoreChildNode nextSibling = oldChild.coreGetNextSibling();
            oldChild.coreDetach(DOMSemantics.INSTANCE);
            if (newChild instanceof CoreChildNode) {
                if (nextSibling == null) {
                    coreAppendChild((CoreChildNode)newChild);
                } else {
                    nextSibling.coreInsertSiblingBefore((CoreChildNode)newChild);
                }
            } else if (newChild instanceof CoreDocumentFragment) {
                if (nextSibling == null) {
                    coreAppendChildren((CoreDocumentFragment)newChild);
                } else {
                    nextSibling.coreInsertSiblingsBefore((CoreDocumentFragment)newChild);
                }
            } else {
                throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
            }
            return _oldChild;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
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
}
