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
import com.google.inject.Module;

/**
 * Abstract base class for fan-out nodes that iterate over a list of values, creating one {@link
 * DynamicContainer} per value. For each value, a child Guice injector is created that binds the
 * value type to the specific instance. The binding is created by {@link
 * #createBindingModule(Object)}, which subclasses may override (e.g. {@link ParameterFanOutNode}
 * adds a {@code @Named} annotation).
 *
 * <p>Subclasses define how test parameters (used for display names and LDAP filter matching) are
 * extracted from each value:
 *
 * <ul>
 *   <li>{@link DimensionFanOutNode} — for types that implement {@link Dimension}, using {@link
 *       Dimension#addTestParameters}. The value is bound as a plain (unannotated) type binding.
 *   <li>{@link ParameterFanOutNode} — for arbitrary types, using a caller-supplied parameter name
 *       and {@link java.util.function.Function}. The value is bound with a {@code @Named}
 *       annotation whose value is the parameter name; injection sites must use
 *       {@code @Inject @Named("paramName")}.
 * </ul>
 *
 * @param <T> the value type
 */
public abstract class AbstractFanOutNode<T> extends MatrixTestNode {
    protected final Class<T> type;
    private final ImmutableList<T> values;
    private final MatrixTestNode child;

    protected AbstractFanOutNode(Class<T> type, ImmutableList<T> values, MatrixTestNode child) {
        this.type = type;
        this.values = values;
        this.child = child;
    }

    /**
     * Extracts test parameters from the given value. The returned map entries are used for the
     * display name and for LDAP filter matching.
     */
    protected abstract Map<String, String> extractParameters(T value);

    /**
     * Creates the Guice module that binds the value for a given fan-out iteration. Subclasses may
     * override this to customise the binding (e.g. adding a binding annotation).
     *
     * @param value the current iteration value
     * @return a module that provides the binding for {@code value}
     */
    protected Module createBindingModule(T value) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(type).toInstance(value);
            }
        };
    }

    @Override
    Stream<DynamicNode> toDynamicNodes(
            Injector parentInjector,
            Map<String, String> inheritedParameters,
            BiPredicate<Class<?>, Map<String, String>> excludes) {
        return values.stream()
                .map(
                        value -> {
                            Injector childInjector =
                                    parentInjector.createChildInjector(createBindingModule(value));

                            Map<String, String> parameters = extractParameters(value);
                            HashMap<String, String> params = new HashMap<>(inheritedParameters);
                            params.putAll(parameters);
                            String displayName =
                                    parameters.entrySet().stream()
                                            .map(e -> e.getKey() + "=" + e.getValue())
                                            .collect(Collectors.joining(", "));
                            return DynamicContainer.dynamicContainer(
                                    displayName,
                                    child.toDynamicNodes(childInjector, params, excludes));
                        });
    }
}
