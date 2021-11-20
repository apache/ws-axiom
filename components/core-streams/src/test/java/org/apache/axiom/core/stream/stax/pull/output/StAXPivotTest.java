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
package org.apache.axiom.core.stream.stax.pull.output;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.junit.Test;

public class StAXPivotTest {
    private static StAXPivot createStAXPivot(Action... actions) throws StreamException {
        StAXPivot pivot = new StAXPivot(null);
        pivot.setReader(new FakeReader(pivot, actions));
        return pivot;
    }

    @Test
    public void testSuccess() throws Exception {
        StAXPivot pivot = createStAXPivot(
                Action.DEFAULT_START_DOCUMENT,
                h -> h.startElement("urn:test", "test", "p"),
                XmlHandler::attributesCompleted,
                h -> h.processEntityReference("ent", "foobar"),
                XmlHandler::endElement,
                XmlHandler::completed);
        pivot.require(XMLStreamConstants.START_DOCUMENT, null, null);
        pivot.next();
        pivot.require(XMLStreamConstants.START_ELEMENT, "urn:test", "test");
        pivot.next();
        pivot.require(XMLStreamConstants.ENTITY_REFERENCE, null, "ent");
        pivot.next();
        pivot.require(XMLStreamConstants.END_ELEMENT, "urn:test", "test");
        pivot.next();
        pivot.require(XMLStreamConstants.END_DOCUMENT, null, null);
    }
    
    @Test(expected=XMLStreamException.class)
    public void testEventTypeMismatch() throws Exception {
        StAXPivot pivot = createStAXPivot(Action.DEFAULT_START_DOCUMENT);
        pivot.require(XMLStreamConstants.CHARACTERS, null, null);
    }
    
    @Test(expected=XMLStreamException.class)
    public void testLocalNameOnStartDocument() throws Exception {
        StAXPivot pivot = createStAXPivot(Action.DEFAULT_START_DOCUMENT);
        pivot.require(XMLStreamConstants.START_DOCUMENT, null, "test");
    }
    
    @Test(expected=XMLStreamException.class)
    public void testLocalNameMismatchOnStartElement() throws Exception {
        StAXPivot pivot = createStAXPivot(
                Action.DEFAULT_START_DOCUMENT,
                h -> h.startElement("urn:test", "test", "p"),
                XmlHandler::attributesCompleted);
        pivot.next();
        pivot.require(XMLStreamConstants.START_ELEMENT, "urn:test", "wrong_name");
    }
    
    @Test(expected=XMLStreamException.class)
    public void testNamespaceURIOnStartDocument() throws Exception {
        StAXPivot pivot = createStAXPivot(Action.DEFAULT_START_DOCUMENT);
        pivot.require(XMLStreamConstants.START_DOCUMENT, "http://example.org", null);
    }
    
    @Test(expected=XMLStreamException.class)
    public void testNamespaceURIMismatchOnStartElement() throws Exception {
        StAXPivot pivot = createStAXPivot(
                Action.DEFAULT_START_DOCUMENT,
                h -> h.startElement("urn:test", "test", "p"),
                XmlHandler::attributesCompleted);
        pivot.next();
        pivot.require(XMLStreamConstants.START_ELEMENT, "urn:wrong_uri", "test");
    }

    @Test
    public void testCDATASection() throws Exception {
        StAXPivot pivot = createStAXPivot(
                Action.DEFAULT_START_DOCUMENT,
                h -> {
                    h.startElement("", "root", "");
                    h.attributesCompleted();
                },
                h -> {
                    h.startCDATASection();
                    h.processCharacterData("test", false);
                    h.endCDATASection();
                },
                XmlHandler::endElement,
                XmlHandler::completed);
        assertThat(pivot.getEventType()).isEqualTo(XMLStreamReader.START_DOCUMENT);
        assertThat(pivot.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        assertThat(pivot.next()).isEqualTo(XMLStreamReader.CDATA);
        assertThat(pivot.getText()).isEqualTo("test");
        assertThat(pivot.next()).isEqualTo(XMLStreamReader.END_ELEMENT);
        assertThat(pivot.next()).isEqualTo(XMLStreamReader.END_DOCUMENT);
    }

    @Test
    public void testGetElementText() throws Exception {
        StAXPivot pivot = createStAXPivot(
                Action.DEFAULT_START_DOCUMENT,
                h -> h.startElement("", "root", ""),
                XmlHandler::attributesCompleted,
                h -> h.processCharacterData("abc", false),
                XmlHandler::startCDATASection,
                h -> h.processCharacterData("def", false),
                XmlHandler::endCDATASection,
                h -> h.processCharacterData("ghi", false),
                XmlHandler::startComment,
                h -> h.processCharacterData("jkl", false),
                XmlHandler::endComment,
                h -> h.processCharacterData("mno", false),
                h -> h.startProcessingInstruction("pi"),
                h -> h.processCharacterData("pqr", false),
                XmlHandler::endProcessingInstruction,
                h -> h.processCharacterData("stu", false),
                h -> h.processEntityReference("ent", "vwx"),
                h -> h.processCharacterData("yz", false),
                XmlHandler::endElement,
                XmlHandler::completed);
        assertThat(pivot.getEventType()).isEqualTo(XMLStreamReader.START_DOCUMENT);
        assertThat(pivot.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        assertThat(pivot.getElementText()).isEqualTo("abcdefghimnostuvwxyz");
        assertThat(pivot.getEventType()).isEqualTo(XMLStreamReader.END_ELEMENT);
        assertThat(pivot.next()).isEqualTo(XMLStreamReader.END_DOCUMENT);
    }

    @Test
    public void testGetElementTextUnexpectedChildElement() throws Exception {
        StAXPivot pivot = createStAXPivot(
                Action.DEFAULT_START_DOCUMENT,
                h -> h.startElement("", "root", ""),
                XmlHandler::attributesCompleted,
                h -> h.processCharacterData("abc", false),
                h -> h.startElement("", "child", ""),
                XmlHandler::attributesCompleted);
        assertThat(pivot.getEventType()).isEqualTo(XMLStreamReader.START_DOCUMENT);
        assertThat(pivot.next()).isEqualTo(XMLStreamReader.START_ELEMENT);
        assertThrows(XMLStreamException.class, () -> pivot.getElementText());
    }
}
