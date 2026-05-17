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
package org.apache.axiom.ts.om.document.sr;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javax.xml.stream.XMLStreamReader;
import junit.framework.TestCase;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXParserConfiguration;

/**
 * Tests that the {@link XMLStreamReader} returned by {@link OMContainer#getXMLStreamReader()} for
 * an OM tree created by a builder correctly implements the {@link DTDReader} extension.
 */
public class TestDTDReaderFromParser extends TestCase {
    @Inject
    private OMFactory factory;

    @Inject
    @Named("build")
    private boolean build;

    @Inject
    @Named("cache")
    private boolean cache;

    @Override
    protected void runTest() throws Throwable {
        OMDocument doc = OMXMLBuilderFactory.createOMBuilder(
                        factory,
                        StAXParserConfiguration.STANDALONE,
                        TestDTDReaderFromParser.class.getResourceAsStream("/web_w_dtd.xml"))
                .getDocument();
        if (build) {
            doc.build();
        }
        XMLStreamReader reader = doc.getXMLStreamReader(cache);
        // Note that according to the specification of the DTDReader interface, it is
        // allowed to look up the extension before reaching the DTD event.
        DTDReader dtdReader = (DTDReader) reader.getProperty(DTDReader.PROPERTY);
        assertThat(dtdReader).isNotNull();
        while (reader.next() != XMLStreamReader.DTD) {
            // Just loop
        }
        assertThat(dtdReader.getRootName()).isEqualTo("web-app");
        assertThat(dtdReader.getPublicId()).isEqualTo("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN");
        assertThat(dtdReader.getSystemId()).isEqualTo("http://java.sun.com/dtd/web-app_2_3.dtd");
    }
}
