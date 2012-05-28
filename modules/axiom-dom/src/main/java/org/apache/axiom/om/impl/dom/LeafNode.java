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

import org.apache.axiom.om.OMFactory;

public abstract class LeafNode extends ChildNode {
    private ParentNode ownerNode;
    
    private ChildNode previousSibling;

    private ChildNode nextSibling;

    protected LeafNode(DocumentImpl ownerDocument, OMFactory factory) {
        super(ownerDocument, factory);
    }

    public LeafNode(OMFactory factory) {
        super(factory);
    }

    final ParentNode internalGetOwnerNode() {
        return ownerNode;
    }

    final void internalSetOwnerNode(ParentNode ownerNode) {
        this.ownerNode = ownerNode;
    }

    final ChildNode internalGetPreviousSibling() {
        return previousSibling;
    }
    
    final ChildNode internalGetNextSibling() {
        return nextSibling;
    }
    
    final void internalSetPreviousSibling(ChildNode previousSibling) {
        this.previousSibling = previousSibling;
    }
    
    final void internalSetNextSibling(ChildNode nextSibling) {
        this.nextSibling = nextSibling;
    }
}
