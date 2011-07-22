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

/**
 * Tests that {@link OMElement#declareNamespace(String, String)} generates a new prefix if the
 * specified prefix is <code>null</code>.
 */
public class TestDeclareNamespaceWithGeneratedPrefix3 extends AxiomTestCase {
    public TestDeclareNamespaceWithGeneratedPrefix3(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = factory.createOMElement(new QName("test"));
        OMNamespace ns = element.declareNamespace("urn:ns", null);
        assertEquals("urn:ns", ns.getNamespaceURI());
        assertNotNull(ns.getPrefix());
        assertTrue(ns.getPrefix().length() > 0);
        Iterator it = element.getAllDeclaredNamespaces();
        assertTrue(it.hasNext());
        OMNamespace ns2 = (OMNamespace)it.next();
        assertEquals(ns, ns2);
        assertFalse(it.hasNext());
    }
}
