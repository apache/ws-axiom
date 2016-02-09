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

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.impl.intf.AxiomSOAPBody;

public aspect AxiomSOAPBodySupport {
    public final SOAPFault AxiomSOAPBody.addFault(Exception e) throws OMException {
        return ((SOAPFactory)getOMFactory()).createSOAPFault(this, e);
    }

    private boolean AxiomSOAPBody.hasLookahead() {
        StAXOMBuilder builder = (StAXOMBuilder)coreGetBuilder();
        if (builder != null && !builder.isCompleted() && builder.getTarget() == this) {
            CoreChildNode child = coreGetFirstChildIfAvailable();
            while (child != null) {
                if (child instanceof OMElement) {
                    return false;
                }
                child = child.coreGetNextSiblingIfAvailable();
            }
            do {
                if (builder.lookahead() == XMLStreamReader.START_ELEMENT) {
                    return true;
                }
                builder.next();
            } while (builder.getTarget() == this);
        }
        return false;
    }
    
    public final boolean AxiomSOAPBody.hasFault() {
        // Set hasSOAPFault if it matches the name matches a SOAP Fault
        if (hasLookahead()) {
            StAXOMBuilder builder = (StAXOMBuilder)coreGetBuilder();
            return SOAPConstants.SOAPFAULT_LOCAL_NAME.equals(builder.getLocalName())
                    && getSOAPHelper().getEnvelopeURI().equals(builder.getNamespaceURI());
        } else {
            return getFirstElement() instanceof SOAPFault;
        }
    }

    public final OMNamespace AxiomSOAPBody.getFirstElementNS() {
        if (hasLookahead()) {
            StAXOMBuilder builder = (StAXOMBuilder)coreGetBuilder();
            String ns = builder.getNamespaceURI();
            if (ns == null) {
                return null;
            } else {
                String prefix = builder.getPrefix();
                return getOMFactory().createOMNamespace(ns, prefix == null ? "" : prefix);
            }
        } else {
            OMElement element = getFirstElement();
            if (element == null) {
                return null;
            } else {
                return element.getNamespace();
            } 
        }
    }
    
    public final String AxiomSOAPBody.getFirstElementLocalName() {
        if (hasLookahead()) {
            return ((StAXOMBuilder)coreGetBuilder()).getLocalName();
        } else {
            OMElement element = getFirstElement();
            if (element == null) {
                return null;
            } else {
                return element.getLocalName();
            } 
        }
    }

    public final SOAPFault AxiomSOAPBody.getFault() {
        OMElement element = getFirstElement();
        return element instanceof SOAPFault ? (SOAPFault)element : null;
    }

    public final void AxiomSOAPBody.addFault(SOAPFault soapFault) {
        if (hasFault()) {
            throw new OMException(
                    "SOAP Body already has a SOAP Fault and there can not be more than one SOAP fault");
        }
        addChild(soapFault);
    }
}
