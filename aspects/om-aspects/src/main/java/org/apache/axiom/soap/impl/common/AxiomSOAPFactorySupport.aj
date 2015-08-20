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

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPMessage;

public aspect AxiomSOAPFactorySupport {
    public final SOAPMessage AxiomSOAPFactory.createSOAPMessage(OMXMLParserWrapper builder) {
        AxiomSOAPMessage message = createSOAPMessage();
        // Null check for Spring-WS compatibility
        if (builder != null) {
            message.coreSetBuilder(builder);
        }
        return message;
    }
    
    public final SOAPEnvelope AxiomSOAPFactory.createSOAPEnvelope() {
        return createSOAPEnvelope(getNamespace());
    }
    
    public final SOAPEnvelope AxiomSOAPFactory.createSOAPEnvelope(OMNamespace ns) {
        return createAxiomElement(AxiomSOAPEnvelope.class, null, SOAPConstants.SOAPENVELOPE_LOCAL_NAME, ns, null, true);
    }

    public final SOAPEnvelope AxiomSOAPFactory.createSOAPEnvelope(SOAPMessage message, OMXMLParserWrapper builder) {
        return createAxiomElement(AxiomSOAPEnvelope.class, message, SOAPConstants.SOAPENVELOPE_LOCAL_NAME, null, builder, false);
    }

    public final SOAPHeaderBlock AxiomSOAPFactory.createSOAPHeaderBlock(String localName, OMNamespace ns, SOAPHeader parent) {
        return createAxiomElement(getSOAPHeaderBlockClass(), parent, localName, ns, null, true);
    }

    public final SOAPHeaderBlock AxiomSOAPFactory.createSOAPHeaderBlock(String localName, OMNamespace ns) {
        return createAxiomElement(getSOAPHeaderBlockClass(), null, localName, ns, null, true);
    }

    public final SOAPHeaderBlock AxiomSOAPFactory.createSOAPHeaderBlock(String localName, SOAPHeader parent, OMXMLParserWrapper builder) {
        return createAxiomElement(getSOAPHeaderBlockClass(), parent, localName, null, builder, false);
    }
}
