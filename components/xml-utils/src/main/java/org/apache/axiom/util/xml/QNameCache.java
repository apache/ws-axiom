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
package org.apache.axiom.util.xml;

import javax.xml.namespace.QName;

public final class QNameCache {
    private static final QNameCacheEntry[] cache;

    static {
        cache = new QNameCacheEntry[1024];
        for (int i = 0; i < cache.length; i++) {
            cache[i] = new QNameCacheEntry();
        }
    }

    private QNameCache() {}

    public static QName getQName(String namespaceURI, String localPart, String prefix) {
        int index =
                (namespaceURI.hashCode() ^ localPart.hashCode() ^ prefix.hashCode())
                        & (cache.length - 1);
        QNameCacheEntry entry = cache[index];
        QName qname = entry.get();
        if (qname == null
                || !qname.getNamespaceURI().equals(namespaceURI)
                || !qname.getLocalPart().equals(localPart)
                || !qname.getPrefix().equals(prefix)) {
            qname = new QName(namespaceURI, localPart, prefix);
            entry.set(qname);
        }
        return qname;
    }

    public static QName getQName(String namespaceURI, String localPart) {
        return getQName(namespaceURI, localPart, "");
    }
}
