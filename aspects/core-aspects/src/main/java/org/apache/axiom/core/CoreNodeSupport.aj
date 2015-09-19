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

public aspect CoreNodeSupport {
    int CoreNode.flags;

    // Default implementation; may be overridden
    public Class<? extends CoreNode> CoreNode.coreGetNodeClass() {
        return coreGetNodeType().getInterface();
    }
    
    public final <T extends CoreNode> T CoreNode.coreCreateNode(Class<T> type) {
        T node = coreGetNodeFactory().createNode(type);
        node.updateFiliation(this);
        return node;
    }
    
    public void CoreNode.updateFiliation(CoreNode creator) {
    }
    
    public abstract CoreNode CoreNode.getRootOrOwnerDocument();

    /**
     * Get the owner document to which this node belongs.
     * 
     * @param create
     *            indicates whether the owner document should be created if it has not been created
     *            yet
     * @return the owner document or <code>null</code> if the owner document has not been created
     *         yet and <code>create</code> is <code>false</code>
     */
    public final CoreDocument CoreNode.coreGetOwnerDocument(boolean create) {
        CoreNode root = getRootOrOwnerDocument();
        if (root instanceof CoreDocument) {
            return (CoreDocument)root;
        } else if (create) {
            CoreDocument ownerDocument = root.coreGetNodeFactory().createNode(CoreDocument.class);
            root.coreSetOwnerDocument(ownerDocument);
            return ownerDocument;
        } else {
            return null;
        }
    }
    
    public final boolean CoreNode.coreHasSameOwnerDocument(CoreNode other) {
        return other.getRootOrOwnerDocument() == getRootOrOwnerDocument();
    }

    final boolean CoreNode.getFlag(int flag) {
        return (flags & flag) != 0;
    }

    final void CoreNode.setFlag(int flag, boolean value) {
        if (value) {
            flags |= flag;
        } else {
            flags &= ~flag;
        }
    }
    
    // TODO: merge this into internalClone once it is no longer referenced elsewhere
    public final <T> CoreNode CoreNode.shallowClone(ClonePolicy<T> policy, T options) {
        CoreNode clone = coreGetNodeFactory().createNode(policy.getTargetNodeClass(options, this));
        clone.init(policy, options, this);
        clone.initAncillaryData(policy, options, this);
        return clone;
    }

    final <T> CoreNode CoreNode.internalClone(ClonePolicy<T> policy, T options, CoreParentNode targetParent) {
        CoreNode clone = shallowClone(policy, options);
        if (targetParent != null) {
            targetParent.coreAppendChild((CoreChildNode)clone, false);
        }
        policy.postProcess(options, clone);
        cloneChildrenIfNecessary(policy, options, clone);
        return clone;
    }
    
    public final <T> CoreNode CoreNode.coreClone(ClonePolicy<T> policy, T options) {
        return internalClone(policy, options, null);
    }
    
    public <T> void CoreNode.initAncillaryData(ClonePolicy<T> policy, T options, CoreNode other) {
    }
}
