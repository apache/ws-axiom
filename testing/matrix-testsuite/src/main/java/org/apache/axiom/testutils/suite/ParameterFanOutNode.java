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
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;

/**
 * Fan-out node for arbitrary value types that do not implement {@link Dimension}. The caller
 * supplies a parameter name and a {@link Function} that maps each value to its parameter value
 * (used for display names and LDAP filter matching).
 *
 * @param <T> the value type
 */
public class ParameterFanOutNode<T> extends AbstractFanOutNode<T> {
    private final String parameterName;
    private final Function<T, String> parameterValueFunction;

    public ParameterFanOutNode(
            Class<T> type,
            ImmutableList<T> values,
            String parameterName,
            Function<T, String> parameterValueFunction,
            ImmutableList<MatrixTestNode> children) {
        super(type, values, children);
        this.parameterName = parameterName;
        this.parameterValueFunction = parameterValueFunction;
    }

    /**
     * Convenience constructor for the case of a single child.
     *
     * @param type the value type
     * @param values the values to fan out over
     * @param parameterName the parameter name
     * @param parameterValueFunction maps each value to its parameter value string
     * @param child the single child node
     */
    public ParameterFanOutNode(
            Class<T> type,
            ImmutableList<T> values,
            String parameterName,
            Function<T, String> parameterValueFunction,
            MatrixTestNode child) {
        this(type, values, parameterName, parameterValueFunction, ImmutableList.of(child));
    }

    @Override
    protected Module createBindingModule(T value) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(type).annotatedWith(Names.named(parameterName)).toInstance(value);
            }
        };
    }

    @Override
    protected Map<String, String> extractParameters(T value) {
        return Map.of(parameterName, parameterValueFunction.apply(value));
    }
}
