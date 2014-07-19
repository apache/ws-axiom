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

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public aspect DOMLeafNodeSupport {
    public final boolean DOMLeafNode.hasChildNodes() {
        return false;
    }
    
    public final Node DOMLeafNode.getFirstChild() {
        return null;
    }

    public final Node DOMLeafNode.getLastChild() {
        return null;
    }

    public final NodeList DOMLeafNode.getChildNodes() {
        return EmptyNodeList.INSTANCE;
    }

    public final Node DOMLeafNode.appendChild(Node newChild) throws DOMException {
        throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
    }

    public final Node DOMLeafNode.removeChild(Node oldChild) throws DOMException {
        throw newDOMException(DOMException.NOT_FOUND_ERR);
    }

    public final Node DOMLeafNode.insertBefore(Node newChild, Node refChild) throws DOMException {
        throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
    }

    public final Node DOMLeafNode.replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw newDOMException(DOMException.HIERARCHY_REQUEST_ERR);
    }
}
