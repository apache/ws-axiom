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

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * A node that creates a child Guice injector from the supplied modules and threads it through its
 * children. Can be used at any level of the test tree to introduce additional bindings.
 *
 * <p>Exclusion filters are <em>not</em> owned by this node because they are specific to each
 * consumer (implementation under test), whereas the tree structure and bindings are defined by the
 * test suite author.
 */
public class InjectorNode extends MatrixTestNode {
    private final ImmutableList<Module> modules;
    private final List<MatrixTestNode> children = new ArrayList<>();

    /**
     * Creates a new node with the given list of modules.
     *
     * @param modules the Guice modules to install when creating the child injector
     */
    public InjectorNode(ImmutableList<Module> modules) {
        this.modules = modules;
    }

    /**
     * Convenience constructor for the common case of a single module.
     *
     * @param module the Guice module to install when creating the child injector
     */
    public InjectorNode(Module module) {
        this(ImmutableList.of(module));
    }

    public void addChild(MatrixTestNode child) {
        children.add(child);
    }

    @Override
    Stream<DynamicNode> toDynamicNodes(
            Injector parentInjector,
            Map<String, String> inheritedParameters,
            BiPredicate<Class<?>, Map<String, String>> excludes) {
        Injector injector = parentInjector.createChildInjector(modules);
        return children.stream()
                .flatMap(child -> child.toDynamicNodes(injector, inheritedParameters, excludes));
    }
}
