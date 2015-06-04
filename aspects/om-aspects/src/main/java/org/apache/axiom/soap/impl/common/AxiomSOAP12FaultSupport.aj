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

import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPProcessingException;

public aspect AxiomSOAP12FaultSupport {
    private static final Class<?>[] sequence = { SOAPFaultCode.class, SOAPFaultReason.class,
            SOAPFaultNode.class, SOAPFaultRole.class, SOAPFaultDetail.class };

    public final void AxiomSOAP12Fault.setCode(SOAPFaultCode soapFaultCode) throws SOAPProcessingException {
        if (!(soapFaultCode instanceof AxiomSOAP12FaultCode)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP 1.2 implementation of SOAP Fault Code. " +
                            "But received some other implementation");
        }
        insertChild(sequence, 0, soapFaultCode);
    }


    public final void AxiomSOAP12Fault.setReason(SOAPFaultReason reason) throws SOAPProcessingException {
        if (!(reason instanceof AxiomSOAP12FaultReason)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP 1.2 implementation of SOAP Fault Reason. But received some other implementation");
        }
        insertChild(sequence, 1, reason);
    }

    public final void AxiomSOAP12Fault.setNode(SOAPFaultNode node) throws SOAPProcessingException {
        if (!(node instanceof AxiomSOAP12FaultNode)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP 1.2 implementation of SOAP Fault Node. But received some other implementation");
        }
        insertChild(sequence, 2, node);
    }

    public final void AxiomSOAP12Fault.setRole(SOAPFaultRole role) throws SOAPProcessingException {
        if (!(role instanceof AxiomSOAP12FaultRole)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP 1.2 implementation of SOAP Fault Role. But received some other implementation");
        }
        insertChild(sequence, 3, role);
    }

    public final void AxiomSOAP12Fault.setDetail(SOAPFaultDetail detail) throws SOAPProcessingException {
        if (!(detail instanceof AxiomSOAP12FaultDetail)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP 1.2 implementation of SOAP Fault Detail. But received some other implementation");
        }
        insertChild(sequence, 4, detail);
    }

    public final SOAPFaultCode AxiomSOAP12Fault.getCode() {
        return (SOAPFaultCode)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_CODE);
    }

    public final SOAPFaultReason AxiomSOAP12Fault.getReason() {
        return (SOAPFaultReason)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_REASON);
    }

    public final SOAPFaultNode AxiomSOAP12Fault.getNode() {
        return (SOAPFaultNode)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_NODE);
    }

    public final SOAPFaultRole AxiomSOAP12Fault.getRole() {
        return (SOAPFaultRole)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_ROLE);
    }

    public final SOAPFaultDetail AxiomSOAP12Fault.getDetail() {
        return (SOAPFaultDetail)getFirstChildWithName(SOAP12Constants.QNAME_FAULT_DETAIL);
    }
}
