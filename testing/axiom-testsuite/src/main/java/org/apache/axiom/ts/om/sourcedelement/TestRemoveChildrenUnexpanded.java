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
package org.apache.axiom.ts.om.sourcedelement;

import java.util.Iterator;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.StringOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests {@link OMContainer#removeChildren()} on an {@link OMSourcedElement} that is not expanded.
 * In this case the sourced element needs to be expanded to build any attributes present on the
 * element and to ensure that the information about the name of the element is complete.
 */
public class TestRemoveChildrenUnexpanded extends AxiomTestCase {
    public TestRemoveChildrenUnexpanded(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMSourcedElement element =
                factory.createOMElement(
                        new StringOMDataSource("<element attr='value'><a/></element>"));
        element.removeChildren();
        // Check that the attribute has been added
        Iterator<OMAttribute> it = element.getAllAttributes();
        assertTrue(it.hasNext());
        OMAttribute attr = it.next();
        assertEquals("attr", attr.getLocalName());
        assertEquals("value", attr.getAttributeValue());
        assertFalse(it.hasNext());
        // Check that the element is empty
        assertNull(element.getFirstOMChild());
    }
}
