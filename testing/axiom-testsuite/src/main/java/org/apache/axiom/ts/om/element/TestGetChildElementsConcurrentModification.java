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

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that the iterator returned by {@link OMElement#getChildElements()} throws a {@link
 * ConcurrentModificationException} if the current node is removed using a method other than {@link
 * Iterator#remove()}. This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-391">AXIOM-391</a>.
 */
public class TestGetChildElementsConcurrentModification extends AxiomTestCase {
    public TestGetChildElementsConcurrentModification(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement parent = factory.createOMElement("parent", null);
        factory.createOMElement("child1", null, parent);
        factory.createOMElement("child2", null, parent);
        factory.createOMElement("child3", null, parent);
        Iterator<OMElement> it = parent.getChildElements();
        it.next();
        OMElement child2 = it.next();
        child2.detach();
        try {
            it.next();
            fail("Expected ConcurrentModificationException");
        } catch (ConcurrentModificationException ex) {
            // Expected
        }
    }
}
