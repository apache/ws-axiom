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

import junit.framework.TestSuite;

/**
 * Builds a matrix test suite. This is an abstract class. Subclasses are expected to implement the
 * {@link #addTests()} method to generate a set of {@link MatrixTestCase} instances. For each type
 * of {@link MatrixTestCase}, the {@link #addTests()} method should add instances for all allowed
 * parameter values. The resulting set can then be filtered using LDAP filter expressions.
 */
public abstract class MatrixTestSuiteBuilder {
    private final MatrixTestFilters.Builder excludesBuilder = MatrixTestFilters.builder();
    private MatrixTestFilters excludes;
    private TestSuite suite;

    public final void exclude(Class<? extends MatrixTestCase> testClass, String filter) {
        excludesBuilder.add(testClass, filter);
    }

    public final void exclude(Class<? extends MatrixTestCase> testClass) {
        excludesBuilder.add(testClass);
    }

    public final void exclude(String filter) {
        excludesBuilder.add(filter);
    }

    protected abstract void addTests();

    public final TestSuite build() {
        excludes = excludesBuilder.build();
        suite = new TestSuite();
        addTests();
        return suite;
    }

    protected final void addTest(MatrixTestCase test) {
        if (!excludes.test(test.getClass(), test.getTestParameters())) {
            suite.addTest(test);
        }
    }
}
