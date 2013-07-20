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

import javax.xml.transform.Result;

import org.apache.axiom.soap.SOAPHeaderBlock;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapHeaderException;

final class SoapHeaderElementImpl extends SoapElementImpl<SOAPHeaderBlock> implements SoapHeaderElement {
    SoapHeaderElementImpl(SOAPHeaderBlock axiomNode) {
        super(axiomNode);
    }

    public String getActorOrRole() throws SoapHeaderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void setActorOrRole(String actorOrRole) throws SoapHeaderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean getMustUnderstand() throws SoapHeaderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void setMustUnderstand(boolean mustUnderstand) throws SoapHeaderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Result getResult() throws SoapHeaderException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public String getText() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void setText(String content) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
}
