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

import javax.xml.namespace.QName;
import javax.xml.transform.Result;

import org.apache.axiom.soap.SOAPHeader;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapHeaderException;

abstract class SoapHeaderImpl extends SoapElementImpl<SOAPHeader> implements SoapHeader {
    SoapHeaderImpl(SOAPHeader axiomNode) {
        super(axiomNode);
    }

    public Result getResult() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public SoapHeaderElement addHeaderElement(QName name) throws SoapHeaderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void removeHeaderElement(QName name) throws SoapHeaderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Iterator<SoapHeaderElement> examineMustUnderstandHeaderElements(String actorOrRole) throws SoapHeaderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Iterator<SoapHeaderElement> examineAllHeaderElements() throws SoapHeaderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Iterator<SoapHeaderElement> examineHeaderElements(QName name) throws SoapHeaderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
}
