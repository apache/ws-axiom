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

package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListMetaFactory;
import org.apache.axiom.soap.impl.common.AxiomSOAP12Factory;
import org.apache.axiom.soap.impl.llom.SOAPFactoryImpl;

/**
 */
public class SOAP12Factory extends SOAPFactoryImpl implements AxiomSOAP12Factory {
    /**
     * For internal use only.
     * 
     * @param metaFactory
     */
    public SOAP12Factory(OMLinkedListMetaFactory metaFactory) {
        super(metaFactory);
    }

    /**
     * @deprecated Use {@link OMAbstractFactory#getSOAP12Factory()} to get an instance of this
     *             class.
     */
    public SOAP12Factory() {
    }
}
