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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicNode;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Root of a test suite. Owns the Guice root injector and the tree of {@link MatrixTestNode}
 * instances. Provides a {@link #toDynamicNodes(BiPredicate)} method that converts the tree to JUnit
 * 5 dynamic nodes, applying the supplied exclusion predicate.
 *
 * <p>Exclusion filters are <em>not</em> owned by the suite itself because they are specific to each
 * consumer (implementation under test), whereas the suite structure and bindings are defined by the
 * test suite author.
 */
public class MatrixTestSuite {
    private final Injector rootInjector;
    private final List<MatrixTestNode> children = new ArrayList<>();

    public MatrixTestSuite(Module... modules) {
        this.rootInjector = Guice.createInjector(modules);
    }

    public void addChild(MatrixTestNode child) {
        children.add(child);
    }

    public Stream<DynamicNode> toDynamicNodes(
            BiPredicate<Class<?>, Dictionary<String, String>> excludes) {
        return children.stream()
                .flatMap(child -> child.toDynamicNodes(rootInjector, new Hashtable<>(), excludes));
    }

    public Stream<DynamicNode> toDynamicNodes() {
        return toDynamicNodes((testClass, parameters) -> false);
    }
}
