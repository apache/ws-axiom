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

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.intf.Sequence;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFault;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin(AxiomSOAPFault.class)
public abstract class AxiomSOAPFaultMixin implements AxiomSOAPFault {
    public final boolean isChildElementAllowed(OMElement child) {
        return child instanceof SOAPFaultCode || child instanceof SOAPFaultDetail
                || child instanceof SOAPFaultReason || child instanceof SOAPFaultRole || child instanceof SOAPFaultNode;
    }

    public final void setCode(SOAPFaultCode soapFaultCode) {
        insertChild(getSequence(), 0, soapFaultCode, true);
    }

    public final void setReason(SOAPFaultReason reason) {
        insertChild(getSequence(), 1, reason, true);
    }

    public final void setRole(SOAPFaultRole role) {
        Sequence sequence = getSequence();
        insertChild(sequence, sequence.index(SOAPFaultRole.class), role, true);
    }

    public final void setDetail(SOAPFaultDetail detail) {
        Sequence sequence = getSequence();
        insertChild(sequence, sequence.index(SOAPFaultDetail.class), detail, true);
    }

    public final void setException(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        sw.flush();
        SOAPFactory factory = (SOAPFactory)getOMFactory();
        SOAPFaultDetail detail = getDetail();
        if (detail == null) {
            detail = factory.createSOAPFaultDetail(this);
            setDetail(detail);
        }
        OMElement faultDetailEnty = factory.createOMElement(
                SOAPConstants.SOAP_FAULT_DETAIL_EXCEPTION_ENTRY, null, detail);
        faultDetailEnty.setText(sw.getBuffer().toString());
    }

    public final Exception getException() {
        SOAPFaultDetail detail = getDetail();
        if (detail == null) {
            return null;
        } else {
            OMElement exceptionElement = getDetail().getFirstChildWithName(
                    new QName(SOAPConstants.SOAP_FAULT_DETAIL_EXCEPTION_ENTRY));
            if (exceptionElement != null) {
                return new Exception(exceptionElement.getText());
            } else {
                return null;
            }
        }
    }
}
