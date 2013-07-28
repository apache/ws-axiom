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

final class SourceExtractionStrategyInvocationHandler implements InvocationHandler {
    private final Object bean;
    private final String beanName;
    private final SourceExtractionStrategy strategy;
    
    SourceExtractionStrategyInvocationHandler(Object bean, String beanName, SourceExtractionStrategy strategy) {
        this.bean = bean;
        this.beanName = beanName;
        this.strategy = strategy;
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
        try {
            return method.invoke(bean, args);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        } finally {
            if (message != null) {
                message.popSourceExtractionStrategy(beanName);
            }
        }
    }
}
