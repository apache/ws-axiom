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

public aspect CoreAttributeSupport {
    /**
     * The owner of the attribute. This is either a {@link CoreDocument} if the attribute is not linked
     * to an element, or a {@link CoreElement} if the attribute has been added to an element.
     */
    private CoreParentNode CoreAttribute.owner;
    
    private CoreAttribute CoreAttribute.nextAttribute;

    public final CoreElement CoreAttribute.coreGetOwnerElement() {
        return owner instanceof CoreElement ? (CoreElement)owner : null;
    }

    public final boolean CoreAttribute.coreHasOwnerElement() {
        return owner instanceof CoreElement;
    }

    public final void CoreAttribute.internalSetOwnerElement(CoreElement element) {
        if (element == null) {
            throw new IllegalArgumentException();
        }
        owner = element;
    }
    
    public final void CoreAttribute.internalUnsetOwnerElement(CoreDocument newOwnerDocument) {
        owner = newOwnerDocument;
    }
    
    public final CoreNode CoreAttribute.getRootOrOwnerDocument() {
        if (owner == null) {
            return this;
        } else {
            return owner.getRootOrOwnerDocument();
        }
    }

    public final void CoreAttribute.coreSetOwnerDocument(CoreDocument document) {
        if (owner instanceof CoreElement) {
            // TODO
//            throw new IllegalStateException();
        }
        owner = document;
    }

    public final CoreAttribute CoreAttribute.coreGetNextAttribute() {
        return nextAttribute;
    }

    public final void CoreAttribute.internalSetNextAttribute(CoreAttribute nextAttribute) {
        this.nextAttribute = nextAttribute;
    }
    
    public final CoreAttribute CoreAttribute.coreGetPreviousAttribute() {
        if (owner instanceof CoreElement) {
            CoreElement ownerElement = (CoreElement)owner;
            CoreAttribute previousAttr = ownerElement.coreGetFirstAttribute();
            while (previousAttr != null) {
                CoreAttribute nextAttr = previousAttr.nextAttribute;
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

    final void CoreAttribute.insertAttributeAfter(CoreAttribute attr) {
        // TODO: throw exception if attribute already has an owner
        attr.internalSetOwnerElement(coreGetOwnerElement());
        if (nextAttribute != null) {
            attr.nextAttribute = nextAttribute;
        }
        nextAttribute = attr;
    }

    public final boolean CoreAttribute.coreRemove() {
        return remove(false, null);
    }

    public final boolean CoreAttribute.coreRemove(CoreDocument document) {
        return remove(true, document);
    }

    private boolean CoreAttribute.remove(boolean newOwnerDocument, CoreDocument ownerDocument) {
        if (owner instanceof CoreElement) {
            CoreElement ownerElement = (CoreElement)owner;
            CoreAttribute previousAttr = coreGetPreviousAttribute();
            owner = newOwnerDocument ? ownerDocument : coreGetOwnerDocument(false); // TODO: create?
            if (previousAttr == null) {
                ownerElement.internalSetFirstAttribute(nextAttribute);
            } else {
                previousAttr.nextAttribute = nextAttribute;
            }
            nextAttribute = null;
            return true;
        } else {
            if (newOwnerDocument) {
                owner = ownerDocument;
            }
            return false;
        }
    }

    public final boolean CoreAttribute.coreGetSpecified() {
        return !getFlag(Flags.DEFAULT_ATTR);
    }

    public final void CoreAttribute.coreSetSpecified(boolean specified) {
        setFlag(Flags.DEFAULT_ATTR, !specified);
    }
}
