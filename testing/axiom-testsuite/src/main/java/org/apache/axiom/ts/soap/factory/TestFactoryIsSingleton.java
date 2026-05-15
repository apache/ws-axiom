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
package org.apache.axiom.ts.soap.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import junit.framework.TestCase;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.soap.FactorySelector;
import org.apache.axiom.ts.soap.SOAPSpec;

/**
 * Tests that the SOAP factories returned by {@link OMMetaFactory} are singletons. More precisely
 * this unit test checks that subsequent calls to {@link OMMetaFactory#getSOAP11Factory()} and
 * {@link OMMetaFactory#getSOAP12Factory()} return the same instances.
 */
public class TestFactoryIsSingleton extends TestCase {
    @Inject
    private SOAPSpec spec;

    @Inject
    private OMMetaFactory metaFactory;

    @Override
    protected void runTest() throws Throwable {
        FactorySelector factorySelector = spec.getAdapter(FactorySelector.class);
        assertThat(factorySelector.getFactory(metaFactory)).isSameAs(factorySelector.getFactory(metaFactory));
    }
}
