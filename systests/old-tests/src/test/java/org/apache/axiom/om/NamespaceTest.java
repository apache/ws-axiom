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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import org.apache.axiom.om.util.AXIOMUtil;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import junit.framework.TestCase;

public class NamespaceTest extends TestCase {

    public void testNoPrefixNamespaces()
            throws IOException, ParserConfigurationException, SAXException {

        String expectedXML =
                "<axis2:DocumentElement xmlns:axis2=\"http://ws.apache.org/axis2\" "
                        + "xmlns:axis2ns1=\"http://undefined-ns-1.org\" xmlns:axis2ns2=\"http://undefined-ns-2.org\">"
                        + "<axis2:FirstChild /><axis2ns2:SecondChild xmlns:axis2ns2=\"http://undefined-ns-2.org\" "
                        + "axis2ns1:testAttr=\"testValue\" /></axis2:DocumentElement>";

        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMNamespace axis2NS = omFactory.createOMNamespace("http://ws.apache.org/axis2", "axis2");
        OMElement docElement = omFactory.createOMElement("DocumentElement", axis2NS);

        OMNamespace firstOrphanNS = docElement.declareNamespace("http://undefined-ns-1.org", null);
        docElement.declareNamespace("http://undefined-ns-2.org", null);

        omFactory.createOMElement("FirstChild", axis2NS, docElement);

        OMElement secondChild =
                omFactory.createOMElement(
                        new QName("http://undefined-ns-2.org", "SecondChild"), docElement);
        secondChild.addAttribute("testAttr", "testValue", firstOrphanNS);

        Iterator allDeclaredNamespaces = docElement.getAllDeclaredNamespaces();
        int namespaceCount = 0;
        while (allDeclaredNamespaces.hasNext()) {
            OMNamespace omNamespace = (OMNamespace) allDeclaredNamespaces.next();
            namespaceCount++;
        }
        assertTrue(namespaceCount == 3);

        assertTrue(
                secondChild
                        .getNamespace()
                        .getPrefix()
                        .equals(
                                docElement
                                        .findNamespace("http://undefined-ns-2.org", null)
                                        .getPrefix()));
    }

    public void attributeNSTest() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace ns1 = fac.createOMNamespace("http://test.org", "");
        OMNamespace ns2 = fac.createOMNamespace("http://test2.org", null);

        OMElement elem = fac.createOMElement("test", ns1);
        elem.addAttribute(fac.createOMAttribute("testAttr", ns2, "attrValue"));

        OMNamespace namespace = elem.findNamespace("http://test.org", null);
        assertTrue(
                namespace != null
                        && namespace.getPrefix() != null
                        && "".equals(namespace.getPrefix()));

