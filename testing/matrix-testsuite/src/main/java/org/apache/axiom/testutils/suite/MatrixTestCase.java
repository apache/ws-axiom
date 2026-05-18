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

/**
 * Interface for matrix test cases. Used by {@link MatrixTest}.
 *
 * <p>This replaces the use of {@code junit.framework.TestCase} as the base type required by {@link
 * MatrixTest}. Test classes should implement this interface (directly or through a domain-specific
 * abstract base class), declare their Guice-injected dependencies with {@code @Inject}, and
 * implement {@link #runTest()}.
 */
public interface MatrixTestCase {
    /** Runs the test logic. Implementing classes must provide this method. */
    void runTest() throws Throwable;
}
