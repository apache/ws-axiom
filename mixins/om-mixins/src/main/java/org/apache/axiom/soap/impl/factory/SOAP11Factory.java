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
package org.apache.axiom.soap.impl.factory;

import org.apache.axiom.om.impl.intf.factory.AxiomNodeFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.intf.soap11.SOAP11Helper;

public class SOAP11Factory extends SOAPFactoryImpl {
    public SOAP11Factory(AxiomNodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override
    public final SOAPHelper getSOAPHelper() {
        return SOAP11Helper.INSTANCE;
    }

    @Override
    public final SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final SOAPFaultValue createSOAPFaultValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final SOAPFaultSubCode createSOAPFaultSubCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final SOAPFaultText createSOAPFaultText(SOAPFaultReason parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final SOAPFaultText createSOAPFaultText() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final SOAPFaultNode createSOAPFaultNode(SOAPFault parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final SOAPFaultNode createSOAPFaultNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final SOAPEnvelope getDefaultFaultEnvelope() {
        SOAPEnvelope defaultEnvelope = getDefaultEnvelope();
        SOAPFault fault = createSOAPFault(defaultEnvelope.getBody());
        createSOAPFaultCode(fault);
        createSOAPFaultReason(fault);
        createSOAPFaultDetail(fault);
        return defaultEnvelope;
    }
}
