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
package org.apache.axiom.mime;

import java.text.ParseException;

final class ContentTypeTokenizer {
    // Note: normally only ' ' and '\t' can appear in unfolded header values, but we accept parsing
    // folded values
    private static final String whitespace = " \t\n\r";

    private static final String tspecials = "()<>@,;:\\\"/[]?=";

    private final String s;
    private int index;

    public ContentTypeTokenizer(String s) {
        this.s = s;
    }

    private void skipWhiteSpace() {
        for (int len = s.length();
                index < len && whitespace.indexOf(s.charAt(index)) != -1;
                index++) {
            // Just loop
        }
    }

    String expectToken() throws ParseException {
        skipWhiteSpace();
        int begin = index;
        for (int len = s.length();
                index < len && tspecials.indexOf(s.charAt(index)) == -1;
                index++) {
            // Just loop
        }
        int end = index;
        for (; end > begin && whitespace.indexOf(s.charAt(end - 1)) != -1; end--) {
            // Just loop
        }
        if (begin == end) {
            if (index == s.length()) {
                return null;
            } else {
                throw new ParseException(
                        "Expected token, but found '" + s.charAt(index) + "'", index);
            }
        } else {
            return s.substring(begin, end);
        }
    }

    String requireToken() throws ParseException {
        String token = expectToken();
        if (token == null) {
            throw new ParseException("Token expected", index);
        }
        return token;
    }

    String requireTokenOrQuotedString() throws ParseException {
        skipWhiteSpace();
        int len = s.length();
        if (index < len) {
            if (s.charAt(index) == '\"') {
                StringBuffer sb = new StringBuffer();
                index++;
                for (; index < len; index++) {
                    char c = s.charAt(index);
                    if (c == '\\') {
                        index++;
                        if (index == len) {
                            throw new ParseException(
                                    "Expected more input after escape character", index);
                        }
                        sb.append(s.charAt(index));
                    } else if (c == '\"') {
                        break;
                    } else {
                        sb.append(c);
                    }
                }
                if (index == len) {
                    throw new ParseException("Unclosed quoted string", index);
                }
                // If we get here, then the current character is a quote; skip it
                index++;
                skipWhiteSpace();
                return sb.toString();
            } else {
                return requireToken();
            }
        } else {
            throw new ParseException(
                    "Unexpected end of string; expected token or quoted string", index);
        }
    }

    boolean expect(char c) throws ParseException {
        if (index == s.length()) {
            return false;
        } else {
            char actual = s.charAt(index);
            if (actual == c) {
                index++;
                return true;
            } else {
                throw new ParseException("Expected '" + c + "' instead of '" + actual + "'", index);
            }
        }
    }

    void require(char c) throws ParseException {
        if (!expect(c)) {
            throw new ParseException("Unexpected end of string; expected '" + c + "'", index);
        }
    }

    void requireEndOfString() throws ParseException {
        if (index != s.length()) {
            throw new ParseException(
                    "Unexpected character '" + s.charAt(index) + "'; expected end of string",
                    index);
        }
    }
}
