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

package org.apache.axiom.util;

public final class UIDGenerator {
    private static final ThreadLocal impl = new ThreadLocal() {
        protected Object initialValue() {
            return new UIDGeneratorImpl();
        }
    };
    
    private UIDGenerator() {}
    
    /**
     * Generates a unique ID suitable for usage as a MIME content ID.
     * <p>
     * RFC2045 (MIME) specifies that the value of the <tt>Content-ID</tt> header
     * must match the <tt>msg-id</tt> production, which is defined by RFC2822 as
     * follows:
     * <pre>
     * msg-id        = [CFWS] "&lt;" id-left "@" id-right ">" [CFWS]
     * id-left       = dot-atom-text / no-fold-quote / obs-id-left
     * id-right      = dot-atom-text / no-fold-literal / obs-id-right
     * dot-atom-text = 1*atext *("." 1*atext)
     * atext         = ALPHA / DIGIT / "!" / "#" / "$" / "%" / "&amp;"
     *                   / "'" / "*" / "+" / "-" / "/" / "=" / "?"
     *                   / "^" / "_" / "`" / "{" / "|" / "}" / "~"</pre>
     * In addition, RFC2111 specifies that when used in an URL with scheme
     * "cid:", the content ID must be URL encoded. Since not all implementations
     * handle this correctly, any characters considered "unsafe" in an URL (and
     * requiring encoding) should be avoided in a content ID.
     * <p>
     * This method generates content IDs that satisfy these requirements. It
     * guarantees a high level of uniqueness, but makes no provisions to
     * guarantee randomness. The implementation is thread safe, but doesn't use
     * synchronization.
     * 
     * @return The generated content ID. Note that this value does not include
     *         the angle brackets of the <tt>msg-id</tt> production, but only
     *         represents the bare content ID.
     */
    public static String generateContentId() {
        StringBuffer buffer = new StringBuffer();
        ((UIDGeneratorImpl)impl.get()).generateHex(buffer);
        buffer.append("@apache.org");
        return buffer.toString();
    }
}
