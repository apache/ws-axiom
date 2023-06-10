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

import java.io.InputStream;

import org.apache.axiom.ts.soap.MTOMSample;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.springws.MessageFactoryConfigurator;
import org.apache.axiom.ts.springws.SimpleTestCase;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageFactory;

/**
 * Tests the characteristics of the {@link SoapMessage} returned by {@link
 * SoapMessageFactory#createWebServiceMessage(InputStream)}.
 */
public class TestCreateWebServiceMessageFromInputStreamMTOM extends SimpleTestCase {
    public TestCreateWebServiceMessageFromInputStreamMTOM(MessageFactoryConfigurator mfc) {
        super(mfc, SOAPSpec.SOAP12);
    }

    @Override
    protected void runTest(SoapMessageFactory messageFactory) throws Throwable {
        SoapMessage message =
                messageFactory.createWebServiceMessage(
                        new TransportInputStreamImpl(MTOMSample.SAMPLE1));
        assertNotNull(message.getEnvelope());
    }
}
