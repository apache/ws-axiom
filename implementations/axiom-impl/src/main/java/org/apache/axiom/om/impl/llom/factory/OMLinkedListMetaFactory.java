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

package org.apache.axiom.om.impl.llom.factory;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.common.factory.AbstractOMMetaFactory;
import org.apache.axiom.om.impl.common.factory.OMFactoryImpl;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.impl.factory.SOAP11Factory;
import org.apache.axiom.soap.impl.factory.SOAP12Factory;

/**
 * Meta factory for the linked list OM implementation.
 */
public class OMLinkedListMetaFactory extends AbstractOMMetaFactory {
    public static final OMLinkedListMetaFactory INSTANCE = new OMLinkedListMetaFactory();
    
    private final OMFactory omFactory = new OMFactoryImpl(this, LLOMNodeFactory.INSTANCE);
    private final SOAPFactory soap11Factory = new SOAP11Factory(this, LLOMNodeFactory.INSTANCE);
    private final SOAPFactory soap12Factory = new SOAP12Factory(this, LLOMNodeFactory.INSTANCE);
    
    private OMLinkedListMetaFactory() {
        super(LLOMNodeFactory.INSTANCE);
    }
    
    @Override
    public OMFactory getOMFactory() {
        return omFactory;
    }
    
    @Override
    public SOAPFactory getSOAP11Factory() {
        return soap11Factory;
    }
    
    @Override
    public SOAPFactory getSOAP12Factory() {
        return soap12Factory;
    }
}
