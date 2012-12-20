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

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.IChildNode;
import org.apache.axiom.om.impl.common.IParentNode;
import org.apache.axiom.om.impl.common.OMNodeHelper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class LeafNode extends NodeImpl implements IChildNode {
    private ParentNode ownerNode;
    
    private NodeImpl previousSibling;

    private NodeImpl nextSibling;

    public LeafNode(OMFactory factory) {
        super(factory);
    }

    final ParentNode internalGetOwnerNode() {
        return ownerNode;
    }

    final void internalSetOwnerNode(ParentNode ownerNode) {
        this.ownerNode = ownerNode;
    }

    final NodeImpl internalGetPreviousSibling() {
        return previousSibling;
    }
    
    final NodeImpl internalGetNextSibling() {
        return nextSibling;
    }
    
    final void internalSetPreviousSibling(NodeImpl previousSibling) {
        this.previousSibling = previousSibling;
    }
    
    final void internalSetNextSibling(NodeImpl nextSibling) {
        this.nextSibling = nextSibling;
    }
    
    public final NodeList getChildNodes() {
        return EmptyNodeList.INSTANCE;
    }

    public final Node appendChild(Node newChild) throws DOMException {
        throw DOMUtil.newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
    }

    public final Node removeChild(Node oldChild) throws DOMException {
        throw DOMUtil.newDOMException(DOMException.NOT_FOUND_ERR);
    }

    public final Node insertBefore(Node newChild, Node refChild) throws DOMException {
        throw DOMUtil.newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
    }

    public final Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw DOMUtil.newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
    }

    final NodeImpl clone(OMCloneOptions options, ParentNode targetParent, boolean deep, boolean namespaceRepairing) {
        beforeClone(options);
        LeafNode clone = createClone();
        if (targetParent != null) {
            targetParent.internalAppendChild(clone);
        }
        return clone;
    }
    
    void beforeClone(OMCloneOptions options) {
        // By default, do nothing
    }
    
    abstract LeafNode createClone();

    public final OMXMLParserWrapper getBuilder() {
        return null;
    }

    public final boolean isComplete() {
        return true;
    }

    public final void setComplete(boolean state) {
        if (state != true) {
            throw new IllegalStateException();
        }
    }

    public final void discard() throws OMException {
        detach();
    }

    public final void build() {
        // Do nothing; a leaf node is always complete
    }

    public final OMNode getNextOMSibling() throws OMException {
        return OMNodeHelper.getNextOMSibling(this);
    }

    public final Node getNextSibling() {
        return (Node)getNextOMSibling();
    }

    public final IParentNode getIParentNode() {
        return parentNode();
    }

    public final String lookupNamespaceURI(String specifiedPrefix) {
        ParentNode parent = parentNode();
        // Note: according to the DOM specs, we need to delegate the lookup if the parent
        // is an element or an entity reference. However, since we don't support entity
        // references fully, we only check for elements.
        return parent instanceof Element ? parent.lookupNamespaceURI(specifiedPrefix) : null;
    }
}
