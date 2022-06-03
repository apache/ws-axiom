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

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMFactory#createOMAttribute(String, OMNamespace, String)} generates a prefix if
 * an {@link OMNamespace} object with a null prefix and a non empty namespace URI is given.
 */
public class TestCreateOMAttributeGeneratedPrefix extends AxiomTestCase {
    public TestCreateOMAttributeGeneratedPrefix(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMAttribute attr =
                factory.createOMAttribute(
                        "attr", factory.createOMNamespace("urn:ns", null), "value");
        OMNamespace ns = attr.getNamespace();
        assertEquals("urn:ns", ns.getNamespaceURI());
        assertNotNull(ns.getPrefix());
        assertTrue(ns.getPrefix().length() > 0);
    }
}
