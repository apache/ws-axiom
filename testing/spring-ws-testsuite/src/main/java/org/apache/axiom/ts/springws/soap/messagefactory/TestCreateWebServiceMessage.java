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
package org.apache.axiom.ts.springws.soap.messagefactory;

import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.springws.MessageFactoryConfigurator;
import org.apache.axiom.ts.springws.SimpleTestCase;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapEnvelope;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;

/**
 * Tests the characteristics of the {@link SoapMessage} returned by {@link
 * SoapMessageFactory#createWebServiceMessage()}.
 */
public class TestCreateWebServiceMessage extends SimpleTestCase {
    public TestCreateWebServiceMessage(MessageFactoryConfigurator mfc, SOAPSpec spec) {
        super(mfc, spec);
    }

    @Override
    protected void runTest(SoapMessageFactory messageFactory) {
        SoapMessage message = messageFactory.createWebServiceMessage();

        SoapEnvelope env = message.getEnvelope();
        assertNotNull(env);
        assertEquals(spec.getEnvelopeQName(), env.getName());

        SoapHeader header = env.getHeader();
        assertNotNull(header);
        assertEquals(spec.getHeaderQName(), header.getName());

        SoapBody body = env.getBody();
        assertNotNull(body);
        assertEquals(spec.getHeaderQName(), header.getName());
    }
}
