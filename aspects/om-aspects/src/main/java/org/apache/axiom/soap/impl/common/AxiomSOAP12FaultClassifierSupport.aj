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

import javax.xml.namespace.QName;

import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultClassifier;

public aspect AxiomSOAP12FaultClassifierSupport {
    private static final Class<?>[] sequence = { SOAPFaultValue.class, SOAPFaultSubCode.class };
    
    public final SOAPFaultValue AxiomSOAP12FaultClassifier.getValue() {
        return (SOAPFaultValue)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_VALUE);
    }

    public final void AxiomSOAP12FaultClassifier.setValue(SOAPFaultValue value) {
        insertChild(sequence, 0, value);
    }
    
    public final SOAPFaultSubCode AxiomSOAP12FaultClassifier.getSubCode() {
        return (SOAPFaultSubCode)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_SUBCODE);
    }
    
    public final void AxiomSOAP12FaultClassifier.setSubCode(SOAPFaultSubCode subCode) {
        insertChild(sequence, 1, subCode);
    }

    public final QName AxiomSOAP12FaultClassifier.getValueAsQName() {
        SOAPFaultValue value = getValue();
        return value == null ? null : value.getTextAsQName();
    }
    
    public final void AxiomSOAP12FaultClassifier.setValue(QName value) {
        SOAPFaultValue valueElement = getValue();
        if (valueElement == null) {
            valueElement = ((SOAP12Factory)getOMFactory()).internalCreateSOAPFaultValue(this, null);
        }
        valueElement.setText(value);
    }
}
