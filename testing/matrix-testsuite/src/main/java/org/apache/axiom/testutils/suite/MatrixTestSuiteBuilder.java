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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestSuite;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * Builds a matrix test suite. This is an abstract class. Subclasses are expected to implement the
 * {@link #addTests()} method to generate a set of {@link MatrixTestCase} instances. For each type
 * of {@link MatrixTestCase}, the {@link #addTests()} method should add instances for all allowed
 * parameter values. The resulting set can then be filtered using LDAP filter expressions.
 */
public abstract class MatrixTestSuiteBuilder {
    private static class Exclude {
        private final Class<? extends MatrixTestCase> testClass;
        private final Filter filter;

        public Exclude(Class<? extends MatrixTestCase> testClass, Filter filter) {
            this.testClass = testClass;
            this.filter = filter;
        }

        public boolean accept(MatrixTestCase test) {
            return (testClass == null || test.getClass().equals(testClass))
                    && (filter == null || filter.match(test.getTestParameters()));
        }
    }

    private final List<Exclude> excludes = new ArrayList<>();
    private TestSuite suite;

    public final void exclude(Class<? extends MatrixTestCase> testClass, String filter) {
        try {
            excludes.add(
                    new Exclude(
                            testClass, filter == null ? null : FrameworkUtil.createFilter(filter)));
        } catch (InvalidSyntaxException ex) {
            throw new IllegalArgumentException("Invalid filter expression", ex);
        }
    }

    public final void exclude(Class<? extends MatrixTestCase> testClass) {
        exclude(testClass, null);
    }

    public final void exclude(String filter) {
        exclude(null, filter);
    }

    protected abstract void addTests();

    public final TestSuite build() {
        suite = new TestSuite();
        addTests();
        return suite;
    }

    protected final void addTest(MatrixTestCase test) {
        for (Exclude exclude : excludes) {
            if (exclude.accept(test)) {
                return;
            }
        }
        suite.addTest(test);
    }
}
