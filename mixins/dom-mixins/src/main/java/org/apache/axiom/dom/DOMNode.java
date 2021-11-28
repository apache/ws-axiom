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
package org.apache.axiom.dom;

import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.weaver.annotation.Inject;
import org.w3c.dom.Node;

public interface DOMNode extends Node, CoreNode {
    @Inject
    DOMNodeFactory getDOMNodeFactory();

    /**
     * Get the element that defines this node's namespace context. The returned element is used by
     * the implementations of {@link Node#lookupNamespaceURI(String)},
     * {@link Node#lookupPrefix(String)} and {@link Node#isDefaultNamespace(String)}. The
     * implementation must be compatible with the provisions in appendix B of the DOM Level 3 Core
     * specification.
     * 
     * @return the element defining the namespace context of this node or <code>null</code> if the
     *         namespace context of this node is defined to be empty
     */
    CoreElement getNamespaceContext();

    void normalizeRecursively(DOMConfigurationImpl config);
}
