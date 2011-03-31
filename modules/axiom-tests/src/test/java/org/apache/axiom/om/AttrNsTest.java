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

package org.apache.axiom.om;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLTestCase;

import java.io.StringReader;
import java.util.Iterator;

public class AttrNsTest extends XMLTestCase {
    public void testAttributesWithProgrammaticalCreation() throws Exception {
        String expectedXML =
                "<AttributeTester xmlns=\"\" xmlns:myAttr2NS=\"http://test-attributes-2.org\" " +
                        "xmlns:myAttr1NS=\"http://test-attributes-1.org\" myAttr2NS:attrNumber=\"2\" myAttr1NS:attrNumber=\"1\" />";

        OMFactory omFactory = OMAbstractFactory.getOMFactory();

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

        Diff diff = compareXML(expectedXML, omElement.toString());
        assertXMLEqual(diff, true);
    }


    public void testAttributesWithNamespaceSerialization() throws Exception {
        String xmlString =
                "<root xmlns='http://custom.com'><node cust:id='123' xmlns:cust='http://custom.com' /></root>";

        // copied code from the generated stub class toOM method
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createOMBuilder(new StringReader(xmlString));
        org.apache.axiom.om.OMElement documentElement = builder
                .getDocumentElement();

        ((org.apache.axiom.om.impl.OMNodeEx) documentElement).setParent(null);
        // end copied code

        // now print the object after it has been processed
        System.out.println("after - '" + documentElement.toString() + "'");
        Diff diff = compareXML(xmlString, documentElement.toString());
        assertXMLEqual(diff, true);
    }
}
