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
package org.apache.axiom.om.impl.llom;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import junit.framework.TestCase;

public class OMSourcedElementTest extends TestCase {
    /** Ensure that each method of OMElementImpl is overridden in OMSourcedElementImpl */
    public void testMethodOverrides() {
        Method[] submeths = OMSourcedElementImpl.class.getDeclaredMethods();
        Method[] supmeths = OMElementImpl.class.getDeclaredMethods();
        outer:
        for (int i = 0; i < supmeths.length; i++) {
            Method supmeth = supmeths[i];
            Class[] params = supmeth.getParameterTypes();
            if (!Modifier.isPrivate(supmeth.getModifiers())) {
                for (int j = 0; j < submeths.length; j++) {
                    Method submeth = submeths[j];
                    if (supmeth.getName().equals(submeth.getName())) {
                        if (Arrays.equals(params, submeth.getParameterTypes())) {
                            continue outer;
                        }
                    }
                }
                fail("OMSourcedElementImpl must override method " + supmeth +
                        "\nSee class JavaDocs for details");
            }
        }
    }
}