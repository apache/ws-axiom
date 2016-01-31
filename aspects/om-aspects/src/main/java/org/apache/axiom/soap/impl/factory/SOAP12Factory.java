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
package org.apache.axiom.soap.impl.factory;

import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.OMMetaFactory;
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
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultSubCode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultText;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultValue;
import org.apache.axiom.soap.impl.intf.SOAPHelper;

public class SOAP12Factory extends SOAPFactoryImpl {
    public SOAP12Factory(OMMetaFactory metaFactory, NodeFactory nodeFactory) {
        super(metaFactory, nodeFactory);
    }

    public final SOAPHelper getSOAPHelper() {
        return SOAPHelper.SOAP12;
    }

    public final SOAPFaultValue internalCreateSOAPFaultValue(SOAPFaultClassifier parent, OMXMLParserWrapper builder) {
        return createSOAPElement(AxiomSOAP12FaultValue.class, parent, SOAP12Constants.QNAME_FAULT_VALUE, builder);
    }

    public final SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent, OMXMLParserWrapper builder) {
        return internalCreateSOAPFaultValue(parent, builder);
    }

    public final SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent, OMXMLParserWrapper builder) {
        return internalCreateSOAPFaultValue(parent, builder);
    }

    public final SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent) {
        return internalCreateSOAPFaultValue(parent, null);
    }

    public final SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent) {
        return internalCreateSOAPFaultValue(parent, null);
    }

    public final SOAPFaultValue createSOAPFaultValue() {
        return internalCreateSOAPFaultValue(null, null);
    }

    private SOAPFaultSubCode internalCreateSOAPFaultSubCode(SOAPFaultClassifier parent, OMXMLParserWrapper builder) {
        return createSOAPElement(AxiomSOAP12FaultSubCode.class, parent, SOAP12Constants.QNAME_FAULT_SUBCODE, builder);
    }

    public final SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent, OMXMLParserWrapper builder) {
        return internalCreateSOAPFaultSubCode(parent, builder);
    }

    public final SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent, OMXMLParserWrapper builder) {
        return internalCreateSOAPFaultSubCode(parent, builder);
    }

    public final SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent) {
        return internalCreateSOAPFaultSubCode(parent, null);
    }

    public final SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent) {
        return internalCreateSOAPFaultSubCode(parent, null);
    }

    public final SOAPFaultSubCode createSOAPFaultSubCode() {
        return internalCreateSOAPFaultSubCode(null, null);
    }

    public final SOAPFaultText createSOAPFaultText(SOAPFaultReason parent, OMXMLParserWrapper builder) {
        return createSOAPElement(AxiomSOAP12FaultText.class, parent, SOAP12Constants.QNAME_FAULT_TEXT, builder);
    }

    public final SOAPFaultText createSOAPFaultText(SOAPFaultReason parent) {
        return createSOAPFaultText(parent, null);
    }

    public final SOAPFaultText createSOAPFaultText() {
        return createSOAPFaultText(null, null);
    }

    public final SOAPFaultNode createSOAPFaultNode(SOAPFault parent, OMXMLParserWrapper builder) {
        return createSOAPElement(AxiomSOAP12FaultNode.class, parent, SOAP12Constants.QNAME_FAULT_NODE, builder);
    }

    public final SOAPFaultNode createSOAPFaultNode(SOAPFault parent) {
        return createSOAPFaultNode(parent, null);
    }

    public final SOAPFaultNode createSOAPFaultNode() {
        return createSOAPFaultNode(null, null);
    }

    public final SOAPEnvelope getDefaultFaultEnvelope() {
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
