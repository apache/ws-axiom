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

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultDetail;

public aspect AxiomSOAPFaultSupport {
    public final void AxiomSOAPFault.setException(Exception e) {
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

    public final Exception AxiomSOAPFault.getException() {
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
