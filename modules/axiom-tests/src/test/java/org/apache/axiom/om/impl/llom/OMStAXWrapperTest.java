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
package org.apache.axiom.om.impl.llom;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.apache.axiom.om.util.StAXUtils;

public class OMStAXWrapperTest extends TestCase {
    // Regression test for WSCOMMONS-338 and WSCOMMONS-341
    public void testCDATAEvent() throws Exception {
        // Make sure that the parser is non coalescing (otherwise no CDATA events will be
        // reported). This is not the default for Woodstox (see WSTX-140).
        StAXUtils.getXMLInputFactory().setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        // Create an element with a CDATA section.
        XMLStreamReader reader = StAXUtils.createXMLStreamReader(new ByteArrayInputStream("<test><![CDATA[test]]></test>".getBytes()));
        OMFactory factory = new OMLinkedListImplFactory();
        OMElement element = new StAXOMBuilder(factory, reader).getDocumentElement();
        // Get the XMLStreamReader for the element. This will return an OMStAXWrapper.
        XMLStreamReader reader2 = element.getXMLStreamReader();
        // Check the sequence of events
        assertEquals(XMLStreamReader.START_ELEMENT, reader2.next());
        assertEquals(XMLStreamReader.CDATA, reader2.next());
        assertEquals("test", reader2.getText()); // WSCOMMONS-341
        assertTrue(Arrays.equals("test".toCharArray(), reader2.getTextCharacters())); // WSCOMMONS-338
        assertEquals(XMLStreamReader.END_ELEMENT, reader2.next());
    }
}
