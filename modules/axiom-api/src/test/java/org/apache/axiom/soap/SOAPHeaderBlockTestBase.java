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
import org.apache.axiom.om.OMNamespace;

public class SOAPHeaderBlockTestBase extends UnifiedSOAPTestCase {
    public SOAPHeaderBlockTestBase(OMMetaFactory omMetaFactory, String envelopeNamespaceURI) {
        super(omMetaFactory, envelopeNamespaceURI);
    }
    
    protected SOAPHeaderBlock createSOAPHeaderBlock() {
        OMNamespace namespace = soapFactory.createOMNamespace("http://www.example.org", "test");;
        SOAPEnvelope soapEnvelope = soapFactory.createSOAPEnvelope();
        SOAPHeader soapHeader = soapFactory.createSOAPHeader(soapEnvelope);
        return soapFactory.createSOAPHeaderBlock("testHeaderBlock", namespace, soapHeader);
    }
    
    // SOAPHeaderBlock Test (Programaticaly Created)
    public void testSetRole() {
        SOAPHeaderBlock soapHeaderBlock = createSOAPHeaderBlock();
        soapHeaderBlock.setRole(
                "http://example.org/my-role");
        assertTrue(
                "SOAP HeaderBlock Test : - After calling setRole method, getRole method returns incorrect role value",
                soapHeaderBlock.getRole().equals(
                        "http://example.org/my-role"));
        try {
            soapHeaderBlock.setRole("Any Value");
        } catch (Exception e) {
            fail(
                    "SOAP HeaderBlock Test : - role value can not be set to any value");
        }
    }

    public void testGetRole() {
        SOAPHeaderBlock soapHeaderBlock = createSOAPHeaderBlock();
        assertTrue(
                "SOAP HeaderBlock Test : - After creating SOAPHeaderBlock, it has a role",
                soapHeaderBlock.getRole() == null);
        soapHeaderBlock.setRole(
                "http://example.org/my-role");
        assertTrue(
                "SOAP HeaderBlock Test : - After calling setRole method, getRole method returns incorrect role value",
                soapHeaderBlock.getRole().equals(
                        "http://example.org/my-role"));
    }

    public void testSetMustUnderstand() {
        SOAPHeaderBlock soapHeaderBlock = createSOAPHeaderBlock();
        soapHeaderBlock.setMustUnderstand(true);
        assertTrue(
                "SOAP HeaderBlock Test : - After setting MustUnderstand true calling setMustUnderstand method , getMustUnderstand method returns false",
                soapHeaderBlock.getMustUnderstand());
        soapHeaderBlock.setMustUnderstand(false);
        assertFalse(
                "SOAP HeaderBlock Test : - After setting MustUnderstand false calling setMustUnderstand method , getMustUnderstand method returns true",
                soapHeaderBlock.getMustUnderstand());
    }
    
    public void testSetMustUnderstandString01() {
        SOAPHeaderBlock soapHeaderBlock = createSOAPHeaderBlock();
        soapHeaderBlock.setMustUnderstand("1");
        assertTrue(
                "SOAP HeaderBlock Test : - After setting MustUnderstand \"1\" calling setMustUnderstand method , getMustUnderstand method returns false",
                soapHeaderBlock.getMustUnderstand());
        soapHeaderBlock.setMustUnderstand("0");
        assertFalse(
                "SOAP HeaderBlock Test : - After setting MustUnderstand \"0\" calling setMustUnderstand method , getMustUnderstand method returns true",
                soapHeaderBlock.getMustUnderstand());
    }
    
    public void testSetMustUnderstandWithInvalidValue() {
        SOAPHeaderBlock soapHeaderBlock = createSOAPHeaderBlock();
        try {
            soapHeaderBlock.setMustUnderstand("otherValue");
            fail(
                    "SOAP HeaderBlock Test : - MustUnderstand value can not be set to any value rather than 1 , 0 , true , false");

        } catch (Exception e) {
            assertTrue(true);
        }
    }

    public void testGetMustUnderstand() {
        SOAPHeaderBlock soapHeaderBlock = createSOAPHeaderBlock();
        assertFalse(
                "SOAP HeaderBlock Test : - After creating SOAPHeaderBlock, default MustUnderstand value true",
                soapHeaderBlock.getMustUnderstand());
        soapHeaderBlock.setMustUnderstand(true);
        assertTrue(
                "SOAP HeaderBlock Test : - After setting MustUnderstand true calling setMustUnderstand method , getMustUnderstand method returns false",
                soapHeaderBlock.getMustUnderstand());
    }
}
