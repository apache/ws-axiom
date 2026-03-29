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
package org.apache.axiom.ts.dom.w3c;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.apache.axiom.testutils.suite.MatrixTestNode;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.w3c.domts.DOMTestCase;

import com.google.inject.Injector;

public final class DOMTestNode extends MatrixTestNode {
    private final DOMTestCase test;

    public DOMTestNode(DOMTestCase test) {
        this.test = test;
    }

    @Override
    protected final Stream<DynamicNode> toDynamicNodes(
            Injector parentInjector,
            Map<String, String> inheritedParameters,
            BiPredicate<Class<?>, Map<String, String>> excludes) {
        Map<String, String> parameters = new HashMap<>(inheritedParameters);
        parameters.put("id", test.getTargetURI());
        if (excludes.test(test.getClass(), parameters)) {
            return Stream.empty();
        }
        return Stream.of(DynamicTest.dynamicTest(test.getClass().getSimpleName(), test::runTest));
    }
}
