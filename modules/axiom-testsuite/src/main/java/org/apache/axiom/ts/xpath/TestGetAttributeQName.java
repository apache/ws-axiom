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
package org.apache.axiom.ts.xpath;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.axiom.om.xpath.DocumentNavigator;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link DocumentNavigator#getAttributeQName(Object)} returns the correct result for an
 * attribute with namespace.
 */
public class TestGetAttributeQName extends AxiomTestCase {
    public TestGetAttributeQName(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement element = factory.createOMElement("test", null);
        element.addAttribute("att", "value", factory.createOMNamespace("urn:test", "p"));
        assertEquals("p:att", new AXIOMXPath("name(@*)").stringValueOf(element));
    }
}
