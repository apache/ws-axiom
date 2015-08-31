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

/**
 * Defines how an object model tree is to be cloned.
 */
public interface ClonePolicy<T> {
    Class<? extends CoreNode> getTargetNodeClass(T options, CoreNode node);
    boolean repairNamespaces(T options);
    boolean cloneAttributes(T options);
    boolean cloneChildren(T options, NodeType nodeType);

    /**
     * Post-process a cloned node. This method is called after all information from the original
     * node has been copied (for elements, this includes the attributes of the element) and the node
     * has been inserted into the cloned tree, but before any children are added.
     * 
     * @param options
     *            API specific options
     * @param clone
     *            the clone to be post-processed
     */
    void postProcess(T options, CoreNode clone);
}
