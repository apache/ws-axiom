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
package org.apache.axiom.core.impl;

import org.apache.axiom.core.Axis;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.Mapper;
import org.apache.axiom.core.Semantics;

public final class NodesIterator<T extends CoreNode, S> extends AbstractNodeIterator<T, S> {
    public NodesIterator(
            CoreParentNode startNode,
            Axis axis,
            Class<T> type,
            Mapper<S, ? super T> mapper,
            Semantics semantics) {
        super(startNode, axis, type, mapper, semantics);
    }

    @Override
    protected boolean matches(CoreNode node) throws CoreModelException {
        return true;
    }
}
