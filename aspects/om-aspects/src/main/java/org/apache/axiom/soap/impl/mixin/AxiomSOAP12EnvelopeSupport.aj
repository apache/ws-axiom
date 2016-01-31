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
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12Envelope;

public aspect AxiomSOAP12EnvelopeSupport {
    public final Class<? extends CoreNode> AxiomSOAP12Envelope.coreGetNodeClass() {
        return AxiomSOAP12Envelope.class;
    }

    public final void AxiomSOAP12Envelope.internalCheckChild(OMNode child) {
        if ((child instanceof OMElement)
                && !(child instanceof SOAPHeader || child instanceof SOAPBody)) {
            throw new SOAPProcessingException(
                    "SOAP Envelope can not have children other than SOAP Header and Body",
                    SOAP12Constants.FAULT_CODE_SENDER);
        }
    }
}
