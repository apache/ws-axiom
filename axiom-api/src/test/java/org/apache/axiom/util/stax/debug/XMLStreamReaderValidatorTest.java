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
package org.apache.axiom.util.stax.debug;

import java.io.StringReader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.axiom.om.util.StAXUtils;

public class XMLStreamReaderValidatorTest extends TestCase {
    private static XMLStreamReaderValidator createValidatorForParser(String xml)
            throws XMLStreamException {
        return new XMLStreamReaderValidator(
                StAXUtils.createXMLStreamReader(new StringReader(xml)), true);
    }

    /**
     * Test that {@link XMLStreamReaderValidator} correctly keeps track of events when {@link
     * XMLStreamReader#getElementText()} is used. This is a regression test for <a
     * href="https://issues.apache.org/jira/browse/AXIOM-63">AXIOM-63</a>.
     *
     * @throws Exception
     */
    public void testGetElementText() throws Exception {
        XMLStreamReaderValidator validator = createValidatorForParser("<root><a>text</a></root>");
        assertEquals(XMLStreamReader.START_ELEMENT, validator.next());
        assertEquals(XMLStreamReader.START_ELEMENT, validator.next());
        assertEquals("text", validator.getElementText());
        assertEquals(XMLStreamReader.END_ELEMENT, validator.getEventType());
        assertEquals(XMLStreamReader.END_ELEMENT, validator.next());
        assertEquals(XMLStreamReader.END_DOCUMENT, validator.next());
    }

    /**
     * Test that {@link XMLStreamReaderValidator} correctly keeps track of events when {@link
     * XMLStreamReader#nextTag()} is used. This is a regression test for <a
     * href="https://issues.apache.org/jira/browse/AXIOM-63">AXIOM-63</a>.
     *
     * @throws Exception
     */
    public void testNextTag() throws Exception {
        XMLStreamReaderValidator validator = createValidatorForParser("<root>\n  <a/>\n</root>");
        assertEquals(XMLStreamReader.START_ELEMENT, validator.nextTag());
        assertEquals(XMLStreamReader.START_ELEMENT, validator.nextTag());
        assertEquals(XMLStreamReader.END_ELEMENT, validator.next());
        assertEquals(XMLStreamReader.END_ELEMENT, validator.nextTag());
        assertEquals(XMLStreamReader.END_DOCUMENT, validator.next());
    }
}
