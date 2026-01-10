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
package org.apache.axiom.systest.springws;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.axiom.AxiomSoapMessage;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.w3c.dom.Document;

public class AxiomSoapMessageTest extends TestCase {
    /**
     * Tests that {@link AxiomSoapMessage#setDocument(Document)} works correctly. There have been
     * issues with that method because Spring-WS instantiates {@link SOAPFactory} implementations
     * directly instead of using {@link OMAbstractFactory}.
     *
     * @throws Exception
     */
    public void testSetDocument() throws Exception {
        AxiomSoapMessageFactory mf = new AxiomSoapMessageFactory();
        mf.afterPropertiesSet();
        AxiomSoapMessage message = mf.createWebServiceMessage();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document document =
                dbf.newDocumentBuilder()
                        .parse(
                                AxiomSoapMessageTest.class
                                        .getResource("soap-message.xml")
                                        .toString());
        message.setDocument(document);
        Iterator<SoapHeaderElement> it =
                message.getEnvelope().getHeader().examineAllHeaderElements();
        assertTrue(it.hasNext());
        SoapHeaderElement headerElement = it.next();
        assertEquals(new QName("urn:test", "myHeader"), headerElement.getName());
    }
}
