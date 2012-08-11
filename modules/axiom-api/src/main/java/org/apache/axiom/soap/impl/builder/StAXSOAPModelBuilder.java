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

package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.CustomBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLStreamReader;

/**
 * StAX based builder that produces a SOAP infoset model.
 * It builds SOAP specific objects such as {@link SOAPEnvelope}, {@link SOAPHeader},
 * {@link org.apache.axiom.soap.SOAPHeaderBlock} and {@link SOAPBody}.
 * <p>
 * This builder offers two different ways to handle SOAP versions:
 * <ul>
 *   <li>Either the SOAP version is specified when the builder is constructed. If the specified
 *       version doesn't match the envelope namespace of the actual message, an exception is
 *       triggered. This approach should be used when the SOAP version is known from information
 *       other than the content of the message. For example, in the HTTP case it is possible
 *       to identify the SOAP version based on the <tt>Content-Type</tt> header.</li>
 *   <li>If no SOAP version is specified, the builder will automatically detect it from the
 *       envelope namespace. It will then build the object model using the
 *       {@link SOAPFactory} implementation corresponding to that SOAP version.</li>
 * </ul>
 * In both cases, the {@link SOAPFactory} is retrieved either from the {@link OMMetaFactory}
 * specified when the builder is constructed, or if none is specified, from the default
 * meta factory returned by {@link OMAbstractFactory#getMetaFactory()}.
 */
public class StAXSOAPModelBuilder extends StAXOMBuilder implements SOAPModelBuilder {
    /**
     * The meta factory used to get the SOAPFactory implementation when SOAP version detection
     * is enabled. This is only used if <code>soapFactory</code> is <code>null</code>.
     */
    private OMMetaFactory metaFactory;

    private SOAPFactoryEx soapFactory;

    /** Field headerPresent */
    private boolean headerPresent = false;

    /** Field bodyPresent */
    private boolean bodyPresent = false;

    /** Field log */
    private static final Log log = LogFactory.getLog(StAXSOAPModelBuilder.class);

    private boolean processingFault = false;

    private SOAPBuilderHelper builderHelper;

    /**
     * Constructor.
     *
     * @param parser the parser to read the SOAP message from
     * @param soapVersion the namespace URI corresponding to the expected SOAP version
     *                    of the message
     */
    public StAXSOAPModelBuilder(XMLStreamReader parser, String soapVersion) {
        this(OMAbstractFactory.getMetaFactory(), parser, soapVersion);
    }

    /**
     * Constructor.
     *
     * @param metaFactory the meta factory used to get the appropriate {@link SOAPFactory}
     *                    implementation
     * @param parser the parser to read the SOAP message from
     * @param soapVersion the namespace URI corresponding to the expected SOAP version
     *                    of the message
     */
    public StAXSOAPModelBuilder(OMMetaFactory metaFactory, XMLStreamReader parser,
            String soapVersion) {
        super(metaFactory.getOMFactory(), parser);
        this.metaFactory = metaFactory;
        identifySOAPVersion(soapVersion);
    }
    
    /**
     * Constructor.
     * 
     * @param parser the parser to read the SOAP message from
     */
    public StAXSOAPModelBuilder(XMLStreamReader parser) {
        this(OMAbstractFactory.getMetaFactory(), parser);
    }
    
    /**
     * Constructor.
     * 
     * @param metaFactory the meta factory used to get the appropriate {@link SOAPFactory}
     *                    implementation
     * @param parser the parser to read the SOAP message from
     */
    public StAXSOAPModelBuilder(OMMetaFactory metaFactory, XMLStreamReader parser) {
        super(metaFactory.getOMFactory(), parser);
        this.metaFactory = metaFactory;
    }

    /**
     * Constructor.
     * 
     * @param parser the parser to read the SOAP message from
     * @param factory the SOAP factory to use
     * @param soapVersion the namespace URI corresponding to the expected SOAP version
     *                    of the message
     */
    public StAXSOAPModelBuilder(XMLStreamReader parser, SOAPFactory factory, String soapVersion) {
        super(factory, parser);
        soapFactory = (SOAPFactoryEx)factory;
        identifySOAPVersion(soapVersion);
    }

