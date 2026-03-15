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
package org.apache.axiom.ts.springws;

import org.apache.axiom.ts.soap.SOAPSpec;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.env.MockPropertySource;

import com.google.inject.Inject;

import junit.framework.TestCase;

public abstract class SpringWSTestCase extends TestCase {
    @Inject protected SOAPSpec spec;

    protected void configureContext(
            GenericApplicationContext context,
            MessageFactoryConfigurator messageFactoryConfigurator,
            Resource configResource) {
        MockPropertySource propertySource = new MockPropertySource("testParameters");
        propertySource.setProperty(
                "soapVersion", spec.getAdapter(SOAPSpecAdapter.class).getSoapVersion());
        context.getEnvironment().getPropertySources().addFirst(propertySource);
        messageFactoryConfigurator.configure(context);
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.loadBeanDefinitions(new ClassPathResource("common.xml", SpringWSTestCase.class));
        if (configResource != null) {
            reader.loadBeanDefinitions(configResource);
        }
    }
}
