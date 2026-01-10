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

package org.apache.axiom.util.base64;

// For internal use only
class Base64Constants {
    static final byte[] S_BASE64CHAR = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
        'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
        '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    static final byte S_BASE64PAD = '=';

    /** Used in {@link #S_DECODETABLE} to indicate that a character is the padding character. */
    static final byte PADDING = -1;

    /** Used in {@link #S_DECODETABLE} to indicate that a character is white space. */
    static final byte WHITE_SPACE = -2;

    /** Used in {@link #S_DECODETABLE} to indicate that a character is invalid. */
    static final byte INVALID = -3;

    static final byte[] S_DECODETABLE = new byte[128];

    static {
        for (int i = 0; i < S_DECODETABLE.length; i++) {
            S_DECODETABLE[i] = INVALID;
        }
        for (int i = 0; i < S_BASE64CHAR.length; i++) {
            // 0 to 63
            S_DECODETABLE[S_BASE64CHAR[i]] = (byte) i;
        }
        S_DECODETABLE[S_BASE64PAD] = PADDING;
        // See http://www.w3.org/TR/2008/REC-xml-20081126/#white
        S_DECODETABLE[' '] = WHITE_SPACE;
        S_DECODETABLE['\t'] = WHITE_SPACE;
        S_DECODETABLE['\r'] = WHITE_SPACE;
        S_DECODETABLE['\n'] = WHITE_SPACE;
    }
}
