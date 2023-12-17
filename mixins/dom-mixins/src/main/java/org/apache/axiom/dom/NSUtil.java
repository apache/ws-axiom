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
package org.apache.axiom.dom;

import static org.apache.axiom.util.xml.XMLChar.isNameChar;
import static org.apache.axiom.util.xml.XMLChar.isNameStartChar;

import javax.xml.XMLConstants;

import org.w3c.dom.DOMException;

public final class NSUtil {
    private NSUtil() {}

    public static void validateName(String name) throws DOMException {
        if (name.length() == 0) {
            throw DOMExceptionUtil.newDOMException(DOMException.INVALID_CHARACTER_ERR);
        }
        for (int i = 0; i < name.length(); ) {
            int c = name.codePointAt(i);
            if (i == 0 && !isNameStartChar(c) || i > 0 && !isNameChar(c)) {
                throw DOMExceptionUtil.newDOMException(DOMException.INVALID_CHARACTER_ERR);
            }
            i += Character.charCount(c);
        }
    }

    public static void validatePrefix(String prefix) throws DOMException {
        for (int i = 0; i < prefix.length(); ) {
            int c = prefix.codePointAt(i);
            if (c == ':') {
                throw DOMExceptionUtil.newDOMException(DOMException.NAMESPACE_ERR);
            }
            if (i == 0 && !isNameStartChar(c) || i > 0 && !isNameChar(c)) {
                throw DOMExceptionUtil.newDOMException(DOMException.INVALID_CHARACTER_ERR);
            }
            i += Character.charCount(c);
        }
    }

    public static int validateQualifiedName(String qualifiedName) throws DOMException {
        if (qualifiedName.length() == 0) {
            throw DOMExceptionUtil.newDOMException(DOMException.INVALID_CHARACTER_ERR);
        }
        int colonPosition = -1;
        boolean checkNameStart = true;
        for (int i = 0; i < qualifiedName.length(); ) {
            int c = qualifiedName.codePointAt(i);
            if (c == ':') {
                if (colonPosition == -1 && i > 0) {
                    colonPosition = i;
                    checkNameStart = true;
                } else {
                    throw DOMExceptionUtil.newDOMException(DOMException.NAMESPACE_ERR);
                }
            } else if (checkNameStart) {
                if (!isNameStartChar(c)) {
                    throw DOMExceptionUtil.newDOMException(
                            isNameChar(c)
                                    ? DOMException.NAMESPACE_ERR
                                    : DOMException.INVALID_CHARACTER_ERR);
                }
                checkNameStart = false;
            } else if (!isNameChar(c)) {
                throw DOMExceptionUtil.newDOMException(DOMException.INVALID_CHARACTER_ERR);
            }
            i += Character.charCount(c);
        }
        if (checkNameStart) {
            // If we get here, then the qualified name ends with a colon
            throw DOMExceptionUtil.newDOMException(DOMException.NAMESPACE_ERR);
        }
        return colonPosition;
    }

    public static String normalizeNamespaceURI(String namespaceURI) {
        return namespaceURI == null ? "" : namespaceURI;
    }

    public static void validateNamespace(String namespaceURI, String prefix) {
        if (prefix.length() != 0 && namespaceURI.length() == 0) {
            throw DOMExceptionUtil.newDOMException(DOMException.NAMESPACE_ERR);
        }
        if (XMLConstants.XML_NS_PREFIX.equals(prefix)
                && !XMLConstants.XML_NS_URI.equals(namespaceURI)) {
            throw DOMExceptionUtil.newDOMException(DOMException.NAMESPACE_ERR);
        }
    }

    public static void validateAttributeName(String namespaceURI, String localName, String prefix) {
        validateNamespace(namespaceURI, prefix);
        if (prefix.length() == 0
                && localName.equals(XMLConstants.XMLNS_ATTRIBUTE)
                && !namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            throw DOMExceptionUtil.newDOMException(DOMException.NAMESPACE_ERR);
        }
        if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
            throw DOMExceptionUtil.newDOMException(DOMException.NAMESPACE_ERR);
        }
    }

    public static String getDeclaredPrefix(String localName, String prefix) {
        if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            return localName;
        } else if (prefix.length() == 0 && localName.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            return "";
        } else {
            throw DOMExceptionUtil.newDOMException(DOMException.NAMESPACE_ERR);
        }
    }
}
