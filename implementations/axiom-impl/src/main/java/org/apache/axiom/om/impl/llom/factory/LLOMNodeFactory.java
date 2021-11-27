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

import org.apache.axiom.om.impl.common.factory.AxiomNodeFactoryImpl;

public final class LLOMNodeFactory extends AxiomNodeFactoryImpl {
    public static LLOMNodeFactory INSTANCE = new LLOMNodeFactory();
    
    private LLOMNodeFactory() {
        super(LLOMNodeFactory.class.getClassLoader(),
                "org.apache.axiom.om.impl.llom.NodeFactory2Impl",
                "org.apache.axiom.om.impl.llom",
                "org.apache.axiom.soap.impl.llom",
                "org.apache.axiom.soap.impl.llom.soap11",
                "org.apache.axiom.soap.impl.llom.soap12");
    }
}
