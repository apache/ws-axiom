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

import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.spring.ws.server.endpoint.mapping.AxiomPayloadRootAnnotationMethodEndpointMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.dom.JDomPayloadMethodProcessor;
import org.springframework.ws.server.endpoint.mapping.PayloadRootAnnotationMethodEndpointMapping;

/**
 * Post processor that adjusts the bean configurations to enable some Axiom specific optimizations.
 * <p>
 * Its primary responsibility is to associate particular {@link PayloadAccessStrategy} instances
 * with different types of {@link MethodArgumentResolver} [TODO: etc.] beans. For these beans, it
 * then creates proxy instances that configure the {@link PayloadAccessStrategy} using
 * {@link AxiomWebServiceMessage#pushPayloadAccessStrategy(PayloadAccessStrategy, Object)} and
 * {@link AxiomWebServiceMessage#popPayloadAccessStrategy(Object)}.
 * <p>
 * The following table describes the supported bean types and the corresponding
 * {@link PayloadAccessStrategy}:
 * <table border="2" rules="all" cellpadding="4" cellspacing="0">
 * <tr>
 * <th>Bean type
 * <th>Strategy
 * </tr>
 * <tr>
 * <td>{@link JDomPayloadMethodProcessor}
 * <td>{@link PayloadAccessStrategy#SAX_CONSUME}
 * </tr>
 * </table>
 * <p>
 * TODO: note about the implications of the fact that proxies for {@link MethodArgumentResolver} use
 * strategies that consume the payload
 * <p>
 * In addition, the post processor carries out the following optimizations:
 * <ul>
 * <li>{@link PayloadRootAnnotationMethodEndpointMapping} beans are replaced by
 * {@link AxiomPayloadRootAnnotationMethodEndpointMapping} beans.
 * </ul>
 */
public class AxiomOptimizationEnabler implements BeanFactoryPostProcessor, BeanPostProcessor {
    private static final Log log = LogFactory.getLog(AxiomOptimizationEnabler.class);
    
    private static final Map<String,String> replacementClasses;
    
    static {
        replacementClasses = new HashMap<String,String>();
        replacementClasses.put(PayloadRootAnnotationMethodEndpointMapping.class.getName(),
                AxiomPayloadRootAnnotationMethodEndpointMapping.class.getName());
    }
    
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            String beanClass = beanDefinition.getBeanClassName();
            String replacementClass = replacementClasses.get(beanClass);
            if (replacementClass != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Changing bean class of " + beanName + " from " + beanClass + " to " + replacementClass);
                }
                beanDefinition.setBeanClassName(replacementClass);
            }
        }
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof JDomPayloadMethodProcessor) {
            return new MethodProcessorProxy(bean, PayloadAccessStrategy.SAX_CONSUME);
        } else {
            return bean;
        }
    }
}
