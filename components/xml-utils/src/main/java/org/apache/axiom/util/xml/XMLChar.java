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
package org.apache.axiom.util.xml;

/**
 * Contains utility methods to test characters for various properties defined by the XML
 * specification.
 */
public final class XMLChar {
    private XMLChar() {}

    /**
     * Determine if the given character is whitespace according to the XML specification.
     *
     * @param c the character to examine
     * @return {@code true} if the character is whitespace, {@code false} otherwise
     */
    public static boolean isWhitespace(char c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }

    /**
     * Determine if the given character matches the <a
     * href="http://www.w3.org/TR/2008/REC-xml-20081126/#NT-NameStartChar">{@code NameStartChar}</a>
     * production in the XML specification.
     *
     * @param c the character to examine
     * @return {@code true} if the character is name start char, {@code false} otherwise
     */
    public static boolean isNameStartChar(int c) {
        return c == ':'
                || 'A' <= c && c <= 'Z'
                || c == '_'
                || 'a' <= c && c <= 'z'
                || 0xC0 <= c && c <= 0xD6
                || 0xD8 <= c && c <= 0xF6
                || 0xF8 <= c && c <= 0x2FF
                || 0x370 <= c && c <= 0x37D
                || 0x37F <= c && c <= 0x1FFF
                || 0x200C <= c && c <= 0x200D
                || 0x2070 <= c && c <= 0x218F
                || 0x2C00 <= c && c <= 0x2FEF
                || 0x3001 <= c && c <= 0xD7FF
                || 0xF900 <= c && c <= 0xFDCF
                || 0xFDF0 <= c && c <= 0xFFFD
                || 0x10000 <= c && c <= 0xEFFFF;
    }

    /**
     * Determine if the given character matches the <a
     * href="http://www.w3.org/TR/2008/REC-xml-20081126/#NT-NameChar">{@code NameChar}</a>
     * production in the XML specification.
     *
     * @param c the character to examine
     * @return {@code true} if the character is name char, {@code false} otherwise
     */
    public static boolean isNameChar(int c) {
        return isNameStartChar(c)
                || c == '-'
                || c == '.'
                || '0' <= c && c <= '9'
                || c == 0xB7
                || 0x0300 <= c && c <= 0x036F
                || 0x203F <= c && c <= 0x2040;
    }
}
