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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.ts.AxiomTestCase;

// Regression test for AXIOM-144 and AXIOM-146
public class TestGetXMLStreamReaderCDATAEventFromParser extends AxiomTestCase {
    public TestGetXMLStreamReaderCDATAEventFromParser(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        // Create an element with a CDATA section.
        InputStream is =
                new ByteArrayInputStream("<test><![CDATA[hello world]]></test>".getBytes());
        // Make sure that the parser is non coalescing (otherwise no CDATA events will be
        // reported). This is not the default for Woodstox (see WSTX-140).
        XMLStreamReader reader =
                StAXUtils.createXMLStreamReader(StAXParserConfiguration.NON_COALESCING, is);

        OMElement element =
                OMXMLBuilderFactory.createStAXOMBuilder(metaFactory.getOMFactory(), reader)
                        .getDocumentElement();

        // Build the element so we have a full StAX tree
        element.build();

        // Get the XMLStreamReader for the element. This will return an OMStAXWrapper.
        XMLStreamReader reader2 = element.getXMLStreamReader();
        // Check the sequence of events
        int event = reader2.next();
        assertEquals(XMLStreamReader.START_ELEMENT, event);

        while (reader2.hasNext() && event != XMLStreamReader.CDATA) {
            event = reader2.next();
        }

        // Only woodstox is guaranteed to generate CDATA events if
        // javax.xml.stream.isCoalescing=false
        if (reader.toString().indexOf("wstx") != -1) {
            assertEquals(XMLStreamReader.CDATA, event);
            assertEquals("hello world", reader2.getText()); // AXIOM-146
            assertTrue(
                    Arrays.equals(
                            "hello world".toCharArray(), reader2.getTextCharacters())); // AXIOM-144
            assertEquals(XMLStreamReader.END_ELEMENT, reader2.next());
        }
    }
}
