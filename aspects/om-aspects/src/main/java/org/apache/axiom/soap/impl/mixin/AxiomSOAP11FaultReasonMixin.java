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

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.impl.intf.soap11.AxiomSOAP11FaultReason;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin(AxiomSOAP11FaultReason.class)
public abstract class AxiomSOAP11FaultReasonMixin implements AxiomSOAP11FaultReason {
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP11FaultReason.class;
    }

    public final boolean isChildElementAllowed(OMElement child) {
        return false;
    }

    public final void addSOAPText(SOAPFaultText soapFaultText) {
        throw new UnsupportedOperationException("addSOAPText() not allowed for SOAP 1.1!");
    }

    public final SOAPFaultText getFirstSOAPText() {
        throw new UnsupportedOperationException("getFirstSOAPText() not allowed for SOAP 1.1!");
    }

    public final List<SOAPFaultText> getAllSoapTexts() {
        return Collections.emptyList();
    }

    public final SOAPFaultText getSOAPFaultText(String language) {
        return null;
    }

    public final String getFaultReasonText(Locale locale) {
        return getText();
    }
}
