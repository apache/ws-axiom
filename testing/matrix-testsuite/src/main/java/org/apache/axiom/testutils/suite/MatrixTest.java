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
import org.junit.jupiter.api.DynamicTest;

import com.google.inject.Injector;

import junit.framework.TestCase;

/**
 * A leaf node that instantiates a {@link TestCase} subclass via Guice and executes it.
 *
 * <p>The test class must have an injectable constructor (either a no-arg constructor or one
 * annotated with {@code @Inject}). Field injection is also supported. The injector received from
 * the ancestor {@link AbstractFanOutNode} chain will have bindings for all dimension types, plus
 * any implementation-level bindings from the root injector.
 *
 * <p>Once the instance is created, it is executed via {@link TestCase#runBare()}, which invokes the
 * full {@code setUp()} → {@code runTest()} → {@code tearDown()} lifecycle.
 */
public class MatrixTest extends MatrixTestNode {
    private final Class<? extends TestCase> testClass;

    public MatrixTest(Class<? extends TestCase> testClass) {
        this.testClass = testClass;
    }

    @Override
    Stream<DynamicNode> toDynamicNodes(
            Injector injector,
            Dictionary<String, String> inheritedParameters,
            BiPredicate<Class<?>, Dictionary<String, String>> excludes) {
        if (excludes.test(testClass, inheritedParameters)) {
            return Stream.empty();
        }
        return Stream.of(
                DynamicTest.dynamicTest(
                        testClass.getSimpleName(),
                        () -> {
                            TestCase testInstance = injector.getInstance(testClass);
                            testInstance.setName(testClass.getSimpleName());
                            testInstance.runBare();
                        }));
    }
}
