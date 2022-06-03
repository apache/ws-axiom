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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamedInformationItem;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;

/**
 * Tests that the effect of {@link OMNamedInformationItem#setLocalName(String)} on a {@link
 * OMSourcedElement} is the same on expanded and unexpanded elements. In both cases, it must behave
 * in the same way as a normal {@link OMElement}, which implies that it must override the local name
 * of the root element returned by the data source.
 */
public class TestSetLocalName extends AxiomTestCase {
    private boolean expand;

    public TestSetLocalName(OMMetaFactory metaFactory, boolean expand) {
        super(metaFactory);
        this.expand = expand;
        addTestParameter("expand", expand);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMSourcedElement element =
                factory.createOMElement(
                        new PullOMDataSource("<p:root xmlns:p='urn:test'><child/></p:root>"),
                        "root",
                        factory.createOMNamespace("urn:test", "p"));
        if (expand) {
            element.getFirstOMChild();
        }
        element.setLocalName("newroot");
        assertAbout(xml())
                .that(element.toString())
                .hasSameContentAs("<p:newroot xmlns:p='urn:test'><child/></p:newroot>");
    }
}
