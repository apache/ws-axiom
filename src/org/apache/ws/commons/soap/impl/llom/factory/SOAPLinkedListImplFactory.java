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

package org.apache.ws.commons.soap.impl.llom.factory;

import org.apache.ws.commons.om.OMNamespace;
import org.apache.ws.commons.om.OMXMLParserWrapper;
import org.apache.ws.commons.om.impl.llom.factory.OMLinkedListImplFactory;
import org.apache.ws.commons.soap.SOAPBody;
import org.apache.ws.commons.soap.SOAPEnvelope;
import org.apache.ws.commons.soap.SOAPFactory;
import org.apache.ws.commons.soap.SOAPFault;
import org.apache.ws.commons.soap.SOAPFaultCode;
import org.apache.ws.commons.soap.SOAPFaultDetail;
import org.apache.ws.commons.soap.SOAPFaultNode;
import org.apache.ws.commons.soap.SOAPFaultReason;
import org.apache.ws.commons.soap.SOAPFaultRole;
import org.apache.ws.commons.soap.SOAPFaultSubCode;
import org.apache.ws.commons.soap.SOAPFaultText;
import org.apache.ws.commons.soap.SOAPFaultValue;
import org.apache.ws.commons.soap.SOAPHeader;
import org.apache.ws.commons.soap.SOAPHeaderBlock;
import org.apache.ws.commons.soap.SOAPMessage;
import org.apache.ws.commons.soap.SOAPProcessingException;
import org.apache.ws.commons.soap.impl.llom.SOAPEnvelopeImpl;
import org.apache.ws.commons.soap.impl.llom.SOAPMessageImpl;

/**
 * Most of the methods in this class will throw UnsupportedOperationException, as the specific
 * implementation should implement this.
 * But the main purpose of this class is as follows.
 * When we build a SOAPEnvelope through our builder, we should know the correct SOAPFactory. i.e. either
 * SOAP 1.1 or SOAP 1.2 factory. But for us to determine the correct factory, first we should read the
 * stream and read the envelope start element. So this is a chicken 'n egg situation.
 * The solution for this is to have an intermediate factory, just to create SOAPEnvelope, and this
 * class will server that purpose. Having identified the correct SOAP version, the builder should
 * switch to the correct factory.
 */
public class SOAPLinkedListImplFactory extends
        OMLinkedListImplFactory implements SOAPFactory {

     /**
     * Eran Chinthaka (chinthaka@apache.org)
     */

    public String getSoapVersionURI() {
        throw new UnsupportedOperationException();
    }

    public SOAPMessage createSOAPMessage() {
        return new SOAPMessageImpl();
    }

    public SOAPMessage createSOAPMessage(OMXMLParserWrapper builder) {
        return new SOAPMessageImpl(builder);
    }


    public SOAPMessage createSOAPMessage(SOAPEnvelope envelope, OMXMLParserWrapper parserWrapper) {
        return new SOAPMessageImpl(envelope, parserWrapper);
    }



    public SOAPEnvelope createSOAPEnvelope(OMXMLParserWrapper builder) {
        return new SOAPEnvelopeImpl(builder, this);
    }

    public SOAPEnvelope createSOAPEnvelope() {
        throw new UnsupportedOperationException();
    }

    public SOAPHeader createSOAPHeader(SOAPEnvelope envelope) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPHeader createSOAPHeader(SOAPEnvelope envelope,
                                       OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();

    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                 OMNamespace ns,
                                                 SOAPHeader parent) throws SOAPProcessingException {
        throw new UnsupportedOperationException();

    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName, OMNamespace ns) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                 OMNamespace ns,
                                                 SOAPHeader parent,
                                                 OMXMLParserWrapper builder) throws SOAPProcessingException {
        throw new UnsupportedOperationException();

    }


    public SOAPFault createSOAPFault(SOAPBody parent, Exception e) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFault createSOAPFault(SOAPBody parent) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFault createSOAPFault(SOAPBody parent,
                                     OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();
    }

    public SOAPBody createSOAPBody(SOAPEnvelope envelope) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPBody createSOAPBody(SOAPEnvelope envelope,
                                   OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultCode createSOAPFaultCode(SOAPFault parent) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultCode createSOAPFaultCode(SOAPFault parent,
                                             OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent,
                                               OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();
    }

    //added
    public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    //added
    public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent,
                                               OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();
    }

    //changed
    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    //changed
    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent,
                                                   OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent,
                                                   OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultReason createSOAPFaultReason(SOAPFault parent) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultReason createSOAPFaultReason(SOAPFault parent,
                                                 OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent,
                                             OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultNode createSOAPFaultNode(SOAPFault parent) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultNode createSOAPFaultNode(SOAPFault parent,
                                             OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultRole createSOAPFaultRole(SOAPFault parent) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultRole createSOAPFaultRole(SOAPFault parent,
                                             OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent) throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent,
                                                 OMXMLParserWrapper builder) {
        throw new UnsupportedOperationException();
    }

    public SOAPHeader createSOAPHeader() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFault createSOAPFault() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPBody createSOAPBody() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultCode createSOAPFaultCode() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultValue createSOAPFaultValue() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultSubCode createSOAPFaultSubCode() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultReason createSOAPFaultReason() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultText createSOAPFaultText() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultNode createSOAPFaultNode() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultRole createSOAPFaultRole() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPFaultDetail createSOAPFaultDetail() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method getDefaultEnvelope
     *
     * @return Returns SOAPEnvelope.
     */
    public SOAPEnvelope getDefaultEnvelope() throws SOAPProcessingException {
        throw new UnsupportedOperationException();
    }

    public SOAPEnvelope getDefaultFaultEnvelope() throws SOAPProcessingException {
        SOAPEnvelope defaultEnvelope = getDefaultEnvelope();
        SOAPFault fault = createSOAPFault(defaultEnvelope.getBody());

        SOAPFaultCode faultCode = createSOAPFaultCode(fault);
        createSOAPFaultValue(faultCode);

        SOAPFaultReason reason = createSOAPFaultReason(fault);
        createSOAPFaultText(reason);

        createSOAPFaultDetail(fault);

        return defaultEnvelope;
    }

    public OMNamespace getNamespace() {
        throw new UnsupportedOperationException();
    }
}
