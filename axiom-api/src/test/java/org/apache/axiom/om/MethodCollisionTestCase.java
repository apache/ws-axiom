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
package org.apache.axiom.om;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class MethodCollisionTestCase extends TestCase {
    private final Class<?> omInterface;
    private final Class<?> domInterface;
    private final MethodSignature[] exceptions;

    public MethodCollisionTestCase(
            Class<?> omInterface, Class<?> domInterface, MethodSignature[] exceptions) {
        this.omInterface = omInterface;
        this.domInterface = domInterface;
        this.exceptions = exceptions;
        setName(omInterface.getName() + " <-> " + domInterface.getName());
    }

    public MethodCollisionTestCase(Class<?> omInterface, Class<?> domInterface) {
        this(omInterface, domInterface, null);
    }

    private Set<MethodSignature> getMethodSignatures(Class<?> iface) {
        Set<MethodSignature> result = new HashSet<MethodSignature>();
        Method[] methods = iface.getMethods();
        for (int i = 0; i < methods.length; i++) {
            result.add(new MethodSignature(methods[i]));
        }
        return result;
    }

    @Override
    protected void runTest() throws Throwable {
        Set<MethodSignature> signatures = getMethodSignatures(omInterface);
        signatures.retainAll(getMethodSignatures(domInterface));
        if (exceptions != null) {
            signatures.removeAll(Arrays.asList(exceptions));
        }
        if (!signatures.isEmpty()) {
            fail("Method collision detected for the following methods: " + signatures);
        }
    }
}
