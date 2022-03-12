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

import java.util.Iterator;

/** Defines the semantics of a particular API. */
public interface Semantics {
    DetachPolicy getDetachPolicy();

    /**
     * Determine how namespace lookups are performed.
     *
     * @return {@code true} if only namespace declarations are taken into account. {@code false} if
     *     the prefixes of the element and its ancestors are also taken into account (limited to
     *     instanced of {@link CoreNSAwareElement}), even if no explicit namespace declarations
     *     exists for these prefixes.
     */
    boolean isUseStrictNamespaceLookup();

    /**
     * Check if the given node type is a parent node type. This determines the set of nodes that are
     * selected by {@link Axis#DESCENDANTS} and {@link Axis#DESCENDANTS_OR_SELF}.
     *
     * @param node the node type to check
     * @return {@code true} if the node type is a parent node; {@link false} if the node type is
     *     considered a leaf node type
     */
    boolean isParentNode(NodeType nodeType);

    /**
     * Translate the given exception to an unchecked exception. This is used by {@link NodeIterator}
     * to translate exceptions that are triggered in {@link Iterator#hasNext()}, {@link
     * Iterator#next()} and {@link Iterator#remove()}.
     *
     * @param ex the original (checked) exception
     * @return the corresponding unchecked exception
     */
    RuntimeException toUncheckedException(CoreModelException ex);
}
