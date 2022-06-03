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

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;

@SuppressWarnings("deprecation")
public class TestSetDataSourceOnAlreadyExpandedElement extends AxiomTestCase {
    public TestSetDataSourceOnAlreadyExpandedElement(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMSourcedElement element =
                TestDocument.DOCUMENT1.createOMSourcedElement(
                        metaFactory.getOMFactory(), false, true);
        // Make sure the OMSourcedElement is expanded
        element.getFirstOMChild();
        assertTrue(element.isExpanded());
        // Now set a new data source
        element.setDataSource(new PullOMDataSource(TestDocument.DOCUMENT2.getContent()));
        assertFalse(element.isExpanded());
        // getNextOMSibling should not expand the element
        assertNull(element.getNextOMSibling());
        assertFalse(element.isExpanded());
    }
}
