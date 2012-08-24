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

public class SOAPFaultValueTestBase extends SOAPFaultSubCodeTestCase {

    public SOAPFaultValueTestBase(OMMetaFactory omMetaFactory) {
        super(omMetaFactory);
    }

    protected void setUp() throws Exception {
        super.setUp();
        soap12FaultValueInFaultSubCode =
                soap12Factory.createSOAPFaultValue(soap12FaultSubCodeInCode);
    }

    //SOAP 1.1 Fault Value Test (Programaticaly Created)
    public void testSOAP11setText() {
        soap11FaultValue.setText("This is only Test");
        assertEquals("SOAP 1.1 Fault Value Test : - value text mismatch",
                "This is only Test", soap11FaultValue.getText());
    }

    public void testSOAP11GetText() {
        assertEquals(
                "SOAP 1.1 Fault Value Test : - After creating Fault Value, it has a text",
                "", soap11FaultValue.getText());
        soap11FaultValue.setText("This is only Test");
        assertEquals("SOAP 1.1 Fault Value Test : - value text mismatch",
                "This is only Test", soap11FaultValue.getText());
    }

    //SOAP 1.2 Fault Value(In Fault Code) Test (Programaticaly Created)
    public void testSOAP12setTextInFaultCode() {
        soap12FaultValueInFaultCode.setText("This is only Test");
        assertEquals(
                "SOAP 1.2 Fault Value Test in Fault Code : - value text mismatch",
                "This is only Test", soap12FaultValueInFaultCode.getText());
    }

    public void testSOAP12GetTextInFaultCode() {
        assertEquals(
                "SOAP 1.2 Fault Value Test in Fault Code : - After creating Fault Value, it has a text",
                "", soap12FaultValueInFaultCode.getText());
        soap12FaultValueInFaultCode.setText("This is only Test");
        assertEquals(
                "SOAP 1.2 Fault Value Test in Fault Code : - value text mismatch",
                "This is only Test", soap12FaultValueInFaultCode.getText());
    }

    //SOAP 1.2 Fault Value(In Fault SubCode) Test (Programaticaly Created)
    public void testSOAP12setTextInFaultSubCode() {
        soap12FaultValueInFaultSubCode.setText("This is only Test");
        assertEquals(
                "SOAP 1.2 Fault Value Test in Fault SubCode : - value text mismatch",
                "This is only Test", soap12FaultValueInFaultSubCode.getText());
    }

    public void testSOAP12GetTextInFaultSubCode() {
        assertEquals(
                "SOAP 1.2 Fault Value Test in Fault SubCode : - After creating Fault Value, it has a text",
                "", soap12FaultValueInFaultSubCode.getText());
        soap12FaultValueInFaultSubCode.setText("This is only Test");
        assertEquals(
                "SOAP 1.2 Fault Value Test in Fault SubCode : - value text mismatch",
                "This is only Test", soap12FaultValueInFaultSubCode.getText());
    }

    //SOAP 1.1 Fault Value Test (With Parser)
    public void testSOAP11GetTextWithParser() {
        assertEquals(
                "SOAP 1.1 Fault Value Test with parser : - value text mismatch",
                "env:Sender", soap11FaultValueWithParser.trim());
    }

    //SOAP 1.2 Fault Value(In Fault Code) Test (With Parser)
    public void testSOAP12setTextWithParserInFaultCode() {
        assertEquals(
                "SOAP 1.2 Fault Value Test with parser in Fault Code : - value text mismatch",
                "env:Sender", soap12FaultValueInFaultCodeWithParser.getText());
    }

    //SOAP 1.2 Fault Value(In Fault SubCode) Test (With Parser)
    public void testSOAP12setTextWithParserInFaultSubCode() {
        assertEquals(
                "SOAP 1.2 Fault Value Test with parser in Fault SubCode : - value text mismatch",
                "m:MessageTimeout_In_First_Subcode",
                soap12FaultValueInFaultSubCodeWithParser.getText());
    }
}
