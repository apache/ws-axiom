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
package org.apache.axiom.soap.impl.intf;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.intf.factory.AxiomElementType;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPVersion;

/**
 * Encapsulates certain SOAP version specific behaviors. This API defines methods that could also be
 * added to {@link SOAPVersion}, but that are not relevant for application code and should therefore
 * not be part of the public API.
 */
public abstract class SOAPHelper {
    private final SOAPVersion version;
    private final OMNamespace namespace;
    private final String specName;
    private final AxiomElementType<? extends AxiomSOAPEnvelope> envelopeType;
    private final AxiomElementType<? extends AxiomSOAPHeader> headerType;
    private final QName headerQName;
    private final AxiomElementType<? extends AxiomSOAPHeaderBlock> headerBlockType;
    private final AxiomElementType<? extends AxiomSOAPBody> bodyType;
    private final QName bodyQName;
    private final AxiomElementType<? extends AxiomSOAPFault> faultType;
    private final QName faultQName;
    private final AxiomElementType<? extends AxiomSOAPFaultCode> faultCodeType;
    private final AxiomElementType<? extends AxiomSOAPFaultReason> faultReasonType;
    private final AxiomElementType<? extends AxiomSOAPFaultRole> faultRoleType;
    private final AxiomElementType<? extends AxiomSOAPFaultDetail> faultDetailType;
    private final QName mustUnderstandAttributeQName;
    private final QName roleAttributeQName;
    private final QName relayAttributeQName;
    
    protected SOAPHelper(SOAPVersion version, String specName,
            AxiomElementType<? extends AxiomSOAPEnvelope> envelopeType,
            AxiomElementType<? extends AxiomSOAPHeader> headerType,
            AxiomElementType<? extends AxiomSOAPHeaderBlock> headerBlockType,
            AxiomElementType<? extends AxiomSOAPBody> bodyType,
            AxiomElementType<? extends AxiomSOAPFault> faultType,
            AxiomElementType<? extends AxiomSOAPFaultCode> faultCodeType,
            AxiomElementType<? extends AxiomSOAPFaultReason> faultReasonType,
            AxiomElementType<? extends AxiomSOAPFaultRole> faultRoleType,
            AxiomElementType<? extends AxiomSOAPFaultDetail> faultDetailType,
            String roleAttributeLocalName, String relayAttributeLocalName) {
        this.version = version;
        namespace = new OMNamespaceImpl(version.getEnvelopeURI(),
                SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        this.specName = specName;
        this.envelopeType = envelopeType;
        this.headerType = headerType;
        headerQName = new QName(version.getEnvelopeURI(), SOAPConstants.HEADER_LOCAL_NAME,
                SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        this.headerBlockType = headerBlockType;
        this.bodyType = bodyType;
        bodyQName = new QName(version.getEnvelopeURI(), SOAPConstants.BODY_LOCAL_NAME,
                SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        this.faultType = faultType;
        faultQName = new QName(version.getEnvelopeURI(), SOAPConstants.SOAPFAULT_LOCAL_NAME,
                SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        this.faultCodeType = faultCodeType;
        this.faultReasonType = faultReasonType;
        this.faultRoleType = faultRoleType;
        this.faultDetailType = faultDetailType;
        mustUnderstandAttributeQName = new QName(
                version.getEnvelopeURI(), SOAPConstants.ATTR_MUSTUNDERSTAND, SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        roleAttributeQName = new QName(
                version.getEnvelopeURI(), roleAttributeLocalName, SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        relayAttributeQName = relayAttributeLocalName == null ? null :
            new QName(version.getEnvelopeURI(), relayAttributeLocalName, SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
    }
    
    public final SOAPVersion getVersion() {
        return version;
    }
    
    public abstract SOAPFactory getSOAPFactory(OMMetaFactory metaFactory);
    
    public final String getEnvelopeURI() {
        return version.getEnvelopeURI();
    }

    public final OMNamespace getNamespace() {
        return namespace;
    }

    public final String getSpecName() {
        return specName;
    }

    public final AxiomElementType<? extends AxiomSOAPEnvelope> getEnvelopeType() {
        return envelopeType;
    }

    public final AxiomElementType<? extends AxiomSOAPHeader> getHeaderType() {
        return headerType;
    }

    public final QName getHeaderQName() {
        return headerQName;
    }

    public final AxiomElementType<? extends AxiomSOAPHeaderBlock> getHeaderBlockType() {
        return headerBlockType;
    }

    public final AxiomElementType<? extends AxiomSOAPBody> getBodyType() {
        return bodyType;
    }

    public final QName getBodyQName() {
        return bodyQName;
    }

    public final AxiomElementType<? extends AxiomSOAPFault> getFaultType() {
        return faultType;
    }

    public final QName getFaultQName() {
        return faultQName;
    }

    public final AxiomElementType<? extends AxiomSOAPFaultCode> getFaultCodeType() {
        return faultCodeType;
    }

    public final QName getFaultCodeQName() {
        return version.getFaultCodeQName();
    }

    public final AxiomElementType<? extends AxiomSOAPFaultReason> getFaultReasonType() {
        return faultReasonType;
    }

    public final QName getFaultReasonQName() {
        return version.getFaultReasonQName();
    }

    public final AxiomElementType<? extends AxiomSOAPFaultRole> getFaultRoleType() {
        return faultRoleType;
    }

    public final QName getFaultRoleQName() {
        return version.getFaultRoleQName();
    }

    public final AxiomElementType<? extends AxiomSOAPFaultDetail> getFaultDetailType() {
        return faultDetailType;
    }

    public final QName getFaultDetailQName() {
        return version.getFaultDetailQName();
    }

    public final QName getMustUnderstandAttributeQName() {
        return mustUnderstandAttributeQName;
    }

    public final QName getRoleAttributeQName() {
        return roleAttributeQName;
    }

    public final QName getRelayAttributeQName() {
        return relayAttributeQName;
    }

    public abstract Boolean parseBoolean(String literal);
    public abstract String formatBoolean(boolean value);
}
