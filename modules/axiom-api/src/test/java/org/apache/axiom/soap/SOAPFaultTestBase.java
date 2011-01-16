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

public class SOAPFaultTestBase extends UnifiedSOAPTestCase {
    protected final SOAPSpec spec;
    protected SOAPFactory altSoapFactory;
    
    public SOAPFaultTestBase(OMMetaFactory omMetaFactory, SOAPSpec spec) {
        super(omMetaFactory, spec.getEnvelopeNamespaceURI());
        this.spec = spec;
    }

    protected void setUp() throws Exception {
        super.setUp();
        altSoapFactory = isSOAP11() ? omMetaFactory.getSOAP12Factory() : omMetaFactory.getSOAP11Factory();
    }

    // Fault Test (Programaticaly created)-----------------------------------------------------------------------------------
    public void testSetCode() {
        SOAPFault soapFault = soapFactory.createSOAPFault();
        soapFault.setCode(soapFactory.createSOAPFaultCode(soapFault));
        assertNotNull(
                "Fault Test:- After calling setCode method, Fault has no code",
                soapFault.getCode());
        assertTrue("Fault Test:- Code local name mismatch",
                   soapFault.getCode().getLocalName().equals(
                           spec.getFaultCodeLocalName()));
        try {
            soapFault.setCode(altSoapFactory.createSOAPFaultCode());
            fail("SOAPFaultCode should not be set in to a SOAPFault for a different SOAP version");
        } catch (Exception e) {
            // Expected
        }
    }

    public void testGetCode() {
        SOAPFault soapFault = soapFactory.createSOAPFault();
        assertTrue(
                "Fault Test:- After creating a SOAPFault, it has a code",
                soapFault.getCode() == null);
        soapFault.setCode(soapFactory.createSOAPFaultCode(soapFault));
        assertFalse(
                "Fault Test:- After calling setCode method, Fault has no code",
                soapFault.getCode() == null);
        assertTrue("Fault Test:- Fault code local name mismatch",
                   soapFault.getCode().getLocalName().equals(
                           spec.getFaultCodeLocalName()));
    }

    public void testSetReason() {
        SOAPFault soapFault = soapFactory.createSOAPFault();
        soapFault.setReason(soapFactory.createSOAPFaultReason(soapFault));
        assertFalse(
                "Fault Test:- After calling setReason method, Fault has no reason",
                soapFault.getReason() == null);
        assertTrue("Fault Test:- Fault reason local name mismatch",
                   soapFault.getReason().getLocalName().equals(
                           spec.getFaultReasonLocalName()));
        try {
            soapFault.setReason(altSoapFactory.createSOAPFaultReason());
            fail("SOAPFaultReason should not be set in to a SOAPFault for a different SOAP version");

        } catch (Exception e) {
            assertTrue(true);
        }
    }

    public void testGetReason() {
        SOAPFault soapFault = soapFactory.createSOAPFault();
        assertTrue(
                "Fault Test:- After creating a SOAPFault, it has a reason",
                soapFault.getReason() == null);
        soapFault.setReason(soapFactory.createSOAPFaultReason(soapFault));
        assertFalse(
                "Fault Test:- After calling setReason method, Fault has no reason",
                soapFault.getReason() == null);
        assertTrue("Fault Test:- Fault reason local name mismatch",
                   soapFault.getReason().getLocalName().equals(
                           spec.getFaultReasonLocalName()));
    }

