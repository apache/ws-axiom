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
package org.apache.axiom.om.impl.mixin;

import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.impl.intf.AxiomContainer;

@org.apache.axiom.weaver.annotation.Mixin(AxiomChildNode.class)
public abstract class AxiomChildNodeMixin implements AxiomChildNode {
    public final OMContainer getParent() {
        CoreParentNode parent = coreGetParent();
        return parent instanceof OMContainer ? (OMContainer)parent : null;
    }
    
    public final OMNode getNextOMSibling() {
        try {
            return (OMNode)coreGetNextSibling();
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    public final OMNode getPreviousOMSibling() {
        return (OMNode)coreGetPreviousSibling();
    }

    public final void insertSiblingAfter(OMNode sibling) throws OMException {
        try {
            AxiomContainer parent = (AxiomContainer)getParent();
            if (parent == null) {
                throw new OMException("Parent can not be null");
            }
            coreInsertSiblingAfter(parent.prepareNewChild(sibling));
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    public final void insertSiblingBefore(OMNode sibling) throws OMException {
        try {
            AxiomContainer parent = (AxiomContainer)getParent();
            if (parent == null) {
                throw new OMException("Parent can not be null");
            }
            coreInsertSiblingBefore(parent.prepareNewChild(sibling));
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }
    
    public final OMNode detach() {
        if (!coreHasParent()) {
            throw new OMException(
                    "Nodes that don't have a parent can not be detached");
        }
        coreDetach(AxiomSemantics.INSTANCE);
        return this;
    }
}
