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

package org.apache.axiom.soap.impl.dom.soap11;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.dom.ParentNode;
import org.apache.axiom.om.impl.dom.factory.OMDOMMetaFactory;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.soap.impl.dom.SOAPFactoryImpl;

/**
 */
public class SOAP11Factory extends SOAPFactoryImpl {
    public SOAP11Factory(OMDOMMetaFactory metaFactory) {
        super(metaFactory);
    }
    
    public SOAP11Factory() {
    }

    public String getSoapVersionURI() {
        return SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI;
    }

    public SOAPVersion getSOAPVersion() {
        return SOAP11Version.getSingleton();
    }

    public SOAPHeader createSOAPHeader() throws SOAPProcessingException {
        return new SOAP11HeaderImpl(null, getNamespace(), null, this, true);
    }

    public SOAPHeader createSOAPHeader(SOAPEnvelope envelope)
            throws SOAPProcessingException {
        return new SOAP11HeaderImpl(envelope, this);
    }

    public SOAPHeader createSOAPHeader(SOAPEnvelope envelope,
                                       OMXMLParserWrapper builder) {
        return new SOAP11HeaderImpl((ParentNode)envelope, null, builder, this, false);
    }


    public SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns)
            throws SOAPProcessingException {
        return new SOAP11HeaderBlockImpl(null, localName, ns, null, this, true);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                 OMNamespace ns, SOAPHeader parent)
            throws SOAPProcessingException {
        return new SOAP11HeaderBlockImpl((ParentNode)parent, localName, ns, null, this, true);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                 SOAPHeader parent,
                                                 OMXMLParserWrapper builder)
            throws SOAPProcessingException {
        return new SOAP11HeaderBlockImpl((ParentNode)parent, localName, null, builder, this, false);
    }

    public SOAPFault createSOAPFault(SOAPBody parent, Exception e)
            throws SOAPProcessingException {
        return new SOAP11FaultImpl(parent, e, this);
    }

    public SOAPFault createSOAPFault(SOAPBody parent)
            throws SOAPProcessingException {
        return new SOAP11FaultImpl(parent, this);
    }

    public SOAPFault createSOAPFault(SOAPBody parent,
                                     OMXMLParserWrapper builder) {
        return new SOAP11FaultImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPBody createSOAPBody() throws SOAPProcessingException {
        return new SOAP11BodyImpl(null, getNamespace(), null, this, true);
    }

    public SOAPBody createSOAPBody(SOAPEnvelope envelope)
            throws SOAPProcessingException {
        return new SOAP11BodyImpl(envelope, (SOAPFactory) envelope
                .getOMFactory());
    }

    public SOAPBody createSOAPBody(SOAPEnvelope envelope,
                                   OMXMLParserWrapper builder) {
        return new SOAP11BodyImpl((ParentNode)envelope, null, builder, this, false);
    }

    public SOAPFaultCode createSOAPFaultCode(SOAPFault parent)
            throws SOAPProcessingException {
        return new SOAP11FaultCodeImpl(parent, this);
    }

    public SOAPFaultCode createSOAPFaultCode(SOAPFault parent,
                                             OMXMLParserWrapper builder) {
        return new SOAP11FaultCodeImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPFaultCode createSOAPFaultCode() {
        return new SOAP11FaultCodeImpl(null, null, null, this, true);
    }

    public SOAPFaultValue createSOAPFaultValue() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent)
            throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    //added
    public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent)
            throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultSubCode createSOAPFaultSubCode() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    //changed
    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent)
            throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent)
            throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultReason createSOAPFaultReason(SOAPFault parent)
            throws SOAPProcessingException {
        return new SOAP11FaultReasonImpl(parent, this);
    }

    public SOAPFaultReason createSOAPFaultReason(SOAPFault parent,
                                                 OMXMLParserWrapper builder) {
        return new SOAP11FaultReasonImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPFaultReason createSOAPFaultReason() {
        return new SOAP11FaultReasonImpl(null, null, null, this, true);
    }

    public SOAPFaultText createSOAPFaultText() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent)
            throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultNode createSOAPFaultNode() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultNode createSOAPFaultNode(SOAPFault parent)
            throws SOAPProcessingException {
        throw new UnsupportedOperationException("SOAP 1.1 has no SOAP Fault Node");
    }

    public SOAPFaultRole createSOAPFaultRole(SOAPFault parent)
            throws SOAPProcessingException {
        return new SOAP11FaultRoleImpl(parent, this);
    }

    public SOAPFaultRole createSOAPFaultRole(SOAPFault parent,
                                             OMXMLParserWrapper builder) {
        return new SOAP11FaultRoleImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPFaultRole createSOAPFaultRole() {
        return new SOAP11FaultRoleImpl(null, null, null, this, true);
    }

    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent)
            throws SOAPProcessingException {
        return new SOAP11FaultDetailImpl(parent, this);
    }

    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent,
                                                 OMXMLParserWrapper builder) {
        return new SOAP11FaultDetailImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPFaultDetail createSOAPFaultDetail() {
        return new SOAP11FaultDetailImpl(null, null, null, this, true);
    }

    public OMNamespace getNamespace() {
        return new OMNamespaceImpl(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                                 SOAP11Constants.SOAP_DEFAULT_NAMESPACE_PREFIX);
    }

    public SOAPFault createSOAPFault() throws SOAPProcessingException {
        return new SOAP11FaultImpl(null, getNamespace(), null, this, true);
    }

    public SOAPEnvelope getDefaultFaultEnvelope() throws SOAPProcessingException {
        SOAPEnvelope defaultEnvelope = getDefaultEnvelope();
        SOAPFault fault = createSOAPFault(defaultEnvelope.getBody());
        createSOAPFaultCode(fault);
        createSOAPFaultReason(fault);
        createSOAPFaultDetail(fault);
        return defaultEnvelope;
    }
}
