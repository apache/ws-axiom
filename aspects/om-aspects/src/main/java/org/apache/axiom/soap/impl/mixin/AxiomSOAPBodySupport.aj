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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.impl.intf.AxiomSOAPBody;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;

public aspect AxiomSOAPBodySupport {
    public final boolean AxiomSOAPBody.isChildElementAllowed(OMElement child) {
        return !(child instanceof AxiomSOAPElement) || child instanceof SOAPFault;
    }
    
    public final SOAPFault AxiomSOAPBody.addFault(Exception e) throws OMException {
        return ((SOAPFactory)getOMFactory()).createSOAPFault(this, e);
    }

    public final boolean AxiomSOAPBody.hasFault() {
        return getFirstElement() instanceof SOAPFault;
    }

    public final OMNamespace AxiomSOAPBody.getFirstElementNS() {
        OMElement element = getFirstElement();
        return element == null ? null : element.getNamespace();
    }
    
    public final String AxiomSOAPBody.getFirstElementLocalName() {
        OMElement element = getFirstElement();
        return element == null ? null : element.getLocalName();
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
