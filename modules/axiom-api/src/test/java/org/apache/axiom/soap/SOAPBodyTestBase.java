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

public class SOAPBodyTestBase extends UnifiedSOAPTestCase {
    public SOAPBodyTestBase(OMMetaFactory omMetaFactory, String envelopeNamespaceURI) {
        super(omMetaFactory, envelopeNamespaceURI);
    }

    // Body Test (Programaticaly created)----------------------------------------------------------------------------------
    public void testAddFault1() {
        SOAPEnvelope envelope = soapFactory.createSOAPEnvelope();
        SOAPBody body = soapFactory.createSOAPBody(envelope);
        body.addFault(new Exception("This an exception for testing"));
        assertTrue(
                "Body Test:- After calling addFault method, SOAP body has no fault",
                body.hasFault());

    }

    public void testAddFault2() {
        SOAPEnvelope envelope = soapFactory.createSOAPEnvelope();
        SOAPBody body = soapFactory.createSOAPBody(envelope);
        body.addFault(soapFactory.createSOAPFault(body));
        assertTrue(
                "Body Test:- After calling addFault method, SOAP body has no fault",
                body.hasFault());
    }

    public void testHasFault() {
        SOAPEnvelope envelope = soapFactory.createSOAPEnvelope();
        SOAPBody body = soapFactory.createSOAPBody(envelope);
        assertFalse(
                "Body Test:- After creating a soap body it has a fault",
                body.hasFault());
        body.addFault(new Exception("This an exception for testing"));
        assertTrue(
                "Body Test:- After calling addFault method, hasFault method returns false",
                body.hasFault());
    }

    public void testGetFault() {
        SOAPEnvelope envelope = soapFactory.createSOAPEnvelope();
        SOAPBody body = soapFactory.createSOAPBody(envelope);
        assertTrue(
                "Body Test:- After creating a soap body it has a fault",
                body.getFault() == null);
        body.addFault(new Exception("This an exception for testing"));
        assertFalse(
                "Body Test:- After calling addFault method, getFault method returns null",
                body.getFault() == null);
    }

    // Body Test (With Parser)-------------------------------------------------------------------------------------------
    public void testHasFaultWithParser() {
        SOAPBody body = getTestMessage(MESSAGE).getBody();
        assertTrue(
                "Body Test With parser :- hasFault method returns false",
                body.hasFault());
    }

    public void testGetFaultWithParser() {
        SOAPBody body = getTestMessage(MESSAGE).getBody();
        assertFalse(
                "Body Test With parser :- getFault method returns null",
                body.getFault() == null);
        assertTrue(
                "Body Test With parser : - SOAP fault name mismatch",
                body.getFault().getLocalName().equals(
                        SOAPConstants.SOAPFAULT_LOCAL_NAME));
    }
}
