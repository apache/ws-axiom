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

package org.apache.axiom.om.impl.llom.util;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class NamespaceContextImpl
        implements
        NamespaceContext {
    protected Map namespaces;

    public NamespaceContextImpl(Map map) {
        namespaces = map;
    }

    /**
     * Get the URI given a prefix
     *
     * @param prefix
     * @return uri string
     */
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("null prefix argument is invalid");
        } else if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
            return XMLConstants.XML_NS_URI;
        } else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        } else if (namespaces.containsKey(prefix)) {
            return (String) namespaces.get(prefix);
        }
        return null;
    }

    /**
     * Get the prefix for a uri
     *
     * @param nsURI
     * @return prefix string
     */
    public String getPrefix(String nsURI) {
        if (nsURI == null) {
            throw new IllegalArgumentException("invalid null nsURI");
        } else if (nsURI.length() == 0) {
            throw new IllegalArgumentException("invalid empty nsURI");
        } else if (nsURI.equals(XMLConstants.XML_NS_URI)) {
            return XMLConstants.XML_NS_PREFIX;
        } else if (nsURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            return XMLConstants.XMLNS_ATTRIBUTE;
        }
        Iterator iter = namespaces.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String uri = (String) entry.getValue();
            if (uri.equals(nsURI)) {
                return (String) entry.getKey();
            }
        }
        if (nsURI.length() == 0) {
            return "";
        }
        return null;
    }

    /**
     * Get list of prefixes
     *
     * @param nsURI
     * @return iterator (of strings)
     */
    public Iterator getPrefixes(String nsURI) {
        if (nsURI == null) {
            throw new IllegalArgumentException("invalid null nsURI");
        } else if (nsURI.equals(XMLConstants.XML_NS_URI)) {
            return Collections.singleton(XMLConstants.XML_NS_PREFIX).iterator();
        } else if (nsURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            return Collections.singleton(XMLConstants.XMLNS_ATTRIBUTE)
                    .iterator();
        }
        Set prefixes = null;
        Iterator iter = namespaces.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String uri = (String) entry.getValue();
            if (uri.equals(nsURI)) {
                if (prefixes == null) {
                    prefixes = new HashSet();
                }
                prefixes.add(entry.getKey());
            }
        }
        if (prefixes != null) {
            return Collections.unmodifiableSet(prefixes).iterator();
        } else if (nsURI.length() == 0) {
            return Collections.singleton("").iterator();
        } else {
            return Collections.EMPTY_LIST.iterator();
        }
    }
}