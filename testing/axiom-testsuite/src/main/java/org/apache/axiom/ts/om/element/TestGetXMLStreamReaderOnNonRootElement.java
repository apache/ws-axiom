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

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMContainer#getXMLStreamReader(boolean)} produces the correct sequence of
 * events when called on an {@link OMElement} that is not the root element. Also tests that the rest
 * of the document can be built after consuming the {@link XMLStreamReader}.
 *
 * <p>This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-288">AXIOM-288</a>.
 */
public class TestGetXMLStreamReaderOnNonRootElement extends AxiomTestCase {
    private final boolean cache;

    public TestGetXMLStreamReaderOnNonRootElement(OMMetaFactory metaFactory, boolean cache) {
        super(metaFactory);
        this.cache = cache;
        addTestParameter("cache", cache);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement root =
                AXIOMUtil.stringToOM(
                        metaFactory.getOMFactory(), "<a><b><c/></b><d>content</d></a>");
        OMElement b = (OMElement) root.getFirstOMChild();
        XMLStreamReader stream = b.getXMLStreamReader(cache);
        assertEquals(XMLStreamReader.START_DOCUMENT, stream.getEventType());
        assertEquals(XMLStreamReader.START_ELEMENT, stream.next());
        assertEquals("b", stream.getLocalName());
        assertEquals(XMLStreamReader.START_ELEMENT, stream.next());
        assertEquals("c", stream.getLocalName());
        assertEquals(XMLStreamReader.END_ELEMENT, stream.next());
        assertEquals(XMLStreamReader.END_ELEMENT, stream.next());
        assertEquals(XMLStreamReader.END_DOCUMENT, stream.next());
        OMElement d = (OMElement) b.getNextOMSibling();
        assertEquals("content", d.getText());
        root.close(false);
    }
}
