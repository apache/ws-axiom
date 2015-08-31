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
package org.apache.axiom.soap.impl.common;

import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultClassifier;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;

public aspect AxiomSOAP12FactorySupport {
    public final SOAPHelper AxiomSOAP12Factory.getSOAPHelper() {
        return SOAPHelper.SOAP12;
    }

    private SOAPFaultValue AxiomSOAP12Factory.internalCreateSOAPFaultValue(SOAPFaultClassifier parent, OMXMLParserWrapper builder) {
        return createSOAPElement(AxiomSOAP12FaultValue.class, parent, SOAP12Constants.QNAME_FAULT_VALUE, builder);
    }

    public final SOAPFaultValue AxiomSOAP12Factory.createSOAPFaultValue(SOAPFaultCode parent, OMXMLParserWrapper builder) {
        return internalCreateSOAPFaultValue(parent, builder);
    }

    public final SOAPFaultValue AxiomSOAP12Factory.createSOAPFaultValue(SOAPFaultSubCode parent, OMXMLParserWrapper builder) {
        return internalCreateSOAPFaultValue(parent, builder);
    }

    public final SOAPFaultValue AxiomSOAP12Factory.createSOAPFaultValue(SOAPFaultCode parent) {
        return internalCreateSOAPFaultValue(parent, null);
    }

    public final SOAPFaultValue AxiomSOAP12Factory.createSOAPFaultValue(SOAPFaultSubCode parent) {
        return internalCreateSOAPFaultValue(parent, null);
    }

    public final SOAPFaultValue AxiomSOAP12Factory.createSOAPFaultValue() {
        return internalCreateSOAPFaultValue(null, null);
    }

    private SOAPFaultSubCode AxiomSOAP12Factory.internalCreateSOAPFaultSubCode(SOAPFaultClassifier parent, OMXMLParserWrapper builder) {
        return createSOAPElement(AxiomSOAP12FaultSubCode.class, parent, SOAP12Constants.QNAME_FAULT_SUBCODE, builder);
    }

    public final SOAPFaultSubCode AxiomSOAP12Factory.createSOAPFaultSubCode(SOAPFaultCode parent, OMXMLParserWrapper builder) {
        return internalCreateSOAPFaultSubCode(parent, builder);
    }

    public final SOAPFaultSubCode AxiomSOAP12Factory.createSOAPFaultSubCode(SOAPFaultSubCode parent, OMXMLParserWrapper builder) {
        return internalCreateSOAPFaultSubCode(parent, builder);
    }

    public final SOAPFaultSubCode AxiomSOAP12Factory.createSOAPFaultSubCode(SOAPFaultCode parent) {
        return internalCreateSOAPFaultSubCode(parent, null);
    }

    public final SOAPFaultSubCode AxiomSOAP12Factory.createSOAPFaultSubCode(SOAPFaultSubCode parent) {
        return internalCreateSOAPFaultSubCode(parent, null);
    }

    public final SOAPFaultSubCode AxiomSOAP12Factory.createSOAPFaultSubCode() {
        return internalCreateSOAPFaultSubCode(null, null);
    }

    public final SOAPFaultText AxiomSOAP12Factory.createSOAPFaultText(SOAPFaultReason parent, OMXMLParserWrapper builder) {
        return createSOAPElement(AxiomSOAP12FaultText.class, parent, SOAP12Constants.QNAME_FAULT_TEXT, builder);
    }

    public final SOAPFaultText AxiomSOAP12Factory.createSOAPFaultText(SOAPFaultReason parent) {
        return createSOAPFaultText(parent, null);
    }

    public final SOAPFaultText AxiomSOAP12Factory.createSOAPFaultText() {
        return createSOAPFaultText(null, null);
    }

    public final SOAPFaultNode AxiomSOAP12Factory.createSOAPFaultNode(SOAPFault parent, OMXMLParserWrapper builder) {
        return createSOAPElement(AxiomSOAP12FaultNode.class, parent, SOAP12Constants.QNAME_FAULT_NODE, builder);
    }

    public final SOAPFaultNode AxiomSOAP12Factory.createSOAPFaultNode(SOAPFault parent) {
        return createSOAPFaultNode(parent, null);
    }

    public final SOAPFaultNode AxiomSOAP12Factory.createSOAPFaultNode() {
        return createSOAPFaultNode(null, null);
    }

    public final SOAPEnvelope AxiomSOAP12Factory.getDefaultFaultEnvelope() {
        SOAPEnvelope defaultEnvelope = getDefaultEnvelope();
        SOAPFault fault = createSOAPFault(defaultEnvelope.getBody());
        SOAPFaultCode faultCode = createSOAPFaultCode(fault);
        createSOAPFaultValue(faultCode);
        SOAPFaultReason reason = createSOAPFaultReason(fault);
        createSOAPFaultText(reason);
        createSOAPFaultDetail(fault);
        return defaultEnvelope;
    }
}
