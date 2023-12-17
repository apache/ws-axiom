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
package org.apache.axiom.ts.dom.builder;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.net.protocol.registry.InstrumentedDataProvider;
import org.apache.axiom.net.protocol.registry.URLRegistration;
import org.apache.axiom.net.protocol.registry.URLRegistry;
import org.apache.axiom.ts.dom.DOMTestCase;
import org.apache.axiom.ts.xml.XMLSample;
import org.w3c.dom.Document;

/** Tests {@link DocumentBuilder#parse(String)}. */
public class TestParseURI extends DOMTestCase {
    public TestParseURI(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        InstrumentedDataProvider dataProvider =
                new InstrumentedDataProvider(XMLSample.SIMPLE.getUrl()::openStream);
        URLRegistration registration = URLRegistry.register(dataProvider);
        try {
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document document = builder.parse(registration.getURL().toExternalForm());
            assertThat(document.getDocumentElement().getLocalName()).isEqualTo("root");
            assertThat(dataProvider.getOpenStreamCount()).isEqualTo(0);
        } finally {
            registration.unregister();
        }
    }
}
