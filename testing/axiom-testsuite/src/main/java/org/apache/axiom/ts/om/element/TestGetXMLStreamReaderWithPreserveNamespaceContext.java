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
package org.apache.axiom.ts.om.element;

import static com.google.common.truth.Truth.assertThat;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMElement#getXMLStreamReader(boolean,
 * OMXMLStreamReaderConfiguration)} in conjunction with {@link
 * OMXMLStreamReaderConfiguration#isPreserveNamespaceContext()}.
 */
public class TestGetXMLStreamReaderWithPreserveNamespaceContext extends AxiomTestCase {
    private final boolean preserveNamespaceContext;
    private final boolean cache;

    public TestGetXMLStreamReaderWithPreserveNamespaceContext(
            OMMetaFactory metaFactory, boolean preserveNamespaceContext, boolean cache) {
        super(metaFactory);
        this.preserveNamespaceContext = preserveNamespaceContext;
        addTestParameter("preserveNamespaceContext", preserveNamespaceContext);
        this.cache = cache;
        addTestParameter("cache", cache);
    }

    @Override
    protected void runTest() throws Throwable {
        InputStream in =
                TestGetXMLStreamReaderWithPreserveNamespaceContext.class.getResourceAsStream(
                        "AXIOM-114.xml");
        OMElement root =
                OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), in)
                        .getDocumentElement();
        root.declareNamespace("http://example.org", "p");
        OMXMLStreamReaderConfiguration configuration = new OMXMLStreamReaderConfiguration();
        configuration.setPreserveNamespaceContext(preserveNamespaceContext);
        XMLStreamReader reader =
                root.getFirstElement().getFirstElement().getXMLStreamReader(cache, configuration);
        assertThat(reader.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        Set<String> prefixes = new HashSet<>();
        for (int i = 0; i < reader.getNamespaceCount(); i++) {
            prefixes.add(reader.getNamespacePrefix(i));
        }
        if (preserveNamespaceContext) {
            assertThat(prefixes).containsExactly("soapenv", "xsd", "xsi", "ns", "p");
        } else {
            assertThat(prefixes).containsExactly("ns");
        }
        // Make sure that we start pulling events directly from the underlying parser.
        reader.nextTag();
        // The following assertions are true irrespective of the value of preserveNamespaceContext.
        assertThat(reader.getNamespaceURI("xsd")).isEqualTo("http://www.w3.org/2001/XMLSchema");
        // Namespace declarations added programmatically on an ancestor should also be visible.
        assertThat(reader.getNamespaceURI("p")).isEqualTo("http://example.org");
        NamespaceContext nc = reader.getNamespaceContext();
        assertThat(nc.getPrefix("http://www.w3.org/2001/XMLSchema")).isEqualTo("xsd");
        assertThat(nc.getPrefix("http://example.org")).isEqualTo("p");
    }
}
