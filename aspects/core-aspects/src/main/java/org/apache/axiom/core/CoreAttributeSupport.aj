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

    public final CoreElement CoreAttribute.coreGetOwnerElement() {
        return owner instanceof CoreElement ? (CoreElement)owner : null;
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
    
    final CoreNode CoreAttribute.getRootOrOwnerDocument() {
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

    public final boolean CoreAttribute.coreGetSpecified() {
        return !getFlag(Flags.DEFAULT_ATTR);
    }

    public final void CoreAttribute.coreSetSpecified(boolean specified) {
        setFlag(Flags.DEFAULT_ATTR, !specified);
    }
}
