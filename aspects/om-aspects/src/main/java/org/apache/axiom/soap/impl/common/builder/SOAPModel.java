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

package org.apache.axiom.soap.impl.common.builder;

import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreNSAwareElement;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.impl.builder.Model;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.intf.soap11.SOAP11Helper;
import org.apache.axiom.soap.impl.intf.soap12.SOAP12Helper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class SOAPModel implements Model {
    private SOAPHelper soapHelper;

    /** Field headerPresent */
    private boolean headerPresent = false;

    /** Field bodyPresent */
    private boolean bodyPresent = false;

    /** Field log */
    private static final Log log = LogFactory.getLog(SOAPModel.class);

    private boolean processingFault = false;

    private SOAPBuilderHelper builderHelper;

    @Override
    public Class<? extends CoreDocument> getDocumentType() {
        return AxiomSOAPMessage.class;
    }

    @Override
    public Class<? extends CoreNSAwareElement> determineElementType(CoreParentNode parent,
            int elementLevel, String namespaceURI, String localName) {
        Class<? extends AxiomElement> elementType;
        if (elementLevel == 1) {

            // Now I've found a SOAP Envelope, now create SOAPEnvelope here.

            if (!localName.equals(SOAPConstants.SOAPENVELOPE_LOCAL_NAME)) {
                throw new SOAPProcessingException("First Element must contain the local name, "
                        + SOAPConstants.SOAPENVELOPE_LOCAL_NAME + " , but found " + localName,
                        SOAPConstants.FAULT_CODE_SENDER);
            }

            // determine SOAP version and from that determine a proper factory here.
            if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(namespaceURI)) {
                soapHelper = SOAP12Helper.INSTANCE;
                log.debug("Starting to process SOAP 1.2 message");
            } else if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(namespaceURI)) {
                soapHelper = SOAP11Helper.INSTANCE;
                log.debug("Starting to process SOAP 1.1 message");
            } else {
                throw new SOAPProcessingException(
                        "Only SOAP 1.1 or SOAP 1.2 messages are supported in the" +
                                " system", SOAPConstants.FAULT_CODE_VERSION_MISMATCH);
            }

            elementType = soapHelper.getEnvelopeClass();
        } else if (elementLevel == 2) {
            if (soapHelper.getEnvelopeURI().equals(namespaceURI)) {
                // this is either a header or a body
                if (localName.equals(SOAPConstants.HEADER_LOCAL_NAME)) {
                    if (headerPresent) {
                        throw new SOAPProcessingException("Multiple headers encountered!",
                                                          getSenderFaultCode());
                    }
                    if (bodyPresent) {
                        throw new SOAPProcessingException("Header Body wrong order!",
                                                          getSenderFaultCode());
                    }
                    headerPresent = true;
                    elementType = soapHelper.getHeaderClass();
                } else if (localName.equals(SOAPConstants.BODY_LOCAL_NAME)) {
                    if (bodyPresent) {
                        throw new SOAPProcessingException("Multiple body elements encountered",
                                                          getSenderFaultCode());
                    }
                    bodyPresent = true;
                    elementType = soapHelper.getBodyClass();
                } else {
                    throw new SOAPProcessingException(localName + " is not supported here.",
                                                      getSenderFaultCode());
                }
            } else if (soapHelper == SOAP11Helper.INSTANCE && bodyPresent) {
                elementType = AxiomElement.class;
            } else {
                throw new SOAPProcessingException("Disallowed element found inside Envelope : {"
                        + namespaceURI + "}" + localName);
            }
        } else if ((elementLevel == 3)
                &&
                ((OMElement)parent).getLocalName().equals(SOAPConstants.HEADER_LOCAL_NAME)) {

            // this is a headerblock
            try {
                elementType = soapHelper.getHeaderBlockClass();
            } catch (SOAPProcessingException e) {
                throw new SOAPProcessingException("Can not create SOAPHeader block",
                                                  getReceiverFaultCode(), e);
            }
        } else if ((elementLevel == 3) &&
                ((OMElement)parent).getLocalName().equals(SOAPConstants.BODY_LOCAL_NAME) &&
                localName.equals(SOAPConstants.BODY_FAULT_LOCAL_NAME) &&
                soapHelper.getEnvelopeURI().equals(namespaceURI)) {
            // this is a SOAP fault
            elementType = soapHelper.getFaultClass();
            processingFault = true;
            if (soapHelper == SOAP12Helper.INSTANCE) {
                builderHelper = new SOAP12BuilderHelper();
            } else if (soapHelper == SOAP11Helper.INSTANCE) {
                builderHelper = new SOAP11BuilderHelper();
            }

        } else if (elementLevel > 3 && processingFault) {
            elementType = builderHelper.handleEvent((OMElement)parent, elementLevel, namespaceURI, localName);
        } else {
            // this is neither of above. Just create an element
            elementType = AxiomElement.class;
        }
        return elementType;
    }

    private String getSenderFaultCode() {
        return soapHelper.getVersion().getSenderFaultCode().getLocalPart();
    }

    private String getReceiverFaultCode() {
        return soapHelper.getVersion().getReceiverFaultCode().getLocalPart();
    }
}
