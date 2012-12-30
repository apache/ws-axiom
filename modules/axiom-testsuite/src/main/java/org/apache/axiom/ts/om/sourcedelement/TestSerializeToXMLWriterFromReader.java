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
package org.apache.axiom.ts.om.sourcedelement;

import java.io.StringWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.AxiomTestCase;
import org.custommonkey.xmlunit.XMLAssert;

/**
 * Tests {@link OMSourcedElement#getXMLStreamReader(boolean)}.
 */
public class TestSerializeToXMLWriterFromReader extends AxiomTestCase {
    private final boolean destructive;
    private final boolean cache;
    private final int expand;
    private final int count;
    
    /**
     * Constructor.
     * 
     * @param metaFactory
     *            the meta factory for the implementation to be tested
     * @param destructive
     *            determines if the {@link OMDataSource} is destructive or not
     * @param cache
     *            the argument to be passed to {@link OMSourcedElement#getXMLStreamReader(boolean)}
     * @param expand
     *            determines if and how the sourced element should be expanded before calling
     *            {@link OMSourcedElement#getXMLStreamReader(boolean)}: 0 = don't expand; 1 =
     *            expand; 2 = expand and build
     * @param count
     *            the number of times {@link OMSourcedElement#getXMLStreamReader(boolean)} will be
     *            called; the only meaningful values are 1 and 2
     */
    public TestSerializeToXMLWriterFromReader(OMMetaFactory metaFactory, boolean destructive, boolean cache, int expand, int count) {
        super(metaFactory);
        this.destructive = destructive;
        this.cache = cache;
        this.expand = expand;
        this.count = count;
        addTestProperty("destructive", String.valueOf(destructive));
        addTestProperty("cache", String.valueOf(cache));
        addTestProperty("expand", String.valueOf(expand));
        addTestProperty("count", String.valueOf(count));
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMSourcedElement element = TestDocument.DOCUMENT1.createOMSourcedElement(factory, destructive);
        if (expand != 0) {
            element.getFirstOMChild();
        }
        if (expand == 2) {
            element.build();
        }
        for (int iteration=0; iteration<count; iteration++) {
            boolean expectException = iteration != 0 && expand != 2 && !cache && (destructive || expand == 1);
            StringWriter writer = new StringWriter();
            try {
                OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(factory, element.getXMLStreamReader(cache));
                builder.getDocument().serialize(writer);
                if (expectException) {
                    fail("Expected exception");
                }
            } catch (Exception ex) {
                if (!expectException) {
                    throw ex;
                } else {
                    continue;
                }
            }
            XMLAssert.assertXMLEqual(TestDocument.DOCUMENT1.getContent(), writer.toString());
            // If the underlying OMDataSource is non destructive, the expansion status should not have been
            // changed by the call to getXMLStreamReader. If it is destructive and caching is enabled, then
            // the sourced element should be expanded.
            if (expand != 0 || (destructive && cache)) {
                assertTrue(element.isExpanded());
                assertEquals(expand == 2 || (expand == 1 && cache) || (expand == 0 && destructive && cache), element.isComplete());
            } else {
                assertFalse(element.isExpanded());
            }
        }
    }
}
