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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.junit.jupiter.api.Test;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.axiom.AxiomSoapMessage;
import org.springframework.ws.soap.axiom.AxiomSoapMessageFactory;
import org.w3c.dom.Document;

import static org.assertj.core.api.Assertions.assertThat;

public class AxiomSoapMessageTest {
    /**
     * Tests that {@link AxiomSoapMessage#setDocument(Document)} works correctly. There have been
     * issues with that method because Spring-WS instantiates {@link SOAPFactory} implementations
     * directly instead of using {@link OMAbstractFactory}.
     */
    @Test
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
        assertThat(it.hasNext()).isTrue();
        SoapHeaderElement headerElement = it.next();
        assertThat(headerElement.getName()).isEqualTo(new QName("urn:test", "myHeader"));
    }
}
