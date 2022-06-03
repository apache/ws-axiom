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

public class TestGetChildrenWithName2 extends AxiomTestCase {
    public TestGetChildrenWithName2(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMNamespace testNamespace = factory.createOMNamespace("http://test.ws.org", "test");
        OMElement documentElement = factory.createOMElement("Employees", testNamespace);
        documentElement.declareNamespace(testNamespace);
        factory.createOMText(documentElement, " ");
        OMElement e = factory.createOMElement("Employee", testNamespace, documentElement);
        e.setText("Apache Developer");

        Iterator<OMElement> childrenIter =
                documentElement.getChildrenWithName(
                        new QName("http://test.ws.org", "Employee", "test"));

        int childCount = getChildrenCount(childrenIter);
        assertEquals("Iterator must return 1 child with the given qname", childCount, 1);
    }
}