    /** @param soapVersionURIFromTransport  */
    protected void identifySOAPVersion(String soapVersionURIFromTransport) {

        SOAPEnvelope soapEnvelope = getSOAPEnvelope();
        if (soapEnvelope == null) {
            throw new SOAPProcessingException("SOAP Message does not contain an Envelope",
                                              SOAPConstants.FAULT_CODE_VERSION_MISMATCH);
        }

        OMNamespace envelopeNamespace = soapEnvelope.getNamespace();

        if (soapVersionURIFromTransport != null) {
            String namespaceName = envelopeNamespace.getNamespaceURI();
            if (!(soapVersionURIFromTransport.equals(namespaceName))) {
                throw new SOAPProcessingException(
                        "Transport level information does not match with SOAP" +
                                " Message namespace URI", envelopeNamespace.getPrefix() + ":" +
                        SOAPConstants.FAULT_CODE_VERSION_MISMATCH);
            }
        }

    }

    public SOAPEnvelope getSOAPEnvelope() throws OMException {
        return (SOAPEnvelope)getDocumentElement();
    }

    protected OMNode createNextOMElement() {
        OMNode newElement = null;
        
        
        if (elementLevel == 3 && 
            customBuilderForPayload != null) {
            
            if (target instanceof SOAPBody) {
                newElement = createWithCustomBuilder(customBuilderForPayload,  soapFactory);
            }
        } 
        if (newElement == null && customBuilders != null && 
                elementLevel <= maxDepthForCustomBuilders) {
            String namespace = parser.getNamespaceURI();
            String localPart = parser.getLocalName();
            CustomBuilder customBuilder = getCustomBuilder(namespace, localPart);
            if (customBuilder != null) {
                newElement = createWithCustomBuilder(customBuilder, soapFactory);
            }
        }
        if (newElement == null) {
            newElement = createOMElement();
        } else {
            elementLevel--; // Decrease level since custom builder read the end element event
        }
        return newElement;
    }
    
    /**
     * Method createOMElement.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    protected OMNode createOMElement() throws OMException {
        OMElement node = constructNode(target, parser.getLocalName());
        if (log.isDebugEnabled()) {
            log.debug("Build the OMElement " + node.getLocalName() +
                    " by the StaxSOAPModelBuilder");
        }
        processNamespaceData(node);
        processAttributes(node);
        return node;
    }

    /**
     * Method constructNode
     *
     * @param parent
     * @param elementName
     */
    protected OMElement constructNode(OMContainer parent, String elementName) {
        OMElement element;
        if (elementLevel == 1) {

            // Now I've found a SOAP Envelope, now create SOAPEnvelope here.

            if (!elementName.equals(SOAPConstants.SOAPENVELOPE_LOCAL_NAME)) {
                throw new SOAPProcessingException("First Element must contain the local name, "
                        + SOAPConstants.SOAPENVELOPE_LOCAL_NAME + " , but found " + elementName,
                        SOAPConstants.FAULT_CODE_SENDER);
            }

            // determine SOAP version and from that determine a proper factory here.
            String namespaceURI = this.parser.getNamespaceURI();
            if (soapFactory == null) {
                if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(namespaceURI)) {
                    soapFactory = (SOAPFactoryEx)metaFactory.getSOAP12Factory();
                    log.debug("Starting to process SOAP 1.2 message");
                } else if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(namespaceURI)) {
                    soapFactory = (SOAPFactoryEx)metaFactory.getSOAP11Factory();
                    log.debug("Starting to process SOAP 1.1 message");
                } else {
                    throw new SOAPProcessingException(
                            "Only SOAP 1.1 or SOAP 1.2 messages are supported in the" +
                                    " system", SOAPConstants.FAULT_CODE_VERSION_MISMATCH);
                }
            } else if (!soapFactory.getSoapVersionURI().equals(namespaceURI)) {
                throw new SOAPProcessingException("Invalid SOAP namespace URI. " +
                        "Expected " + soapFactory.getSoapVersionURI(), SOAP12Constants.FAULT_CODE_SENDER);
            }

