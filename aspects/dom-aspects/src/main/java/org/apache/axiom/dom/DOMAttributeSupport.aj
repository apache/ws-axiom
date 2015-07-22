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

import org.apache.axiom.core.CoreElement;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public aspect DOMAttributeSupport {
    public final Document DOMAttribute.getOwnerDocument() {
        return (Document)coreGetOwnerDocument(true);
    }

    public final short DOMAttribute.getNodeType() {
        return Node.ATTRIBUTE_NODE;
    }

    public final String DOMAttribute.getNodeValue() throws DOMException {
        return getValue();
    }

    public final void DOMAttribute.setNodeValue(String nodeValue) throws DOMException {
        setValue(nodeValue);
    }

    public final String DOMAttribute.getNodeName() {
        return getName();
    }
    
    public final boolean DOMAttribute.hasAttributes() {
        return false;
    }

    public final NamedNodeMap DOMAttribute.getAttributes() {
        return null;
    }
    
    public final String DOMAttribute.getTextContent() {
        return getValue();
    }

    public final void DOMAttribute.setTextContent(String textContent) {
        setValue(textContent);
    }
    
    public final String DOMAttribute.getValue() {
        return coreGetValue();
    }
    
    public final void DOMAttribute.setValue(String value) {
        coreSetCharacterData(value, Policies.DETACH_POLICY);
    }
    
    public final CoreElement DOMAttribute.getNamespaceContext() {
        return coreGetOwnerElement();
    }
}
