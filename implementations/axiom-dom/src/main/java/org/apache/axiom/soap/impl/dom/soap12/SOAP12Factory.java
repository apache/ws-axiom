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

package org.apache.axiom.soap.impl.dom.soap12;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.dom.ParentNode;
import org.apache.axiom.om.impl.dom.factory.OMDOMMetaFactory;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.impl.common.AxiomSOAP12Factory;
import org.apache.axiom.soap.impl.dom.SOAPFactoryImpl;

/**
 */
public class SOAP12Factory extends SOAPFactoryImpl implements AxiomSOAP12Factory {
    public SOAP12Factory(OMDOMMetaFactory metaFactory) {
        super(metaFactory);
    }

    public SOAP12Factory() {
    }

    public String getSoapVersionURI() {
        return SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI;
    }

    public SOAPVersion getSOAPVersion() {
        return SOAP12Version.getSingleton();
    }

    public SOAPFault createSOAPFault(SOAPBody parent, Exception e) throws SOAPProcessingException {
        return new SOAP12FaultImpl(parent, e, this);
    }

    public SOAPFaultValue createSOAPFaultValue() throws SOAPProcessingException {
        return new SOAP12FaultValueImpl(null, getNamespace(), null, this, true);
    }

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent)
            throws SOAPProcessingException {
        return new SOAP12FaultValueImpl(parent, this);
    }

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent,
                                               OMXMLParserWrapper builder) {
        return new SOAP12FaultValueImpl((ParentNode)parent, null, builder, this, false);
    }

    //added
    public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent)
            throws SOAPProcessingException {
        return new SOAP12FaultValueImpl(parent, this);
    }

    //added
    public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent,
                                               OMXMLParserWrapper builder) {
        return new SOAP12FaultValueImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPFaultSubCode createSOAPFaultSubCode() throws SOAPProcessingException {
        return new SOAP12FaultSubCodeImpl(null, getNamespace(), null, this, true);
    }

    //changed
    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent)
            throws SOAPProcessingException {
        return new SOAP12FaultSubCodeImpl(parent, this);
    }

    //changed
    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent,
                                                   OMXMLParserWrapper builder) {
        return new SOAP12FaultSubCodeImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent)
            throws SOAPProcessingException {
        return new SOAP12FaultSubCodeImpl(parent, this);
    }

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent,
                                                   OMXMLParserWrapper builder) {
        return new SOAP12FaultSubCodeImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPFaultText createSOAPFaultText() throws SOAPProcessingException {
        return new SOAP12FaultTextImpl(null, getNamespace(), null, this, true);
    }

    public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent)
            throws SOAPProcessingException {
        return new SOAP12FaultTextImpl(parent, this);
    }

    public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent,
                                             OMXMLParserWrapper builder) {
        return new SOAP12FaultTextImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPFaultNode createSOAPFaultNode() throws SOAPProcessingException {
        return new SOAP12FaultNodeImpl(null, getNamespace(), null, this, true);
    }

    public SOAPFaultNode createSOAPFaultNode(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP12FaultNodeImpl(parent, this);
    }

    public SOAPFaultNode createSOAPFaultNode(SOAPFault parent,
                                             OMXMLParserWrapper builder) {
        return new SOAP12FaultNodeImpl((ParentNode)parent, null, builder, this, false);
    }

    public OMNamespace getNamespace() {
        return new OMNamespaceImpl(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                                 SOAP12Constants.SOAP_DEFAULT_NAMESPACE_PREFIX);
    }

    public SOAPEnvelope getDefaultFaultEnvelope() throws SOAPProcessingException {
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
