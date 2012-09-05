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

import javax.xml.XMLConstants;

import org.w3c.dom.DOMException;

/** Utility class for the OM-DOM implementation */
class DOMUtil {

    public static boolean isQualifiedName(String value) {
        // TODO check for valid characters
        // throw new UnsupportedOperationException("TODO");
        return true;
    }

    public static void validateAttrNamespace(String namespaceURI, String localName, String prefix) {
        // TODO check for valid namespace
        /**
         * if the qualifiedName has a prefix and the namespaceURI is null, if
         * the qualifiedName has a prefix that is "xml" and the namespaceURI is
         * different from " http://www.w3.org/XML/1998/namespace", or if the
         * qualifiedName, or its prefix, is "xmlns" and the namespaceURI is
         * different from " http://www.w3.org/2000/xmlns/".
         */
        
        if (namespaceURI == null) {
            if (localName.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                throw DOMUtil.newDOMException(DOMException.NAMESPACE_ERR);
            }
        } else if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            if (prefix != null && !prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)
                    || prefix == null && !localName.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                throw DOMUtil.newDOMException(DOMException.NAMESPACE_ERR);
            }
        } else {
            if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                throw DOMUtil.newDOMException(DOMException.NAMESPACE_ERR);
            }
        }
    }

    /**
     * Get the local name from a qualified name
     *
     * @param qualifiedName
     */
    public static String getLocalName(String qualifiedName) {
        int idx = qualifiedName.indexOf(':');
        return idx == -1 ? qualifiedName : qualifiedName.substring(idx+1);
    }

    /**
     * Get the prefix from a qualified name
     *
     * @param qualifiedName
     */
    public static String getPrefix(String qualifiedName) {
        int idx = qualifiedName.indexOf(':');
        return idx == -1 ? null : qualifiedName.substring(0, idx);
    }
    
    public static DOMException newDOMException(short code) {
        throw new DOMException(code, DOMMessageFormatter.formatMessage(
                DOMMessageFormatter.DOM_DOMAIN, code, null));
    }
}
