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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that the rest of a document can still be built after calling {@link
 * OMContainer#getXMLStreamReader(boolean)} and closing the returned {@link XMLStreamReader}. A call
 * to {@link XMLStreamReader#close()} must not close the builder in this case.
 *
 * <p>This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-288">AXIOM-288</a>.
 */
public class TestCloseAndContinueBuilding extends AxiomTestCase {
    private final boolean cache;

    public TestCloseAndContinueBuilding(OMMetaFactory metaFactory, boolean cache) {
        super(metaFactory);
        this.cache = cache;
        addTestParameter("cache", cache);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement root =
                OMXMLBuilderFactory.createOMBuilder(
                                metaFactory.getOMFactory(),
                                new StringReader(
                                        "<root><a><b>some text</b></a><c>content</c></root>"))
                        .getDocumentElement();
        OMElement a = (OMElement) root.getFirstOMChild();
        XMLStreamReader reader = a.getXMLStreamReader(cache);
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals(new QName("b"), reader.getName());
        reader.close();
        assertFalse(root.isComplete());
        OMElement c = (OMElement) a.getNextOMSibling();
        assertEquals("content", c.getText());
    }
}
