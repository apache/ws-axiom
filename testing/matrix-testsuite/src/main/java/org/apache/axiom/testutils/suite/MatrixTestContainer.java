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

import com.google.inject.Injector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

/**
 * A leaf node that instantiates a test class via Guice and executes all methods annotated with
 * {@link Test @Test}.
 *
 * <p>The test class must have an injectable constructor (either a no-arg constructor or one
 * annotated with {@code @Inject}). Field injection is also supported. The injector received from
 * the ancestor {@link FanOutNode} chain will have bindings for all dimension types, plus any
 * implementation-level bindings from the root injector.
 *
 * <p>This node produces a {@link DynamicContainer} named after the class, containing one
 * {@link DynamicTest} per {@link Test @Test}-annotated method. Methods are sorted alphabetically
 * for reproducibility. A fresh Guice-injected instance of the test class is created for each
 * method invocation.
 *
 * <p>Each method is evaluated against the exclusion filters independently: a label {@code "test"}
 * set to the method name is added to the inherited label map before testing. Methods that match
 * the exclusion predicate are omitted from the container. If all methods are excluded the node
 * produces an empty stream.
 */
public class MatrixTestContainer extends MatrixTestNode {
    private final Class<?> testClass;

    public MatrixTestContainer(Class<?> testClass) {
        this.testClass = testClass;
    }

    @Override
    protected Stream<DynamicNode> toDynamicNodes(
            Injector injector,
            Map<String, String> inheritedLabels,
            BiPredicate<Class<?>, Map<String, String>> excludes) {
        List<Method> testMethods = Arrays.stream(testClass.getMethods())
                .filter(m -> m.isAnnotationPresent(Test.class))
                .sorted(Comparator.comparing(Method::getName))
                .filter(m -> {
                    Map<String, String> methodLabels = new HashMap<>(inheritedLabels);
                    methodLabels.put("test", m.getName());
                    return !excludes.test(testClass, methodLabels);
                })
                .collect(Collectors.toList());
        if (testMethods.isEmpty()) {
            return Stream.empty();
        }
        return Stream.of(DynamicContainer.dynamicContainer(
                testClass.getSimpleName(),
                testMethods.stream()
                        .map(method -> DynamicTest.dynamicTest(method.getName(), () -> {
                            Object testInstance = injector.getInstance(testClass);
                            try {
                                method.invoke(testInstance);
                            } catch (InvocationTargetException e) {
                                throw e.getCause() != null ? e.getCause() : e;
                            }
                        }))));
    }
}
