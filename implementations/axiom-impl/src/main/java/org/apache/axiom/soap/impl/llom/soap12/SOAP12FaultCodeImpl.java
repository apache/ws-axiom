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

package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.ElementHelper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultCode;
import org.apache.axiom.soap.impl.llom.SOAPFaultCodeImpl;

import javax.xml.namespace.QName;

public class SOAP12FaultCodeImpl extends SOAPFaultCodeImpl implements AxiomSOAP12FaultCode {
    public SOAP12FaultCodeImpl(SOAPFactory factory) {
        super(factory.getNamespace(), factory);
    }

    /**
     * Constructor OMElementImpl
     *
     * @param parent
     * @param builder
     */
    public SOAP12FaultCodeImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, builder, factory);
    }

    /** @param parent  */
    public SOAP12FaultCodeImpl(SOAPFault parent, SOAPFactory factory)
            throws SOAPProcessingException {
        super(parent, true, factory);
    }


    public void setSubCode(SOAPFaultSubCode subCode) throws SOAPProcessingException {
        if (!(subCode instanceof SOAP12FaultSubCodeImpl)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP12FaultSubCodeImpl, got " + subCode.getClass());
        }
        ElementHelper.setNewElement(this, getSubCode(), subCode);
    }

    public void setValue(SOAPFaultValue value) throws SOAPProcessingException {
        if (!(value instanceof SOAP12FaultValueImpl)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP12FaultValueImpl, got " + value.getClass());
        }
        ElementHelper.setNewElement(this, getValue(), value);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultImpl)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP12FaultImpl as parent, got " + parent.getClass());
        }
    }

    // TODO: For compatibility with Axiom 1.2.x; remove in Axiom 1.3
    public QName getTextAsQName() {
        return getValueAsQName();
    }

    public SOAPFaultValue getValue() {
        return (SOAPFaultValue)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_VALUE);
    }

    public SOAPFaultSubCode getSubCode() {
        return (SOAPFaultSubCode)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_SUBCODE);
    }

    public void setValue(QName value) {
        SOAPFaultValue valueElement = getValue();
        if (valueElement == null) {
            valueElement = ((SOAPFactory)getOMFactory()).createSOAPFaultValue(this);
        }
        valueElement.setText(value);
    }

    public QName getValueAsQName() {
        SOAPFaultValue value = getValue();
        return value == null ? null : value.getTextAsQName();
    }
}
