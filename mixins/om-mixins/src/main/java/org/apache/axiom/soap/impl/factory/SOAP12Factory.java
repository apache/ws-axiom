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

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.impl.intf.factory.AxiomNodeFactory;
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
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.intf.soap12.SOAP12Helper;

public class SOAP12Factory extends SOAPFactoryImpl {
    public SOAP12Factory(OMMetaFactory metaFactory, AxiomNodeFactory nodeFactory) {
        super(metaFactory, nodeFactory);
    }

    @Override
    public final SOAPHelper getSOAPHelper() {
        return SOAP12Helper.INSTANCE;
    }

    public final SOAPFaultValue internalCreateSOAPFaultValue(SOAPFaultClassifier parent) {
        return createSOAPElement(AxiomNodeFactory::createSOAP12FaultValue, parent, SOAP12Constants.QNAME_FAULT_VALUE);
    }

    @Override
    public final SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent) {
        return internalCreateSOAPFaultValue(parent);
    }

    @Override
    public final SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent) {
        return internalCreateSOAPFaultValue(parent);
    }

    @Override
    public final SOAPFaultValue createSOAPFaultValue() {
        return internalCreateSOAPFaultValue(null);
    }

    private SOAPFaultSubCode internalCreateSOAPFaultSubCode(SOAPFaultClassifier parent) {
        return createSOAPElement(AxiomNodeFactory::createSOAP12FaultSubCode, parent, SOAP12Constants.QNAME_FAULT_SUBCODE);
    }

    @Override
    public final SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent) {
        return internalCreateSOAPFaultSubCode(parent);
    }

    @Override
    public final SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent) {
        return internalCreateSOAPFaultSubCode(parent);
    }

    @Override
    public final SOAPFaultSubCode createSOAPFaultSubCode() {
        return internalCreateSOAPFaultSubCode(null);
    }

    @Override
    public final SOAPFaultText createSOAPFaultText(SOAPFaultReason parent) {
        return createSOAPElement(AxiomNodeFactory::createSOAP12FaultText, parent, SOAP12Constants.QNAME_FAULT_TEXT);
    }

    @Override
    public final SOAPFaultText createSOAPFaultText() {
        return createSOAPFaultText(null);
    }

    @Override
    public final SOAPFaultNode createSOAPFaultNode(SOAPFault parent) {
        return createSOAPElement(AxiomNodeFactory::createSOAP12FaultNode, parent, SOAP12Constants.QNAME_FAULT_NODE);
    }

    @Override
    public final SOAPFaultNode createSOAPFaultNode() {
        return createSOAPFaultNode(null);
    }

    @Override
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
