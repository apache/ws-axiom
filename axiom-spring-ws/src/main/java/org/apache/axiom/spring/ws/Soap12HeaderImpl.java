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

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.soap.SOAPHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapHeaderException;
import org.springframework.ws.soap.soap12.Soap12Header;

final class Soap12HeaderImpl extends SoapHeaderImpl implements Soap12Header {
    Soap12HeaderImpl(SOAPHeader axiomNode) {
        super(axiomNode);
    }

    public SoapHeaderElement addNotUnderstoodHeaderElement(QName headerName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public SoapHeaderElement addUpgradeHeaderElement(String[] supportedSoapUris) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Iterator<SoapHeaderElement> examineHeaderElementsToProcess(String[] roles, boolean isUltimateReceiver) throws SoapHeaderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
}
