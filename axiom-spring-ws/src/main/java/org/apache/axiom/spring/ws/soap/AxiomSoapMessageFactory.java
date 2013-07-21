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
package org.apache.axiom.spring.ws.soap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.SoapMessageCreationException;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.transport.TransportConstants;
import org.springframework.ws.transport.TransportInputStream;

public class AxiomSoapMessageFactory implements SoapMessageFactory, InitializingBean {
    private SoapVersion soapVersion = SoapVersion.SOAP_11;
    private OMMetaFactory metaFactory;
    private SOAPFactory soapFactory;
    
    public void setSoapVersion(SoapVersion soapVersion) {
        this.soapVersion = soapVersion;
    }

    public void afterPropertiesSet() throws Exception {
        // TODO: make the implementation configurable
        metaFactory = OMAbstractFactory.getMetaFactory();
        if (soapVersion == SoapVersion.SOAP_11) {
            soapFactory = metaFactory.getSOAP11Factory();
        } else if (soapVersion == SoapVersion.SOAP_12) {
            soapFactory = metaFactory.getSOAP12Factory();
        } else {
            // TODO: proper exception type?
            throw new RuntimeException("Unrecognized SOAP version: " + soapVersion);
        }
    }

    public SoapMessage createWebServiceMessage() {
        SOAPMessage axiomMessage = soapFactory.createSOAPMessage();
        axiomMessage.setSOAPEnvelope(soapFactory.getDefaultEnvelope());
        return new SoapMessageImpl(axiomMessage);
    }

    public SoapMessage createWebServiceMessage(InputStream inputStream) throws IOException {
        TransportInputStream transportInputStream = (TransportInputStream)inputStream;
        Iterator<String> it = transportInputStream.getHeaders(TransportConstants.HEADER_CONTENT_TYPE);
        ContentType contentType;
        if (it.hasNext()) {
            // TODO: this depends on javamail!
            try {
                contentType = new ContentType(it.next());
            } catch (ParseException ex) {
                throw new SoapMessageCreationException("Failed to parse Content-Type header", ex);
            }
        } else {
            throw new SoapMessageCreationException("No Content-Type header found");
        }
        SOAPModelBuilder builder = OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory, inputStream, contentType.getParameter("charset"));
        // TODO: should SOAPModelBuilder have a getSOAPMessage() method?
        // TODO: need to check that the SOAP version matches the content type
        return new SoapMessageImpl((SOAPMessage)builder.getDocument());
    }
}
