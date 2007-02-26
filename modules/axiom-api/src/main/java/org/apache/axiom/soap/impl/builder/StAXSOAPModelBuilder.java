/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLStreamReader;

/**
 * Class StAXSOAPModelBuilder
 */
public class StAXSOAPModelBuilder extends StAXOMBuilder {

    SOAPMessage soapMessage;
    /**
     * Field envelope
     */
    private SOAPEnvelope envelope;
    private OMNamespace envelopeNamespace;


    private SOAPFactory soapFactory;

    /**
     * Field headerPresent
     */
    private boolean headerPresent = false;

    /**
     * Field bodyPresent
     */
    private boolean bodyPresent = false;

    /**
     * Field log
     */
    private static final Log log = LogFactory.getLog(StAXSOAPModelBuilder.class);

    /**
     * element level 1 = envelope level element level 2 = Header or Body level
     * element level 3 = HeaderElement or BodyElement level
     */
    protected int elementLevel = 0;

    private boolean processingFault = false;


    //added
    /* This is used to indicate whether detail element is processing in soap 1.2 builderhelper
    */
    private boolean processingDetailElements = false;

    private SOAPBuilderHelper builderHelper;
    private String senderfaultCode;
    private String receiverfaultCode;

    private String charEncoding = null;
    private String parserVersion = null;
    private static final boolean isDebugEnabled = log.isDebugEnabled();

    /**
     * Constructor StAXSOAPModelBuilder
     * soapVersion parameter is to give the soap version from the transport. For example, in HTTP case
     * you can identify the version of the soap message u have recd by looking at the HTTP headers.
     * It is used to check whether the actual soap message contained is of that version.
     * If one is creates the builder from the transport, then can just pass null for version.
     *
     * @param parser
     * @param soapVersion parameter is to give the soap version for the transport.
     */
    public StAXSOAPModelBuilder(XMLStreamReader parser, String soapVersion) {
        super(parser);
        charEncoding = parser.getCharacterEncodingScheme();
        parserVersion = parser.getVersion();
        identifySOAPVersion(soapVersion);
    }
    
    /**
	 * Constructor StAXSOAPModelBuilder Users of this constructor needs to
	 * externally take care validating the transport level soap version with the
	 * Envelope version.
	 * 
	 * @param parser
	 * @param soapVersion
	 *            parameter is to give the soap version for the transport.
	 */
    public StAXSOAPModelBuilder(XMLStreamReader parser) {
        super(parser);
        charEncoding = parser.getCharacterEncodingScheme();
        parserVersion = parser.getVersion();
        SOAPEnvelope soapEnvelope = getSOAPEnvelope();
        envelopeNamespace = soapEnvelope.getNamespace();
    }

    /**
     * @param parser
     * @param factory
     * @param soapVersion parameter is to give the soap version from the transport. For example, in
     *                    HTTP case you can identify the version of the soap message u have recd by looking at
     *                    the HTTP headers. It is used to check whether the actual soap message
     *                    contained is of that version.If one is creates the builder from the transport,
     *                    then can just pass null for version.
     */
    public StAXSOAPModelBuilder(XMLStreamReader parser, SOAPFactory factory, String soapVersion) {
        super(factory, parser);
        soapFactory = factory;
        charEncoding = parser.getCharacterEncodingScheme();
        parserVersion = parser.getVersion();
        identifySOAPVersion(soapVersion);
    }

    /**
     * @param soapVersionURIFromTransport
     */
    protected void identifySOAPVersion(String soapVersionURIFromTransport) {

        SOAPEnvelope soapEnvelope = getSOAPEnvelope();
        if (soapEnvelope == null) {
            throw new SOAPProcessingException("SOAP Message does not contain an Envelope",
                    SOAPConstants.FAULT_CODE_VERSION_MISMATCH);
        }

        envelopeNamespace = soapEnvelope.getNamespace();

        if (soapVersionURIFromTransport != null) {
            String namespaceName = envelopeNamespace.getNamespaceURI();
            if (!(soapVersionURIFromTransport.equals(namespaceName))){
                throw new SOAPProcessingException("Transport level information does not match with SOAP" +
                        " Message namespace URI", envelopeNamespace.getPrefix() + ":" + SOAPConstants.FAULT_CODE_VERSION_MISMATCH);
            }
        }

    }

    /**
     * Method getSOAPEnvelope.
     *
     * @return Returns SOAPEnvelope.
     * @throws OMException
     */
    public SOAPEnvelope getSOAPEnvelope() throws OMException {
        while ((envelope == null) && !done) {
            next();
        }
        return envelope;
    }

