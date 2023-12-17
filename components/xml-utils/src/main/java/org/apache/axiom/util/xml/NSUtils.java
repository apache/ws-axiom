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

/** Contains utility methods to work with XML namespaces. */
public final class NSUtils {
    private static final char[] prefixChars = new char[62];

    static {
        for (int i = 0; i < 10; i++) {
            prefixChars[i] = (char) ('0' + i);
        }
        for (int i = 0; i < 26; i++) {
            prefixChars[i + 10] = (char) ('a' + i);
        }
        for (int i = 0; i < 26; i++) {
            prefixChars[i + 36] = (char) ('A' + i);
        }
    }

    private NSUtils() {}

    /**
     * Generate a namespace prefix for the given namespace URI. The generated prefix is based on a
     * hash of the namespace URI. This implies that a given namespace URI is always mapped to the
     * same prefix and that there is no guarantee that the generated prefixes are unique. However,
     * the likelihood of a (accidental) collisions is very small.
     *
     * <p>Using hash based prefixes has the advantage of reducing the number of entries created in
     * the symbol table of the parser on the receiving side, assuming that the symbol table is
     * reused to parse multiple documents (which is a common optimization).
     *
     * @param namespaceURI the namespace URI to generate a prefix for; must not be {@code null}
     * @return the generated prefix
     */
    public static String generatePrefix(String namespaceURI) {
        char[] prefix = new char[7];
        prefix[0] = 'n';
        prefix[1] = 's';
        int hash = namespaceURI.hashCode() & 0x7FFFFFFF;
        for (int i = prefix.length - 1; i >= 2; i--) {
            prefix[i] = prefixChars[hash % 62];
            hash /= 62;
        }
        return new String(prefix);
    }
}
