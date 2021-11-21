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
    private final Class<? extends AxiomSOAPEnvelope> envelopeClass;
    private final Class<? extends AxiomSOAPHeader> headerClass;
    private final QName headerQName;
    private final Class<? extends AxiomSOAPHeaderBlock> headerBlockClass;
    private final Class<? extends AxiomSOAPBody> bodyClass;
    private final QName bodyQName;
    private final Class<? extends AxiomSOAPFault> faultClass;
    private final QName faultQName;
    private final Class<? extends AxiomSOAPFaultCode> faultCodeClass;
    private final Class<? extends AxiomSOAPFaultReason> faultReasonClass;
    private final Class<? extends AxiomSOAPFaultRole> faultRoleClass;
    private final Class<? extends AxiomSOAPFaultDetail> faultDetailClass;
    private final QName mustUnderstandAttributeQName;
    private final QName roleAttributeQName;
    private final QName relayAttributeQName;
    
    protected SOAPHelper(SOAPVersion version, String specName,
            Class<? extends AxiomSOAPEnvelope> envelopeClass,
            Class<? extends AxiomSOAPHeader> headerClass,
            Class<? extends AxiomSOAPHeaderBlock> headerBlockClass,
            Class<? extends AxiomSOAPBody> bodyClass,
            Class<? extends AxiomSOAPFault> faultClass,
            Class<? extends AxiomSOAPFaultCode> faultCodeClass,
            Class<? extends AxiomSOAPFaultReason> faultReasonClass,
            Class<? extends AxiomSOAPFaultRole> faultRoleClass,
            Class<? extends AxiomSOAPFaultDetail> faultDetailClass,
            String roleAttributeLocalName, String relayAttributeLocalName) {
        this.version = version;
        namespace = new OMNamespaceImpl(version.getEnvelopeURI(),
                SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        this.specName = specName;
        this.envelopeClass = envelopeClass;
        this.headerClass = headerClass;
        headerQName = new QName(version.getEnvelopeURI(), SOAPConstants.HEADER_LOCAL_NAME,
                SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        this.headerBlockClass = headerBlockClass;
        this.bodyClass = bodyClass;
        bodyQName = new QName(version.getEnvelopeURI(), SOAPConstants.BODY_LOCAL_NAME,
                SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        this.faultClass = faultClass;
        faultQName = new QName(version.getEnvelopeURI(), SOAPConstants.SOAPFAULT_LOCAL_NAME,
                SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        this.faultCodeClass = faultCodeClass;
        this.faultReasonClass = faultReasonClass;
        this.faultRoleClass = faultRoleClass;
        this.faultDetailClass = faultDetailClass;
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

    public final Class<? extends AxiomSOAPEnvelope> getEnvelopeClass() {
        return envelopeClass;
    }

    public final Class<? extends AxiomSOAPHeader> getHeaderClass() {
        return headerClass;
    }

    public final QName getHeaderQName() {
        return headerQName;
    }

    public final Class<? extends AxiomSOAPHeaderBlock> getHeaderBlockClass() {
        return headerBlockClass;
    }

    public final Class<? extends AxiomSOAPBody> getBodyClass() {
        return bodyClass;
    }

    public final QName getBodyQName() {
        return bodyQName;
    }

    public final Class<? extends AxiomSOAPFault> getFaultClass() {
        return faultClass;
    }

    public final QName getFaultQName() {
        return faultQName;
    }

    public final Class<? extends AxiomSOAPFaultCode> getFaultCodeClass() {
        return faultCodeClass;
    }

    public final QName getFaultCodeQName() {
        return version.getFaultCodeQName();
    }

    public final Class<? extends AxiomSOAPFaultReason> getFaultReasonClass() {
        return faultReasonClass;
    }

    public final QName getFaultReasonQName() {
        return version.getFaultReasonQName();
    }

    public final Class<? extends AxiomSOAPFaultRole> getFaultRoleClass() {
        return faultRoleClass;
    }

    public final QName getFaultRoleQName() {
        return version.getFaultRoleQName();
    }

    public final Class<? extends AxiomSOAPFaultDetail> getFaultDetailClass() {
        return faultDetailClass;
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
