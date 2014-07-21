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
package org.apache.axiom.core;

import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.builder.StAXBuilder;

public aspect CoreChildNodeSupport {
    private CoreParentNode CoreChildNode.owner;
    CoreChildNode CoreChildNode.nextSibling;
    CoreChildNode CoreChildNode.previousSibling;
    
    /**
     * Check if this node has a parent.
     * 
     * @return <code>true</code> if and only if this node currently has a parent
     */
    public final boolean CoreChildNode.coreHasParent() {
        return getFlag(Flags.HAS_PARENT);
    }
    
    /**
     * Get the parent of this node.
     * 
     * @return the parent of this node or <code>null</code> if this node doesn't have a parent
     */
    public final CoreParentNode CoreChildNode.coreGetParent() {
        return getFlag(Flags.HAS_PARENT) ? owner : null;
    }
    
    public void CoreChildNode.internalSetParent(CoreParentNode parent) {
        if (parent == null) {
            throw new IllegalArgumentException();
        }
        owner = parent;
        setFlag(Flags.HAS_PARENT, true);
    }
    
    public final void CoreChildNode.internalUnsetParent(CoreDocument newOwnerDocument) {
        owner = newOwnerDocument;
        setFlag(Flags.HAS_PARENT, false);
    }
    
    final CoreNode CoreChildNode.getRootOrOwnerDocument() {
        if (owner == null) {
            return this;
        } else {
            return owner.getRootOrOwnerDocument();
        }
    }
    
    public final void CoreChildNode.coreSetOwnerDocument(CoreDocument document) {
        if (getFlag(Flags.HAS_PARENT)) {
            throw new IllegalStateException();
        }
        owner = document;
    }
    
    public final CoreChildNode CoreChildNode.coreGetNextSiblingIfAvailable() {
        return nextSibling;
    }

    public final void CoreChildNode.coreSetNextSibling(CoreChildNode nextSibling) {
        this.nextSibling = nextSibling;
    }
    
    public final CoreChildNode CoreChildNode.coreGetPreviousSibling() {
        return previousSibling;
    }
    
    public final void CoreChildNode.coreSetPreviousSibling(CoreChildNode previousSibling) {
        this.previousSibling = previousSibling;
    }
    
    public final CoreChildNode CoreChildNode.coreGetNextSibling() throws OMException {
        CoreChildNode nextSibling = coreGetNextSiblingIfAvailable();
        if (nextSibling == null) {
            CoreParentNode parent = coreGetParent();
            if (parent != null && parent.getBuilder() != null) {
                switch (parent.getState()) {
                    case CoreParentNode.DISCARDED:
                        ((StAXBuilder)parent.getBuilder()).debugDiscarded(parent);
                        throw new NodeUnavailableException();
                    case CoreParentNode.INCOMPLETE:
                        do {
                            parent.buildNext();
                        } while (parent.getState() == CoreParentNode.INCOMPLETE
                                && (nextSibling = coreGetNextSiblingIfAvailable()) == null);
                }
            }
        }
        return nextSibling;
    }

    public final void CoreChildNode.coreInsertSiblingAfter(CoreChildNode sibling) {
        CoreParentNode parent = coreGetParent();
        // TODO: don't use OMException here
        if (parent == null) {
            throw new OMException("Parent can not be null");
        } else if (this == sibling) {
            throw new OMException("Inserting self as the sibling is not allowed");
        }
        sibling.coreDetach(null);
        sibling.internalSetParent(parent);
        CoreChildNode nextSibling = coreGetNextSibling();
        sibling.previousSibling = this;
        if (nextSibling == null) {
            parent.lastChild = sibling;
        } else {
            nextSibling.previousSibling = sibling;
        }
        sibling.nextSibling = nextSibling;
        this.nextSibling = sibling;
    }
    
    public final void CoreChildNode.coreInsertSiblingBefore(CoreChildNode sibling) {
        CoreParentNode parent = coreGetParent();
        // TODO: don't use OMException here
        if (parent == null) {
            throw new OMException("Parent can not be null");
        } else if (this == sibling) {
            throw new OMException("Inserting self as the sibling is not allowed");
        }
        sibling.coreDetach(null);
        sibling.internalSetParent(parent);
        sibling.nextSibling = this;
        if (previousSibling == null) {
            parent.firstChild = sibling;
        } else {
            previousSibling.nextSibling = sibling;
        }
        sibling.previousSibling = previousSibling;
        previousSibling = sibling;
    }
    
    public final void CoreChildNode.coreInsertSiblingsBefore(CoreDocumentFragment fragment) {
        if (fragment.firstChild == null) {
            // Fragment is empty; nothing to do
            return;
        }
        CoreParentNode parent = coreGetParent();
        // TODO: check parent != null
        CoreChildNode child = fragment.firstChild;
        while (child != null) {
            child.internalSetParent(parent);
            child = child.nextSibling;
        }
        fragment.lastChild.nextSibling = this;
        if (previousSibling == null) {
            parent.firstChild = fragment.firstChild;
        } else {
            previousSibling.nextSibling = fragment.firstChild;
        }
        fragment.firstChild.previousSibling = previousSibling;
        previousSibling = fragment.lastChild;
        fragment.firstChild = null;
        fragment.lastChild = null;
    }
    
    public final void CoreChildNode.coreDetach(CoreDocument newOwnerDocument) {
        CoreParentNode parent = coreGetParent();
        if (parent != null) {
            // TODO: ugly hack
            if (this instanceof CoreParentNode &&
                    ((CoreParentNode)this).getState() == CoreParentNode.INCOMPLETE) {
                ((CoreParentNode)this).build();
            }
            
            if (previousSibling == null) {
                parent.firstChild = nextSibling;
            } else {
                previousSibling.nextSibling = nextSibling;
            }
            if (nextSibling == null) {
                parent.lastChild = previousSibling;
            } else {
                nextSibling.previousSibling = previousSibling;
            }
            previousSibling = null;
            nextSibling = null;
            // TODO: what if parent == null? shouldn't we always set the new owner document?
            internalUnsetParent(newOwnerDocument);
        }
    }
}
