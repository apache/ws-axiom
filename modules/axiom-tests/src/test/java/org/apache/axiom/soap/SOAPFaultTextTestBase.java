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

public class SOAPFaultTextTestBase extends SOAPFaultReasonTestCase {
    protected SOAPFaultText soap11FaultText;
    protected SOAPFaultText soap12FaultText;
    protected String soap11FaultTextWithParser;
    protected SOAPFaultText soap12FaultTextWithParser;

    public SOAPFaultTextTestBase(OMMetaFactory omMetaFactory) {
        super(omMetaFactory);
    }

    protected void setUp() throws Exception {
        super.setUp();
        soap11FaultText = soap11Factory.createSOAPFaultText(soap11FaultReason);
        soap12FaultText = soap12Factory.createSOAPFaultText(soap12FaultReason);
        soap11FaultTextWithParser = soap11FaultReasonWithParser.getText();
        soap12FaultTextWithParser = soap12FaultReasonWithParser.getFirstSOAPText();
    }

    public void testSOAP11GetLang() {


        assertNull(
                "SOAP 1.1 Fault Text Test : - After creating SOAPFaultText, it has a Lnag attribute",
                soap11FaultText.getLang());

        soap11FaultText.setLang("en");
        assertEquals(
                "SOAP 1.1 Fault Text Test : - After calling setLang method, Lang attribute value mismatch",
                "en", soap11FaultText.getLang());
    }

    public void testSOAP11SetText() {
        soap11FaultText.setText("This is only a test");
        assertEquals(
                "SOAP 1.1 Fault Text Test : - After calling setText method, getText method return incorrect string",
                "This is only a test", soap11FaultText.getText());
    }

    public void testSOAP11GetText() {
        assertEquals(
                "SOAP 1.1 Fault Text Test : - After creating SOAPFaultText, it has a text",
                "", soap11FaultText.getText());
        soap11FaultText.setText("This is only a test");
        assertEquals(
                "SOAP 1.1 Fault Text Test : - After calling setText method, getText method return incorrect string",
                "This is only a test", soap11FaultText.getText());
    }

    public void testSOAP12GetLang() {

        assertNull(
                "SOAP 1.2 Fault Text Test : - After creating SOAPFaultText, it has a Lnag attribute",
                soap12FaultText.getLang());

        soap12FaultText.setLang("en");
        assertEquals(
                "SOAP 1.2 Fault Text Test : - After calling setLang method, Lang attribute value mismatch",
                "en", soap12FaultText.getLang());
    }

    public void testSOAP12SetText() {
        soap12FaultText.setText("This is only a test");
        assertEquals(
                "SOAP 1.2 Fault Text Test : - After calling setText method, getText method return incorrect string",
                "This is only a test", soap12FaultText.getText());
    }

    public void testSOAP12GetText() {
        assertEquals(
                "SOAP 1.2 Fault Text Test : - After creating SOAPFaultText, it has a text",
                "", soap12FaultText.getText());
        soap12FaultText.setText("This is only a test");
        assertEquals(
                "SOAP 1.2 Fault Text Test : - After calling setText method, getText method return incorrect string",
                "This is only a test", soap12FaultText.getText());
    }

    //SOAP 1.1 Fault Text Test (With Parser)
    public void testSOAP11GetTextWithParser() {
        assertEquals(
                "SOAP 1.1 Fault Text Test With Parser : - getText method returns incorrect string",
                "Sender Timeout", soap11FaultTextWithParser.trim());
    }

    //SOAP 1.2 Fault Text Test (With Parser)
    public void testSOAP12GetTextWithParser() {

        assertEquals(
                "SOAP 1.2 Fault Text Test With Parser : - getText method returns incorrect string",
                "Sender Timeout", soap12FaultTextWithParser.getText());

    }
}
