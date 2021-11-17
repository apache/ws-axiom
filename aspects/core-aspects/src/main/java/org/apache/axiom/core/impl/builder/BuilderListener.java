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
package org.apache.axiom.core.impl.builder;

import org.apache.axiom.core.CoreCharacterDataNode;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreNode;

public interface BuilderListener {
    /**
     * Inform the listener that a node has been added to the tree. Note that this method will not be
     * invoked for {@link CoreCharacterDataNode}s (because they are created lazily). On the other
     * hand, it will be invoked for the {@link CoreDocument}. Since the builder is not reentrant,
     * implementations must not perform any operations on the node that would require building
     * additional nodes. If such operations need to be executed, the listener should return a
     * {@link Runnable} encapsulating these operations. The builder will then execute that runnable
     * as soon as it is safe to do so.
     * 
     * @param node
     *            the node that has been added
     * @param depth
     *            the depth of the node, with 0 corresponding to the document
     * @return an action to be executed when the object model can safely be accessed again
     */
    // TODO: specify if what happens for attributes (including depth)
    // TODO: an ancestor of the node may have been detached or moved; specify what this means for the depth
    DeferredAction nodeAdded(CoreNode node, int depth);
}
