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
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMContainer#getXMLStreamReaderWithoutCaching()} in the specific case
 * where the element is partially built and the last created node has been modified. In Axiom 1.2.14
 * the information returned for that node was incorrect because the builder switched too early to
 * pull through mode.
 */
public class TestGetXMLStreamReaderWithoutCachingPartiallyBuiltModified extends AxiomTestCase {
    public TestGetXMLStreamReaderWithoutCachingPartiallyBuiltModified(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement root =
                OMXMLBuilderFactory.createOMBuilder(
                                metaFactory.getOMFactory(),
                                new StringReader("<root><a/><b/><c/></root>"))
                        .getDocumentElement();

        OMElement b = root.getFirstChildWithName(new QName("b"));
        b.addAttribute("att", "value", null);
        assertFalse(b.isComplete());

        XMLStreamReader reader = root.getXMLStreamReaderWithoutCaching();

        // Skip to the START_ELEMENT event corresponding to b
        for (int i = 0; i < 4; i++) {
            reader.next();
        }
        assertEquals(XMLStreamReader.START_ELEMENT, reader.getEventType());
        assertEquals("b", reader.getLocalName());

        // The previously added attribute must be visible
        assertEquals(1, reader.getAttributeCount());
        assertEquals("att", reader.getAttributeLocalName(0));
        assertEquals("value", reader.getAttributeValue(0));
    }
}
