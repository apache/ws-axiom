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

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.ts.soap.SOAPSpec;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import junit.framework.TestCase;

/**
 * Tests that the clone of a {@link SOAPMessage} created by {@link
 * OMInformationItem#clone(OMCloneOptions)} is a {@link SOAPMessage} if and only if {@link
 * OMCloneOptions#isPreserveModel()} is <code>true</code>.
 */
public class TestClone extends TestCase {
    @Inject private SOAPSpec spec;
    @Inject private SOAPFactory soapFactory;

    @Inject
    @Named("preserveModel")
    private boolean preserveModel;

    @Override
    protected void runTest() throws Throwable {
        SOAPMessage message = soapFactory.createSOAPMessage();
        message.addChild(soapFactory.getDefaultEnvelope());
        OMCloneOptions options = new OMCloneOptions();
        options.setPreserveModel(preserveModel);
        OMInformationItem clone = message.clone(options);
        if (preserveModel) {
            assertThat(clone).isInstanceOf(SOAPMessage.class);
        } else {
            assertThat(clone).isInstanceOf(OMDocument.class);
            assertThat(clone).isNotInstanceOf(SOAPMessage.class);
        }
        OMElement envelope = ((OMDocument) clone).getOMDocumentElement();
        if (preserveModel) {
            assertThat(envelope).isInstanceOf(SOAPEnvelope.class);
        } else {
            assertThat(envelope).isNotInstanceOf(SOAPEnvelope.class);
        }
        assertThat(envelope.getLocalName()).isEqualTo("Envelope");
        assertThat(envelope.getNamespaceURI()).isEqualTo(spec.getEnvelopeNamespaceURI());
    }
}
