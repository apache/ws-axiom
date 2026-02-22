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

import java.util.Dictionary;
import java.util.Hashtable;

import junit.framework.TestCase;

/** A test case that can be executed multiple times with different parameters. */
public abstract class MatrixTestCase extends TestCase implements TestParameterTarget {
    private final Dictionary<String, String> parameters = new Hashtable<>();

    public MatrixTestCase() {
        setName(getClass().getName());
    }

    @Override
    public final void addTestParameter(String name, String value) {
        setName(getName() + " [" + name + "=" + value + "]");
        parameters.put(name, value);
    }

    @Override
    public final void addTestParameter(String name, boolean value) {
        addTestParameter(name, String.valueOf(value));
    }

    @Override
    public final void addTestParameter(String name, int value) {
        addTestParameter(name, String.valueOf(value));
    }

    public final Dictionary<String, String> getTestParameters() {
        return parameters;
    }

    // Force subclasses to override the runTest method:
    @Override
    protected abstract void runTest() throws Throwable;
}
