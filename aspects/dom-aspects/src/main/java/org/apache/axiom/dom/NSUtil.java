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

import javax.xml.XMLConstants;

import org.w3c.dom.DOMException;

public final class NSUtil {
    private NSUtil() {}
    
    // This is the NameStartChar production from http://www.w3.org/TR/2008/REC-xml-20081126/
    private static boolean isNameStartChar(char c) {
        return c == ':' || 'A' <= c && c <= 'Z' || c == '_' || 'a' <= c && c <= 'z' || 0xC0 <= c && c <= 0xD6
                || 0xD8 <= c && c <= 0xF6 || 0xF8 <= c && c <= 0x2FF || 0x370 <= c && c <= 0x37D
                || 0x37F <= c && c <= 0x1FFF || 0x200C <= c && c <= 0x200D
                || 0x2070 <= c && c <= 0x218F || 0x2C00 <= c && c <= 0x2FEF
                || 0x3001 <= c && c <= 0xD7FF || 0xF900 <= c && c <= 0xFDCF
                || 0xFDF0 <= c && c <= 0xFFFD || 0x10000 <= c && c <= 0xEFFFF;
    }
    
    // This is the NameChar production from http://www.w3.org/TR/2008/REC-xml-20081126/
    private static boolean isNameChar(char c) {
        return isNameStartChar(c) || c == '-' || c == '.' || '0' <= c && c <= '9' || c == 0xB7
                || 0x0300 <= c && c <= 0x036F || 0x203F <= c && c <= 0x2040;
    }
    
    public static void validateName(String name) throws DOMException {
        if (name.length() == 0) {
            throw DOMExceptionUtil.newDOMException(DOMException.INVALID_CHARACTER_ERR);
        }
        for (int i=0; i<name.length(); i++) {
            char c = name.charAt(i);
            if (i == 0 && !isNameStartChar(c) || i > 0 && !isNameChar(c)) {
                throw DOMExceptionUtil.newDOMException(DOMException.INVALID_CHARACTER_ERR);
            }
        }
    }
    
    public static void validatePrefix(String prefix) throws DOMException {
        for (int i=0; i<prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (c == ':') {
                throw DOMExceptionUtil.newDOMException(DOMException.NAMESPACE_ERR);
            }
            if (i == 0 && !isNameStartChar(c) || i > 0 && !isNameChar(c)) {
                throw DOMExceptionUtil.newDOMException(DOMException.INVALID_CHARACTER_ERR);
            }
        }
    }
    
    public static int validateQualifiedName(String qualifiedName) throws DOMException {
        if (qualifiedName.length() == 0) {
            throw DOMExceptionUtil.newDOMException(DOMException.INVALID_CHARACTER_ERR);
        }
        int colonPosition = -1;
        boolean checkNameStart = true;
        for (int i=0; i<qualifiedName.length(); i++) {
            char c = qualifiedName.charAt(i);
            if (c == ':') {
                if (colonPosition == -1 && i > 0) {
                    colonPosition = i;
                    checkNameStart = true;
                } else {
                    throw DOMExceptionUtil.newDOMException(DOMException.NAMESPACE_ERR);
                }
            } else if (checkNameStart && !isNameStartChar(c) || i > 0 && !isNameChar(c)) {
                throw DOMExceptionUtil.newDOMException(DOMException.INVALID_CHARACTER_ERR);
            } else {
                checkNameStart = false;
            }
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
        if (XMLConstants.XML_NS_PREFIX.equals(prefix) && !XMLConstants.XML_NS_URI.equals(namespaceURI)) {
            throw DOMExceptionUtil.newDOMException(DOMException.NAMESPACE_ERR);
        }
    }
    
    public static void validateAttributeName(String namespaceURI, String localName, String prefix) {
        validateNamespace(namespaceURI, prefix);
        if (prefix.length() == 0 && localName.equals(XMLConstants.XMLNS_ATTRIBUTE) && !namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
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
