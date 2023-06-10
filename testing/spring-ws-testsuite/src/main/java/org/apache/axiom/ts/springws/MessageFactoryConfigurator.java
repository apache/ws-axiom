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

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.soap.SoapVersion;

/** Defines a particular {@link SoapMessageFactory} configuration. */
public abstract class MessageFactoryConfigurator {
    public static final MessageFactoryConfigurator SAAJ =
            new SimpleMessageFactoryConfigurator(
                    "saaj",
                    new ClassPathResource(
                            "saaj-message-factory.xml", MessageFactoryConfigurator.class));

    private final String name;

    public MessageFactoryConfigurator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Configure a {@link SoapMessageFactory} in the given application context. The method must
     *
     * <ul>
     *   <li>add a bean of type {@link SoapMessageFactory} and name {@code messageFactory} to the
     *       context;
     *   <li>configure that {@link SoapMessageFactory} to use the SOAP version specified by the
     *       {@code soapVersion} property (which specifies the name of one of the constants defined
     *       in {@link SoapVersion}).
     * </ul>
     *
     * @param context
     */
    public abstract void configure(GenericApplicationContext context);
}
