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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;

/**
 * Fan-out node that iterates over a list of values, creating one {@link DynamicContainer} per
 * value. For each value, a child Guice injector is created that binds the value type to the
 * specific instance.
 *
 * @param <T> the value type
 */
public final class FanOutNode<T> extends MatrixTestNode {
    private final Function<Injector, ? extends Iterable<T>> valuesFunction;
    private final Binding<T> binding;
    private final ParameterBinding<? super T> parameterBinding;
    private final MatrixTestNode child;

    public FanOutNode(
            Function<Injector, ? extends Iterable<T>> valuesFunction,
            Binding<T> binding,
            ParameterBinding<? super T> parameterBinding,
            MatrixTestNode child) {
        this.valuesFunction = valuesFunction;
        this.binding = binding;
        this.parameterBinding = parameterBinding;
        this.child = child;
    }

    public FanOutNode(
            ImmutableList<T> values,
            Binding<T> binding,
            ParameterBinding<? super T> parameterBinding,
            MatrixTestNode child) {
        this(injector -> values, binding, parameterBinding, child);
    }

    @Override
    Stream<DynamicNode> toDynamicNodes(
            Injector parentInjector,
            Map<String, String> inheritedParameters,
            BiPredicate<Class<?>, Map<String, String>> excludes) {
        return StreamSupport.stream(valuesFunction.apply(parentInjector).spliterator(), false)
                .map(
                        value -> {
                            Injector childInjector =
                                    parentInjector.createChildInjector(
                                            binder -> binding.configure(binder, value));
                            Map<String, String> parameters = new HashMap<>(inheritedParameters);
                            parameterBinding.addTestParameters(
                                    parentInjector,
                                    value,
                                    new TestParameterTarget() {
                                        @Override
                                        public void addTestParameter(String name, String value) {
                                            parameters.put(name, value);
                                        }

                                        @Override
                                        public void addTestParameter(String name, boolean value) {
                                            addTestParameter(name, String.valueOf(value));
                                        }

                                        @Override
                                        public void addTestParameter(String name, int value) {
                                            addTestParameter(name, String.valueOf(value));
                                        }
                                    });
                            String displayName =
                                    parameters.entrySet().stream()
                                            .map(e -> e.getKey() + "=" + e.getValue())
                                            .collect(Collectors.joining(", "));
                            return DynamicContainer.dynamicContainer(
                                    displayName,
                                    child.toDynamicNodes(childInjector, parameters, excludes));
                        });
    }
}
