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

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicNode;

import com.google.inject.Injector;

/**
 * A node that conditionally delegates to its single child based on a predicate evaluated against
 * the current Guice injector. If the predicate returns {@code true}, the child's dynamic nodes are
 * produced; otherwise an empty stream is returned.
 *
 * <p>This is useful for removing tests or entire groups of tests for specific values in a fan-out.
 * For example, if a certain test only makes sense for a particular implementation bound in the
 * injector, this node can inspect the injector to decide whether to include the subtree.
 */
public final class ConditionalNode extends MatrixTestNode {
    private final Predicate<Injector> predicate;
    private final MatrixTestNode child;

    public ConditionalNode(Predicate<Injector> predicate, MatrixTestNode child) {
        this.predicate = predicate;
        this.child = child;
    }

    @Override
    protected Stream<DynamicNode> toDynamicNodes(
            Injector parentInjector,
            Map<String, String> inheritedParameters,
            BiPredicate<Class<?>, Map<String, String>> excludes) {
        if (!predicate.test(parentInjector)) {
            return Stream.empty();
        }
        return child.toDynamicNodes(parentInjector, inheritedParameters, excludes);
    }
}
