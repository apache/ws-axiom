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
package org.apache.axiom.ts.dimension;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.Dimension;
import org.apache.axiom.testutils.suite.TestParameterTarget;

public abstract class NoNamespaceStrategy extends Multiton implements Dimension {
    public static final NoNamespaceStrategy NULL =
            new NoNamespaceStrategy() {
                @Override
                public void addTestParameters(TestParameterTarget testCase) {
                    testCase.addTestParameter("ns", "null");
                }

                @Override
                public OMNamespace createOMNamespace(OMFactory factory) {
                    return null;
                }
            };

    public static final NoNamespaceStrategy NULL_PREFIX =
            new NoNamespaceStrategy() {
                @Override
                public void addTestParameters(TestParameterTarget testCase) {
                    testCase.addTestParameter("ns", "nullPrefix");
                }

                @Override
                public OMNamespace createOMNamespace(OMFactory factory) {
                    return factory.createOMNamespace("", null);
                }
            };

    public static final NoNamespaceStrategy EMPTY =
            new NoNamespaceStrategy() {
                @Override
                public void addTestParameters(TestParameterTarget testCase) {
                    testCase.addTestParameter("ns", "empty");
                }

                @Override
                public OMNamespace createOMNamespace(OMFactory factory) {
                    return factory.createOMNamespace("", "");
                }
            };

    private NoNamespaceStrategy() {}

    public abstract OMNamespace createOMNamespace(OMFactory factory);
}
