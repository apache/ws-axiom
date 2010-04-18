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

public class SOAP12FaultTestBase extends SOAPFaultTestBase {
    public SOAP12FaultTestBase(OMMetaFactory omMetaFactory) {
        super(omMetaFactory, SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                SOAP12Constants.SOAP_FAULT_CODE_LOCAL_NAME,
                SOAP12Constants.SOAP_FAULT_REASON_LOCAL_NAME,
                SOAP12Constants.SOAP_FAULT_ROLE_LOCAL_NAME,
                SOAP12Constants.SOAP_FAULT_DETAIL_LOCAL_NAME);
    }

    public void testSetNode() {
        SOAPFault soapFault = soapFactory.createSOAPFault();
        soapFault.setNode(soapFactory.createSOAPFaultNode(soapFault));
        assertFalse(
                "SOAP 1.2 Fault Test:- After calling setNode method, Fault has no node",
                soapFault.getNode() == null);
        assertTrue("SOAP 1.2 Fault Test:- Fault node local name mismatch",
                   soapFault.getNode().getLocalName().equals(
                           SOAP12Constants.SOAP_FAULT_NODE_LOCAL_NAME));
        try {
            soapFault.setNode(altSoapFactory.createSOAPFaultNode());
            fail("SOAP11FaultNode should nott be set in to a SOAP12Fault");

        } catch (Exception e) {
            assertTrue(true);
        }
    }

    public void testGetNode() {
        SOAPFault soapFault = soapFactory.createSOAPFault();
        assertTrue(
                "SOAP 1.2 Fault Test:- After creating a SOAP12Fault, it has a node",
                soapFault.getNode() == null);
        soapFault.setNode(soapFactory.createSOAPFaultNode(soapFault));
        assertFalse(
                "SOAP 1.2 Fault Test:- After calling setNode method, Fault has no node",
                soapFault.getNode() == null);
        assertTrue("SOAP 1.2 Fault Test:- Fault node local name mismatch",
                   soapFault.getNode().getLocalName().equals(
                           SOAP12Constants.SOAP_FAULT_NODE_LOCAL_NAME));
    }

    public void testGetNodeWithParser() {
        SOAPFault soapFaultWithParser = getTestMessage(MESSAGE).getBody().getFault();
        assertFalse(
                "SOAP 1.2 Fault Test with parser: - getNode method returns null",
                soapFaultWithParser.getNode() == null);
        assertTrue(
                "SOAP 1.2 Fault Test with parser: - Fault node local name mismatch",
                soapFaultWithParser.getNode().getLocalName().equals(
                        SOAP12Constants.SOAP_FAULT_NODE_LOCAL_NAME));
    }
}