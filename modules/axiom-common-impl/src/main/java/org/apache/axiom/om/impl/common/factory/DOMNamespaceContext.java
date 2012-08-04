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
package org.apache.axiom.om.impl.common.factory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.axiom.util.namespace.AbstractNamespaceContext;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class DOMNamespaceContext extends AbstractNamespaceContext {
    private final DOMXMLStreamReader reader;

    DOMNamespaceContext(DOMXMLStreamReader reader) {
        this.reader = reader;
    }

    protected String doGetNamespaceURI(String prefix) {
        String namespaceURI = reader.getNamespaceURI(prefix);
        return namespaceURI == null ? "" : namespaceURI;
    }

    protected String doGetPrefix(String namespaceURI) {
        Set seenPrefixes = new HashSet();
        Node current = reader.currentNode();
        do {
            NamedNodeMap attributes = current.getAttributes();
            if (attributes != null) {
                for (int i=0, l=attributes.getLength(); i<l; i++) {
                    Attr attr = (Attr)attributes.item(i);
                    if (DOMUtils.isNSDecl(attr)) {
                        String prefix = DOMUtils.getNSDeclPrefix(attr);
                        if (prefix == null) {
                            prefix = "";
                        }
                        if (seenPrefixes.add(prefix) && attr.getValue().equals(namespaceURI)) {
                            return prefix;
                        }
                    }
                }
            }
            current = current.getParentNode();
        } while (current != null);
        return null;
    }

    protected Iterator doGetPrefixes(String namespaceURI) {
        // seenPrefixes tracks all prefixes we have encountered; this is important to
        // handle prefixes that are overridden by descendant elements
        Set seenPrefixes = new HashSet();
        Set matchingPrefixes = new HashSet();
        Node current = reader.currentNode();
        do {
            NamedNodeMap attributes = current.getAttributes();
            if (attributes != null) {
                for (int i=0, l=attributes.getLength(); i<l; i++) {
                    Attr attr = (Attr)attributes.item(i);
                    if (DOMUtils.isNSDecl(attr)) {
                        String prefix = DOMUtils.getNSDeclPrefix(attr);
                        if (prefix == null) {
                            prefix = "";
                        }
                        if (seenPrefixes.add(prefix) && attr.getValue().equals(namespaceURI)) {
                            matchingPrefixes.add(prefix);
                        }
                    }
                }
            }
            current = current.getParentNode();
        } while (current != null);
        return matchingPrefixes.iterator();
    }
}
