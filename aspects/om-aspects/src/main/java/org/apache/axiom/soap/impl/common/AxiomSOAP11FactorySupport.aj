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
package org.apache.axiom.soap.impl.common;

import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;

public aspect AxiomSOAP11FactorySupport {
    public final SOAPHelper AxiomSOAP11Factory.getSOAPHelper() {
        return SOAPHelper.SOAP11;
    }

    public final SOAPFaultValue AxiomSOAP11Factory.createSOAPFaultValue(SOAPFaultCode parent) {
        throw new UnsupportedOperationException();
    }

    public final SOAPFaultValue AxiomSOAP11Factory.createSOAPFaultValue(SOAPFaultSubCode parent) {
        throw new UnsupportedOperationException();
    }

    public final SOAPFaultValue AxiomSOAP11Factory.createSOAPFaultValue() {
        throw new UnsupportedOperationException();
    }

    public final SOAPFaultSubCode AxiomSOAP11Factory.createSOAPFaultSubCode(SOAPFaultCode parent) {
        throw new UnsupportedOperationException();
    }

    public final SOAPFaultSubCode AxiomSOAP11Factory.createSOAPFaultSubCode(SOAPFaultSubCode parent) {
        throw new UnsupportedOperationException();
    }

    public final SOAPFaultSubCode AxiomSOAP11Factory.createSOAPFaultSubCode() {
        throw new UnsupportedOperationException();
    }

    public final SOAPFaultText AxiomSOAP11Factory.createSOAPFaultText(SOAPFaultReason parent) {
        throw new UnsupportedOperationException();
    }

    public final SOAPFaultText AxiomSOAP11Factory.createSOAPFaultText() {
        throw new UnsupportedOperationException();
    }

    public final SOAPFaultNode AxiomSOAP11Factory.createSOAPFaultNode(SOAPFault parent) {
        throw new UnsupportedOperationException();
    }

    public final SOAPFaultNode AxiomSOAP11Factory.createSOAPFaultNode() {
        throw new UnsupportedOperationException();
    }
}
