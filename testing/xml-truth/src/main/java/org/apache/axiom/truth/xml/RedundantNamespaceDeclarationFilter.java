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
package org.apache.axiom.truth.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import org.apache.axiom.truth.xml.spi.Event;
import org.apache.axiom.truth.xml.spi.Traverser;
import org.apache.axiom.truth.xml.spi.TraverserException;

final class RedundantNamespaceDeclarationFilter extends Filter {
    private static final Map<String, String> implicitNamespaces;

    static {
        implicitNamespaces = new HashMap<>();
        implicitNamespaces.put("", "");
        implicitNamespaces.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        implicitNamespaces.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    }

    private final List<Map<String, String>> stack;

    RedundantNamespaceDeclarationFilter(Traverser parent) {
        super(parent);
        stack = new ArrayList<>(10);
        stack.add(implicitNamespaces);
    }

    private String lookupNamespaceURI(String prefix) {
        for (int i = stack.size() - 1; i >= 0; i--) {
            Map<String, String> namespaces = stack.get(i);
            if (namespaces != null) {
                String namespaceURI = namespaces.get(prefix);
                if (namespaceURI != null) {
                    return namespaceURI;
                }
            }
        }
        return null;
    }

    @Override
    public Event next() throws TraverserException {
        Event event = super.next();
        if (event == Event.START_ELEMENT) {
            Map<String, String> namespaces = super.getNamespaces();
            if (namespaces != null) {
                for (Iterator<Map.Entry<String, String>> it = namespaces.entrySet().iterator();
                        it.hasNext(); ) {
                    Map.Entry<String, String> namespace = it.next();
                    if (namespace.getValue().equals(lookupNamespaceURI(namespace.getKey()))) {
                        it.remove();
                    }
                }
                if (namespaces.isEmpty()) {
                    namespaces = null;
                }
            }
            stack.add(namespaces);
        } else if (event == Event.END_ELEMENT) {
            stack.remove(stack.size() - 1);
        }
        return event;
    }

    @Override
    public Map<String, String> getNamespaces() {
        return stack.get(stack.size() - 1);
    }
}
