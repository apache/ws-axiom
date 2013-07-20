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
package org.apache.axiom.spring.ws;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.springframework.ws.mime.Attachment;
import org.springframework.ws.mime.AttachmentException;
import org.springframework.ws.soap.AbstractSoapMessage;
import org.springframework.ws.soap.SoapEnvelope;
import org.springframework.ws.soap.SoapEnvelopeException;
import org.springframework.ws.soap.SoapMessageException;
import org.springframework.ws.transport.TransportConstants;
import org.springframework.ws.transport.TransportOutputStream;
import org.w3c.dom.Document;

final class SoapMessageImpl extends AbstractSoapMessage {
    private final SOAPMessage axiomMessage;
    private SoapEnvelopeImpl envelope;
    
    SoapMessageImpl(SOAPMessage axiomMessage) {
        this.axiomMessage = axiomMessage;
    }

    public SoapEnvelope getEnvelope() throws SoapEnvelopeException {
        SOAPEnvelope axiomEnvelope = axiomMessage.getSOAPEnvelope();
        if (envelope == null || envelope.axiomNode != axiomEnvelope) {
            envelope = new SoapEnvelopeImpl(axiomEnvelope);
        }
        return envelope;
    }

    public String getSoapAction() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void setSoapAction(String soapAction) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Document getDocument() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void setDocument(Document document) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean isXopPackage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean convertToXopPackage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Attachment getAttachment(String contentId) throws AttachmentException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Iterator<Attachment> getAttachments() throws AttachmentException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Attachment addAttachment(String contentId, DataHandler dataHandler) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        OMOutputFormat outputFormat = new OMOutputFormat();
        outputFormat.setSOAP11(((SOAPFactory)axiomMessage.getOMFactory()).getSOAPVersion() == SOAP11Version.getSingleton());
        if (outputStream instanceof TransportOutputStream) {
            TransportOutputStream transportOutputStream = (TransportOutputStream)outputStream;
            // TODO: ensure that charset is specified in content type
            // TODO: omit XML declaration
            transportOutputStream.addHeader(TransportConstants.HEADER_CONTENT_TYPE, outputFormat.getContentType());
        }
        try {
            axiomMessage.serializeAndConsume(outputStream);
        } catch (XMLStreamException ex) {
            throw new SoapMessageSerializationException("Message serialization failure", ex);
        }
    }
}
