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

package org.apache.ws.commons.soap.impl.llom;

import org.apache.ws.commons.om.*;
import org.apache.ws.commons.om.impl.OMOutputImpl;
import org.apache.ws.commons.soap.*;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

/**
 * Class SOAPEnvelopeImpl
 */
public class SOAPEnvelopeImpl extends SOAPElement
        implements SOAPEnvelope, OMConstants {

    /**
     * @param builder
     */
    public SOAPEnvelopeImpl(OMXMLParserWrapper builder, SOAPFactory factory) {
        super(null, SOAPConstants.SOAPENVELOPE_LOCAL_NAME, builder, factory);
        this.factory = factory;
    }

    /**
     * @param ns
     */
    public SOAPEnvelopeImpl(OMNamespace ns, SOAPFactory factory) {
        super(SOAPConstants.SOAPENVELOPE_LOCAL_NAME, ns, factory);
        this.factory = factory;
    }

    /**
     * Returns the <CODE>SOAPHeader</CODE> object for this <CODE>
     * SOAPEnvelope</CODE> object. <P> This SOAPHeader will just be a container
     * for all the headers in the <CODE>OMMessage</CODE> </P>
     *
     * @return the <CODE>SOAPHeader</CODE> object or <CODE> null</CODE> if there
     *         is none
     * @throws org.apache.ws.commons.om.OMException
     *                     if there is a problem obtaining
     *                     the <CODE>SOAPHeader</CODE>
     *                     object
     * @throws OMException
     */
    public SOAPHeader getHeader() throws OMException {
        SOAPHeader header =
                (SOAPHeader) getFirstChildWithName(
                        new QName(SOAPConstants.HEADER_LOCAL_NAME));
        if (builder == null && header == null) {
            inferFactory();
            header = ((SOAPFactory)factory).createSOAPHeader(this);
            addChild(header);
        }
        return header;
    }

    private void inferFactory() {
        if (ns != null) {
            if (SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(ns.getName())) {
                factory = OMAbstractFactory.getSOAP12Factory();
            } else if (SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI.equals(ns.getName())) {
                factory = OMAbstractFactory.getSOAP11Factory();
            }
        }
    }

    public void addChild(OMNode child) {
        if ((child instanceof OMElement) && !(child instanceof SOAPHeader || child instanceof SOAPBody))
        {
            throw new SOAPProcessingException("SOAP Envelope can not have children other than SOAP Header and Body", SOAP12Constants.FAULT_CODE_SENDER);
        } else {
            super.addChild(child);
        }
    }

    /**
     * Returns the <CODE>SOAPBody</CODE> object associated with this
     * <CODE>SOAPEnvelope</CODE> object. <P> This SOAPBody will just be a
     * container for all the BodyElements in the <CODE>OMMessage</CODE> </P>
     *
     * @return the <CODE>SOAPBody</CODE> object for this <CODE>
     *         SOAPEnvelope</CODE> object or <CODE>null</CODE> if there is none
     * @throws org.apache.ws.commons.om.OMException
     *                     if there is a problem obtaining
     *                     the <CODE>SOAPBody</CODE> object
     * @throws OMException
     */
    public SOAPBody getBody() throws OMException {
        //check for the first element
        OMElement element = getFirstElement();
        if (element != null) {
            if (SOAPConstants.BODY_LOCAL_NAME.equals(element.getLocalName())) {
                return (SOAPBody) element;
            } else {      // if not second element SHOULD be the body
                OMNode node = element.getNextOMSibling();
                while (node != null && node.getType() != OMNode.ELEMENT_NODE) {
                    node = node.getNextOMSibling();
                }
                element = (OMElement) node;

                if (node != null &&
                        SOAPConstants.BODY_LOCAL_NAME.equals(element.getLocalName())) {
                    return (SOAPBody) element;
                } else {
                    throw new OMException("SOAPEnvelope must contain a body element which is either first or second child element of the SOAPEnvelope.");
                }
            }
        }
        return null;
    }

    /**
     * Method detach
     *
     * @throws OMException
     */
    public OMNode detach() throws OMException {
        throw new OMException("Root Element can not be detached");
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        // here do nothing as SOAPEnvelope doesn't have a parent !!!
    }

    protected void serialize(OMOutputImpl omOutput, boolean cache) throws XMLStreamException {

        if (!omOutput.isIgnoreXMLDeclaration()) {
            String charSetEncoding = omOutput.getCharSetEncoding();
            String xmlVersion = omOutput.getXmlVersion();
            omOutput.getXmlStreamWriter().writeStartDocument(charSetEncoding == null ?
                    OMConstants.DEFAULT_CHAR_SET_ENCODING : charSetEncoding,
                    xmlVersion == null ? OMConstants.DEFAULT_XML_VERSION : xmlVersion);
        }
        super.serialize(omOutput, cache);
    }
}
