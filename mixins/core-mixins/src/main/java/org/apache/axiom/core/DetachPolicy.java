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

/** Determines how nodes are detached from an object model tree. */
public interface DetachPolicy {
    DetachPolicy NEW_DOCUMENT =
            new DetachPolicy() {
                @Override
                public CoreDocument getNewOwnerDocument(CoreParentNode owner) {
                    return null;
                }
            };

    DetachPolicy SAME_DOCUMENT =
            new DetachPolicy() {
                @Override
                public CoreDocument getNewOwnerDocument(CoreParentNode owner) {
                    return owner.coreGetOwnerDocument(true);
                }
            };

    /**
     * Get the new owner document for the node (or group of child nodes) to be detached. This method
     * is called before any node is detached.
     *
     * @param owner The owner of the node (or group of child nodes) to be detached. For child nodes,
     *     this is the parent node. For attributes, this is the owner element.
     * @return the new owner document or <code>null</code> to (lazily) create a new owner document
     *     for the node(s)
     */
    CoreDocument getNewOwnerDocument(CoreParentNode owner);
}
