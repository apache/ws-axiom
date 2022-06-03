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

import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.testing.multiton.AdapterFactory;
import org.apache.axiom.testing.multiton.Adapters;

import com.google.auto.service.AutoService;

@AutoService(AdapterFactory.class)
public class HeaderBlockAttributeAdapterFactory implements AdapterFactory<HeaderBlockAttribute> {
    @Override
    public void createAdapters(HeaderBlockAttribute attribute, Adapters adapters) {
        if (attribute == HeaderBlockAttribute.MUST_UNDERSTAND) {
            adapters.add(
                    new BooleanAttributeAccessor() {
                        @Override
                        public boolean getValue(SOAPHeaderBlock headerBlock) {
                            return headerBlock.getMustUnderstand();
                        }

                        @Override
                        public void setValue(SOAPHeaderBlock headerBlock, boolean value) {
                            headerBlock.setMustUnderstand(value);
                        }
                    });
        } else if (attribute == HeaderBlockAttribute.RELAY) {
            adapters.add(
                    new BooleanAttributeAccessor() {
                        @Override
                        public boolean getValue(SOAPHeaderBlock headerBlock) {
                            return headerBlock.getRelay();
                        }

                        @Override
                        public void setValue(SOAPHeaderBlock headerBlock, boolean value) {
                            headerBlock.setRelay(value);
                        }
                    });
        }
    }
}
