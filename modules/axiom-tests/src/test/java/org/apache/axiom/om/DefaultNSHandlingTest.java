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

import java.io.File;
import java.io.FileInputStream;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.custommonkey.xmlunit.XMLTestCase;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

public class DefaultNSHandlingTest extends XMLTestCase {

    public void testForIssueWSCOMMONS119() throws Exception {
        String planXML = "src/test/resources/defaultNamespace2.xml";
        XMLStreamReader parser = XMLInputFactory.newInstance().
               createXMLStreamReader(new FileInputStream(new File(planXML)));
        StAXOMBuilder staxOMBuilder = new StAXOMBuilder(parser);
        OMElement docEle = staxOMBuilder.getDocumentElement();
        OMElement omElement = getOMElement("//ns:config-property-setting[@name='ConnectionURL']",
                                           docEle);
        omElement.setText("jdbc:derby:/home/azeez/.tungsten/database/TUNGSTEN_DB");

        String serializedXML = docEle.toString();

//        System.out.println("serializedXML = " + serializedXML);

        assertTrue(serializedXML.indexOf("xmlns=\"\"") == -1);
    }


    private OMElement getOMElement(String xpathString,
                                   OMElement parentElement) throws JaxenException {
        XPath xpath = getXPath(xpathString);
        return (OMElement) xpath.selectSingleNode(parentElement);
    }

    private XPath getXPath(String xpathString) throws JaxenException {
        SimpleNamespaceContext nsCtx = new SimpleNamespaceContext();
        nsCtx.addNamespace("ns", "http://geronimo.apache.org/xml/ns/j2ee/connector-1.1");
        XPath xpath = new AXIOMXPath(xpathString);
        xpath.setNamespaceContext(nsCtx);
        return xpath;
    }
}

