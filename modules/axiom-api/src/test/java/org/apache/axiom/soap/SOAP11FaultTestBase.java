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

public class SOAP11FaultTestBase extends SOAPFaultTestBase {
    public SOAP11FaultTestBase(OMMetaFactory omMetaFactory) {
        super(omMetaFactory, SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                SOAP11Constants.SOAP_FAULT_CODE_LOCAL_NAME,
                SOAP11Constants.SOAP_FAULT_STRING_LOCAL_NAME,
                SOAP11Constants.SOAP_FAULT_ACTOR_LOCAL_NAME,
                SOAP11Constants.SOAP_FAULT_DETAIL_LOCAL_NAME);
    }
    
    public void testSetNode() {
        SOAPFault soapFault = soapFactory.createSOAPFault();
        try {
            soapFault.setNode(soapFactory.createSOAPFaultNode(soapFault));
        } catch (UnsupportedOperationException e) {
            // Exactly!
            return;
        }
        fail("Didn't get UnsupportedOperationException");
    }

    public void testGetNode() {
        SOAPFault soapFault = soapFactory.createSOAPFault();
        // TODO: LLOM returns null while DOM throws UnsupportedOperationException
        try {
            assertTrue(
                    "SOAP 1.1 Fault Test:- After creating a SOAP11Fault, it has a node",
                    soapFault.getNode() == null);
        } catch (UnsupportedOperationException ex) {
            // This is also fine.
        }
    }

}