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

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that the sequence of events produced by the {@link XMLStreamReader} returned by {@link
 * OMContainer#getXMLStreamReader(boolean)} is correct for a programmatically created {@link
 * OMElement} that has an incomplete descendant (produced by {@link
 * OMXMLParserWrapper#getDocumentElement(boolean)} with <code>discardDocument</code> set to true).
 *
 * <p>This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-431">AXIOM-431</a>.
 */
public class TestGetXMLStreamReaderWithIncompleteDescendant extends AxiomTestCase {
    private final boolean cache;

    public TestGetXMLStreamReaderWithIncompleteDescendant(
            OMMetaFactory metaFactory, boolean cache) {
        super(metaFactory);
        this.cache = cache;
        addTestParameter("cache", cache);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement root = factory.createOMElement(new QName("root"));
        OMElement child =
                OMXMLBuilderFactory.createOMBuilder(factory, new StringReader("<a>test</a>"))
                        .getDocumentElement(true);
        root.addChild(child);
        assertFalse(child.isComplete());
        XMLStreamReader stream = root.getXMLStreamReader(cache);
        assertEquals(XMLStreamReader.START_ELEMENT, stream.next());
        assertEquals("root", stream.getLocalName());
        assertEquals(XMLStreamReader.START_ELEMENT, stream.next());
        assertEquals("a", stream.getLocalName());
        assertEquals(XMLStreamReader.CHARACTERS, stream.next());
        assertEquals("test", stream.getText());
        assertEquals(XMLStreamReader.END_ELEMENT, stream.next());
        assertEquals("a", stream.getLocalName());
        assertEquals(XMLStreamReader.END_ELEMENT, stream.next());
        assertEquals("root", stream.getLocalName());
        assertEquals(XMLStreamReader.END_DOCUMENT, stream.next());
        assertEquals(cache, child.isComplete());
    }
}
