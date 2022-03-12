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
package org.apache.axiom.soap.impl.mixin;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.intf.Sequence;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.impl.factory.SOAP12Factory;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12FaultClassifier;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin
public abstract class AxiomSOAP12FaultClassifierMixin implements AxiomSOAP12FaultClassifier {
    private static final Sequence sequence =
            new Sequence(SOAPFaultValue.class, SOAPFaultSubCode.class);

    public final boolean isChildElementAllowed(OMElement child) {
        return child instanceof SOAPFaultValue || child instanceof SOAPFaultSubCode;
    }

    @Override
    public final SOAPFaultValue getValue() {
        return (SOAPFaultValue) getFirstChildWithName(SOAP12Constants.QNAME_FAULT_VALUE);
    }

    @Override
    public final void setValue(SOAPFaultValue value) {
        insertChild(sequence, 0, value, true);
    }

    @Override
    public final SOAPFaultSubCode getSubCode() {
        return (SOAPFaultSubCode) getFirstChildWithName(SOAP12Constants.QNAME_FAULT_SUBCODE);
    }

    @Override
    public final void setSubCode(SOAPFaultSubCode subCode) {
        insertChild(sequence, 1, subCode, true);
    }

    @Override
    public final QName getValueAsQName() {
        SOAPFaultValue value = getValue();
        return value == null ? null : value.getTextAsQName();
    }

    @Override
    public final void setValue(QName value) {
        SOAPFaultValue valueElement = getValue();
        if (valueElement == null) {
            valueElement = ((SOAP12Factory) getOMFactory()).internalCreateSOAPFaultValue(this);
        }
        valueElement.setText(value);
    }
}
