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

import java.text.ParseException;

import javax.xml.namespace.QName;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.datatype.xsd.XSQNameType;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11FaultCode;

public aspect AxiomSOAP11FaultCodeSupport {
    public final Class<? extends CoreNode> AxiomSOAP11FaultCode.coreGetNodeClass() {
        return AxiomSOAP11FaultCode.class;
    }

    public final SOAPFaultValue AxiomSOAP11FaultCode.getValue() {
        return null;
    }

    public final void AxiomSOAP11FaultCode.setValue(SOAPFaultValue value) {
        throw new UnsupportedOperationException();
    }

    public final SOAPFaultSubCode AxiomSOAP11FaultCode.getSubCode() {
        return null;
    }

    public final void AxiomSOAP11FaultCode.setSubCode(SOAPFaultSubCode subCode) {
        throw new UnsupportedOperationException();
    }

    public final QName AxiomSOAP11FaultCode.getValueAsQName() {
        try {
            return coreGetValue(XSQNameType.INSTANCE, AxiomSemantics.INSTANCE);
        } catch (ParseException ex) {
            throw new SOAPProcessingException("Invalid fault code", ex);
        }
    }

    public final void AxiomSOAP11FaultCode.setValue(QName value) {
        coreSetValue(XSQNameType.INSTANCE, value, AxiomSemantics.INSTANCE);
    }
}
