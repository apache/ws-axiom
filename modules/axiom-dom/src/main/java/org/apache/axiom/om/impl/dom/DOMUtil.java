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

package org.apache.axiom.om.impl.dom;

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import javax.xml.XMLConstants;

import org.w3c.dom.DOMException;

/** Utility class for the OM-DOM implementation */
class DOMUtil {

    public static boolean isQualifiedName(String value) {
        // TODO check for valid characters
        // throw new UnsupportedOperationException("TODO");
        return true;
    }

    private static void validateName(String namespaceURI, String localName, String prefix) {
        if (prefix != null && !XMLChar.isValidNCName(prefix)
                || !XMLChar.isValidNCName(localName)) {
            throw newDOMException(DOMException.INVALID_CHARACTER_ERR);
        }
        if (namespaceURI == null && prefix != null
                || XMLConstants.XML_NS_PREFIX.equals(prefix) && !XMLConstants.XML_NS_URI.equals(namespaceURI)) {
            throw newDOMException(DOMException.NAMESPACE_ERR);
        }
    }
    
    public static void validateElementName(String namespaceURI, String localName, String prefix) {
        validateName(namespaceURI, localName, prefix);
    }

    public static void validateAttrName(String namespaceURI, String localName, String prefix) {
        validateName(namespaceURI, localName, prefix);
        
        if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix != null ? prefix : localName)
                != XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
            throw newDOMException(DOMException.NAMESPACE_ERR);
        }
    }

    /**
     * Get the local name from a qualified name
     *
     * @param qualifiedName
     */
    public static String getLocalName(String qualifiedName) {
        int idx = qualifiedName.indexOf(':');
        if (idx == -1) {
            return qualifiedName;
        } else if (qualifiedName.indexOf(':', idx+1) == -1) {
            return qualifiedName.substring(idx+1);
        } else {
            throw newDOMException(DOMException.NAMESPACE_ERR);
        }
    }

    /**
     * Get the prefix from a qualified name
     *
     * @param qualifiedName
     */
    public static String getPrefix(String qualifiedName) {
        int idx = qualifiedName.indexOf(':');
        if (idx == -1) {
            return null;
        } else if (idx == 0 || idx == qualifiedName.length()-1) {
            throw newDOMException(DOMException.NAMESPACE_ERR);
        } else {
            return qualifiedName.substring(0, idx);
        }
    }
}
