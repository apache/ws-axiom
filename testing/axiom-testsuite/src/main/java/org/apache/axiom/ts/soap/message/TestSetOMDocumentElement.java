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
package org.apache.axiom.ts.soap.message;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests the behavior of {@link OMDocument#setOMDocumentElement(OMElement)} when used to set a
 * {@link SOAPEnvelope} as the root element of a {@link SOAPMessage}.
 */
public class TestSetOMDocumentElement implements Executable {
    @Inject
    private SOAPFactory soapFactory;

    @Override
    public void execute() throws Throwable {
        SOAPMessage message = soapFactory.createSOAPMessage();
        OMElement envelope = soapFactory.getDefaultEnvelope();
        message.setOMDocumentElement(envelope);
        assertThat(message.getFirstOMChild()).isSameAs(envelope);
    }
}
