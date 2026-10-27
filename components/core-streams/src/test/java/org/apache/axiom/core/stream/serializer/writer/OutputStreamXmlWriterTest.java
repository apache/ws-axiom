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
package org.apache.axiom.core.stream.serializer.writer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class OutputStreamXmlWriterTest {
    @Test
    public void testUnmappableCharacterToCharacterReference() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlWriter writer = new OutputStreamXmlWriter(baos, StandardCharsets.ISO_8859_1);
        writer.setUnmappableCharacterHandler(UnmappableCharacterHandler.CONVERT_TO_CHARACTER_REFERENCE);
        writer.write("abc\u20ACdef");
        writer.flushBuffer();
        assertThat(baos.toString("iso-8859-1")).isEqualTo("abc&#x20ac;def");
    }

    private static final String PREFIX = "<a>";

    private static Stream<Arguments> charsets() {
        return Stream.of(Arguments.of(StandardCharsets.UTF_8), Arguments.of(StandardCharsets.ISO_8859_1));
    }

    /** Sizes around the length of the internal buffer (4096). */
    private static Stream<Arguments> prefixedPayloadSizes() {
        return Stream.of(4093, 4094, 4095, 4096, 4097, 8192, 10000).map(Arguments::of);
    }

    /**
     * Generates a string with an incrementing pattern of printable ASCII characters (period 95,
     * which is coprime with the buffer size), so that any wrong slice shows up as a mismatch.
     */
    private static String pattern(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) (0x20 + i % 95));
        }
        return sb.toString();
    }

    private static String roundTrip(Charset charset, WriterAction action) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlWriter writer = new OutputStreamXmlWriter(baos, charset);
        action.run(writer);
        writer.flushBuffer();
        return baos.toString(charset.name());
    }

    private interface WriterAction {
        void run(XmlWriter writer) throws Exception;
    }

    @ParameterizedTest
    @MethodSource("charsets")
    public void testLargeStringIntoEmptyBuffer(Charset charset) throws Exception {
        String payload = pattern(10500);
        assertThat(roundTrip(charset, writer -> writer.write(payload))).isEqualTo(payload);
    }

    @ParameterizedTest
    @MethodSource("charsets")
    public void testLargeCharArrayIntoEmptyBuffer(Charset charset) throws Exception {
        String payload = pattern(10500);
        char[] chars = payload.toCharArray();
        assertThat(roundTrip(charset, writer -> writer.write(chars, 0, chars.length)))
                .isEqualTo(payload);
    }

    @ParameterizedTest
    @MethodSource("prefixedPayloadSizes")
    public void testStringIntoPartlyFilledBuffer(int size) throws Exception {
        String payload = pattern(size);
        assertThat(roundTrip(StandardCharsets.UTF_8, writer -> {
                    writer.write(PREFIX);
                    writer.write(payload);
                }))
                .isEqualTo(PREFIX + payload);
    }

    @ParameterizedTest
    @MethodSource("prefixedPayloadSizes")
    public void testCharArrayIntoPartlyFilledBuffer(int size) throws Exception {
        char[] payload = pattern(size).toCharArray();
        assertThat(roundTrip(StandardCharsets.ISO_8859_1, writer -> {
                    writer.write(PREFIX);
                    writer.write(payload, 0, payload.length);
                }))
                .isEqualTo(PREFIX + new String(payload));
    }

    @ParameterizedTest
    @MethodSource("prefixedPayloadSizes")
    public void testCharArrayWithOffset(int size) throws Exception {
        String full = pattern(size + 1000);
        char[] chars = full.toCharArray();
        assertThat(roundTrip(StandardCharsets.UTF_8, writer -> {
                    writer.write(PREFIX);
                    writer.write(chars, 777, size);
                }))
                .isEqualTo(PREFIX + full.substring(777, 777 + size));
    }

    @ParameterizedTest
    @MethodSource("prefixedPayloadSizes")
    public void testRepeatedLargeStringWrites(int size) throws Exception {
        // XmlWriter has no write(String, int, int); the internal offset into the source string
        // advances across several chunks here, following a prefix and a previous large write.
        String payload = pattern(size);
        assertThat(roundTrip(StandardCharsets.UTF_8, writer -> {
                    writer.write(PREFIX);
                    writer.write(payload);
                    writer.write(payload);
                }))
                .isEqualTo(PREFIX + payload + payload);
    }

    @ParameterizedTest
    @MethodSource("charsets")
    public void testInterleavedSmallAndLargeWrites(Charset charset) throws Exception {
        StringBuilder expected = new StringBuilder();
        String large = pattern(5000);
        char[] largeChars = pattern(9000).toCharArray();
        String result = roundTrip(charset, writer -> {
            for (int round = 0; round < 3; round++) {
                for (int i = 0; i < 1000; i++) {
                    writer.write((char) ('a' + i % 26));
                    expected.append((char) ('a' + i % 26));
                    writer.write("xy");
                    expected.append("xy");
                }
                writer.write(large);
                expected.append(large);
                writer.write(largeChars, 0, largeChars.length);
                expected.append(largeChars);
            }
        });
        assertThat(result).isEqualTo(expected.toString());
    }

    @ParameterizedTest
    @MethodSource("prefixedPayloadSizes")
    public void testLargeInputWithUnmappableCharacters(int size) throws Exception {
        // Place a euro sign every 97 characters
        StringBuilder input = new StringBuilder();
        StringBuilder expected = new StringBuilder();
        String base = pattern(size);
        for (int i = 0; i < size; i++) {
            if (i % 97 == 96) {
                input.append('\u20AC');
                expected.append("&#x20ac;");
            } else {
                input.append(base.charAt(i));
                expected.append(base.charAt(i));
            }
        }
        char[] chars = input.toString().toCharArray();
        for (boolean useString : new boolean[] {false, true}) {
            String result = roundTrip(StandardCharsets.ISO_8859_1, writer -> {
                writer.setUnmappableCharacterHandler(UnmappableCharacterHandler.CONVERT_TO_CHARACTER_REFERENCE);
                writer.write(PREFIX);
                if (useString) {
                    writer.write(input.toString());
                } else {
                    writer.write(chars, 0, chars.length);
                }
            });
            assertThat(result).isEqualTo(PREFIX + expected);
        }
    }
}
