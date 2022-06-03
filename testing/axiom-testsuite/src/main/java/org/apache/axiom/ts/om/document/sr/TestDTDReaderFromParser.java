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

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that the {@link XMLStreamReader} returned by {@link OMContainer#getXMLStreamReader()} for
 * an OM tree created by a builder correctly implements the {@link DTDReader} extension.
 */
public class TestDTDReaderFromParser extends AxiomTestCase {
    private final boolean build;
    private final boolean cache;

    public TestDTDReaderFromParser(OMMetaFactory metaFactory, boolean build, boolean cache) {
        super(metaFactory);
        this.build = build;
        this.cache = cache;
        addTestParameter("build", build);
        addTestParameter("cache", cache);
    }

    @Override
    protected void runTest() throws Throwable {
        OMDocument doc =
                OMXMLBuilderFactory.createOMBuilder(
                                metaFactory.getOMFactory(),
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
        assertNotNull(dtdReader);
        while (reader.next() != XMLStreamReader.DTD) {
            // Just loop
        }
        assertEquals("web-app", dtdReader.getRootName());
        assertEquals(
                "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN", dtdReader.getPublicId());
        assertEquals("http://java.sun.com/dtd/web-app_2_3.dtd", dtdReader.getSystemId());
    }
}
