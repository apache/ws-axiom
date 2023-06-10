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
package org.apache.axiom.ts.springws.scenario.wsadom;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.springws.scenario.ScenarioConfig;
import org.apache.axiom.ts.springws.scenario.ScenarioTestCase;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.addressing.client.ActionCallback;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WSAddressingDOMTest extends ScenarioTestCase {
    public WSAddressingDOMTest(ScenarioConfig config, SOAPSpec spec) {
        super(config, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document requestDocument = documentBuilder.newDocument();
        Element request = requestDocument.createElementNS("urn:test", "p:testRequest");
        request.setTextContent("test");
        Document responseDocument = documentBuilder.newDocument();
        context.getBean(WebServiceTemplate.class)
                .sendSourceAndReceiveToResult(
                        new DOMSource(request),
                        new ActionCallback(EchoEndpoint.ACTION),
                        new DOMResult(responseDocument));
        Element response = responseDocument.getDocumentElement();
        assertEquals("urn:test", response.getNamespaceURI());
        assertEquals("testRequest", response.getLocalName());
        assertEquals("test", response.getTextContent());
    }
}
