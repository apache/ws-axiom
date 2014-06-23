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

import java.util.Iterator;
import java.util.Locale;

import javax.xml.namespace.QName;

import org.apache.axiom.soap.SOAPFault;
import org.springframework.ws.soap.soap12.Soap12Fault;

public class Soap12FaultImpl extends SoapFaultImpl implements Soap12Fault {
    Soap12FaultImpl(SoapMessageImpl message, SOAPFault axiomNode) {
        super(message, axiomNode);
    }

    @Override
    public Iterator<QName> getFaultSubcodes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void addFaultSubcode(QName subcode) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFaultNode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFaultNode(String uri) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFaultReasonText(Locale locale, String text) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFaultReasonText(Locale locale) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
}
