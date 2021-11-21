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
package org.apache.axiom.dom;

import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreTypedAttribute;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class AttributesNamedNodeMap implements NamedNodeMap {
    private final DOMElement element;
    
    public AttributesNamedNodeMap(DOMElement element) {
        this.element = element;
    }

    @Override
    public int getLength() {
        int length = 0;
        CoreAttribute attr = element.coreGetFirstAttribute();
        while (attr != null) {
            attr = attr.coreGetNextAttribute();
            length++;
        }
        return length;
    }
    
    @Override
    public Node item(int index) {
        // TODO: wrong result for negative indexes
        CoreAttribute attr = element.coreGetFirstAttribute();
        for (int i=0; i<index && attr != null; i++) {
            attr = attr.coreGetNextAttribute();
        }
        return (Node)attr;
    }

    @Override
    public Node getNamedItem(String name) {
        return element.getAttributeNode(name);
    }

    @Override
    public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
        return element.getAttributeNodeNS(namespaceURI, localName);
    }

    @Override
    public Node setNamedItem(Node arg) throws DOMException {
        if (arg instanceof CoreTypedAttribute) {
            return element.setAttributeNode((Attr)arg);
        } else {
            throw DOMExceptionUtil.newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
        }
    }

    @Override
    public Node setNamedItemNS(Node arg) throws DOMException {
        if (arg instanceof CoreTypedAttribute) {
            return element.setAttributeNodeNS((Attr)arg);
        } else {
            throw DOMExceptionUtil.newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
        }
    }

    @Override
    public Node removeNamedItem(String name) throws DOMException {
        // TODO: try to merge with corresponding method in ElementImpl
        Attr attr = element.getAttributeNode(name);
        if (attr != null) {
            element.removeAttributeNode(attr);
            return attr;
        } else {
            throw DOMExceptionUtil.newDOMException(DOMException.NOT_FOUND_ERR);
        }
    }

    @Override
    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        // TODO: try to merge with corresponding method in ElementImpl
        Attr attr = element.getAttributeNodeNS(namespaceURI, localName);
        if (attr != null) {
            element.removeAttributeNode(attr);
            return attr;
        } else {
            throw DOMExceptionUtil.newDOMException(DOMException.NOT_FOUND_ERR);
        }
    }
}
