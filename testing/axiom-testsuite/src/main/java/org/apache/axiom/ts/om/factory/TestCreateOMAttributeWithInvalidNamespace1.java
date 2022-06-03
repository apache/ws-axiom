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
package org.apache.axiom.ts.om.factory;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMFactory#createOMAttribute(String, OMNamespace, String)} throws an exception
 * if the specified namespace is invalid, i.e. if the {@link OMNamespace} object specifies a prefix
 * for an empty namespace.
 */
public class TestCreateOMAttributeWithInvalidNamespace1 extends AxiomTestCase {
    public TestCreateOMAttributeWithInvalidNamespace1(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("", "p");
        try {
            factory.createOMAttribute("attr", ns, "value");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                    "Cannot create a prefixed attribute with an empty namespace name",
                    ex.getMessage());
        }
    }
}
