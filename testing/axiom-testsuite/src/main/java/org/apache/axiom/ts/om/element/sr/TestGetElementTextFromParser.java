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
package org.apache.axiom.ts.om.element.sr;

import java.io.StringReader;
import java.util.Iterator;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.dimension.BuilderFactory;
import org.xml.sax.InputSource;

public class TestGetElementTextFromParser extends AxiomTestCase {
    private final BuilderFactory builderFactory;
    private final boolean cache;
    private final int build;

    /**
     * Constructor.
     *
     * @param metaFactory
     * @param builderFactory
     * @param cache
     * @param build the number of descendants that should be built before calling {@link
     *     OMContainer#getXMLStreamReader(boolean)}
     */
    public TestGetElementTextFromParser(
            OMMetaFactory metaFactory, BuilderFactory builderFactory, boolean cache, int build) {
        super(metaFactory);
        this.builderFactory = builderFactory;
        this.cache = cache;
        this.build = build;
        builderFactory.addTestParameters(this);
        addTestParameter("cache", cache);
        addTestParameter("build", build);
    }

    @Override
    protected void runTest() throws Throwable {
        // Note: We test getElementText on a child element ("b") of the element from which we
        // request the XMLStreamReader ("a"). This is to make sure that the XMLStreamReader
        // implementation actually delegates to the underlying parser (which is not necessarily the
        // case on "a").
        OMXMLParserWrapper builder =
                builderFactory.getBuilder(
                        metaFactory,
                        new InputSource(new StringReader("<a><b>AB<!--comment text-->CD</b></a>")));
        OMElement element = builder.getDocumentElement();

        // Build a certain number of descendants. This is used to test scenarios where the
        // XMLStreamReader needs to switch to pull through mode in the middle of the element from
        // which we attempt to get the text.
        Iterator<OMNode> it = element.getDescendants(true);
        for (int i = 0; i < build; i++) {
            it.next();
        }

        XMLStreamReader reader = element.getXMLStreamReader(cache);
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());

        assertEquals("ABCD", reader.getElementText());
    }
}
