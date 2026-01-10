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

package org.apache.axiom.om.impl.dom;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPFactory;

/**
 * @deprecated Use {@link OMAbstractFactory#getMetaFactory(String)} with {@link
 *     OMAbstractFactory#FEATURE_DOM} to get a meta factory for DOOM.
 */
public class DOOMAbstractFactory {
    private static final OMMetaFactory metaFactory =
            OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM);

    public static OMFactory getOMFactory() {
        return metaFactory.getOMFactory();
    }

    public static SOAPFactory getSOAP11Factory() {
        return metaFactory.getSOAP11Factory();
    }

    public static SOAPFactory getSOAP12Factory() {
        return metaFactory.getSOAP12Factory();
    }
}
