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

import static org.apache.axiom.dom.DOMExceptionTranslator.newDOMException;

import org.apache.axiom.core.CoreElement;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public aspect DOMEntityReferenceSupport {
    public final Document DOMEntityReference.getOwnerDocument() {
        return (Document)coreGetOwnerDocument(true);
    }

    public final short DOMEntityReference.getNodeType() {
        return Node.ENTITY_REFERENCE_NODE;
    }

    public final String DOMEntityReference.getNodeName() {
        return coreGetName();
    }

    public final String DOMEntityReference.getNodeValue() {
        return null;
    }

    public final void DOMEntityReference.setNodeValue(String nodeValue) {
    }

    public final String DOMEntityReference.getPrefix() {
        return null;
    }

    public final void DOMEntityReference.setPrefix(String prefix) throws DOMException {
        throw newDOMException(DOMException.NAMESPACE_ERR);
    }

    public final String DOMEntityReference.getNamespaceURI() {
        return null;
    }

    public final String DOMEntityReference.getLocalName() {
        return null;
    }

    public final boolean DOMEntityReference.hasAttributes() {
        return false;
    }

    public final NamedNodeMap DOMEntityReference.getAttributes() {
        return null;
    }

    public final String DOMEntityReference.getTextContent() {
        throw new UnsupportedOperationException();
    }

    public final void DOMEntityReference.setTextContent(String textContent) {
        throw new UnsupportedOperationException();
    }
    
    public final CoreElement DOMEntityReference.getNamespaceContext() {
        return coreGetParentElement();
    }

    public final boolean DOMEntityReference.hasChildNodes() {
        throw new UnsupportedOperationException();
    }

    public final Node DOMEntityReference.getFirstChild() {
        throw new UnsupportedOperationException();
    }

    public final Node DOMEntityReference.getLastChild() {
        throw new UnsupportedOperationException();
    }

    public final NodeList DOMEntityReference.getChildNodes() {
        throw new UnsupportedOperationException();
    }

    public final Node DOMEntityReference.appendChild(Node newChild) throws DOMException {
        throw newDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
    }

    public final Node DOMEntityReference.removeChild(Node oldChild) throws DOMException {
        throw newDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
    }

    public final Node DOMEntityReference.insertBefore(Node newChild, Node refChild) throws DOMException {
        throw newDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
    }

    public final Node DOMEntityReference.replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw newDOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
    }
}
