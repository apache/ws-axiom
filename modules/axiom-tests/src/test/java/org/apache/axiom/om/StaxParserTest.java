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

import org.apache.axiom.om.util.StAXUtils;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.util.Iterator;

public class StaxParserTest extends AbstractTestCase {
    private String xmlDocument = "<purchase-order xmlns=\"http://openuri.org/easypo\">" +
            "<customer>" +
            "    <name>Gladys Kravitz</name>" +
            "    <address>Anytown, PA</address>" +
            "  </customer>" +
            "  <date>2005-03-06T14:06:12.697+06:00</date>" +
            "</purchase-order>";

    public void testParserBehaviornonCaching() throws Exception {

        OMXMLParserWrapper builder2 = OMXMLBuilderFactory.createOMBuilder(
                OMAbstractFactory.getOMFactory(),
                new ByteArrayInputStream(xmlDocument.getBytes()));

        OMElement documentElement = builder2.getDocumentElement();
        XMLStreamReader originalParser =
                documentElement.getXMLStreamReaderWithoutCaching();

        //consume the parser. this should force the xml stream to be exhausted without
        //building the tree
        while (originalParser.hasNext()) {
            originalParser.next();
        }

        //try to find the children of the document element. This should produce an
        //error since the underlying stream is fully consumed without building the object tree
        Iterator childElements = documentElement.getChildElements();
        try {
            while (childElements.hasNext()) {
                childElements.next();
            }
            fail("The stream should've been consumed by now!");
        } catch (Exception e) {
            //if we are here without failing, then we are successful
        }
        
        documentElement.close(false);
    }


    public void testParserBehaviorCaching() throws Exception {

        OMXMLParserWrapper builder2 = OMXMLBuilderFactory.createOMBuilder(
                OMAbstractFactory.getSOAP11Factory(),
                new ByteArrayInputStream(xmlDocument.getBytes()));

        OMElement documentElement = builder2.getDocumentElement();
        XMLStreamReader originalParser =
                documentElement.getXMLStreamReader();

        //consume the parser. this should force the xml stream to be exhausted but the
        //tree to be fully built
        while (originalParser.hasNext()) {
            originalParser.next();
        }

        //try to find the children of the document element. This should *NOT* produce an
        //error even when the underlying stream is fully consumed , the object tree is already complete
        Iterator childElements = documentElement.getChildElements();
        int count = 0;
        try {
            while (childElements.hasNext()) {
                childElements.next();
                count++;
            }
        } catch (Exception e) {
            fail("The object tree needs to be built and traversing the children is to be a success!");
        }

        assertEquals("Number of elements need to be 2", count, 2);
        
        documentElement.close(false);
    }


    public void testParserBehaviorNonCaching2() throws Exception {

        OMXMLParserWrapper builder2 = OMXMLBuilderFactory.createOMBuilder(
                OMAbstractFactory.getSOAP11Factory(),
                new ByteArrayInputStream(xmlDocument.getBytes()));

        OMElement documentElement = builder2.getDocumentElement();

        XMLStreamReader originalParser =
                documentElement.getXMLStreamReaderWithoutCaching();

        //consume the parser. this should force the xml stream to be exhausted without
        //building the tree
        while (originalParser.hasNext()) {
            originalParser.next();
        }

        //try to find the children of the document element. This should produce an
        //error since the underlying stream is fully consumed without building the object tree
        Iterator childElements = documentElement.getChildElements();
        try {
            XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(System.out);
            documentElement.serializeAndConsume(writer);
            fail("Stream should be consumed by now");
        } catch (XMLStreamException e) {
            //wea re cool
        } catch (Exception e) {
            fail("This should throw an XMLStreamException");
        }
        
        documentElement.close(false);
    }

}

