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

package org.apache.axiom.util.namespace;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;

/** Namespace context implementation that stores namespace bindings in a {@link Map}. */
public class MapBasedNamespaceContext extends AbstractNamespaceContext {
    private final Map<String, String> namespaces;

    /**
     * Constructor.
     *
     * @param map a map containing the (prefix, namespace URI) entries
     */
    public MapBasedNamespaceContext(Map<String, String> map) {
        namespaces = map;
    }

    @Override
    protected String doGetNamespaceURI(String prefix) {
        String namespaceURI = namespaces.get(prefix);
        return namespaceURI == null ? XMLConstants.NULL_NS_URI : namespaceURI;
    }

    @Override
    protected String doGetPrefix(String nsURI) {
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            String uri = entry.getValue();
            if (uri.equals(nsURI)) {
                return entry.getKey();
            }
        }
        if (nsURI.length() == 0) {
            return "";
        }
        return null;
    }

    @Override
    protected Iterator<String> doGetPrefixes(String nsURI) {
        Set<String> prefixes = null;
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            String uri = entry.getValue();
            if (uri.equals(nsURI)) {
                if (prefixes == null) {
                    prefixes = new HashSet<String>();
                }
                prefixes.add(entry.getKey());
            }
        }
        if (prefixes != null) {
            return Collections.unmodifiableSet(prefixes).iterator();
        } else if (nsURI.length() == 0) {
            return Collections.singleton("").iterator();
        } else {
            return Collections.<String>emptyList().iterator();
        }
    }
}
