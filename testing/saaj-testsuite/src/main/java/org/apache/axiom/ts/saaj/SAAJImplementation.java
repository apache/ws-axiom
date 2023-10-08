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
package org.apache.axiom.ts.saaj;

import java.lang.reflect.Method;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SAAJMetaFactory;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;

public final class SAAJImplementation {
    private static final Method newMessageFactoryMethod;
    private static final Method newSOAPFactoryMethod;

    static {
        try {
            newMessageFactoryMethod =
                    SAAJMetaFactory.class.getDeclaredMethod("newMessageFactory", String.class);
            newMessageFactoryMethod.setAccessible(true);
            newSOAPFactoryMethod =
                    SAAJMetaFactory.class.getDeclaredMethod("newSOAPFactory", String.class);
            newSOAPFactoryMethod.setAccessible(true);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    private final SAAJMetaFactory metaFactory;

    public SAAJImplementation(SAAJMetaFactory metaFactory) {
        this.metaFactory = metaFactory;
    }

    public MessageFactory newMessageFactory(String protocol) throws SOAPException {
        try {
            return (MessageFactory) newMessageFactoryMethod.invoke(metaFactory, protocol);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public SOAPFactory newSOAPFactory(String protocol) throws SOAPException {
        try {
            return (SOAPFactory) newSOAPFactoryMethod.invoke(metaFactory, protocol);
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }
}
