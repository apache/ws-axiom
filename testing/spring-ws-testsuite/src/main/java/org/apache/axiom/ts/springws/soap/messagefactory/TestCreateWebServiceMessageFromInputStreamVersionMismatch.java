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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;

import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.springws.MessageFactoryConfigurator;
import org.apache.axiom.ts.springws.SimpleTestCase;
import org.springframework.ws.soap.SoapMessageCreationException;
import org.springframework.ws.soap.SoapMessageFactory;

/**
 * Tests that {@link SoapMessageFactory#createWebServiceMessage(InputStream)} throws {@link
 * SoapMessageCreationException} if there is a mismatch between the SOAP version implied by the
 * content type and the actual SOAP version used by the message.
 */
public class TestCreateWebServiceMessageFromInputStreamVersionMismatch extends SimpleTestCase {
    public TestCreateWebServiceMessageFromInputStreamVersionMismatch(
            MessageFactoryConfigurator mfc, SOAPSpec spec) {
        super(mfc, spec);
    }

    @Override
    protected void runTest(SoapMessageFactory messageFactory) throws Throwable {
        assertThatThrownBy(
                        () ->
                                messageFactory.createWebServiceMessage(
                                        new TransportInputStreamImpl(
                                                SOAPSampleSet.NO_HEADER.getMessage(
                                                        spec.getAltSpec()))))
                .isInstanceOf(SoapMessageCreationException.class);
    }
}
