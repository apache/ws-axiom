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
package org.apache.axiom.datatype;

/**
 * Contains utility methods for usage by {@link Type} implementations.
 */
public final class TypeHelper {
    private TypeHelper() {}
    
    /**
     * Determine if the given character is whitespace according to the XML specification.
     * 
     * @param c
     *            the character to examine
     * @return {@code true} if the character is whitespace, {@code false} otherwise
     */
    public static boolean isWhitespace(char c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }
    
    /**
     * Determine the index of the first non whitespace character in the given literal. This method
     * is intended for use in implementations of XSD datatypes for which the {@code whiteSpace}
     * facet is {@code collapse}.
     * 
     * @param literal
     *            the literal
     * @return the index of the first non whitespace character
     * @throws UnexpectedEndOfStringException
     *             if the literal is empty or contains only whitespace characters
     */
    public static int getStartIndex(String literal) throws UnexpectedEndOfStringException {
        final int len = literal.length();
        if (len == 0) {
            throw new UnexpectedEndOfStringException(literal);
        }
        int start = 0;
        while (TypeHelper.isWhitespace(literal.charAt(start))) {
            if (++start == len) {
                throw new UnexpectedEndOfStringException(literal);
            }
        }
        return start;
    }
    
    /**
     * Determine the index following the last non whitespace character in the given literal. This
     * method is intended for use in conjunction with {@link #getStartIndex(String)}.
     * 
     * @param literal
     *            the literal
     * @return the index following the last non whitespace character
     * @throws UnexpectedEndOfStringException
     *             if the literal is empty or contains only whitespace characters (Note that this
     *             means that the order in which {@link #getStartIndex(String)} and
     *             {@link #getEndIndex(String)} are called is not important)
     */
    public static int getEndIndex(String literal) throws UnexpectedEndOfStringException {
        final int len = literal.length();
        if (len == 0) {
            throw new UnexpectedEndOfStringException(literal);
        }
        int end = len;
        while (TypeHelper.isWhitespace(literal.charAt(end-1))) {
            if (--end == 0) {
                throw new UnexpectedEndOfStringException(literal);
            }
        }
        return end;
    }
}
