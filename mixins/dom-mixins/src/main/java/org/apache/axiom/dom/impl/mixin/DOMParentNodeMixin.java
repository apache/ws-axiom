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
package org.apache.axiom.dom.impl.mixin;

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreDocumentFragment;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.dom.DOMConfigurationImpl;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMNode;
import org.apache.axiom.dom.DOMParentNode;
import org.apache.axiom.dom.DOMSemantics;
import org.apache.axiom.dom.DocumentWhitespaceFilter;
import org.apache.axiom.weaver.annotation.Mixin;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Mixin
public abstract class DOMParentNodeMixin implements DOMParentNode {
    @Override
    public final NodeList getChildNodes() {
        return this;
    }

    @Override
    public final int getLength() {
        int count = 0;
        Node child = getFirstChild();
        while (child != null) {
            count++;
            child = child.getNextSibling();
        }
        return count;
    }

    @Override
    public final Node item(int index) {
        int count = 0;
        Node child = getFirstChild();
        while (child != null) {
            if (count == index) {
                return child;
            } else {
                child = child.getNextSibling();
            }
            count++;
        }
        return null;
    }

    @Override
    public final Node getFirstChild() {
        try {
            return (Node)coreGetFirstChild(DocumentWhitespaceFilter.INSTANCE);
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final Node getLastChild() {
        try {
            return (Node)coreGetLastChild(DocumentWhitespaceFilter.INSTANCE);
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final boolean hasChildNodes() {
        return getFirstChild() != null;
    }

    @Override
    public final Node removeChild(Node oldChild) throws DOMException {
        if (oldChild.getParentNode() == this) {
            ((CoreChildNode)oldChild).coreDetach(DOMSemantics.INSTANCE);
            return oldChild;
        } else {
            throw DOMExceptionUtil.newDOMException(DOMException.NOT_FOUND_ERR);
        }
    }

    @Override
    public final void normalizeRecursively(DOMConfigurationImpl config) {
        try {
            normalize(config);
            CoreChildNode child = coreGetFirstChild();
            while (child != null) {
                ((DOMNode)child).normalizeRecursively(config);
                child = child.coreGetNextSibling();
            }
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    private void checkNewChild(Node newChild) {
        if (newChild instanceof DOMNode) {
            DOMNode newDomChild = (DOMNode)newChild;
            if (!coreHasSameOwnerDocument(newDomChild)) {
                throw newDOMException(DOMException.WRONG_DOCUMENT_ERR);
            }
            checkNewChild0((DOMNode)newChild);
        } else {
            throw newDOMException(DOMException.WRONG_DOCUMENT_ERR);
        }
    }
    
    void checkNewChild0(DOMNode newChild) {
    }

    @Override
    public final Node appendChild(Node newChild) {
        try {
            checkNewChild(newChild);
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
    
    // TODO: should be final
    @Override
    public Node insertBefore(Node newChild, Node refChild) {
        try {
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
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final Node replaceChild(Node newChild, Node _oldChild) {
        try {
            if (!(_oldChild instanceof CoreChildNode)) {
                throw newDOMException(DOMException.NOT_FOUND_ERR);
            }
            CoreChildNode oldChild = (CoreChildNode)_oldChild;
            if (oldChild.coreGetParent() != this) {
                throw newDOMException(DOMException.NOT_FOUND_ERR);
            }
            checkNewChild(newChild);
            if (newChild instanceof CoreChildNode) {
                oldChild.coreReplaceWith(((CoreChildNode)newChild), DOMSemantics.INSTANCE);
            } else if (newChild instanceof CoreDocumentFragment) {
                CoreChildNode nextSibling = oldChild.coreGetNextSibling();
                oldChild.coreDetach(DOMSemantics.INSTANCE);
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
}
