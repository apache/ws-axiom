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

import java.util.Locale;

import javax.xml.transform.Result;
import javax.xml.transform.Source;

import org.apache.axiom.soap.SOAPBody;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultException;

final class SoapBodyImpl extends SoapElementImpl<SOAPBody> implements SoapBody {
    SoapBodyImpl(SOAPBody axiomNode) {
        super(axiomNode);
    }

    public Source getPayloadSource() {
        return axiomNode.getFirstElement().getSAXSource(false);
    }

    public Result getPayloadResult() {
        // TODO: clear content first?
        return axiomNode.getSAXResult();
    }

    public SoapFault addMustUnderstandFault(String faultStringOrReason, Locale locale) throws SoapFaultException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public SoapFault addClientOrSenderFault(String faultStringOrReason, Locale locale) throws SoapFaultException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public SoapFault addServerOrReceiverFault(String faultStringOrReason, Locale locale) throws SoapFaultException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public SoapFault addVersionMismatchFault(String faultStringOrReason, Locale locale) throws SoapFaultException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean hasFault() {
        return axiomNode.hasFault();
    }

    public SoapFault getFault() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
}
