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
package org.apache.axiom.testutils.suite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicNode;

import com.google.inject.Injector;

/** A {@link MatrixTestNode} that maintains an ordered list of child nodes. */
public abstract class ParentNode extends MatrixTestNode {
    private final List<MatrixTestNode> children = new ArrayList<>();

    public final void addChild(MatrixTestNode child) {
        children.add(child);
    }

    protected final Stream<DynamicNode> childDynamicNodes(
            Injector injector,
            Map<String, String> parameters,
            BiPredicate<Class<?>, Map<String, String>> excludes) {
        return children.stream()
                .flatMap(child -> child.toDynamicNodes(injector, parameters, excludes));
    }
}
