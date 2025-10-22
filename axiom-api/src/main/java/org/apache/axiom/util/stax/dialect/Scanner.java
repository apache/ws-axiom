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
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLStreamException;

final class Scanner {
    private final String s;
    private int pos;

    Scanner(String s) {
        this.s = s;
    }

    int peek() {
        return pos == s.length() ? -1 : s.charAt(pos);
    }

    String getName() {
        int start = pos;
        while (pos < s.length()) {
            char c = s.charAt(pos);
            // This corresponds to the NameChar production, except for characters above 0x80.
            // We expect that the underlying parser strictly enforces the grammar and we don't
            // care here about NameStartChar and characters above 0x80.
            if ('a' <= c && c <= 'z'
                    || 'A' <= c && c <= 'Z'
                    || '0' <= c && c <= '9'
                    || c == ':'
                    || c == '_'
                    || c == '-'
                    || c == '.'
                    || c > 0x80) {
                pos++;
            } else {
                break;
            }
        }
        return pos == start ? null : s.substring(start, pos);
    }

    String getQuotedString() throws XMLStreamException {
        int quoteChar = peek();
        if (quoteChar == '\'' || quoteChar == '"') {
            pos++;
            int start = pos;
            while (pos < s.length() && s.charAt(pos) != quoteChar) {
                pos++;
            }
            if (peek() == quoteChar) {
                return s.substring(start, pos++);
            } else {
                throw new XMLStreamException("Unterminated quoted string");
            }
        } else {
            throw new XMLStreamException("Expected quote char at position " + pos);
        }
    }

    void expect(String seq) throws XMLStreamException {
        boolean found;
        if (pos + seq.length() > s.length()) {
            found = false;
        } else {
            found = true;
            for (int i = 0; i < seq.length(); i++) {
                if (s.charAt(pos + i) != seq.charAt(i)) {
                    found = false;
                    break;
                }
            }
        }
        if (found) {
            pos += seq.length();
        } else {
            throw new XMLStreamException("Expected \"" + seq + "\" at position " + pos);
        }
    }

    void skipSpace() {
        while (pos < s.length()) {
            char c = s.charAt(pos);
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                pos++;
            } else {
                break;
            }
        }
    }
}
