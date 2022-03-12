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

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;

public interface CoreNode {
    /**
     * Get the owner document to which this node belongs.
     *
     * @param create indicates whether the owner document should be created if it has not been
     *     created yet
     * @return the owner document or <code>null</code> if the owner document has not been created
     *     yet and <code>create</code> is <code>false</code>
     */
    CoreDocument coreGetOwnerDocument(boolean create);

    CoreNode getRootOrOwnerDocument();

    boolean coreHasSameOwnerDocument(CoreNode other);

    void coreSetOwnerDocument(CoreDocument document);

    NodeFactory coreGetNodeFactory();

    /**
     * Get the node type.
     *
     * @return the node type
     */
    NodeType coreGetNodeType();

    Class<? extends CoreNode> coreGetNodeClass();

    /**
     * Clone this node according to the provided policy.
     *
     * @param policy the policy to use when cloning this node (and its children)
     * @return the clone of this node
     */
    <T> CoreNode coreClone(ClonePolicy<T> policy, T options) throws CoreModelException;

    <T> void init(ClonePolicy<T> policy, T options, CoreNode other) throws CoreModelException;

    <T> void cloneChildrenIfNecessary(ClonePolicy<T> policy, T options, CoreNode clone)
            throws CoreModelException;

    void internalSerialize(XmlHandler handler, boolean cache)
            throws CoreModelException, StreamException;

    boolean internalGetFlag(int flag);

    void internalSetFlag(int flag, boolean value);

    int internalGetFlags(int mask);

    void internalSetFlags(int mask, int flags);

    <T> void initAncillaryData(ClonePolicy<T> policy, T options, CoreNode other);

    <T> CoreNode internalClone(ClonePolicy<T> policy, T options, CoreParentNode targetParent)
            throws CoreModelException;
}
