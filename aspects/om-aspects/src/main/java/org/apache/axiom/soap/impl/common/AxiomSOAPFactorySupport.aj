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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.AxiomContainer;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPMessage;

public aspect AxiomSOAPFactorySupport {
    public final <T extends AxiomSOAPElement> T AxiomSOAPFactory.createSOAPElement(Class<T> type, OMElement parent, QName qname, OMXMLParserWrapper builder) {
        T element = createNode(type);
        if (builder != null) {
            element.coreSetBuilder(builder);
        } else if (parent != null) {
            element.checkParent(parent);
        }
        if (parent != null) {
            ((AxiomContainer)parent).addChild(element, builder != null);
        }
        if (builder != null) {
            element.internalSetLocalName(qname.getLocalPart());
        } else if (qname.getNamespaceURI().length() == 0) {
            element.initName(qname.getLocalPart(), null, true);
        } else if (parent != null) {
            element.initName(qname.getLocalPart(), parent.getNamespace(), false);
        } else {
            element.initName(qname.getLocalPart(), getNamespace(), true);
        }
        return element;
    }

    public final SOAPMessage AxiomSOAPFactory.createSOAPMessage() {
        return createNode(AxiomSOAPMessage.class);
    }

    public final SOAPMessage AxiomSOAPFactory.createSOAPMessage(OMXMLParserWrapper builder) {
        AxiomSOAPMessage message = createNode(AxiomSOAPMessage.class);
        // Null check for Spring-WS compatibility
        if (builder != null) {
            message.coreSetBuilder(builder);
        }
        return message;
    }
    
    public final SOAPEnvelope AxiomSOAPFactory.createSOAPEnvelope() {
        return createSOAPEnvelope(getNamespace());
    }
    
    public final SOAPEnvelope AxiomSOAPFactory.createSOAPEnvelope(OMNamespace ns) {
        return createAxiomElement(AxiomSOAPEnvelope.class, null, SOAPConstants.SOAPENVELOPE_LOCAL_NAME, ns, null, true);
    }

    public final SOAPEnvelope AxiomSOAPFactory.createSOAPEnvelope(SOAPMessage message, OMXMLParserWrapper builder) {
        return createAxiomElement(AxiomSOAPEnvelope.class, message, SOAPConstants.SOAPENVELOPE_LOCAL_NAME, null, builder, false);
    }

    public final SOAPHeader AxiomSOAPFactory.createSOAPHeader(SOAPEnvelope parent, OMXMLParserWrapper builder) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getHeaderClass(), parent, helper.getHeaderQName(), builder);
    }

    public final SOAPHeader AxiomSOAPFactory.createSOAPHeader(SOAPEnvelope parent) {
        return createSOAPHeader(parent, null);
    }

    public final SOAPHeader AxiomSOAPFactory.createSOAPHeader() {
        return createSOAPHeader(null, null);
    }

    public final SOAPHeaderBlock AxiomSOAPFactory.createSOAPHeaderBlock(String localName, OMNamespace ns, SOAPHeader parent) {
        return createAxiomElement(getSOAPHelper().getHeaderBlockClass(), parent, localName, ns, null, true);
    }

    public final SOAPHeaderBlock AxiomSOAPFactory.createSOAPHeaderBlock(String localName, OMNamespace ns) {
        return createAxiomElement(getSOAPHelper().getHeaderBlockClass(), null, localName, ns, null, true);
    }

    public final SOAPHeaderBlock AxiomSOAPFactory.createSOAPHeaderBlock(String localName, SOAPHeader parent, OMXMLParserWrapper builder) {
        return createAxiomElement(getSOAPHelper().getHeaderBlockClass(), parent, localName, null, builder, false);
    }

    public final SOAPBody AxiomSOAPFactory.createSOAPBody(SOAPEnvelope parent, OMXMLParserWrapper builder) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getBodyClass(), parent, helper.getBodyQName(), builder);
    }

    public final SOAPBody AxiomSOAPFactory.createSOAPBody(SOAPEnvelope parent) {
        return createSOAPBody(parent, null);
    }

    public final SOAPBody AxiomSOAPFactory.createSOAPBody() {
        return createSOAPBody(null, null);
    }

    public final SOAPFault AxiomSOAPFactory.createSOAPFault(SOAPBody parent, OMXMLParserWrapper builder) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getFaultClass(), parent, helper.getFaultQName(), builder);
    }

    public final SOAPFault AxiomSOAPFactory.createSOAPFault(SOAPBody parent) {
        return createSOAPFault(parent, (OMXMLParserWrapper)null);
    }

    public final SOAPFault AxiomSOAPFactory.createSOAPFault() {
        return createSOAPFault(null, (OMXMLParserWrapper)null);
    }

    public final SOAPFault AxiomSOAPFactory.createSOAPFault(SOAPBody parent, Exception e) {
        SOAPFault fault = createSOAPFault(parent, (OMXMLParserWrapper)null);
        fault.setException(e);
        return fault;
    }

    public final SOAPFaultCode AxiomSOAPFactory.createSOAPFaultCode(SOAPFault parent, OMXMLParserWrapper builder) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getFaultCodeClass(), parent, helper.getFaultCodeQName(), builder);
    }

    public final SOAPFaultCode AxiomSOAPFactory.createSOAPFaultCode(SOAPFault parent) {
        return createSOAPFaultCode(parent, null);
    }

    public final SOAPFaultCode AxiomSOAPFactory.createSOAPFaultCode() {
        return createSOAPFaultCode(null, null);
    }

    public final SOAPFaultReason AxiomSOAPFactory.createSOAPFaultReason(SOAPFault parent, OMXMLParserWrapper builder) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getFaultReasonClass(), parent, helper.getFaultReasonQName(), builder);
    }

    public final SOAPFaultReason AxiomSOAPFactory.createSOAPFaultReason(SOAPFault parent) {
        return createSOAPFaultReason(parent, null);
    }

    public final SOAPFaultReason AxiomSOAPFactory.createSOAPFaultReason() {
        return createSOAPFaultReason(null, null);
    }

    public final SOAPFaultRole AxiomSOAPFactory.createSOAPFaultRole(SOAPFault parent, OMXMLParserWrapper builder) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getFaultRoleClass(), parent, helper.getFaultRoleQName(), builder);
    }

    public final SOAPFaultRole AxiomSOAPFactory.createSOAPFaultRole(SOAPFault parent) {
        return createSOAPFaultRole(parent, null);
    }

    public final SOAPFaultRole AxiomSOAPFactory.createSOAPFaultRole() {
        return createSOAPFaultRole(null, null);
    }

    public final SOAPFaultDetail AxiomSOAPFactory.createSOAPFaultDetail(SOAPFault parent, OMXMLParserWrapper builder) {
        SOAPHelper helper = getSOAPHelper();
        return createSOAPElement(helper.getFaultDetailClass(), parent, helper.getFaultDetailQName(), builder);
    }

    public final SOAPFaultDetail AxiomSOAPFactory.createSOAPFaultDetail(SOAPFault parent) {
        return createSOAPFaultDetail(parent, null);
    }

    public final SOAPFaultDetail AxiomSOAPFactory.createSOAPFaultDetail() {
        return createSOAPFaultDetail(null, null);
    }
}
