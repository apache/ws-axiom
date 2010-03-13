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

package org.apache.axiom.soap;

import org.apache.axiom.om.OMMetaFactory;

public class SOAPEnvelopeTestBase extends UnifiedSOAPTestCase {
    public SOAPEnvelopeTestBase(OMMetaFactory omMetaFactory, String envelopeNamespaceURI) {
        super(omMetaFactory, envelopeNamespaceURI);
    }

    // Envelope Test (Programaticaly Created)-----------------------------------------------
    public void testGetHeader() {
        SOAPEnvelope envelope = soapFactory.getDefaultEnvelope();
        SOAPHeader header = envelope.getHeader();
        assertTrue("Header Test : - Header local name mismatch",
                   header.getLocalName().equals(SOAPConstants.HEADER_LOCAL_NAME));
        assertTrue("Header Test : - Header namespace mismatch",
                   header.getNamespace().getNamespaceURI().equals(
                           envelopeNamespaceURI));
    }

    public void testGetBody() {
        SOAPEnvelope envelope = soapFactory.getDefaultEnvelope();
        SOAPBody body = envelope.getBody();
        assertTrue("Body Test : - Body local name mismatch",
                   body.getLocalName().equals(SOAPConstants.BODY_LOCAL_NAME));
        assertTrue("Body Test : - Body namespace mismatch",
                   body.getNamespace().getNamespaceURI().equals(
                           envelopeNamespaceURI));
    }

    // Envelope Test (With Parser)-----------------------------------------------------------------
    public void testGetHeaderWithParser() {
        SOAPEnvelope envelope = getTestMessage(MESSAGE);
        SOAPHeader header = envelope.getHeader();
        assertTrue("Header Test : - Header local name mismatch",
                   header.getLocalName().equals(SOAPConstants.HEADER_LOCAL_NAME));
        assertTrue("Header Test : - Header namespace mismatch",
                   header.getNamespace().getNamespaceURI().equals(
                           envelopeNamespaceURI));
    }

    public void testGetBodyWithParser() {
        SOAPEnvelope envelope = getTestMessage(MESSAGE);
        SOAPBody body = envelope.getBody();
        assertTrue("Body Test : - Body local name mismatch",
                   body.getLocalName().equals(SOAPConstants.BODY_LOCAL_NAME));
        assertTrue("Body Test : - Body namespace mismatch",
                   body.getNamespace().getNamespaceURI().equals(
                           envelopeNamespaceURI));
    }

    // Make sure order of header/body creation doesn't matter
    public void testBodyHeaderOrder() throws Exception {
        SOAPEnvelope env = soapFactory.createSOAPEnvelope();
        soapFactory.createSOAPBody(env);
        soapFactory.createSOAPHeader(env);
        assertTrue("Header isn't the first child!", env.getFirstElement() instanceof SOAPHeader);
    }
}
