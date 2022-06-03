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
package org.apache.axiom.ts.om.element;

import static org.apache.axiom.truth.AxiomTruth.assertThat;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that when {@link OMElement#addAttribute(String, String, OMNamespace)} is called with an
 * {@link OMNamespace} with a <code>null</code> prefix and a namespace declaration for the given
 * namespace URI is in scope, the method reuses the existing prefix instead of generating one.
 */
public class TestAddAttributeReuseExistingPrefix extends AxiomTestCase {
    public TestAddAttributeReuseExistingPrefix(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement parent = factory.createOMElement("parent", null);
        OMElement element = factory.createOMElement("element", null, parent);
        parent.declareNamespace("urn:test", "p");
        OMAttribute attr =
                element.addAttribute("attr", "test", factory.createOMNamespace("urn:test", null));
        assertThat(attr).hasPrefix("p");
        assertThat(element).hasNoNamespaceDeclarations();
    }
}
