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

import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPVersion;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapBodyException;
import org.springframework.ws.soap.SoapEnvelope;
import org.springframework.ws.soap.SoapEnvelopeException;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderException;

final class SoapEnvelopeImpl extends SoapElementImpl<SOAPEnvelope> implements SoapEnvelope {
    private SoapHeaderImpl header;
    private SoapBodyImpl body;
    
    SoapEnvelopeImpl(SOAPEnvelope axiomNode) {
        super(axiomNode);
    }

    public SoapHeader getHeader() throws SoapHeaderException {
        SOAPHeader axiomHeader = axiomNode.getHeader();
        if (header == null || header.axiomNode != axiomHeader) {
            if (axiomHeader == null) {
                header = null;
            } else {
                SOAPVersion soapVersion = ((SOAPFactory)axiomHeader.getOMFactory()).getSOAPVersion();
                if (soapVersion == SOAP11Version.getSingleton()) {
                    header = new Soap11HeaderImpl(axiomHeader);
                } else if (soapVersion == SOAP12Version.getSingleton()) {
                    header = new Soap12HeaderImpl(axiomHeader);
                } else {
                    throw new SoapEnvelopeException("Unrecognized SOAP version");
                }
            }
        }
        return header;
    }

    public SoapBody getBody() throws SoapBodyException {
        SOAPBody axiomBody = axiomNode.getBody();
        if (body == null || body.axiomNode != axiomBody) {
            body = new SoapBodyImpl(axiomBody);
        }
        return body;
    }
}
