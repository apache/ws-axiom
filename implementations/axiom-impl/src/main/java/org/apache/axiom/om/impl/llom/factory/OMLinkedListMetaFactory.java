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

import org.apache.axiom.om.impl.common.factory.meta.AbstractOMMetaFactory;
import org.apache.axiom.om.impl.intf.factory.AxiomNodeFactory;

/**
 * Meta factory for the linked list OM implementation.
 */
public class OMLinkedListMetaFactory extends AbstractOMMetaFactory {
    private static final AxiomNodeFactory NODE_FACTORY;

    static {
        try {
            NODE_FACTORY = (AxiomNodeFactory)OMLinkedListMetaFactory.class.getClassLoader().loadClass("org.apache.axiom.om.impl.llom.factory.AxiomNodeFactoryImpl").getField("INSTANCE").get(null);
        } catch (ReflectiveOperationException ex) {
            throw new Error(ex);
        }
    }

    public static final OMLinkedListMetaFactory INSTANCE = new OMLinkedListMetaFactory();
    
    private OMLinkedListMetaFactory() {
        super(NODE_FACTORY);
    }
}
