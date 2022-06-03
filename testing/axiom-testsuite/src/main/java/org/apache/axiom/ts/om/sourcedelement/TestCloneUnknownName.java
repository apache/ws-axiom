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

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.StringOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMInformationItem#clone(OMCloneOptions)} on an {@link
 * OMSourcedElement} backed by a non destructive {@link OMDataSource} if the name of the element is
 * unknown and the {@link OMCloneOptions#setCopyOMDataSources(boolean)} option is enabled. In this
 * case, the call to {@link OMInformationItem#clone(OMCloneOptions)} should not cause expansion of
 * the original {@link OMSourcedElement}.
 */
public class TestCloneUnknownName extends AxiomTestCase {
    public TestCloneUnknownName(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMDataSource ds = new StringOMDataSource("<p:element xmlns:p='urn:ns'>test</p:element>");
        OMSourcedElement element = factory.createOMElement(ds);
        OMCloneOptions options = new OMCloneOptions();
        options.setCopyOMDataSources(true);
        OMElement clone = (OMElement) element.clone(options);
        assertTrue(clone instanceof OMSourcedElement);
        assertFalse(element.isExpanded());
        OMNamespace expectedNS = factory.createOMNamespace("urn:ns", "p");
        assertEquals("element", element.getLocalName());
        assertEquals("element", clone.getLocalName());
        assertEquals(expectedNS, element.getNamespace());
        assertEquals(expectedNS, clone.getNamespace());
    }
}
