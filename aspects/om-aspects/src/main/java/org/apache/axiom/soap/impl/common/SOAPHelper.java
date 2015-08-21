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
            AxiomSOAP11HeaderBlock.class,
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
            AxiomSOAP12HeaderBlock.class,
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
    private final String specName;
    private final Class<? extends AxiomSOAPHeaderBlock> headerBlockClass;
    private final QName mustUnderstandAttributeQName;
    private final QName roleAttributeQName;
    private final QName relayAttributeQName;
    
    private SOAPHelper(SOAPVersion version, String specName,
            Class<? extends AxiomSOAPHeaderBlock> headerBlockClass,
            String roleAttributeLocalName, String relayAttributeLocalName) {
        this.version = version;
        this.specName = specName;
        this.headerBlockClass = headerBlockClass;
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
    
    final String getSpecName() {
        return specName;
    }

    final Class<? extends AxiomSOAPHeaderBlock> getHeaderBlockClass() {
        return headerBlockClass;
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
