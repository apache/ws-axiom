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
package org.apache.axiom.om.impl.common;

import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;

public aspect OMNodeSupport {
    declare parents: (InformationItem+ && OMNode+) implements INode;

    public final OMContainer INode.getParent() {
        CoreParentNode parent = coreGetParent();
        return parent instanceof OMContainer ? (OMContainer)parent : null;
    }
    
    public OMNode INode.getNextOMSiblingIfAvailable() {
        return (OMNode)coreGetNextSiblingIfAvailable();
    }

    public OMNode INode.getNextOMSibling() {
        return (OMNode)coreGetNextSibling();
    }

    public final OMNode INode.getPreviousOMSibling() {
        return (OMNode)coreGetPreviousSibling();
    }

    public final void INode.insertSiblingAfter(OMNode sibling) throws OMException {
        IContainer parent = (IContainer)getParent();
        if (parent == null) {
            throw new OMException("Parent can not be null");
        }
        coreInsertSiblingAfter(parent.prepareNewChild(sibling));
    }

    public final void INode.insertSiblingBefore(OMNode sibling) throws OMException {
        IContainer parent = (IContainer)getParent();
        if (parent == null) {
            throw new OMException("Parent can not be null");
        }
        coreInsertSiblingBefore(parent.prepareNewChild(sibling));
    }
    
    public OMNode INode.detach() {
        if (!coreHasParent()) {
            throw new OMException(
                    "Nodes that don't have a parent can not be detached");
        }
        coreDetach(null);
        return this;
    }
    
    public void INode.buildWithAttachments() {
        if (!isComplete()) {
            build();
        }
    }
}
