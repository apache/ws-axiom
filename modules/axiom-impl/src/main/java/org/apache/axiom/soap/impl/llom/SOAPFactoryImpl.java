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
package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListMetaFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.builder.SOAPFactoryEx;

public abstract class SOAPFactoryImpl extends OMLinkedListImplFactory implements SOAPFactoryEx {
    public SOAPFactoryImpl(OMLinkedListMetaFactory metaFactory) {
        super(metaFactory);
    }

    /**
     * @deprecated
     */
    public SOAPFactoryImpl() {
    }

    public final SOAPMessage createSOAPMessage() {
        return new SOAPMessageImpl(this);
    }

    public final SOAPMessage createSOAPMessage(OMXMLParserWrapper builder) {
        if (builder == null) {
            // For Spring-WS compatibility
            return createSOAPMessage();
        } else {
            return new SOAPMessageImpl(builder, this);
        }
    }

    public final SOAPMessage createDefaultSOAPMessage() {
        SOAPMessage message = createSOAPMessage();
        SOAPEnvelope env = createSOAPEnvelope();
        message.addChild(env);
        createSOAPBody(env);
        return message;
    }
    
    public final SOAPEnvelope createSOAPEnvelope(SOAPMessage message, OMXMLParserWrapper builder) {
        return new SOAPEnvelopeImpl(message, builder, this);
    }

    public final SOAPEnvelope createSOAPEnvelope() {
        return createSOAPEnvelope(getNamespace());
    }
    
    public final SOAPEnvelope createSOAPEnvelope(OMNamespace ns) {
        return new SOAPEnvelopeImpl(ns, this);
    }

    public final SOAPEnvelope getDefaultEnvelope() throws SOAPProcessingException {
        SOAPEnvelope env = createSOAPEnvelope();
        createSOAPHeader(env);
        createSOAPBody(env);
        return env;
    }
}
