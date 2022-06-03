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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.ts.AxiomTestCase;

public class TestGetNamespacesInScope extends AxiomTestCase {
    public TestGetNamespacesInScope(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement element =
                AXIOMUtil.stringToOM(
                        metaFactory.getOMFactory(),
                        "<a xmlns:ns1='urn:ns1'><b xmlns:ns2='urn:ns2'/></a>");
        boolean ns1seen = false;
        boolean ns2seen = false;
        Iterator<OMNamespace> it = element.getFirstElement().getNamespacesInScope();
        int count = 0;
        while (it.hasNext()) {
            OMNamespace ns = it.next();
            count++;
            if (ns.getPrefix().equals("ns1")) {
                ns1seen = true;
                assertEquals("urn:ns1", ns.getNamespaceURI());
            } else if (ns.getPrefix().equals("ns2")) {
                ns2seen = true;
                assertEquals("urn:ns2", ns.getNamespaceURI());
            } else {
                fail("Unexpected prefix: " + ns.getPrefix());
            }
        }
        assertEquals("Number of namespaces in scope", 2, count);
        assertTrue(ns1seen);
        assertTrue(ns2seen);
    }
}
