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

package org.apache.axiom.om.impl.llom;

import junit.framework.TestCase;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;

import java.io.StringReader;

public class OMAttributeTest extends TestCase {

    public void testAddAttribute() throws Exception {
        String xmlString =
                "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header name = \"jhon\"/><soapenv:Body><my:uploadFileUsingMTOM xmlns:my=\"http://localhost/my\"><my:folderName>/home/saliya/Desktop</my:folderName></my:uploadFileUsingMTOM></soapenv:Body><Body>TTTT</Body> </soapenv:Envelope>";

        assertEquals(addAttributeMethod1(xmlString), addAttributeMethod2(xmlString));
    }

    private String addAttributeMethod1(String xmlString) throws Exception {
        OMXMLParserWrapper builder2 =
                OMXMLBuilderFactory.createOMBuilder(new StringReader(xmlString));
        OMElement doc = builder2.getDocumentElement();

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://www.me.com", "axiom");

        // code line to be tested
        OMAttribute at = factory.createOMAttribute("id", ns, "value");
        doc.addAttribute(at);

        return doc.toString();
    }

    private String addAttributeMethod2(String xmlString) throws Exception {
        OMXMLParserWrapper builder2 =
                OMXMLBuilderFactory.createOMBuilder(new StringReader(xmlString));
        OMElement doc = builder2.getDocumentElement();

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://www.me.com", "axiom");

        // code line to be tested
        doc.addAttribute("id", "value", ns);

        return doc.toString();
    }
}
