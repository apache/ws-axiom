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
package org.apache.axiom.spring.ws;

import org.springframework.core.MethodParameter;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.MethodReturnValueHandler;

final class MethodProcessorProxy implements MethodArgumentResolver, MethodReturnValueHandler {
    private final Object target;
    private final PayloadAccessStrategy strategy;

    MethodProcessorProxy(Object target, PayloadAccessStrategy strategy) {
        this.target = target;
        this.strategy = strategy;
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return ((MethodArgumentResolver)target).supportsParameter(parameter);
    }

    public Object resolveArgument(MessageContext messageContext, MethodParameter parameter)
            throws Exception {
        WebServiceMessage request = messageContext.getRequest();
        if (request instanceof AxiomWebServiceMessage) {
            ((AxiomWebServiceMessage)request).pushPayloadAccessStrategy(strategy, this);
        }
        try {
            return ((MethodArgumentResolver)target).resolveArgument(messageContext, parameter);
        } finally {
            if (request instanceof AxiomWebServiceMessage) {
                ((AxiomWebServiceMessage)request).popPayloadAccessStrategy(this);
            }
        }
    }

    public boolean supportsReturnType(MethodParameter returnType) {
        return ((MethodReturnValueHandler)target).supportsReturnType(returnType);
    }

    public void handleReturnValue(MessageContext messageContext, MethodParameter returnType,
            Object returnValue) throws Exception {
        ((MethodReturnValueHandler)target).handleReturnValue(messageContext, returnType, returnValue);
    }
}
