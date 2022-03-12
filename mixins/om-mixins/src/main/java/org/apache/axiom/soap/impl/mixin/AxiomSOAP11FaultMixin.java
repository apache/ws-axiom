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

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.impl.intf.Sequence;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.impl.intf.soap11.AxiomSOAP11Fault;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin
public abstract class AxiomSOAP11FaultMixin implements AxiomSOAP11Fault {
    private static final Sequence sequence =
            new Sequence(
                    SOAPFaultCode.class,
                    SOAPFaultReason.class,
                    SOAPFaultRole.class,
                    SOAPFaultDetail.class);

    @Override
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP11Fault.class;
    }

    @Override
    public final Sequence getSequence() {
        return sequence;
    }

    @Override
    public final void setNode(SOAPFaultNode node) {
        throw new UnsupportedOperationException("SOAP 1.1 has no SOAP Fault Node");
    }

    @Override
    public final SOAPFaultCode getCode() {
        return (SOAPFaultCode) getFirstChildWithName(SOAP11Constants.QNAME_FAULT_CODE);
    }

    @Override
    public final SOAPFaultReason getReason() {
        return (SOAPFaultReason) getFirstChildWithName(SOAP11Constants.QNAME_FAULT_REASON);
    }

    @Override
    public final SOAPFaultNode getNode() {
        return null;
    }

    @Override
    public final SOAPFaultRole getRole() {
        return (SOAPFaultRole) getFirstChildWithName(SOAP11Constants.QNAME_FAULT_ROLE);
    }

    @Override
    public final SOAPFaultDetail getDetail() {
        return (SOAPFaultDetail) getFirstChildWithName(SOAP11Constants.QNAME_FAULT_DETAIL);
    }
}