    /**
     * Method createOMElement.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    protected OMNode createOMElement() throws OMException {
        elementLevel++;
        OMElement node;
        String elementName = parser.getLocalName();
        if (lastNode == null) {
            node = constructNode(null, elementName, true);
            setSOAPEnvelope(node);
        } else if (lastNode.isComplete()) {
            node = constructNode((OMElement) lastNode.getParent(),
                    elementName,
                    false);
            ((OMNodeEx) lastNode).setNextOMSibling(node);
            ((OMNodeEx) node).setPreviousOMSibling(lastNode);
        } else {
            OMContainerEx e = (OMContainerEx) lastNode;
            node = constructNode((OMElement) lastNode, elementName, false);
            e.setFirstChild(node);
        }

        if(isDebugEnabled) {
            log.debug("Build the OMElelment " + node.getLocalName() +
                    "By the StaxSOAPModelBuilder");
        }
        return node;
    }

    protected void setSOAPEnvelope(OMElement node) {
        soapMessage.setSOAPEnvelope((SOAPEnvelope) node);
        soapMessage.setXMLVersion(parserVersion);
        soapMessage.setCharsetEncoding(charEncoding);
    }

    /**
     * Method constructNode
     *
     * @param parent
     * @param elementName
     * @param isEnvelope
     */
    protected OMElement constructNode(OMElement parent, String elementName,
                                      boolean isEnvelope) {
        OMElement element;
        if (parent == null) {

            // Now I've found a SOAP Envelope, now create SOAPDocument and SOAPEnvelope here.

            if (!elementName.equalsIgnoreCase(SOAPConstants.SOAPENVELOPE_LOCAL_NAME)) {
                throw new SOAPProcessingException("First Element must contain the local name, "
                        + SOAPConstants.SOAPENVELOPE_LOCAL_NAME, SOAPConstants.FAULT_CODE_VERSION_MISMATCH);
            }

            // determine SOAP version and from that determine a proper factory here.
            if (soapFactory == null) {
                String namespaceURI = this.parser.getNamespaceURI();
                if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(namespaceURI)) {
                    soapFactory = OMAbstractFactory.getSOAP12Factory();
                    if(isDebugEnabled) {
                        log.debug("Starting to process SOAP 1.2 message");
                    }
                } else if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(namespaceURI)) {
                    soapFactory = OMAbstractFactory.getSOAP11Factory();
                    if(isDebugEnabled) {
                        log.debug("Starting to process SOAP 1.1 message");
                    }
                } else {
                    throw new SOAPProcessingException("Only SOAP 1.1 or SOAP 1.2 messages are supported in the" +
                            " system", SOAPConstants.FAULT_CODE_VERSION_MISMATCH);
                }
            }

            // create a SOAPMessage to hold the SOAP envelope and assign the SOAP envelope in that.
            soapMessage = soapFactory.createSOAPMessage(this);
            this.document = soapMessage;
            if (charEncoding != null) {
                document.setCharsetEncoding(charEncoding);
            }

