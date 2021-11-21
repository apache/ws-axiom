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
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin(AxiomSOAPMessage.class)
public abstract class AxiomSOAPMessageMixin implements AxiomSOAPMessage {
    private SOAPFactory factory;

    @Override
    public Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAPMessage.class;
    }

    @Override
    public final void initSOAPFactory(SOAPFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public final <T> void initAncillaryData(ClonePolicy<T> policy, T options, CoreNode other) {
        factory = (SOAPFactory)((AxiomSOAPMessage)other).getOMFactory();
    }
    
    public final void checkDocumentElement(OMElement element) {
        if (!(element instanceof SOAPEnvelope)) {
            throw new OMException("Child not allowed; must be a SOAPEnvelope");
        }
    }
    
    @Override
    public final OMFactory getOMFactory() {
        if (factory == null) {
            // Force expansion up to the SOAP envelope; this will initialize the factory
            getSOAPEnvelope();
        }
        return factory;
    }
    
    @Override
    public final SOAPEnvelope getSOAPEnvelope() {
        return (SOAPEnvelope)getOMDocumentElement();
    }

    @Override
    public final void setSOAPEnvelope(SOAPEnvelope envelope) {
        setOMDocumentElement(envelope);
    }
}
