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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

/** Tests that {@link OMElement#cloneOMElement()} performs namespace repairing. */
// TODO: this is actually undocumented; in addition, it should probably made configurable
public class TestCloneOMElementNamespaceRepairing extends AxiomTestCase {
    public TestCloneOMElementNamespaceRepairing(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();

        // Create a root element on which we declare the namespaces
        OMElement root = factory.createOMElement("root", null);
        OMNamespace ns1 = root.declareNamespace("urn:ns1", "ns1");
        OMNamespace ns2 = root.declareNamespace("urn:ns2", "ns2");
        root.declareNamespace("urn:ns3", "ns3");

        // Create a child that uses these namespaces (in the element name and in the name of an
        // attribute)
        OMElement child = factory.createOMElement("child", ns1, root);
        child.addAttribute("attr", "value", ns2);

        // Check that the child has no namespace declarations (to validate the correctness of the
        // test)
        assertFalse(child.getAllDeclaredNamespaces().hasNext());

        // Clone the child and check that namespace declarations have been generated automatically
        OMElement clone = child.cloneOMElement();
        Set<OMNamespace> expectedNSDecls = new HashSet<>();
        expectedNSDecls.add(ns1);
        expectedNSDecls.add(ns2);
        Set<OMNamespace> actualNSDecls = new HashSet<>();
        for (Iterator<OMNamespace> it = clone.getAllDeclaredNamespaces(); it.hasNext(); ) {
            actualNSDecls.add(it.next());
        }
        assertEquals(expectedNSDecls, actualNSDecls);
    }
}
