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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.EndpointInvocationChain;

final class SourceExtractionStrategyInvocationHandler implements InvocationHandler {
    private final Object bean;
    private final String beanName;
    private final SourceExtractionStrategy strategy;
    private final AxiomOptimizationEnabler optimizer;
    
    SourceExtractionStrategyInvocationHandler(Object bean, String beanName, SourceExtractionStrategy strategy, AxiomOptimizationEnabler optimizer) {
        this.bean = bean;
        this.beanName = beanName;
        this.strategy = strategy;
        this.optimizer = optimizer;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        AxiomWebServiceMessage message = null;
        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof MessageContext) {
                    WebServiceMessage candidateMessage = ((MessageContext)arg).getRequest();
                    if (candidateMessage instanceof AxiomWebServiceMessage) {
                        message = (AxiomWebServiceMessage)candidateMessage;
                    }
                    break;
                }
            }
        }
        if (message != null) {
            // Use the beanName here (instead of bean) to improve logging
            message.pushSourceExtractionStrategy(strategy, beanName);
        }
        Object result;
        try {
            result = method.invoke(bean, args);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        } finally {
            if (message != null) {
                message.popSourceExtractionStrategy(beanName);
            }
        }
        // The WS-Addressing dynamically adds an EndpointInterceptor (that is not a bean); ensure that
        // a proxy is created for that interceptor as well
        if (result instanceof EndpointInvocationChain) {
            EndpointInterceptor[] interceptors = ((EndpointInvocationChain)result).getInterceptors();
            for (int i=0; i<interceptors.length; i++) {
                // TODO: this may be problematic if the array is shared
                // TODO: need to filter out EndpointInterceptors that are already proxies
                // TODO: improve logging with respect to bean names
                interceptors[i] = (EndpointInterceptor)optimizer.createSourceExtractionStrategyProxy(interceptors[i], "<anonymous>");
            }
        }
        return result;
    }
}
