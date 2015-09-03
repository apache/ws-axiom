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
package org.apache.axiom.om.impl.builder;

import javax.xml.namespace.QName;

/**
 * Interface allowing registration of a custom builder. This is a semi-public API used by Axis2; it
 * is not meant for general consumption.
 */
public interface CustomBuilderSupport {
    /**
     * Register a CustomBuilder associated with the indicated QName.
     * The CustomBuilder will be used when an element of that qname is encountered.
     * @param qName
     * @param maxDepth indicate the maximum depth that this qname will be found. (root = 0)
     * @param customBuilder
     * @return replaced CustomBuilder or null
     */
    CustomBuilder registerCustomBuilder(QName qName, int maxDepth, CustomBuilder customBuilder);
    
    /**
     * Register a CustomBuilder for a payload.
     * The payload is defined as the elements inside a SOAPBody or the 
     * document element of a REST message.
     * @param customBuilder
     * @return replaced CustomBuilder or null
     */
    CustomBuilder registerCustomBuilderForPayload(CustomBuilder customBuilder);
}
