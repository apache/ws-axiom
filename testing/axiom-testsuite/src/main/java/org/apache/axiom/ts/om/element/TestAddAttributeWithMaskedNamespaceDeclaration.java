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

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.dimension.AddAttributeStrategy;

/**
 * Test checking that adding an attribute correctly generates a new namespace declaration if an
 * equivalent namespace declaration exists but is masked. The test attempts to create the following
 * XML:
 *
 * <pre>
 * &lt;a xmlns:p="urn:ns1"&gt;
 *   &lt;b xmlns:p="urn:ns2"&gt;
 *     &lt;c xmlns:p="urn:ns1" p:attr="test"/&gt;
 *   &lt;/b&gt;
 * &lt;/a&gt;</pre>
 *
 * It only explicitly creates the namespace declarations on {@code <a>} and {@code <b>}. When adding
 * the attribute to {@code <c>}, Axiom must generate a new namespace declaration because the
 * declaration on {@code <a>} is masked by the one on {@code <b>}.
 *
 * <p>Note that because of WSTX-202, Axiom will not be able to serialize the resulting XML.
 */
public class TestAddAttributeWithMaskedNamespaceDeclaration extends AxiomTestCase {
    private final AddAttributeStrategy strategy;

    public TestAddAttributeWithMaskedNamespaceDeclaration(
            OMMetaFactory metaFactory, AddAttributeStrategy strategy) {
        super(metaFactory);
        this.strategy = strategy;
        strategy.addTestParameters(this);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMNamespace ns1 = factory.createOMNamespace("urn:ns1", "p");
        OMNamespace ns2 = factory.createOMNamespace("urn:ns2", "p");
        OMElement element1 = factory.createOMElement(new QName("a"));
        element1.declareNamespace(ns1);
        OMElement element2 = factory.createOMElement(new QName("b"), element1);
        element2.declareNamespace(ns2);
        OMElement element3 = factory.createOMElement(new QName("c"), element2);
        strategy.addAttribute(element3, "attr", ns1, "test");
        Iterator<OMNamespace> it = element3.getAllDeclaredNamespaces();
        assertTrue(it.hasNext());
        assertEquals(ns1, it.next());
        assertFalse(it.hasNext());
    }
}
