package org.apache.axiom.om;

import org.custommonkey.xmlunit.XMLTestCase;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
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


}
