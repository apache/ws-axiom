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
package org.apache.axiom.ts.om.container;

import java.io.InputStream;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.testutils.conformance.ConformanceTestFile;
import org.apache.axiom.testutils.stax.XMLStreamReaderComparator;
import org.apache.axiom.ts.ConformanceTestCase;
import org.xml.sax.InputSource;

/**
 * Test comparing the output of {@link OMContainer#getXMLStreamReader(boolean)} with that of a
 * native StAX parser.
 */
public class TestGetXMLStreamReader extends ConformanceTestCase {
    private final BuilderFactory builderFactory;
    private final OMContainerFactory containerFactory;
    private final boolean cache;
    
    public TestGetXMLStreamReader(OMMetaFactory metaFactory, ConformanceTestFile file,
            BuilderFactory builderFactory, OMContainerFactory containerFactory, boolean cache) {
        super(metaFactory, file);
        this.builderFactory = builderFactory;
        this.containerFactory = containerFactory;
        this.cache = cache;
        builderFactory.addTestProperties(this);
        containerFactory.addTestProperties(this);
        addTestProperty("cache", Boolean.toString(cache));
    }
    
    protected final void runTest() throws Throwable {
        InputStream in = file.getAsStream();
        try {
            XMLStreamReader expected = StAXUtils.createXMLStreamReader(TEST_PARSER_CONFIGURATION, file.getUrl().toString(), in);
            try {
                OMXMLParserWrapper builder = builderFactory.getBuilder(metaFactory, new InputSource(file.getUrl().toString()));
                try {
                    XMLStreamReader actual = containerFactory.getContainer(builder).getXMLStreamReader(cache);
                    XMLStreamReaderComparator comparator = new XMLStreamReaderComparator(containerFactory.filter(expected), containerFactory.filter(actual));
                    builderFactory.configureXMLStreamReaderComparator(comparator);
                    comparator.compare();
                } finally {
                    builder.close();
                }
            } finally {
                expected.close();
            }
        } finally {
            in.close();
        }
    }
}
