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
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicNode;

import com.google.inject.Injector;

/**
 * A node that only delegates to its child if a given parameter has a given value. The parameter is
 * removed from the inherited parameters before delegating.
 */
public final class SelectorNode extends MatrixTestNode {
    private final String parameterName;
    private final String parameterValue;
    private final MatrixTestNode child;

    public SelectorNode(String parameterName, String parameterValue, MatrixTestNode child) {
        this.parameterName = parameterName;
        this.parameterValue = parameterValue;
        this.child = child;
    }

    @Override
    protected Stream<DynamicNode> toDynamicNodes(
            Injector parentInjector,
            Map<String, String> inheritedParameters,
            BiPredicate<Class<?>, Map<String, String>> excludes) {
        if (!parameterValue.equals(inheritedParameters.get(parameterName))) {
            return Stream.empty();
        }
        Map<String, String> filteredParameters = new HashMap<>(inheritedParameters);
        filteredParameters.remove(parameterName);
        return child.toDynamicNodes(parentInjector, filteredParameters, excludes);
    }
}
