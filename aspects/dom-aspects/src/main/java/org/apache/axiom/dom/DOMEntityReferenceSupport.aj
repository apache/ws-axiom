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
import org.w3c.dom.NamedNodeMap;

public aspect DOMEntityReferenceSupport {
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

    public final String DOMEntityReference.getTextContent() throws DOMException {
        throw new UnsupportedOperationException();
    }
}
