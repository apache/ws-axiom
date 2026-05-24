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

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.intf.factory.AxiomElementType;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.intf.AxiomSOAPBody;
import org.apache.axiom.soap.impl.intf.AxiomSOAPEnvelope;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFault;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFaultCode;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFaultDetail;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFaultReason;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFaultRole;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeader;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeaderBlock;
import org.apache.axiom.soap.impl.intf.SOAPHelper;

public abstract class SOAPHelperImpl implements SOAPHelper {
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

    protected SOAPHelperImpl(
            SOAPVersion version,
            String specName,
            AxiomElementType<? extends AxiomSOAPEnvelope> envelopeType,
            AxiomElementType<? extends AxiomSOAPHeader> headerType,
            AxiomElementType<? extends AxiomSOAPHeaderBlock> headerBlockType,
            AxiomElementType<? extends AxiomSOAPBody> bodyType,
            AxiomElementType<? extends AxiomSOAPFault> faultType,
            AxiomElementType<? extends AxiomSOAPFaultCode> faultCodeType,
            AxiomElementType<? extends AxiomSOAPFaultReason> faultReasonType,
            AxiomElementType<? extends AxiomSOAPFaultRole> faultRoleType,
            AxiomElementType<? extends AxiomSOAPFaultDetail> faultDetailType,
            String roleAttributeLocalName,
            String relayAttributeLocalName) {
        this.version = version;
        namespace = new OMNamespaceImpl(version.getEnvelopeURI(), SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        this.specName = specName;
        this.envelopeType = envelopeType;
        this.headerType = headerType;
        headerQName = new QName(
                version.getEnvelopeURI(), SOAPConstants.HEADER_LOCAL_NAME, SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        this.headerBlockType = headerBlockType;
        this.bodyType = bodyType;
        bodyQName = new QName(
                version.getEnvelopeURI(), SOAPConstants.BODY_LOCAL_NAME, SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        this.faultType = faultType;
        faultQName = new QName(
                version.getEnvelopeURI(),
                SOAPConstants.SOAPFAULT_LOCAL_NAME,
                SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        this.faultCodeType = faultCodeType;
        this.faultReasonType = faultReasonType;
        this.faultRoleType = faultRoleType;
        this.faultDetailType = faultDetailType;
        mustUnderstandAttributeQName = new QName(
                version.getEnvelopeURI(),
                SOAPConstants.ATTR_MUSTUNDERSTAND,
                SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        roleAttributeQName = new QName(
                version.getEnvelopeURI(), roleAttributeLocalName, SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
        relayAttributeQName = relayAttributeLocalName == null
                ? null
                : new QName(
                        version.getEnvelopeURI(), relayAttributeLocalName, SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX);
    }

    @Override
    public final SOAPVersion getVersion() {
        return version;
    }

    @Override
    public final String getEnvelopeURI() {
        return version.getEnvelopeURI();
    }

    @Override
    public final OMNamespace getNamespace() {
        return namespace;
    }

    @Override
    public final String getSpecName() {
        return specName;
    }

    @Override
    public final AxiomElementType<? extends AxiomSOAPEnvelope> getEnvelopeType() {
        return envelopeType;
    }

    @Override
    public final AxiomElementType<? extends AxiomSOAPHeader> getHeaderType() {
        return headerType;
    }

    @Override
    public final QName getHeaderQName() {
        return headerQName;
    }

    @Override
    public final AxiomElementType<? extends AxiomSOAPHeaderBlock> getHeaderBlockType() {
        return headerBlockType;
    }

    @Override
    public final AxiomElementType<? extends AxiomSOAPBody> getBodyType() {
        return bodyType;
    }

    @Override
    public final QName getBodyQName() {
        return bodyQName;
    }

    @Override
    public final AxiomElementType<? extends AxiomSOAPFault> getFaultType() {
        return faultType;
    }

    @Override
    public final QName getFaultQName() {
        return faultQName;
    }

    @Override
    public final AxiomElementType<? extends AxiomSOAPFaultCode> getFaultCodeType() {
        return faultCodeType;
    }

    @Override
    public final QName getFaultCodeQName() {
        return version.getFaultCodeQName();
    }

    @Override
    public final AxiomElementType<? extends AxiomSOAPFaultReason> getFaultReasonType() {
        return faultReasonType;
    }

    @Override
    public final QName getFaultReasonQName() {
        return version.getFaultReasonQName();
    }

    @Override
    public final AxiomElementType<? extends AxiomSOAPFaultRole> getFaultRoleType() {
        return faultRoleType;
    }

    @Override
    public final QName getFaultRoleQName() {
        return version.getFaultRoleQName();
    }

    @Override
    public final AxiomElementType<? extends AxiomSOAPFaultDetail> getFaultDetailType() {
        return faultDetailType;
    }

    @Override
    public final QName getFaultDetailQName() {
        return version.getFaultDetailQName();
    }

    @Override
    public final QName getMustUnderstandAttributeQName() {
        return mustUnderstandAttributeQName;
    }

    @Override
    public final QName getRoleAttributeQName() {
        return roleAttributeQName;
    }

    @Override
    public final QName getRelayAttributeQName() {
        return relayAttributeQName;
    }
}
