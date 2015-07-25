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

public interface CoreChildNode extends CoreNode {
    /**
     * Get the parent element of this node.
     * 
     * @return the parent element of this node or <code>null</code> if the node has no parent or if
     *         the parent is not an element
     */
    CoreElement coreGetParentElement();

    /**
     * Get the next sibling if it is available. The sibling is available if it is complete or
     * if the builder has started building the node. In the latter case,
     * {@link OMNode#isComplete()} may return <code>false</code> when called on the sibling. 
     * In contrast to {@link OMNode#getNextOMSibling()}, this method will never modify
     * the state of the underlying parser.
     * 
     * @return the next sibling or <code>null</code> if the node has no next sibling or
     *         the builder has not yet started to build the next sibling
     */
    CoreChildNode coreGetNextSiblingIfAvailable();
}
