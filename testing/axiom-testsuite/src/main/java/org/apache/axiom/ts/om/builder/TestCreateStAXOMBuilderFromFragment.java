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
package org.apache.axiom.ts.om.builder;

import java.io.StringReader;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMXMLBuilderFactory#createStAXOMBuilder(OMFactory, XMLStreamReader)}
 * if the supplied {@link XMLStreamReader} is positioned on a {@link
 * XMLStreamConstants#START_ELEMENT} event.
 */
public class TestCreateStAXOMBuilderFromFragment extends AxiomTestCase {
    public TestCreateStAXOMBuilderFromFragment(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        XMLStreamReader reader =
                StAXUtils.createXMLStreamReader(new StringReader("<a><b>text</b></a>"));
        // Position the reader on the event for <b>
        while (reader.getEventType() != XMLStreamReader.START_ELEMENT
                || !reader.getLocalName().equals("b")) {
            reader.next();
        }
        // Check that the builder only builds the part of the document corresponding to <b>text</b>
        OMElement element =
                OMXMLBuilderFactory.createStAXOMBuilder(metaFactory.getOMFactory(), reader)
                        .getDocumentElement();
        assertEquals("b", element.getLocalName());
        OMNode child = element.getFirstOMChild();
        assertTrue(child instanceof OMText);
        assertNull(element.getNextOMSibling());
        // Check that the original reader is now positioned on the event just following </b>
        assertEquals(XMLStreamReader.END_ELEMENT, reader.getEventType());
        assertEquals("a", reader.getLocalName());
    }
}
