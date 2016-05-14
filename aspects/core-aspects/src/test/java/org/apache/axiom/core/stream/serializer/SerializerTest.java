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
package org.apache.axiom.core.stream.serializer;

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import org.apache.axiom.core.stream.StreamException;
import org.apache.commons.io.output.NullOutputStream;
import org.junit.Test;

public class SerializerTest {
    @Test
    public void testEmptyElement() throws Exception {
        StringWriter sw = new StringWriter();
        SerializerXmlHandler handler = new SerializerXmlHandler(sw);
        handler.startElement("", "test", "");
        handler.attributesCompleted();
        handler.endElement();
        assertThat(sw.toString()).matches("<test ?/>");
    }

    /**
     * Test that characters are converted to entities only when necessary.
     * 
     * @throws Exception
     */
    @Test
    public void testUnmappableCharacterInCharacterData() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerXmlHandler handler = new SerializerXmlHandler(baos, "iso-8859-15");
        handler.startElement("", "test", "");
        handler.attributesCompleted();
        handler.processCharacterData("a\u03A3\u20AC", false);  // 20AC = Euro sign
        handler.endElement();
        handler.completed();
        assertThat(new String(baos.toByteArray(), "iso-8859-15")).isEqualTo("<test>a&#931;\u20AC</test>");
    }

    @Test
    public void testUnmappableCharacterInAttributeValue() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerXmlHandler handler = new SerializerXmlHandler(baos, "ascii");
        handler.startElement("", "test", "");
        handler.processAttribute("", "attr", "", "néant", "CDATA", true);
        handler.attributesCompleted();
        handler.endElement();
        handler.completed();
        assertThat(new String(baos.toByteArray(), "ascii")).isEqualTo("<test attr=\"n&#233;ant\"/>");
    }

    /**
     * Tests the scenario described in XALANJ-2593.
     * 
     * @throws Exception
     */
    @Test
    public void testUnmappableSurrogatePairInAttributeValue() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializerXmlHandler handler = new SerializerXmlHandler(baos, "ascii");
        handler.startElement("", "x", "");
        handler.processAttribute("", "y", "", "\uD84C\uDFB4 - \uD841\uDE28", "CDATA", true);
        handler.attributesCompleted();
        handler.endElement();
        handler.completed();
        assertThat(new String(baos.toByteArray(), "ascii")).isEqualTo("<x y=\"&#144308; - &#132648;\"/>");
    }

    @Test(expected=StreamException.class)
    public void testUnmappableCharacterInComment() throws Exception {
        SerializerXmlHandler handler = new SerializerXmlHandler(new NullOutputStream(), "iso-8859-1");
        handler.startFragment();
        handler.startComment();
        handler.processCharacterData("\u20AC", false);
        handler.endComment();
        handler.completed();
    }

    @Test
    // TODO: this should throw an exception
    public void testUnmappableCharacterInCDATASection() throws Exception {
        SerializerXmlHandler handler = new SerializerXmlHandler(new NullOutputStream(), "ascii");
        handler.startFragment();
        handler.startCDATASection();
        handler.processCharacterData("c'est la fête!", false);
        handler.endCDATASection();
        handler.completed();
    }

    @Test(expected=StreamException.class)
    public void testUnmappableCharacterInProcessingInstruction() throws Exception {
        SerializerXmlHandler handler = new SerializerXmlHandler(new NullOutputStream(), "ascii");
        handler.startFragment();
        handler.startProcessingInstruction("test");
        handler.processCharacterData("c'est la fête!", false);
        handler.endProcessingInstruction();
        handler.completed();
    }

    @Test(expected=StreamException.class)
    public void testUnmappableCharacterInName() throws Exception {
        SerializerXmlHandler handler = new SerializerXmlHandler(new NullOutputStream(), "iso-8859-15");
        handler.startFragment();
        handler.startElement("", "\u0370", "");
        handler.attributesCompleted();
        handler.endElement();
        handler.completed();
    }
}
