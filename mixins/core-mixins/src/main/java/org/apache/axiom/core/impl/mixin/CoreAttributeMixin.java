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
package org.apache.axiom.core.impl.mixin;

import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.core.impl.Flags;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin
public abstract class CoreAttributeMixin implements CoreAttribute {
    /**
     * The owner of the attribute. This is either a {@link CoreDocument} if the attribute is not linked
     * to an element, or a {@link CoreElement} if the attribute has been added to an element.
     */
    private CoreParentNode owner;
    
    private CoreAttribute nextAttribute;

    @Override
    public final CoreElement coreGetOwnerElement() {
        return owner instanceof CoreElement ? (CoreElement)owner : null;
    }

    @Override
    public final boolean coreHasOwnerElement() {
        return owner instanceof CoreElement;
    }

    @Override
    public final void internalSetOwnerElement(CoreElement element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        owner = element;
    }
    
    @Override
    public final void internalUnsetOwnerElement(CoreDocument newOwnerDocument) {
        owner = newOwnerDocument;
    }
    
    @Override
    public final CoreNode getRootOrOwnerDocument() {
        if (owner == null) {
            return this;
        } else {
            return owner.getRootOrOwnerDocument();
        }
    }

    @Override
    public final void coreSetOwnerDocument(CoreDocument document) {
        if (owner instanceof CoreElement) {
            // TODO
//            throw new IllegalStateException();
        }
        owner = document;
    }

    @Override
    public final CoreAttribute coreGetNextAttribute() {
        return nextAttribute;
    }

    @Override
    public final void internalSetNextAttribute(CoreAttribute nextAttribute) {
        this.nextAttribute = nextAttribute;
    }
    
    @Override
    public final CoreAttribute coreGetPreviousAttribute() {
        if (owner instanceof CoreElement) {
            CoreElement ownerElement = (CoreElement)owner;
            CoreAttribute previousAttr = ownerElement.coreGetFirstAttribute();
            while (previousAttr != null) {
                CoreAttribute nextAttr = previousAttr.coreGetNextAttribute();
                if (nextAttr == this) {
                    break;
                }
                previousAttr = nextAttr;
            }
            return previousAttr;
        } else {
            return null;
        }
    }

    @Override
    public final void internalInsertAttributeAfter(CoreAttribute attr) {
        // TODO: throw exception if attribute already has an owner
        attr.internalSetOwnerElement(coreGetOwnerElement());
        if (nextAttribute != null) {
            attr.internalSetNextAttribute(nextAttribute);
        }
        nextAttribute = attr;
    }

    @Override
    public final boolean coreRemove(Semantics semantics) {
        return internalRemove(semantics, null);
    }
    
    @Override
    public final boolean internalRemove(Semantics semantics, CoreElement newOwner) {
        if (owner instanceof CoreElement) {
            CoreElement ownerElement = (CoreElement)owner;
            CoreAttribute previousAttr = coreGetPreviousAttribute();
            owner = newOwner != null ? newOwner : semantics.getDetachPolicy().getNewOwnerDocument(ownerElement);
            if (previousAttr == null) {
                ownerElement.internalSetFirstAttribute(nextAttribute);
            } else {
                previousAttr.internalSetNextAttribute(nextAttribute);
            }
            nextAttribute = null;
            return true;
        } else {
            if (newOwner != null) {
                owner = newOwner;
            }
            return false;
        }
    }

    @Override
    public final boolean coreGetSpecified() {
        return !internalGetFlag(Flags.DEFAULT_ATTR);
    }

    @Override
    public final void coreSetSpecified(boolean specified) {
        internalSetFlag(Flags.DEFAULT_ATTR, !specified);
    }
}
