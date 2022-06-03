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
package org.apache.axiom.ts.soap;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.testing.multiton.AdapterFactory;
import org.apache.axiom.testing.multiton.Adapters;

import com.google.auto.service.AutoService;

@AutoService(AdapterFactory.class)
public class SOAPSpecAdapterFactory implements AdapterFactory<SOAPSpec> {
    @Override
    public void createAdapters(SOAPSpec spec, Adapters adapters) {
        if (spec == SOAPSpec.SOAP11) {
            adapters.add(
                    new FactorySelector() {
                        @Override
                        public SOAPFactory getFactory(OMMetaFactory metaFactory) {
                            return metaFactory.getSOAP11Factory();
                        }
                    });
            adapters.add(SOAPVersion.class, SOAPVersion.SOAP11);
        } else if (spec == SOAPSpec.SOAP12) {
            adapters.add(
                    new FactorySelector() {
                        @Override
                        public SOAPFactory getFactory(OMMetaFactory metaFactory) {
                            return metaFactory.getSOAP12Factory();
                        }
                    });
            adapters.add(SOAPVersion.class, SOAPVersion.SOAP12);
        }
    }
}