            envelope = soapFactory.createSOAPEnvelope(this);
            element = envelope;
            processNamespaceData(element, true);
            // fill in the attributes
            processAttributes(element);

        } else if (elementLevel == 2) {

            // this is either a header or a body
            if (elementName.equals(SOAPConstants.HEADER_LOCAL_NAME)) {
                if (headerPresent) {
                    throw new SOAPProcessingException("Multiple headers encountered!", getSenderFaultCode());
                }
                if (bodyPresent) {
                    throw new SOAPProcessingException("Header Body wrong order!", getSenderFaultCode());
                }
                headerPresent = true;
                element =
                        soapFactory.createSOAPHeader((SOAPEnvelope) parent,
                                this);

                processNamespaceData(element, true);
                processAttributes(element);

            } else if (elementName.equals(SOAPConstants.BODY_LOCAL_NAME)) {
                if (bodyPresent) {
                    throw new SOAPProcessingException("Multiple body elements encountered", getSenderFaultCode());
                }
                bodyPresent = true;
                element =
                        soapFactory.createSOAPBody((SOAPEnvelope) parent,
                                this);

                processNamespaceData(element, true);
                processAttributes(element);

            } else {
                throw new SOAPProcessingException(elementName
                        +
                        " is not supported here. Envelope can not have elements other than Header and Body.", getSenderFaultCode());
            }
        } else if ((elementLevel == 3)
                &&
                parent.getLocalName().equalsIgnoreCase(SOAPConstants.HEADER_LOCAL_NAME)) {

            // this is a headerblock
            try {
                element =
                        soapFactory.createSOAPHeaderBlock(elementName, null,
                                (SOAPHeader) parent, this);
            } catch (SOAPProcessingException e) {
                throw new SOAPProcessingException("Can not create SOAPHeader block", getReceiverFaultCode(), e);
            }
            processNamespaceData(element, false);
            processAttributes(element);

        } else if ((elementLevel == 3) &&
                parent.getLocalName().equalsIgnoreCase(SOAPConstants.BODY_LOCAL_NAME) &&
                elementName.equalsIgnoreCase(SOAPConstants.BODY_FAULT_LOCAL_NAME)) {

            // this is a headerblock
            element = soapFactory.createSOAPFault((SOAPBody) parent, this);
            processNamespaceData(element, false);
            processAttributes(element);


            processingFault = true;
            if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(envelopeNamespace.getNamespaceURI()))
            {
                builderHelper = new SOAP12BuilderHelper(this);
            } else
            if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(envelopeNamespace.getNamespaceURI()))
            {
                builderHelper = new SOAP11BuilderHelper(this);
            }

        } else if (elementLevel > 3 && processingFault) {
            element = builderHelper.handleEvent(parser, parent, elementLevel);
        } else {

            // this is neither of above. Just create an element
            element = soapFactory.createOMElement(elementName, null,
                    parent, this);
            processNamespaceData(element, false);
            processAttributes(element);

        }
        return element;
    }

    private String getSenderFaultCode() {
        if (senderfaultCode == null) {
            senderfaultCode = SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(envelopeNamespace.getNamespaceURI()) ? SOAP12Constants.FAULT_CODE_SENDER : SOAP11Constants.FAULT_CODE_SENDER;
        }
        return senderfaultCode;
    }

    private String getReceiverFaultCode() {
        if (receiverfaultCode == null) {
            receiverfaultCode = SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(envelopeNamespace.getNamespaceURI()) ? SOAP12Constants.FAULT_CODE_RECEIVER : SOAP11Constants.FAULT_CODE_RECEIVER;
        }
        return receiverfaultCode;
    }

    public void endElement() {
        if (lastNode.isComplete()) {
            OMElement parent = (OMElement) lastNode.getParent();
            ((OMNodeEx) parent).setComplete(true);
            lastNode = parent;
        } else {
            OMNode e = lastNode;
            ((OMNodeEx) e).setComplete(true);
        }
        elementLevel--;
    }

    /**
     * Method createDTD.
     * Overriding the default behaviour as a SOAPMessage should not have a DTD.
     */
    protected OMNode createDTD() throws OMException {
        throw new OMException("SOAP message MUST NOT contain a Document Type Declaration(DTD)");
    }

    /**
     * Method createPI.
     * Overriding the default behaviour as a SOAP Message should not have a PI.
     */
    protected OMNode createPI() throws OMException {
        throw new OMException("SOAP message MUST NOT contain Processing Instructions(PI)");
    }

    /**
     * Method getDocumentElement.
     *
     * @return Returns OMElement.
     */
    public OMElement getDocumentElement() {
        return envelope != null ? envelope : getSOAPEnvelope();
    }

    /**
     * Method processNamespaceData.
     *
     * @param node
     * @param isSOAPElement
     */
    protected void processNamespaceData(OMElement node, boolean isSOAPElement) {

        super.processNamespaceData(node);

        if (isSOAPElement) {
            if (node.getNamespace() != null &&
                    !node.getNamespace().getNamespaceURI().equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI) &&
                    !node.getNamespace().getNamespaceURI().equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI))
            {
                throw new SOAPProcessingException("invalid SOAP namespace URI. " +
                        "Only " + SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI +
                        " and " + SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI +
                        " are supported.", SOAP12Constants.FAULT_CODE_SENDER);
            }
        }

    }

/*these three methods to set and check detail element processing or mandatory fault element are present
*/

    public OMNamespace getEnvelopeNamespace() {
        return envelopeNamespace;
    }

    public boolean isProcessingDetailElements() {
        return processingDetailElements;
    }

    public void setProcessingDetailElements(boolean value) {
        processingDetailElements = value;
    }

    public SOAPMessage getSoapMessage() {
        return soapMessage;
    }

    public OMDocument getDocument() {
        return this.soapMessage;
    }

    /**
     * @return Returns the soapFactory.
     */
    protected SOAPFactory getSoapFactory() {
        return soapFactory;
    }

}
