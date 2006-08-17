package org.apache.axiom.om;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.custommonkey.xmlunit.XMLTestCase;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class NamespaceTest extends XMLTestCase {

    public void testNoPrefixNamespaces() throws IOException, ParserConfigurationException, SAXException {

        String expectedXML = "<axis2:DocumentElement xmlns:axis2=\"http://ws.apache.org/axis2\" " +
                "xmlns:axis2ns1=\"http://undefined-ns-1.org\" xmlns:axis2ns2=\"http://undefined-ns-2.org\">" +
                "<axis2:FirstChild /><axis2ns2:SecondChild xmlns:axis2ns2=\"http://undefined-ns-2.org\" " +
                "axis2ns1:testAttr=\"testValue\" /></axis2:DocumentElement>";

        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMNamespace axis2NS = omFactory.createOMNamespace("http://ws.apache.org/axis2", "axis2");
        OMElement docElement = omFactory.createOMElement("DocumentElement", axis2NS);

        OMNamespace firstOrphanNS = docElement.declareNamespace("http://undefined-ns-1.org", null);
        docElement.declareNamespace("http://undefined-ns-2.org", null);

        omFactory.createOMElement("FirstChild", axis2NS, docElement);

        OMElement secondChild = omFactory.createOMElement(new QName("http://undefined-ns-2.org", "SecondChild"), docElement);
        secondChild.addAttribute("testAttr", "testValue", firstOrphanNS);


        Iterator allDeclaredNamespaces = docElement.getAllDeclaredNamespaces();
        int namespaceCount = 0;
        while (allDeclaredNamespaces.hasNext()) {
            OMNamespace omNamespace = (OMNamespace) allDeclaredNamespaces.next();
            namespaceCount++;
        }
        assertTrue(namespaceCount == 3);

        assertTrue(secondChild.getNamespace().getPrefix().equals(docElement.findNamespace("http://undefined-ns-2.org", null).getPrefix()));


    }

    public void attributeNSTest() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace ns1 = fac.createOMNamespace("http://test.org", "");
        OMNamespace ns2 = fac.createOMNamespace("http://test2.org", null);


        OMElement elem = fac.createOMElement("test", ns1);
        elem.addAttribute(fac.createOMAttribute("testAttr", ns2, "attrValue"));

        OMNamespace namespace = elem.findNamespace("http://test.org", null);
        assertTrue(namespace != null && namespace.getPrefix() != null && "".equals(namespace.getPrefix()));

        OMNamespace namespace2 = elem.findNamespace("http://test2.org", null);
        assertTrue(namespace2 != null && namespace2.getPrefix() != null && "".equals(namespace2.getPrefix()));
    }

    /**
     * Here a namespace will be defined with a certain prefix in the root element. But later the same ns
     * is defined with the same uri in a child element, but this time with a different prefix.
     */
    public void testNamespaceProblem() throws XMLStreamException {

        /**
         * <RootElement xmlns:ns1="http://ws.apache.org/axis2">
         *    <ns2:ChildElement xmlns:ns2="http://ws.apache.org/axis2"/>
         * </RootElement>
         *
         */

        OMFactory omFac = OMAbstractFactory.getOMFactory();

        OMElement documentElement = omFac.createOMElement("RootElement", null);
        documentElement.declareNamespace("http://ws.apache.org/axis2", "ns1");

        OMNamespace ns = omFac.createOMNamespace("http://ws.apache.org/axis2", "ns2");
        omFac.createOMElement("ChildElement", ns, documentElement);

        assertTrue(documentElement.toStringWithConsume().indexOf("ns2:ChildElement") > -1);
    }

    public void testNamespaceProblem2() throws XMLStreamException {

        /**
         * <RootElement xmlns="http://one.org">
         *   <ns2:ChildElementOne xmlns:ns2="http://ws.apache.org/axis2" xmlns="http://two.org">
         *      <ChildElementTwo xmlns="http://one.org" />
         *   </ns2:ChildElementOne>
         * </RootElement>
         */

        OMFactory omFac = OMAbstractFactory.getOMFactory();

        OMElement documentElement = omFac.createOMElement("RootElement", null);
        documentElement.declareDefaultNamespace("http://one.org");

        OMNamespace ns = omFac.createOMNamespace("http://ws.apache.org/axis2", "ns2");
        OMElement childOne = omFac.createOMElement("ChildElementOne", ns, documentElement);
        childOne.declareDefaultNamespace("http://two.org");

        OMElement childTwo = omFac.createOMElement("ChildElementTwo", null, childOne);
        childTwo.declareDefaultNamespace("http://one.org");


        assertEquals(2, getNumberOfOccurrences(documentElement.toStringWithConsume(), "xmlns=\"http://one.org\""));
    }

    public void testNamespaceProblem3() throws XMLStreamException {

        /**
         * <RootElement xmlns:ns1="http://one.org" xmlns:ns2="http://one.org">
         *   <ns2:ChildElementOne xmlns="http://one.org">
         *      <ns2:ChildElementTwo />
         *   </ns2:ChildElementOne>
         * </RootElement>
         */

        OMFactory omFac = OMAbstractFactory.getOMFactory();

        OMElement documentElement = omFac.createOMElement("RootElement", null);
        OMNamespace ns1 = documentElement.declareNamespace("http://one.org", "ns1");
        OMNamespace ns2 = documentElement.declareNamespace("http://one.org", "ns2");

        OMElement childOne = omFac.createOMElement("ChildElementOne", ns2, documentElement);
        childOne.declareDefaultNamespace("http://one.org");

        OMElement childTwo = omFac.createOMElement("ChildElementTwo", ns1, childOne);

        assertEquals(1, getNumberOfOccurrences(documentElement.toStringWithConsume(), "xmlns:ns2=\"http://one.org\""));
    }


    public void testNamespaceProblem4() throws Exception {
        String xml = "<getCreditScoreResponse xmlns=\"http://www.example.org/creditscore/doclitwrapped/\"><score xmlns=\"\">750</score></getCreditScoreResponse>";
        XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(xml.getBytes()));
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(OMAbstractFactory.getOMFactory(), parser);
        OMElement root = builder.getDocumentElement();
        String actualXML = root.toString();
        assertTrue(actualXML.indexOf("xmlns=\"\"") != -1);
    }

    public void testNamespaceProblem5() {
        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header /><soapenv:Body><ns1:createAccountRequest xmlns:ns1=\"http://www.wso2.com/types\"><clientinfo xmlns=\"http://www.wso2.com/types\"><name>bob</name><ssn>123456789</ssn></clientinfo><password xmlns=\"\">passwd</password></ns1:createAccountRequest></soapenv:Body></soapenv:Envelope>";

        try {
            OMElement documentElement = new StAXOMBuilder(new ByteArrayInputStream(xml.getBytes())).getDocumentElement();
            String actualXML = documentElement.toString();
            assertXMLEqual(xml, actualXML);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private int getNumberOfOccurrences(String xml, String pattern) {
        int index = -1;
        int count = 0;
        while ((index = xml.indexOf(pattern, index + 1)) != -1) {
            count ++;
        }

        return count;
    }

    public void testNamespaceProblem6() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        //TODO: Find the constants for "Parameter" and "name"
        OMElement paramElement = fac.createOMElement("Parameter", null);
        OMNamespace ns = paramElement.declareDefaultNamespace("");
        paramElement.addAttribute(fac.createOMAttribute("name", null, "someName"));


        for (int i = 0; i < 5; i++) {
            // Create the action element
            OMElement actionElem = fac.createOMElement(
                    "Action", ns);

            for (int j = 0; j < 5; j++) {
                // Create an element with the name of the key
                OMElement elem = fac.createOMElement("someKey"+j, ns);
                // Set the text value of the element
                elem.setText("someValue"+j);
                // Add the element as a child of this action element
                actionElem.addChild(elem);
            }

            paramElement.addChild(actionElem);
        }

    }


}
