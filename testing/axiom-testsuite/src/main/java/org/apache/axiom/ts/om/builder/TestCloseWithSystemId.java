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
package org.apache.axiom.ts.om.builder;

import static com.google.common.truth.Truth.assertThat;

import javax.activation.URLDataSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.net.protocol.registry.URLRegistration;
import org.apache.axiom.net.protocol.registry.URLRegistry;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.testutils.activation.InstrumentedDataSource;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.xml.XMLSample;

public class TestCloseWithSystemId extends AxiomTestCase {
    public TestCloseWithSystemId(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        InstrumentedDataSource ds =
                new InstrumentedDataSource(new URLDataSource(XMLSample.SIMPLE.getUrl()));
        URLRegistration registration = URLRegistry.register(ds);
        try {
            OMXMLParserWrapper builder =
                    OMXMLBuilderFactory.createOMBuilder(
                            metaFactory.getOMFactory(),
                            new StreamSource(registration.getURL().toExternalForm()));
            builder.getDocumentElement();
            builder.close();
            // Since the caller doesn't have control over the stream, the builder is responsible
            // for closing it.
            assertThat(ds.getOpenStreamCount()).isEqualTo(0);
        } finally {
            registration.unregister();
        }
    }
}
