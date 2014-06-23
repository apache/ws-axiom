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

import java.util.Locale;

import javax.xml.namespace.QName;

import org.apache.axiom.soap.SOAPBody;
import org.springframework.ws.soap.SoapFaultException;
import org.springframework.ws.soap.soap11.Soap11Body;
import org.springframework.ws.soap.soap11.Soap11Fault;

final class Soap11BodyImpl extends SoapBodyImpl implements Soap11Body {
    Soap11BodyImpl(SoapMessageImpl message, SOAPBody axiomNode) {
        super(message, axiomNode);
    }

    public Soap11Fault getFault() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Soap11Fault addMustUnderstandFault(String faultStringOrReason, Locale locale) throws SoapFaultException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Soap11Fault addClientOrSenderFault(String faultStringOrReason, Locale locale) throws SoapFaultException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Soap11Fault addServerOrReceiverFault(String faultStringOrReason, Locale locale) throws SoapFaultException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Soap11Fault addVersionMismatchFault(String faultStringOrReason, Locale locale) throws SoapFaultException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public Soap11Fault addFault(QName faultCode, String faultString, Locale faultStringLocale) throws SoapFaultException {
        // TODO
        throw new UnsupportedOperationException();
    }
}
