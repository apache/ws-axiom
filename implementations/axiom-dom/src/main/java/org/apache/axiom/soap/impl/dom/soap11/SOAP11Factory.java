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
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.dom.factory.OMDOMMetaFactory;
import org.apache.axiom.soap.SOAP11Constants;
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
import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.soap.impl.common.AxiomSOAP11Factory;
import org.apache.axiom.soap.impl.dom.SOAPFactoryImpl;

/**
 */
public class SOAP11Factory extends SOAPFactoryImpl implements AxiomSOAP11Factory {
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

    public SOAPFault createSOAPFault(SOAPBody parent, Exception e)
            throws SOAPProcessingException {
        return new SOAP11FaultImpl(parent, e, this);
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

    public OMNamespace getNamespace() {
        return new OMNamespaceImpl(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                                 SOAP11Constants.SOAP_DEFAULT_NAMESPACE_PREFIX);
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
