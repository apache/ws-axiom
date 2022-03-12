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
package org.apache.axiom.core;

/** Selects elements based on some match rule. */
public interface ElementMatcher<T extends CoreElement> {
    /** Matches any {@link CoreElement}. */
    ElementMatcher<CoreElement> ANY =
            new ElementMatcher<CoreElement>() {
                @Override
                public boolean matches(CoreElement element, String namespaceURI, String name) {
                    return true;
                }
            };

    /**
     * Matches {@link CoreNSAwareElement} nodes by qualified name, i.e. namespace URI and local
     * name.
     */
    ElementMatcher<CoreNSAwareElement> BY_QNAME =
            new ElementMatcher<CoreNSAwareElement>() {
                @Override
                public boolean matches(
                        CoreNSAwareElement element, String namespaceURI, String name) {
                    return name.equals(element.coreGetLocalName())
                            && namespaceURI.equals(element.coreGetNamespaceURI());
                }
            };

    /** Matches {@link CoreNSAwareElement} nodes by namespace URI. */
    ElementMatcher<CoreNSAwareElement> BY_NAMESPACE_URI =
            new ElementMatcher<CoreNSAwareElement>() {
                @Override
                public boolean matches(
                        CoreNSAwareElement element, String namespaceURI, String name) {
                    return namespaceURI.equals(element.coreGetNamespaceURI());
                }
            };

    /** Matches {@link CoreNSAwareElement} nodes by local name. */
    ElementMatcher<CoreNSAwareElement> BY_LOCAL_NAME =
            new ElementMatcher<CoreNSAwareElement>() {
                @Override
                public boolean matches(
                        CoreNSAwareElement element, String namespaceURI, String name) {
                    return name.equals(element.coreGetLocalName());
                }
            };

    /** Matches elements (of any kind) by tag name. */
    ElementMatcher<CoreElement> BY_NAME =
            new ElementMatcher<CoreElement>() {
                @Override
                public boolean matches(CoreElement element, String namespaceURI, String name) {
                    if (element instanceof CoreNSUnawareElement) {
                        return name.equals(((CoreNSUnawareElement) element).coreGetName());
                    } else {
                        CoreNSAwareElement nsAwareElement = (CoreNSAwareElement) element;
                        String prefix = nsAwareElement.coreGetPrefix();
                        int prefixLength = prefix.length();
                        String localName = nsAwareElement.coreGetLocalName();
                        if (prefixLength == 0) {
                            return name.equals(localName);
                        } else {
                            int localNameLength = localName.length();
                            if (prefixLength + localNameLength + 1 == name.length()) {
                                if (name.charAt(prefixLength) != ':') {
                                    return false;
                                }
                                for (int i = 0; i < localNameLength; i++) {
                                    if (name.charAt(prefixLength + i + 1) != localName.charAt(i)) {
                                        return false;
                                    }
                                }
                                for (int i = 0; i < prefix.length(); i++) {
                                    if (name.charAt(i) != prefix.charAt(i)) {
                                        return false;
                                    }
                                }
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                }
            };

    /**
     * Check if the given element matches. The values of the <code>namespaceURI</code> and <code>
     * name</code> parameters are those passed to {@link CoreParentNode#coreGetElements(Axis, Class,
     * ElementMatcher, String, String, ExceptionTranslator, Semantics)}.
     *
     * @param element the element to check
     * @param namespaceURI see above
     * @param name see above
     * @return <code>true</code> if the element matches, <code>false</code> otherwise
     */
    boolean matches(T element, String namespaceURI, String name);
}
