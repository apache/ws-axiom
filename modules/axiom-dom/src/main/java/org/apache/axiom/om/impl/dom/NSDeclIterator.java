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

import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.w3c.dom.Attr;

class NSDeclIterator implements Iterator {
    private final AttributeMap attributes;
    private boolean hasNextCalled;
    private int index = -1;
    private Attr nsDecl;

    public NSDeclIterator(AttributeMap attributes) {
        this.attributes = attributes;
    }

    public boolean hasNext() {
        if (!hasNextCalled) {
            while (true) {
                index++;
                if (index >= attributes.getLength()) {
                    nsDecl = null;
                    hasNextCalled = true;
                    break;
                }
                Attr attr = (Attr)attributes.item(index);
                if (OMConstants.XMLNS_NS_URI.equals(attr.getNamespaceURI())) {
                    nsDecl = attr;
                    hasNextCalled = true;
                    break;
                }
            }
        }
        return nsDecl != null;
    }

    public Object next() {
        hasNext();
        hasNextCalled = false;
        return new OMNamespaceImpl(nsDecl.getValue(), nsDecl.getPrefix() == null ? "" : nsDecl.getLocalName());
    }

    public void remove() {
        if (hasNextCalled || nsDecl == null) {
            throw new IllegalStateException();
        }
        attributes.removeItem(index);
        nsDecl = null;
    }
}