    public void testSetRole() {
        SOAPFault soapFault = soapFactory.createSOAPFault();
        soapFault.setRole(soapFactory.createSOAPFaultRole(soapFault));
        assertFalse(
                "Fault Test:- After calling setRole method, Fault has no role",
                soapFault.getRole() == null);
        assertTrue("Fault Test:- Fault role local name mismatch",
                   soapFault.getRole().getLocalName().equals(
                           spec.getFaultRoleLocalName()));
        try {
            soapFault.setRole(altSoapFactory.createSOAPFaultRole());
            fail("SOAPFaultRole should not be set in to a SOAPFault for a different SOAP version");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    public void testGetRole() {
        SOAPFault soapFault = soapFactory.createSOAPFault();
        assertTrue(
                "Fault Test:- After creating a SOAPFault, it has a role",
                soapFault.getRole() == null);
        soapFault.setRole(soapFactory.createSOAPFaultRole(soapFault));
        assertFalse(
                "Fault Test:- After calling setRole method, Fault has no role",
                soapFault.getRole() == null);
        assertTrue("Fault Test:- Fault role local name mismatch",
                   soapFault.getRole().getLocalName().equals(
                           spec.getFaultRoleLocalName()));
    }

    public void testSetDetail() {
        SOAPFault soapFault = soapFactory.createSOAPFault();
        soapFault.setDetail(soapFactory.createSOAPFaultDetail(soapFault));
        assertFalse(
                "Fault Test:- After calling setDetail method, Fault has no detail",
                soapFault.getDetail() == null);
        assertTrue("Fault Test:- Fault detail local name mismatch",
                   soapFault.getDetail().getLocalName().equals(
                           spec.getFaultDetailLocalName()));
        try {
            soapFault.setDetail(altSoapFactory.createSOAPFaultDetail());
            fail("SOAPFaultDetail should not be set in to a SOAPFault for a different SOAP version");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    public void testGetDetail() {
        SOAPFault soapFault = soapFactory.createSOAPFault();
        assertTrue(
                "Fault Test:- After creating a SOAPFault, it has a detail",
                soapFault.getDetail() == null);
        soapFault.setDetail(soapFactory.createSOAPFaultDetail(soapFault));
        assertFalse(
                "Fault Test:- After calling setDetail method, Fault has no detail",
                soapFault.getDetail() == null);
        assertTrue("Fault Test:- Fault detail local name mismatch",
                   soapFault.getDetail().getLocalName().equals(
                           spec.getFaultDetailLocalName()));
    }

    // Fault Test (With parser)
    public void testGetCodeWithParser() {
        SOAPFault soapFaultWithParser = getTestMessage(MESSAGE).getBody().getFault();
        assertNotNull(
                "Fault Test with parser: - getCode method returns null",
                soapFaultWithParser.getCode());
        assertTrue(
                "Fault Test with parser: - Fault code local name mismatch",
                soapFaultWithParser.getCode().getLocalName().equals(
                        spec.getFaultCodeLocalName()));
    }

    public void testGetReasonWithParser() {
        SOAPFault soapFaultWithParser = getTestMessage(MESSAGE).getBody().getFault();
        assertFalse(
                "Fault Test with parser: - getReason method returns null",
                soapFaultWithParser.getReason() == null);
        assertTrue(
                "Fault Test with parser: - Fault reason local name mismatch",
                soapFaultWithParser.getReason().getLocalName().equals(
                        spec.getFaultReasonLocalName()));
    }

    public void testGetRoleWithParser() {
        SOAPFault soapFaultWithParser = getTestMessage(MESSAGE).getBody().getFault();
        assertFalse(
                "Fault Test with parser: - getRole method returns null",
                soapFaultWithParser.getRole() == null);
        assertTrue(
                "Fault Test with parser: - Fault role local name mismatch",
                soapFaultWithParser.getRole().getLocalName().equals(
                        spec.getFaultRoleLocalName()));
    }

    public void testGetDetailWithParser() {
        SOAPFault soapFaultWithParser = getTestMessage(MESSAGE).getBody().getFault();
        assertNotNull(
                "Fault Test with parser: - getDetail method returns null",
                soapFaultWithParser.getDetail());
        assertTrue(
                "Fault Test with parser: - Fault detail local name mismatch",
                soapFaultWithParser.getDetail().getLocalName().equals(
                        spec.getFaultDetailLocalName()));
    }
}