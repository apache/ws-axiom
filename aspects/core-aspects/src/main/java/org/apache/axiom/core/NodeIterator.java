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

import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * Extended iterator interface used by various methods in {@link CoreParentNode}. It defines an
 * additional method that allows to replace the child node returned by the last call to
 * {@link #next()}.
 * <p>
 * All implementations of this interface must satisfy the following requirements:
 * <ol>
 * <li>The implementation MUST properly implement the {@link #remove()} method, i.e. it is not
 * allowed to throw {@link UnsupportedOperationException}.
 * <li>A {@link ConcurrentModificationException} MUST be thrown when the iterator is used after the
 * last node returned by {@link Iterator#next()} has been removed using a method other than
 * {@link Iterator#remove()} (e.g. {@link CoreChildNode#coreDetach(Semantics)}).
 * <li>If a {@link CoreModelException} occurs inside {@link Iterator#hasNext()},
 * {@link Iterator#next()} or {@link Iterator#remove()}, then the implementation MUST use the
 * supplied {@link ExceptionTranslator} to translate that checked exception into an unchecked
 * exception.
 * </ol>
 */
public interface NodeIterator<T> extends Iterator<T> {
    /**
     * Replace the current node.
     * 
     * This method only has an effect if the current node is a {@link CoreChildNode}.
     * 
     * @param newNode
     * @throws CoreModelException
     */
    // TODO: the meaning of this method is not clear for Axis.DESCENDANTS
    void replace(CoreChildNode newNode) throws CoreModelException;
}
