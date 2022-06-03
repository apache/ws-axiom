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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.StringOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMElement#getNextOMSibling()} doesn't change the state of an {@link
 * OMSourcedElement} that is expanded but not complete.
 */
public class TestGetNextOMSiblingIncomplete extends AxiomTestCase {
    public TestGetNextOMSiblingIncomplete(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMSourcedElement omse =
                factory.createOMElement(new StringOMDataSource("<sourcedelement/>"));
        OMElement parent = factory.createOMElement(new QName("parent"));
        parent.addChild(omse);
        // Cause expansion of the sourced element without building it completely
        omse.getLocalName();
        assertTrue(omse.isExpanded());
        assertFalse(omse.isComplete());
        // Call getNextOMSibling(); this should not build the element
        omse.getNextOMSibling();
        assertFalse(omse.isComplete());
    }
}
