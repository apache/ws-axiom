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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.dimension.AddAttributeStrategy;

/**
 * Tests that when adding multiple attributes with different namespaces, a corresponding namespace
 * declaration is generated for each of them.
 */
public class TestAddAttributeMultiple extends AxiomTestCase {
    private final AddAttributeStrategy strategy;

    public TestAddAttributeMultiple(OMMetaFactory metaFactory, AddAttributeStrategy strategy) {
        super(metaFactory);
        this.strategy = strategy;
        strategy.addTestParameters(this);
    }

    @Override
    protected void runTest() throws Throwable {
        String expectedXML =
                "<AttributeTester xmlns:myAttr2NS=\"http://test-attributes-2.org\" "
                        + "xmlns:myAttr1NS=\"http://test-attributes-1.org\" myAttr2NS:attrNumber=\"2\" myAttr1NS:attrNumber=\"1\" />";

        OMFactory omFactory = metaFactory.getOMFactory();

        OMNamespace attrNS1 =
                omFactory.createOMNamespace("http://test-attributes-1.org", "myAttr1NS");
        OMNamespace attrNS2 =
                omFactory.createOMNamespace("http://test-attributes-2.org", "myAttr2NS");
        OMElement omElement = omFactory.createOMElement("AttributeTester", null);
        strategy.addAttribute(omElement, "attrNumber", attrNS1, "1");
        strategy.addAttribute(omElement, "attrNumber", attrNS2, "2");

        assertAbout(xml()).that(xml(OMElement.class, omElement)).hasSameContentAs(expectedXML);
    }
}
