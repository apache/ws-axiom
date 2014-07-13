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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMElement#declareDefaultNamespace(String)} throws an exception if an attempt is
 * made to add a namespace declaration that would conflict with the namespace information of the
 * element. The case considered in this test is adding a default namespace declaration on an
 * unprefixed element that belongs to a different namespace. This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-376">AXIOM-376</a>.
 */
public class TestDeclareDefaultNamespaceConflict2 extends AxiomTestCase {
    public TestDeclareDefaultNamespaceConflict2(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("urn:ns1", "");
        OMElement element = factory.createOMElement("test", ns);
        try {
            element.declareDefaultNamespace("urn:ns2");
            fail("Expected OMException");
        } catch (OMException ex) {
            // Expected
        }
    }
}
