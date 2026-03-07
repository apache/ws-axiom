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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.axiom.core.stream.StreamException;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.io.output.NullWriter;
import org.junit.Test;

public class SerializerTest {
    @Test
    public void testEmptyElement() throws Exception {
        StringWriter sw = new StringWriter();
        Serializer handler = new Serializer(sw);
        handler.startFragment();
        handler.startElement("", "test", "");
        handler.attributesCompleted();
        handler.endElement();
        handler.completed();
        assertThat(sw.toString()).matches("<test ?/>");
    }

    /**
     * Tests the scenario described in AXIOM-509.
     *
     * @throws Exception
     */
    @Test
    public void testMixedContentAfterEmptyElement() throws Exception {
        StringWriter sw = new StringWriter();
        Serializer handler = new Serializer(sw);
        handler.startFragment();
        handler.startElement("", "test", "");
        handler.attributesCompleted();
        handler.endElement();
        handler.processCharacterData("R&D", false);
        handler.completed();
        assertThat(sw.toString()).matches("<test ?/>R&amp;D");
    }

    /**
     * Test that characters are converted to entities only when necessary.
     *
     * @throws Exception
     */
    @Test
    public void testUnmappableCharacterInCharacterData() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Serializer handler = new Serializer(baos, "iso-8859-15");
        handler.startFragment();
        handler.startElement("", "test", "");
        handler.attributesCompleted();
        handler.processCharacterData("a\u03A3\u20AC", false); // 20AC = Euro sign
        handler.endElement();
        handler.completed();
        assertThat(new String(baos.toByteArray(), "iso-8859-15"))
                .isEqualTo("<test>a&#x3a3;\u20AC</test>");
    }

    @Test
    public void testUnmappableCharacterInAttributeValue() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Serializer handler = new Serializer(baos, "ascii");
        handler.startFragment();
        handler.startElement("", "test", "");
        handler.processAttribute("", "attr", "", "néant", "CDATA", true);
        handler.attributesCompleted();
        handler.endElement();
        handler.completed();
        assertThat(new String(baos.toByteArray(), StandardCharsets.US_ASCII))
                .isEqualTo("<test attr=\"n&#xe9;ant\"/>");
    }

    /**
     * Tests the scenario described in XALANJ-2593.
     *
     * @throws Exception
     */
    @Test
    public void testUnmappableSurrogatePairInAttributeValue() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Serializer handler = new Serializer(baos, "ascii");
        handler.startFragment();
        handler.startElement("", "x", "");
        handler.processAttribute("", "y", "", "\uD84C\uDFB4 - \uD841\uDE28", "CDATA", true);
        handler.attributesCompleted();
        handler.endElement();
        handler.completed();
        assertThat(new String(baos.toByteArray(), StandardCharsets.US_ASCII))
                .isEqualTo("<x y=\"&#x233b4; - &#x20628;\"/>");
    }

    @Test
    public void testUnmappableCharacterInComment() throws Exception {
        Serializer handler = new Serializer(NullOutputStream.INSTANCE, "iso-8859-1");
        handler.startFragment();
        handler.startComment();
        assertThatThrownBy(() -> handler.processCharacterData("\u20AC", false))
                .isInstanceOf(StreamException.class);
    }

    @Test
    public void testUnmappableCharacterInCDATASection() throws Exception {
        Serializer handler = new Serializer(NullOutputStream.INSTANCE, "ascii");
        handler.startFragment();
        handler.startCDATASection();
        assertThatThrownBy(() -> handler.processCharacterData("c'est la fête!", false))
                .isInstanceOf(StreamException.class);
    }

    @Test
    public void testUnmappableCharacterInProcessingInstruction() throws Exception {
        Serializer handler = new Serializer(NullOutputStream.INSTANCE, "ascii");
        handler.startFragment();
        handler.startProcessingInstruction("test");
        assertThatThrownBy(() -> handler.processCharacterData("c'est la fête!", false))
                .isInstanceOf(StreamException.class);
    }

    @Test
    public void testUnmappableCharacterInName() throws Exception {
        Serializer handler = new Serializer(NullOutputStream.INSTANCE, "iso-8859-15");
        handler.startFragment();
        assertThatThrownBy(
                        () -> {
                            // Even though the unmappable character is passed to startElement, the
                            // exception will be thrown later because the data to be encoded is
                            // buffered.
                            handler.startElement("", "\u0370", "");
                            handler.attributesCompleted();
                            handler.endElement();
                        })
                .isInstanceOf(StreamException.class);
    }

    @Test
    public void testIllegalCharacterSequenceInComment() throws Exception {
        Serializer handler = new Serializer(NullWriter.INSTANCE);
        handler.startFragment();
        handler.startComment();
        assertThatThrownBy(() -> handler.processCharacterData("abc--def", false))
                .isInstanceOf(IllegalCharacterSequenceException.class);
    }

    @Test
    public void testIllegalCharacterSequenceInProcessingInstruction() throws Exception {
        Serializer handler = new Serializer(NullWriter.INSTANCE);
        handler.startFragment();
        handler.startProcessingInstruction("test");
        assertThatThrownBy(() -> handler.processCharacterData("aaa???>bbb", false))
                .isInstanceOf(IllegalCharacterSequenceException.class);
    }

    @Test
    public void testIllegalCharacterSequenceInCDATASection() throws Exception {
        Serializer handler = new Serializer(NullWriter.INSTANCE);
        handler.startFragment();
        handler.startCDATASection();
        handler.processCharacterData("xxx]]]", false);
        assertThatThrownBy(() -> handler.processCharacterData(">yyy", false))
                .isInstanceOf(IllegalCharacterSequenceException.class);
    }

    @Test
    public void testGTEscapedAfterSquareBrackets() throws Exception {
        String sequence = "xxx]]>yyy";
        for (int i = 1; i < sequence.length() - 1; i++) {
            StringWriter sw = new StringWriter();
            Serializer handler = new Serializer(sw);
            handler.startFragment();
            handler.processCharacterData(sequence.substring(0, i), false);
            handler.processCharacterData(sequence.substring(i), false);
            handler.completed();
            assertThat(sw.toString()).matches("xxx]]&gt;yyy");
        }
    }
}
