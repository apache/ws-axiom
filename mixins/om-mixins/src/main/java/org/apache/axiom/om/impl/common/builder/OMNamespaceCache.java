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
package org.apache.axiom.om.impl.common.builder;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;

public final class OMNamespaceCache {
    private OMNamespace[] items = new OMNamespace[16];
    private int size;

    public OMNamespace getOMNamespace(String uri, String prefix) {
        if (uri.isEmpty() && prefix.isEmpty()) {
            return null;
        }
        int index = index(uri, prefix);
        while (true) {
            OMNamespace ns = items[index];
            if (ns == null) {
                break;
            } else if (ns.getNamespaceURI().equals(uri) && ns.getPrefix().equals(prefix)) {
                return ns;
            }
            if (++index == items.length) {
                index = 0;
            }
        }
        if (items.length < size * 4 / 3) {
            OMNamespace[] oldItems = items;
            items = new OMNamespace[items.length * 2];
            for (OMNamespace ns : oldItems) {
                if (ns != null) {
                    items[freeIndex(ns.getNamespaceURI(), ns.getPrefix())] = ns;
                }
            }
            index = freeIndex(uri, prefix);
        }
        OMNamespace ns = new OMNamespaceImpl(uri, prefix);
        items[index] = ns;
        size++;
        return ns;
    }

    private int index(String uri, String prefix) {
        return (uri.hashCode() ^ prefix.hashCode()) & (items.length - 1);
    }

    private int freeIndex(String uri, String prefix) {
        int index = index(uri, prefix);
        while (items[index] != null) {
            if (++index == items.length) {
                index = 0;
            }
        }
        return index;
    }
}
