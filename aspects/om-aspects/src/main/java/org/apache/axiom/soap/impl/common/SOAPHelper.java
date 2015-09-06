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
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPVersion;

/**
 * Encapsulates certain SOAP version specific behaviors. This API defines methods that could also be
 * added to {@link SOAPVersion}, but that are not relevant for application code and should therefore
 * not be part of the public API.
 */
abstract class SOAPHelper {
    static final SOAPHelper SOAP11 = new SOAPHelper(SOAP11Version.getSingleton(), "SOAP 1.1",
            AxiomSOAP11Envelope.class,
            AxiomSOAP11Header.class,
            AxiomSOAP11HeaderBlock.class,
            AxiomSOAP11Body.class,
            AxiomSOAP11Fault.class,
            AxiomSOAP11FaultCode.class,
            AxiomSOAP11FaultReason.class,
            AxiomSOAP11FaultRole.class,
            AxiomSOAP11FaultDetail.class,
            SOAP11Constants.ATTR_ACTOR, null) {
        public Boolean parseBoolean(String literal) {
            if (literal.equals("1")) {
                return Boolean.TRUE;
            } else if (literal.equals("0")) {
                return Boolean.FALSE;
            } else {
                return null;
            }
        }

        @Override
        public String formatBoolean(boolean value) {
            return value ? "1" : "0";
        }
    };
    
    static final SOAPHelper SOAP12 = new SOAPHelper(SOAP12Version.getSingleton(), "SOAP 1.2",
            AxiomSOAP12Envelope.class,
            AxiomSOAP12Header.class,
            AxiomSOAP12HeaderBlock.class,
            AxiomSOAP12Body.class,
            AxiomSOAP12Fault.class,
            AxiomSOAP12FaultCode.class,
            AxiomSOAP12FaultReason.class,
            AxiomSOAP12FaultRole.class,
            AxiomSOAP12FaultDetail.class,
            SOAP12Constants.SOAP_ROLE, SOAP12Constants.SOAP_RELAY) {
        public Boolean parseBoolean(String literal) {
            if (literal.equals("true") || literal.equals("1")) {
                return Boolean.TRUE;
            } else if (literal.equals("false") || literal.equals("0")) {
                return Boolean.FALSE;
            } else {
                return null;
            }
        }

        @Override
        public String formatBoolean(boolean value) {
            return String.valueOf(value);
        }
    };
    
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
    
    private SOAPHelper(SOAPVersion version, String specName,
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
    
    final SOAPVersion getVersion() {
        return version;
    }
    
    final String getEnvelopeURI() {
        return version.getEnvelopeURI();
    }

    final OMNamespace getNamespace() {
        return namespace;
    }

    final String getSpecName() {
        return specName;
    }

    final Class<? extends AxiomSOAPEnvelope> getEnvelopeClass() {
        return envelopeClass;
    }

    final Class<? extends AxiomSOAPHeader> getHeaderClass() {
        return headerClass;
    }

    final QName getHeaderQName() {
        return headerQName;
    }

    final Class<? extends AxiomSOAPHeaderBlock> getHeaderBlockClass() {
        return headerBlockClass;
    }

    final Class<? extends AxiomSOAPBody> getBodyClass() {
        return bodyClass;
    }

    final QName getBodyQName() {
        return bodyQName;
    }

    final Class<? extends AxiomSOAPFault> getFaultClass() {
        return faultClass;
    }

    final QName getFaultQName() {
        return faultQName;
    }

    final Class<? extends AxiomSOAPFaultCode> getFaultCodeClass() {
        return faultCodeClass;
    }

    final QName getFaultCodeQName() {
        return version.getFaultCodeQName();
    }

    final Class<? extends AxiomSOAPFaultReason> getFaultReasonClass() {
        return faultReasonClass;
    }

    final QName getFaultReasonQName() {
        return version.getFaultReasonQName();
    }

    final Class<? extends AxiomSOAPFaultRole> getFaultRoleClass() {
        return faultRoleClass;
    }

    final QName getFaultRoleQName() {
        return version.getFaultRoleQName();
    }

    final Class<? extends AxiomSOAPFaultDetail> getFaultDetailClass() {
        return faultDetailClass;
    }

    final QName getFaultDetailQName() {
        return version.getFaultDetailQName();
    }

    final QName getMustUnderstandAttributeQName() {
        return mustUnderstandAttributeQName;
    }

    final QName getRoleAttributeQName() {
        return roleAttributeQName;
    }

    final QName getRelayAttributeQName() {
        return relayAttributeQName;
    }

    abstract Boolean parseBoolean(String literal);
    abstract String formatBoolean(boolean value);
}
