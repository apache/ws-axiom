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

import java.util.Dictionary;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicNode;

import com.google.inject.Injector;

/**
 * Represents a node in the test tree that can be filtered before conversion to JUnit 5's dynamic
 * test API.
 *
 * <p>The {@code parentInjector} parameter threads through the tree: each fan-out node ({@link
 * DimensionFanOutNode}, {@link ParameterFanOutNode}) creates child injectors from it, and each
 * {@link MatrixTest} uses it to instantiate the test class.
 */
public abstract class MatrixTestNode {
    abstract Stream<DynamicNode> toDynamicNodes(
            Injector parentInjector,
            Dictionary<String, String> inheritedParameters,
            BiPredicate<Class<?>, Dictionary<String, String>> excludes);
}
