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

import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin
public abstract class CoreNodeMixin implements CoreNode {
    int flags;

    // Default implementation; may be overridden
    @Override
    public Class<? extends CoreNode> coreGetNodeClass() {
        return coreGetNodeType().getInterface();
    }
    
    @Override
    public final CoreDocument coreGetOwnerDocument(boolean create) {
        CoreNode root = getRootOrOwnerDocument();
        if (root instanceof CoreDocument) {
            return (CoreDocument)root;
        } else if (create) {
            CoreDocument ownerDocument = root.coreGetNodeFactory().getFactory2().createDocument();
            root.coreSetOwnerDocument(ownerDocument);
            return ownerDocument;
        } else {
            return null;
        }
    }
    
    @Override
    public final boolean coreHasSameOwnerDocument(CoreNode other) {
        return other.getRootOrOwnerDocument() == getRootOrOwnerDocument();
    }

    @Override
    public final boolean internalGetFlag(int flag) {
        return (flags & flag) != 0;
    }

    @Override
    public final void internalSetFlag(int flag, boolean value) {
        if (value) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }
    }
    
    @Override
    public final int internalGetFlags(int mask) {
        return flags & mask;
    }

    @Override
    public final void internalSetFlags(int mask, int value) {
        flags = (flags & ~mask) | value;
    }
    
    // TODO: merge this into internalClone once it is no longer referenced elsewhere
    public final <T> CoreNode shallowClone(ClonePolicy<T> policy, T options) throws CoreModelException {
        CoreNode clone = coreGetNodeFactory().createNode(policy.getTargetNodeClass(options, this));
        clone.init(policy, options, this);
        clone.initAncillaryData(policy, options, this);
        return clone;
    }

    @Override
    public final <T> CoreNode internalClone(ClonePolicy<T> policy, T options, CoreParentNode targetParent) throws CoreModelException {
        CoreNode clone = shallowClone(policy, options);
        if (targetParent != null) {
            targetParent.coreAppendChild((CoreChildNode)clone);
        }
        policy.postProcess(options, clone);
        cloneChildrenIfNecessary(policy, options, clone);
        return clone;
    }
    
    @Override
    public final <T> CoreNode coreClone(ClonePolicy<T> policy, T options) throws CoreModelException {
        return internalClone(policy, options, null);
    }
    
    @Override
    public <T> void initAncillaryData(ClonePolicy<T> policy, T options, CoreNode other) {
    }
}
