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

import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;

public aspect AxiomSOAPMessageSupport {
    private SOAPFactory AxiomSOAPMessage.factory;

    public Class<? extends CoreNode> AxiomSOAPMessage.coreGetNodeClass() {
        return AxiomSOAPMessage.class;
    }

    public final void AxiomSOAPMessage.initSOAPFactory(SOAPFactory factory) {
        this.factory = factory;
    }
    
    public final <T> void AxiomSOAPMessage.initAncillaryData(ClonePolicy<T> policy, T options, CoreNode other) {
        factory = (SOAPFactory)((AxiomSOAPMessage)other).getOMFactory();
    }
    
    public final OMFactory AxiomSOAPMessage.getOMFactory() {
        if (factory == null) {
            // Force expansion up to the SOAP envelope; this will initialize the factory
            getSOAPEnvelope();
        }
        return factory;
    }
    
    // TODO: this violates OO design principles and should disappear in a future Axiom version
    public final void AxiomSOAPMessage.internalSerialize(Serializer serializer, OMOutputFormat format,
            boolean cache, boolean includeXMLDeclaration) throws OutputException {
        ((AxiomElement)getOMDocumentElement()).internalSerialize(serializer, format, cache);
    }

    public final SOAPEnvelope AxiomSOAPMessage.getSOAPEnvelope() {
        return (SOAPEnvelope)getOMDocumentElement();
    }

    public final void AxiomSOAPMessage.setSOAPEnvelope(SOAPEnvelope envelope) {
        setOMDocumentElement(envelope);
    }
}