            element = soapFactory.createSOAPEnvelope((SOAPMessage)parent, this);
        } else if (elementLevel == 2) {
            String elementNS = parser.getNamespaceURI();

            if (soapFactory.getSoapVersionURI().equals(elementNS)) {
                // this is either a header or a body
                if (elementName.equals(SOAPConstants.HEADER_LOCAL_NAME)) {
                    if (headerPresent) {
                        throw new SOAPProcessingException("Multiple headers encountered!",
                                                          getSenderFaultCode());
                    }
                    if (bodyPresent) {
                        throw new SOAPProcessingException("Header Body wrong order!",
                                                          getSenderFaultCode());
                    }
                    headerPresent = true;
                    element =
                            soapFactory.createSOAPHeader((SOAPEnvelope) parent,
                                                         this);
                } else if (elementName.equals(SOAPConstants.BODY_LOCAL_NAME)) {
                    if (bodyPresent) {
                        throw new SOAPProcessingException("Multiple body elements encountered",
                                                          getSenderFaultCode());
                    }
                    bodyPresent = true;
                    element =
                            soapFactory.createSOAPBody((SOAPEnvelope) parent,
                                                       this);
                } else {
                    throw new SOAPProcessingException(elementName + " is not supported here.",
                                                      getSenderFaultCode());
                }
            } else if (soapFactory.getSOAPVersion() == SOAP11Version.getSingleton() && bodyPresent) {
                element = omfactory.createOMElement(parser.getLocalName(), parent, this);
            } else {
                throw new SOAPProcessingException("Disallowed element found inside Envelope : {"
                        + elementNS + "}" + elementName);
            }
        } else if ((elementLevel == 3)
                &&
                ((OMElement)parent).getLocalName().equals(SOAPConstants.HEADER_LOCAL_NAME)) {

            // this is a headerblock
            try {
                element =
                        soapFactory.createSOAPHeaderBlock(elementName, (SOAPHeader) parent,
                                                          this);
            } catch (SOAPProcessingException e) {
                throw new SOAPProcessingException("Can not create SOAPHeader block",
                                                  getReceiverFaultCode(), e);
            }
        } else if ((elementLevel == 3) &&
                ((OMElement)parent).getLocalName().equals(SOAPConstants.BODY_LOCAL_NAME) &&
                elementName.equals(SOAPConstants.BODY_FAULT_LOCAL_NAME) &&
                soapFactory.getSoapVersionURI().equals(parser.getNamespaceURI())) {
            // this is a SOAP fault
            element = soapFactory.createSOAPFault((SOAPBody) parent, this);
            processingFault = true;
            if (soapFactory.getSOAPVersion() == SOAP12Version.getSingleton()) {
                builderHelper = new SOAP12BuilderHelper(this, soapFactory);
            } else if (soapFactory.getSOAPVersion() == SOAP11Version.getSingleton()) {
                builderHelper = new SOAP11BuilderHelper(this, soapFactory);
            }

        } else if (elementLevel > 3 && processingFault) {
            element = builderHelper.handleEvent(parser, (OMElement)parent, elementLevel);
        } else {
            // this is neither of above. Just create an element
            element = soapFactory.createOMElement(elementName, parent,
                                                  this);
        }
        return element;
    }

    private String getSenderFaultCode() {
        return getSOAPEnvelope().getVersion().getSenderFaultCode().getLocalPart();
    }

    private String getReceiverFaultCode() {
        return getSOAPEnvelope().getVersion().getReceiverFaultCode().getLocalPart();
    }

    protected OMDocument createDocument() {
        if (soapFactory != null) {
            return soapFactory.createSOAPMessage(this);
        } else {
            return ((OMMetaFactoryEx)metaFactory).createSOAPMessage(this);
        }
    }

    /** Method createDTD. Overriding the default behaviour as a SOAPMessage should not have a DTD. */
    protected OMNode createDTD() throws OMException {
        throw new SOAPProcessingException("SOAP message MUST NOT contain a Document Type Declaration(DTD)");
    }

    /** Method createPI. Overriding the default behaviour as a SOAP Message should not have a PI. */
    protected OMNode createPI() throws OMException {
        throw new SOAPProcessingException("SOAP message MUST NOT contain Processing Instructions(PI)");
    }

    protected OMNode createEntityReference() {
        throw new SOAPProcessingException("A SOAP message cannot contain entity references because it must not have a DTD");
    }

/*these three methods to set and check detail element processing or mandatory fault element are present
*/

    public OMNamespace getEnvelopeNamespace() {
        return getSOAPEnvelope().getNamespace();
    }

    public SOAPMessage getSoapMessage() {
        return (SOAPMessage)getDocument();
    }

    public SOAPFactory getSOAPFactory() {
        if (soapFactory == null) {
            getSOAPEnvelope();
        }
        return soapFactory;
    }
}
