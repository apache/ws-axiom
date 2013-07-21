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
package org.apache.axiom.spring.ws.server.endpoint.mapping;

import javax.xml.namespace.QName;

import org.apache.axiom.spring.ws.AxiomOptimizationEnabler;
import org.apache.axiom.spring.ws.AxiomWebServiceMessage;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.mapping.PayloadRootAnnotationMethodEndpointMapping;

/**
 * Replacement for {@link PayloadRootAnnotationMethodEndpointMapping} that can leverage Axiom
 * specific optimizations, namely {@link AxiomWebServiceMessage#getPayloadRootQName()}.
 * <p>
 * Typically this class is not configured explicitly. Instead it is automatically configured by
 * {@link AxiomOptimizationEnabler}.
 */
public class AxiomPayloadRootAnnotationMethodEndpointMapping extends PayloadRootAnnotationMethodEndpointMapping {
    @Override
    protected QName getLookupKeyForMessage(MessageContext messageContext) throws Exception {
        WebServiceMessage request = messageContext.getRequest();
        if (request instanceof AxiomWebServiceMessage) {
            return ((AxiomWebServiceMessage)request).getPayloadRootQName();
        } else {
            return super.getLookupKeyForMessage(messageContext);
        }
    }
}
