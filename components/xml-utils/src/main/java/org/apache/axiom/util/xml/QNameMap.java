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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

public final class QNameMap<V> {
    private QNameMapEntry<V>[] buckets;
    private int size;

    public QNameMap() {
        buckets = createBuckets(16);
    }

    @SuppressWarnings("unchecked")
    private QNameMapEntry<V>[] createBuckets(int count) {
        return new QNameMapEntry[count];
    }

    public void put(QName qname, V value) {
        if (buckets.length < size * 4 / 3) {
            QNameMapEntry<V>[] oldBuckets = buckets;
            buckets = createBuckets(buckets.length * 2);
            for (QNameMapEntry<V> entry : oldBuckets) {
                while (entry != null) {
                    QNameMapEntry<V> next = entry.next;
                    int index = index(entry.qname);
                    entry.next = buckets[index];
                    buckets[index] = entry;
                    entry = next;
                }
            }
        }
        int index = index(qname);
        QNameMapEntry<V> entry = buckets[index];
        while (entry != null) {
            if (entry.qname.equals(qname)) {
                entry.value = value;
                break;
            }
            entry = entry.next;
        }
        if (entry == null) {
            entry = new QNameMapEntry<V>();
            entry.qname = qname;
            entry.next = buckets[index];
            buckets[index] = entry;
            size++;
        }
        entry.value = value;
    }

    public V get(QName qname) {
        return get(qname.getNamespaceURI(), qname.getLocalPart());
    }

    public V get(String namespaceURI, String localPart) {
        if (namespaceURI == null) {
            namespaceURI = XMLConstants.NULL_NS_URI;
        }
        if (localPart == null) {
            throw new IllegalArgumentException("localPart cannot be null");
        }
        int index = index(namespaceURI, localPart);
        QNameMapEntry<V> entry = buckets[index];
        while (entry != null) {
            if (entry.qname.getLocalPart().equals(localPart)
                    && entry.qname.getNamespaceURI().equals(namespaceURI)) {
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    private int index(QName qname) {
        return index(qname.getNamespaceURI(), qname.getLocalPart());
    }

    private int index(String namespaceURI, String localPart) {
        return (namespaceURI.hashCode() ^ localPart.hashCode()) & (buckets.length - 1);
    }
}
