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

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMFilterIterator;
import org.apache.axiom.om.impl.traverse.OMQNameFilterIterator;
import org.apache.axiom.om.impl.traverse.OMQualifiedNameFilterIterator;
import org.w3c.dom.NodeList;

public aspect DOMContainerSupport {
    declare parents: (NodeImpl+ && OMContainer+) implements DOMContainer;

    private NodeList DOMContainer.getElementsWildcard() {
        return new NodeListImpl() {
            protected Iterator getIterator() {
                return new OMFilterIterator(getDescendants(false)) {
                    protected boolean matches(OMNode node) {
                        return node.getType() == OMNode.ELEMENT_NODE;
                    }
                };
            }
        };
    }
    
    public final NodeList DOMContainer.getElementsByTagNameNS(String namespaceURI, String localName) {
        if ("*".equals(namespaceURI) && "*".equals(localName)) {
            return getElementsWildcard();
        } else {
            final QName qname = new QName(namespaceURI, localName);
            return new NodeListImpl() {
                protected Iterator getIterator() {
                    return new OMQNameFilterIterator(getDescendants(false), qname);
                }
            };
        }
    }
    
    public final NodeList DOMContainer.getElementsByTagName(final String name) {
        if (name.equals("*")) {
            return getElementsWildcard();
        } else {
            return new NodeListImpl() {
                protected Iterator getIterator() {
                    return new OMQualifiedNameFilterIterator(getDescendants(false), name);
                }
            };
        }
    }

    void DOMContainer.notifyChildComplete() {
        if (!this.isComplete() && getBuilder() == null) {
            Iterator iterator = getChildren();
            while (iterator.hasNext()) {
                OMNode node = (OMNode) iterator.next();
                if (!node.isComplete()) {
                    return;
                }
            }
            this.setComplete(true);
        }
    }
}
