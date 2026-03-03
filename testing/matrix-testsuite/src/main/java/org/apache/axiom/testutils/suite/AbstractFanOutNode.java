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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;

/**
 * Abstract base class for fan-out nodes that iterate over a list of values, creating one {@link
 * DynamicContainer} per value. For each value, a child Guice injector is created that binds the
 * value type to the specific instance.
 *
 * <p>Subclasses define how test parameters (used for display names and LDAP filter matching) are
 * extracted from each value:
 *
 * <ul>
 *   <li>{@link DimensionFanOutNode} — for types that implement {@link Dimension}, using {@link
 *       Dimension#addTestParameters}.
 *   <li>{@link ParameterFanOutNode} — for arbitrary types, using a caller-supplied parameter name
 *       and {@link java.util.function.Function}.
 * </ul>
 *
 * @param <T> the value type
 */
public abstract class AbstractFanOutNode<T> extends ParentNode {
    private final Class<T> type;
    private final ImmutableList<T> values;

    protected AbstractFanOutNode(Class<T> type, ImmutableList<T> values) {
        this.type = type;
        this.values = values;
    }

    /**
     * Extracts test parameters from the given value. The returned map entries are used for the
     * display name and for LDAP filter matching.
     */
    protected abstract Map<String, String> extractParameters(T value);

    @Override
    Stream<DynamicNode> toDynamicNodes(
            Injector parentInjector,
            Map<String, String> inheritedParameters,
            BiPredicate<Class<?>, Map<String, String>> excludes) {
        return values.stream()
                .map(
                        value -> {
                            Injector childInjector =
                                    parentInjector.createChildInjector(
                                            new AbstractModule() {
                                                @Override
                                                protected void configure() {
                                                    bind(type).toInstance(value);
                                                }
                                            });

                            Map<String, String> parameters = extractParameters(value);
                            HashMap<String, String> params = new HashMap<>(inheritedParameters);
                            params.putAll(parameters);
                            String displayName =
                                    parameters.entrySet().stream()
                                            .map(e -> e.getKey() + "=" + e.getValue())
                                            .collect(Collectors.joining(", "));
                            return DynamicContainer.dynamicContainer(
                                    displayName,
                                    childDynamicNodes(childInjector, params, excludes));
                        });
    }
}
