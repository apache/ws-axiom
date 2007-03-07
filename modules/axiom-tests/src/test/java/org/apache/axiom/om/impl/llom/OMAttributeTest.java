package org.apache.axiom.om.impl.llom;
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

import junit.framework.TestCase;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;

public class OMAttributeTest extends TestCase {

    public void testAddAttribute() {
        String xmlString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header name = \"jhon\"/><soapenv:Body><my:uploadFileUsingMTOM xmlns:my=\"http://localhost/my\"><my:folderName>/home/saliya/Desktop</my:folderName></my:uploadFileUsingMTOM></soapenv:Body><Body>TTTT</Body> </soapenv:Envelope>";


        String test1 = "";
        String test2 = "";

        test1 = addAttributeMethod1(xmlString);
        test2 = addAttributeMethod2(xmlString);

        assertEquals(test1, test2);
    }

    private String addAttributeMethod1(String xmlString) {
        XMLStreamReader parser2;

        try {
            parser2 = XMLInputFactory.newInstance()
                    .createXMLStreamReader(new ByteArrayInputStream(xmlString.getBytes()));
            StAXOMBuilder builder2 = new StAXOMBuilder(parser2);
            OMElement doc = builder2.getDocumentElement();

            OMFactory factory = OMAbstractFactory.getOMFactory();
            OMNamespace ns = factory.createOMNamespace("http://www.me.com", "axiom");

            //code line to be tested
            OMAttribute at = factory.createOMAttribute("id", ns, "value");
            doc.addAttribute(at);

            return doc.toString();

        } catch (Exception e) {
            return "ERROR";
            //e.printStackTrace();
        }

    }

    private String addAttributeMethod2(String xmlString) {
        XMLStreamReader parser2;

        try {
            parser2 = XMLInputFactory.newInstance()
                    .createXMLStreamReader(new ByteArrayInputStream(xmlString.getBytes()));
            StAXOMBuilder builder2 = new StAXOMBuilder(parser2);
            OMElement doc = builder2.getDocumentElement();

            OMFactory factory = OMAbstractFactory.getOMFactory();
            OMNamespace ns = factory.createOMNamespace("http://www.me.com", "axiom");

            //code line to be tested
            doc.addAttribute("id", "value", ns);

            return doc.toString();

        } catch (Exception e) {
            return "ERROR";
            //e.printStackTrace();
        }

    }

}
