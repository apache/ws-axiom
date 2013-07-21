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
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.ws.server.endpoint.mapping.PayloadRootAnnotationMethodEndpointMapping;

/**
 * {@link BeanFactoryPostProcessor} that adjusts the configuration to enable some Axiom specific
 * optimizations. Currently, the following optimizations are done:
 * <ul>
 * <li>{@link PayloadRootAnnotationMethodEndpointMapping} beans are replaced by
 * {@link AxiomPayloadRootAnnotationMethodEndpointMapping} beans.
 * </ul>
 */
public class AxiomOptimizationEnabler implements BeanFactoryPostProcessor {
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
}
