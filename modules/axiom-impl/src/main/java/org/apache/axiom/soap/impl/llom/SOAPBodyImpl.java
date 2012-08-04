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

package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;

/** Class SOAPBodyImpl */
public abstract class SOAPBodyImpl extends SOAPElement
        implements SOAPBody, OMConstants {
    private boolean enableLookAhead = true;
    private boolean lookAheadAttempted = false;
    private boolean lookAheadSuccessful = false;
    private String lookAheadLocalName = null;
    private OMNamespace lookAheadNS = null;

    protected SOAPBodyImpl(String localName, OMNamespace ns,
                           SOAPFactory factory) {
        super(localName, ns, factory);
    }

    /** @param envelope  */
    public SOAPBodyImpl(SOAPEnvelope envelope, SOAPFactory factory)
            throws SOAPProcessingException {
        super(envelope, SOAPConstants.BODY_LOCAL_NAME, true, factory);

    }

    /**
     * Constructor SOAPBodyImpl
     *
     * @param envelope
     * @param builder
     */
    public SOAPBodyImpl(SOAPEnvelope envelope, OMXMLParserWrapper builder,
                        SOAPFactory factory) {
        super(envelope, SOAPConstants.BODY_LOCAL_NAME, builder, factory);
    }

    /**
     * Creates a new <code>SOAPFault</code> object and adds it to this <code>SOAPBody</code>
     * object.
     *
     * @param e
     * @return the new <code>SOAPFault</code> object
     * @throws org.apache.axiom.om.OMException
     *                     if there is a SOAP error
     * @throws OMException
     */
    public abstract SOAPFault addFault(Exception e) throws OMException;

    /**
     * Indicates whether a <code>SOAPFault</code> object exists in this <code>SOAPBody</code>
     * object.
     *
     * @return <code>true</code> if a <code>SOAPFault</code> object exists in this
     *         <code>SOAPBody</code> object; <code>false</code> otherwise
     */
    public boolean hasFault() {
        // Set hasSOAPFault if it matches the name matches a SOAP Fault
        if (hasLookahead()) {
            return SOAPConstants.SOAPFAULT_LOCAL_NAME.equals(lookAheadLocalName)
                    && lookAheadNS != null
                    && (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(lookAheadNS.getNamespaceURI()) ||
                        SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(lookAheadNS.getNamespaceURI()));
        } else {
            return getFirstElement() instanceof SOAPFault;
        }
    }

    /**
     * Returns the <code>SOAPFault</code> object in this <code>SOAPBody</code> object.
     *
     * @return the <code>SOAPFault</code> object in this <code>SOAPBody</code> object
     */
    public SOAPFault getFault() {
        OMElement element = getFirstElement();
        return element instanceof SOAPFault ? (SOAPFault)element : null;
    }

    /**
     * @param soapFault
     * @throws org.apache.axiom.om.OMException
     *
     * @throws OMException
     */
    public void addFault(SOAPFault soapFault) throws OMException {
        if (hasFault()) {
            throw new OMException(
                    "SOAP Body already has a SOAP Fault and there can not be more than one SOAP fault");
        }
        addChild(soapFault);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAPEnvelopeImpl)) {
            throw new SOAPProcessingException(
                    "Expecting an implementation of SOAP Envelope as the parent. But received some other implementation");
        }
    }

    public OMNode detach() throws OMException {
        throw new SOAPProcessingException(
                "Can not detach SOAP Body, SOAP Envelope must have a Body !!");
    }

    private boolean hasLookahead() {
        if (!enableLookAhead) {
           return false; 
        }
        if (lookAheadAttempted) {
            return lookAheadSuccessful;
        }
        lookAheadAttempted = true;
        StAXSOAPModelBuilder soapBuilder = (StAXSOAPModelBuilder) this.builder;
        if (soapBuilder != null &&
            soapBuilder.isCache() &&
            !soapBuilder.isCompleted() &&
            !soapBuilder.isClosed()) {
            lookAheadSuccessful = soapBuilder.lookahead();
            if (lookAheadSuccessful) {
                this.lookAheadLocalName = soapBuilder.getName();
                String ns = soapBuilder.getNamespace();
                if (ns == null) {
                    lookAheadNS = null;
                } else {
                    String prefix = soapBuilder.getPrefix();
                    lookAheadNS = factory.createOMNamespace(ns, prefix == null ? "" : prefix);
                }
            }
        }
        return lookAheadSuccessful;
    }
    
    public OMNamespace getFirstElementNS() {
        if (hasLookahead()) {
            return this.lookAheadNS;
        } else {
            OMElement element = this.getFirstElement();
            if (element == null) {
                return null;
            } else {
                return element.getNamespace();
            } 
        }
    }
    
    public String getFirstElementLocalName() {
        if (hasLookahead()) {
            return this.lookAheadLocalName;
        } else {
            OMElement element = this.getFirstElement();
            if (element == null) {
                return null;
            } else {
                return element.getLocalName();
            } 
        }
    }

    public void addChild(OMNode child, boolean fromBuilder) {
        this.enableLookAhead = false;
        super.addChild(child, fromBuilder);
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return ((SOAPFactory)factory).createSOAPBody((SOAPEnvelope)targetParent);
    }
}
