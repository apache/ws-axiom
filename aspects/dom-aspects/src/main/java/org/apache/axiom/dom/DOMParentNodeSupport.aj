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

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import org.apache.axiom.core.CoreChildNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public aspect DOMParentNodeSupport {
    public final NodeList DOMParentNode.getChildNodes() {
        return this;
    }

    public final int DOMParentNode.getLength() {
        int count = 0;
        Node child = getFirstChild();
        while (child != null) {
            count++;
            child = child.getNextSibling();
        }
        return count;
    }

    public final Node DOMParentNode.item(int index) {
        int count = 0;
        Node child = getFirstChild();
        while (child != null) {
            if (count == index) {
                return child;
            } else {
                child = child.getNextSibling();
            }
            count++;
        }
        return null;
    }

    public final Node DOMParentNode.getFirstChild() {
        return (Node)coreGetFirstChild();
    }

    public final Node DOMParentNode.getLastChild() {
        return (Node)coreGetLastChild();
    }

    public final boolean DOMParentNode.hasChildNodes() {
        return getFirstChild() != null;
    }

    public final Node DOMParentNode.removeChild(Node oldChild) throws DOMException {
        if (oldChild.getParentNode() == this) {
            ((CoreChildNode)oldChild).coreDetach(coreGetOwnerDocument(true));
            return oldChild;
        } else {
            throw newDOMException(DOMException.NOT_FOUND_ERR);
        }
    }

    public void DOMParentNode.normalize(DOMConfigurationImpl config) {
        CoreChildNode child = coreGetFirstChild();
        while (child != null) {
            ((DOMNode)child).normalize(config);
            child = child.coreGetNextSibling();
        }
    }
}