        OMNamespace namespace2 = elem.findNamespace("http://test2.org", null);
        assertTrue(
                namespace2 != null
                        && namespace2.getPrefix() != null
                        && "".equals(namespace2.getPrefix()));
    }

    /**
     * Here a namespace will be defined with a certain prefix in the root element. But later the
     * same ns is defined with the same uri in a child element, but this time with a different
     * prefix.
     */
    public void testNamespaceProblem() throws XMLStreamException {

        /**
         * <RootElement xmlns:ns1="http://ws.apache.org/axis2"> <ns2:ChildElement
         * xmlns:ns2="http://ws.apache.org/axis2"/> </RootElement>
         */
        OMFactory omFac = OMAbstractFactory.getOMFactory();

        OMElement documentElement = omFac.createOMElement("RootElement", null);
        documentElement.declareNamespace("http://ws.apache.org/axis2", "ns1");

        OMNamespace ns = omFac.createOMNamespace("http://ws.apache.org/axis2", "ns2");
        omFac.createOMElement("ChildElement", ns, documentElement);

        assertTrue(documentElement.toStringWithConsume().indexOf("ns2:ChildElement") > -1);
    }

    public void testNamespaceProblem5() {
        String xml =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header /><soapenv:Body><ns1:createAccountRequest xmlns:ns1=\"http://www.wso2.com/types\"><clientinfo xmlns=\"http://www.wso2.com/types\"><name>bob</name><ssn>123456789</ssn></clientinfo><password xmlns=\"\">passwd</password></ns1:createAccountRequest></soapenv:Body></soapenv:Envelope>";

        try {
            OMElement documentElement =
                    OMXMLBuilderFactory.createOMBuilder(new StringReader(xml)).getDocumentElement();
            String actualXML = documentElement.toString();
            assertAbout(xml())
                    .that(actualXML)
                    .ignoringRedundantNamespaceDeclarations()
                    .hasSameContentAs(xml);
            documentElement.close(false);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * This is re-producing and testing the bug mentioned in
     * http://issues.apache.org/jira/browse/AXIOM-35
     */
    public void testNamespaceProblem7() throws Exception {

        String expectedString =
                "<person xmlns=\"http://ws.apache.org/axis2/apacheconasia/06\">"
                        + "<name>John</name>"
                        + "<age>34</age>"
                        + "<weight>50</weight>"
                        + "</person>";

        OMFactory fac = OMAbstractFactory.getOMFactory();

        OMNamespace ns = fac.createOMNamespace("http://ws.apache.org/axis2/apacheconasia/06", "");
        OMElement personElem = fac.createOMElement("person", ns);
        OMElement nameElem = fac.createOMElement("name", ns);
        nameElem.setText("John");

        OMElement ageElem = fac.createOMElement("age", ns);
        ageElem.setText("34");

        OMElement weightElem = fac.createOMElement("weight", ns);
        weightElem.setText("50");

        // Add children to the person element
        personElem.addChild(nameElem);
        personElem.addChild(ageElem);
        personElem.addChild(weightElem);

        String result = personElem.toString();

        assertAbout(xml()).that(result).hasSameContentAs(expectedString);
    }

    /**
     * This is re-producing and testing the bug mentioned in
     * http://issues.apache.org/jira/browse/AXIOM-35
     */
    public void testNamespaceProblem8() throws Exception {

        String expectedXML =
                "<person xmlns=\"http://ws.apache.org/axis2/apacheconasia/06\"><name xmlns=\"\">John</name><age>34</age><weight>50</weight></person>";
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace ns = fac.createOMNamespace("http://ws.apache.org/axis2/apacheconasia/06", "");
        OMElement personElem = fac.createOMElement("person", ns);

        // Create and add an unqualified element
        OMElement nameElem = fac.createOMElement("name", null);
        nameElem.setText("John");
        personElem.addChild(nameElem);

        OMElement ageElem = fac.createOMElement("age", ns);
        ageElem.setText("34");

        OMElement weightElem = fac.createOMElement("weight", ns);
        weightElem.setText("50");

        personElem.addChild(ageElem);
        personElem.addChild(weightElem);

        assertAbout(xml()).that(personElem.toString()).hasSameContentAs(expectedXML);
    }

    public void testAxis2_3155() {
        try {
            String xmlString =
                    "<outerTag xmlns=\"http://someNamespace\">"
                            + "<innerTag>"
                            + "<node1>Hello</node1>"
                            + "<node2>Hello</node2>"
                            + "</innerTag>"
                            + "</outerTag>";

            OMElement elem = AXIOMUtil.stringToOM(xmlString);
            //            System.out.println("--- Calling toStringWithConsume() ---\n");
            //            System.out.println(elem.toStringWithConsume());

            xmlString =
                    "<outerTag xmlns=\"http://someNamespace\">"
                            + "<innerTag>"
                            + "<node1>Hello</node1>"
                            + "<node2>Hello</node2>"
                            + "</innerTag>"
                            + "</outerTag>";

            elem.close(false);

            elem = AXIOMUtil.stringToOM(xmlString);
            //            System.out.println("\n--- Calling toString() ---\n");
            //            System.out.println(elem.toString());

            elem.close(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
