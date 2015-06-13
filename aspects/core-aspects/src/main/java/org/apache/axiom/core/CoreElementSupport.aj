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

public aspect CoreElementSupport {
    private CoreAttribute CoreElement.firstAttribute;

    public final CoreAttribute CoreElement.coreGetFirstAttribute() {
        return firstAttribute;
    }

    final void CoreElement.internalSetFirstAttribute(CoreAttribute firstAttribute) {
        this.firstAttribute = firstAttribute;
    }

    public final CoreAttribute CoreElement.coreGetLastAttribute() {
        CoreAttribute previousAttribute = null;
        CoreAttribute attribute = firstAttribute;
        while (attribute != null) {
            previousAttribute = attribute;
            attribute = attribute.coreGetNextAttribute();
        }
        return previousAttribute;
    }

    private CoreAttribute CoreElement.accept(CoreAttribute attr, NodeMigrationPolicy policy) throws NodeMigrationException {
        boolean hasParent = attr.coreHasOwnerElement();
        boolean isForeignDocument = !coreHasSameOwnerDocument(attr);
        boolean isForeignModel = attr.coreGetNodeFactory() != coreGetNodeFactory();
        if (hasParent || isForeignDocument || isForeignModel) {
            switch (policy.getAction(hasParent, isForeignDocument, isForeignModel)) {
                case REJECT:
                    if (isForeignDocument) {
                        // Note that since isForeignModel implies isForeignDocument, we also get here
                        // if isForeignModel is true.
                        throw new WrongDocumentException();
                    } else {
                        // We get here if isForeignDocument and isForeignModel are false. Since at least
                        // one of the three booleans must be true, this implies that hasParent is true.
                        throw new NodeInUseException();
                    }
                case MOVE:
                    if (isForeignDocument || isForeignModel) {
                        // TODO
//                        throw new UnsupportedOperationException();
                        return attr;
                    } else {
                        attr.coreRemove();
                        return attr;
                    }
                case CLONE:
                    // TODO: probably we need to distinguish between cloning an attribute from the same model and importing it from another model (does that actually ever occur?)
                    throw new UnsupportedOperationException();
//                    return cloneAttribute(attr);
                default:
                    // Should never get here unless new values are added to the enum
                    throw new IllegalStateException();
            }
        } else {
            return attr;
        }
    }

    public final void CoreElement.coreAppendAttribute(CoreAttribute attr, NodeMigrationPolicy policy) throws NodeMigrationException {
        // TODO: we should probably check if the attribute is already owned by the element
        attr = accept(attr, policy);
        attr.internalSetOwnerElement(this);
        CoreAttribute lastAttribute = coreGetLastAttribute();
        if (lastAttribute == null) {
            firstAttribute = attr;
        } else {
            lastAttribute.insertAttributeAfter(attr);
        }
    }
}
