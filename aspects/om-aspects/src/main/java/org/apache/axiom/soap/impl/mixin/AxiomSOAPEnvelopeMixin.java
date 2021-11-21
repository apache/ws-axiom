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
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.intf.AxiomSOAPEnvelope;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin(AxiomSOAPEnvelope.class)
public abstract class AxiomSOAPEnvelopeMixin implements AxiomSOAPEnvelope {
    public final SOAPVersion getVersion() {
        return getSOAPHelper().getVersion();
    }

    public final SOAPHeader getHeader() {
        // The soap header is the first element in the envelope.
        OMElement e = getFirstElement();
        return e instanceof SOAPHeader ? (SOAPHeader)e : null;
    }

    public final SOAPHeader getOrCreateHeader() {
        SOAPHeader header = getHeader();
        return header != null ? header : ((SOAPFactory)getOMFactory()).createSOAPHeader(this);
    }

    public final SOAPBody getBody() {
        OMElement element = getFirstElement();
        if (element instanceof SOAPBody) {
            return (SOAPBody)element;
        } else if (element instanceof SOAPHeader) {
            OMNode node = element.getNextOMSibling();
            while (node != null && !(node instanceof OMElement)) {
                node = node.getNextOMSibling();
            }
            if (node instanceof SOAPBody) {
                return (SOAPBody)node;
            }
        }
        return null;
    }

    public final boolean hasFault() {
        SOAPBody body = getBody();
        return (body == null) ? false : body.hasFault();
    }

    public final String getSOAPBodyFirstElementLocalName() {
        SOAPBody body = getBody();
        return (body == null) ? null : body.getFirstElementLocalName();
    }

    public final OMNamespace getSOAPBodyFirstElementNS() {
        SOAPBody body = getBody();
        return (body == null) ? null : body.getFirstElementNS();
    }
}
