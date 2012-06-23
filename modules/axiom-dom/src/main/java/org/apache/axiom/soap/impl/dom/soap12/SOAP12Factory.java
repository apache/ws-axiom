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
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.impl.dom.SOAPEnvelopeImpl;
import org.apache.axiom.soap.impl.dom.factory.DOMSOAPFactory;

/**
 */
public class SOAP12Factory extends DOMSOAPFactory {
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

    public SOAPEnvelope createSOAPEnvelope() {
        return new SOAPEnvelopeImpl(
                null,
                new OMNamespaceImpl(
                        SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                        SOAP12Constants.SOAP_DEFAULT_NAMESPACE_PREFIX),
                null, this, true);
    }
    
    public SOAPEnvelope createSOAPEnvelope(OMNamespace ns) {
        return new SOAPEnvelopeImpl(null, ns, null, this, true);
    }

    public SOAPHeader createSOAPHeader(SOAPEnvelope envelope) throws SOAPProcessingException {
        return new SOAP12HeaderImpl(envelope, this);
    }

    public SOAPHeader createSOAPHeader(SOAPEnvelope envelope,
                                       OMXMLParserWrapper builder) {
        return new SOAP12HeaderImpl((ParentNode)envelope, null, builder, this, false);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns)
            throws SOAPProcessingException {
        return new SOAP12HeaderBlockImpl(null, localName, ns, null, this, true);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                 OMNamespace ns,
                                                 SOAPHeader parent) throws SOAPProcessingException {
        return new SOAP12HeaderBlockImpl((ParentNode)parent, localName, ns, null, this, true);
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                 SOAPHeader parent,
                                                 OMXMLParserWrapper builder)
            throws SOAPProcessingException {
        return new SOAP12HeaderBlockImpl((ParentNode)parent, localName, null, builder, this, false);
    }

    public SOAPFault createSOAPFault(SOAPBody parent, Exception e) throws SOAPProcessingException {
        return new SOAP12FaultImpl(parent, e, this);
    }

    public SOAPFault createSOAPFault(SOAPBody parent) throws SOAPProcessingException {
        return new SOAP12FaultImpl(parent, this);
    }

    public SOAPFault createSOAPFault(SOAPBody parent,
                                     OMXMLParserWrapper builder) {
        return new SOAP12FaultImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPBody createSOAPBody(SOAPEnvelope envelope) throws SOAPProcessingException {
        return new SOAP12BodyImpl(envelope, this);
    }

    public SOAPBody createSOAPBody(SOAPEnvelope envelope,
                                   OMXMLParserWrapper builder) {
        return new SOAP12BodyImpl((ParentNode)envelope, null, builder, this, false);
    }

    public SOAPFaultCode createSOAPFaultCode(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP12FaultCodeImpl(parent, this);
    }

    public SOAPFaultCode createSOAPFaultCode(SOAPFault parent,
                                             OMXMLParserWrapper builder) {
        return new SOAP12FaultCodeImpl((ParentNode)parent, null, builder, this, false);
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

    public SOAPFaultReason createSOAPFaultReason(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP12FaultReasonImpl(parent, this);
    }

    public SOAPFaultReason createSOAPFaultReason(SOAPFault parent,
                                                 OMXMLParserWrapper builder) {
        return new SOAP12FaultReasonImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent)
            throws SOAPProcessingException {
        return new SOAP12FaultTextImpl(parent, this);
    }

    public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent,
                                             OMXMLParserWrapper builder) {
        return new SOAP12FaultTextImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPFaultNode createSOAPFaultNode(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP12FaultNodeImpl(parent, this);
    }

    public SOAPFaultNode createSOAPFaultNode(SOAPFault parent,
                                             OMXMLParserWrapper builder) {
        return new SOAP12FaultNodeImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPFaultRole createSOAPFaultRole(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP12FaultRoleImpl(parent, this);
    }

    public SOAPFaultRole createSOAPFaultRole(SOAPFault parent,
                                             OMXMLParserWrapper builder) {
        return new SOAP12FaultRoleImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent) throws SOAPProcessingException {
        return new SOAP12FaultDetailImpl(parent, this);
    }

    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent,
                                                 OMXMLParserWrapper builder) {
        return new SOAP12FaultDetailImpl((ParentNode)parent, null, builder, this, false);
    }

    public SOAPFaultDetail createSOAPFaultDetail() throws SOAPProcessingException {
        return new SOAP12FaultDetailImpl(null, getNamespace(), null, this, true);
    }

    public OMNamespace getNamespace() {
        return new OMNamespaceImpl(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                                 SOAP12Constants.SOAP_DEFAULT_NAMESPACE_PREFIX);
    }

    public SOAPFault createSOAPFault() throws SOAPProcessingException {
        return new SOAP12FaultImpl(this.getDefaultEnvelope().getBody(), this);
    }


}
