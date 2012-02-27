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
package org.apache.axiom.ts;

import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.testutils.suite.TestSuiteBuilder;

/**
 * Builds a test suite for Axiom implementations that also implement DOM. Note that this test suite
 * only contains tests that depend on Axiom specific features. Pure DOM tests (that are executable
 * with a standard DOM implementation) should go to
 * {@link org.apache.axiom.ts.dom.DOMTestSuiteBuilder}.
 */
public class OMDOMTestSuiteBuilder extends TestSuiteBuilder {
    private final DOMMetaFactory metaFactory;

    public OMDOMTestSuiteBuilder(DOMMetaFactory metaFactory) {
        this.metaFactory = metaFactory;
    }

    protected void addTests() {
        addTest(new org.apache.axiom.ts.om.dom.TestRemoveChildIncomplete(metaFactory));
    }
}
