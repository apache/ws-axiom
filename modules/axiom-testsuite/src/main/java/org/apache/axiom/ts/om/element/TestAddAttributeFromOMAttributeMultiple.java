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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;

/**
 * Tests that when {@link OMElement#addAttribute(org.apache.axiom.om.OMAttribute)} is called
 * multiple times for attributes with different namespaces, each call adds a corresponding namespace
 * declaration.
 */
public class TestAddAttributeFromOMAttributeMultiple extends AxiomTestCase {
    public TestAddAttributeFromOMAttributeMultiple(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        String expectedXML =
                "<AttributeTester xmlns=\"\" xmlns:myAttr2NS=\"http://test-attributes-2.org\" " +
                        "xmlns:myAttr1NS=\"http://test-attributes-1.org\" myAttr2NS:attrNumber=\"2\" myAttr1NS:attrNumber=\"1\" />";
    
        OMFactory omFactory = metaFactory.getOMFactory();
    
        OMNamespace attrNS1 =
                omFactory.createOMNamespace("http://test-attributes-1.org", "myAttr1NS");
        OMNamespace attrNS2 =
                omFactory.createOMNamespace("http://test-attributes-2.org", "myAttr2NS");
        OMElement omElement = omFactory.createOMElement("AttributeTester", null);
        omElement.addAttribute(omFactory.createOMAttribute("attrNumber", attrNS1, "1"));
        omElement.addAttribute(omFactory.createOMAttribute("attrNumber", attrNS2, "2"));
    
        int nsCount = 0;
        for (Iterator iterator = omElement.getAllDeclaredNamespaces(); iterator.hasNext();) {
            iterator.next();
            nsCount++;
        }
        assertTrue(nsCount == 2);
    
        Diff diff = XMLUnit.compareXML(expectedXML, omElement.toString());
        XMLAssert.assertXMLEqual(diff, true);
    }
}
