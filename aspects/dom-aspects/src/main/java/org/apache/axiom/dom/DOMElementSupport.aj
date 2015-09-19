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

import javax.xml.XMLConstants;

import org.apache.axiom.core.AttributeMatcher;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNSAwareAttribute;
import org.apache.axiom.core.CoreNamespaceDeclaration;
import org.apache.axiom.core.ElementAction;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

public aspect DOMElementSupport {
    public final Document DOMElement.getOwnerDocument() {
        return (Document)coreGetOwnerDocument(true);
    }

    public final short DOMElement.getNodeType() {
        return Node.ELEMENT_NODE;
    }
    
    public final String DOMElement.getNodeName() {
        return getTagName();
    }

    // TODO: should be final
    public String DOMElement.getNodeValue() {
        return null;
    }

    // TODO: should be final
    public void DOMElement.setNodeValue(String nodeValue) {
    }

    public final String DOMElement.getTagName() {
        return internalGetName();
    }
    
    public final TypeInfo DOMElement.getSchemaTypeInfo() {
        throw new UnsupportedOperationException();
    }

    public final CoreElement DOMElement.getNamespaceContext() {
        return this;
    }

    public final boolean DOMElement.hasAttributes() {
        return coreGetFirstAttribute() != null;
    }

    public final NamedNodeMap DOMElement.getAttributes() {
        return new AttributesNamedNodeMap(this);
    }
    
    public final Attr DOMElement.getAttributeNode(String name) {
        return (DOMAttribute)coreGetAttribute(Policies.DOM1_ATTRIBUTE_MATCHER, null, name);
    }

    public final Attr DOMElement.getAttributeNodeNS(String namespaceURI, String localName) {
        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
            return (DOMAttribute)coreGetAttribute(Policies.NAMESPACE_DECLARATION_MATCHER, null, localName.equals(XMLConstants.XMLNS_ATTRIBUTE) ? "" : localName);
        } else {
            return (DOMAttribute)coreGetAttribute(Policies.DOM2_ATTRIBUTE_MATCHER, namespaceURI == null ? "" : namespaceURI, localName);
        }
    }
    
    public final String DOMElement.getAttribute(String name) {
        Attr attr = getAttributeNode(name);
        return attr != null ? attr.getValue() : "";
    }

    public final String DOMElement.getAttributeNS(String namespaceURI, String localName) {
        Attr attr = getAttributeNodeNS(namespaceURI, localName);
        return attr != null ? attr.getValue() : "";
    }

    public final boolean DOMElement.hasAttribute(String name) {
        return getAttributeNode(name) != null;
    }

    public final boolean DOMElement.hasAttributeNS(String namespaceURI, String localName) {
        return getAttributeNodeNS(namespaceURI, localName) != null;
    }

    public final void DOMElement.setAttribute(String name, String value) {
        NSUtil.validateName(name);
        coreSetAttribute(Policies.DOM1_ATTRIBUTE_MATCHER, null, name, null, value);
    }

    public final void DOMElement.setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        int i = NSUtil.validateQualifiedName(qualifiedName);
        String prefix;
        String localName;
        if (i == -1) {
            prefix = "";
            localName = qualifiedName;
        } else {
            prefix = qualifiedName.substring(0, i);
            localName = qualifiedName.substring(i+1);
        }
        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
            coreSetAttribute(Policies.NAMESPACE_DECLARATION_MATCHER, null, NSUtil.getDeclaredPrefix(localName, prefix), null, value);
        } else {
            namespaceURI = NSUtil.normalizeNamespaceURI(namespaceURI);
            NSUtil.validateAttributeName(namespaceURI, localName, prefix);
            coreSetAttribute(Policies.DOM2_ATTRIBUTE_MATCHER, namespaceURI, localName, prefix, value);
        }
    }

    public final Attr DOMElement.setAttributeNode(Attr newAttr) throws DOMException {
        return setAttributeNodeNS(newAttr);
    }
    
    public final Attr DOMElement.setAttributeNodeNS(Attr _newAttr) throws DOMException {
        DOMAttribute newAttr = (DOMAttribute)_newAttr;
        if (newAttr.coreGetOwnerElement() == this) {
            // This means that the "new" attribute is already linked to the element
            // and replaces itself.
            return newAttr;
        } else {
            AttributeMatcher matcher;
            if (newAttr instanceof CoreNSAwareAttribute) {
                matcher = Policies.DOM2_ATTRIBUTE_MATCHER;
            } else if (newAttr instanceof CoreNamespaceDeclaration) {
                matcher = Policies.NAMESPACE_DECLARATION_MATCHER;
            } else {
                // Must be a DOM1 (namespace unaware) attribute
                matcher = Policies.DOM1_ATTRIBUTE_MATCHER;
            }
            try {
                return (DOMAttribute)coreSetAttribute(matcher, newAttr, Policies.ATTRIBUTE_MIGRATION_POLICY, false, null, ReturnValue.REPLACED_ATTRIBUTE);
            } catch (CoreModelException ex) {
                throw DOMExceptionTranslator.translate(ex);
            }
        }
    }

    public final Attr DOMElement.removeAttributeNode(Attr oldAttr) throws DOMException {
        DOMAttribute attr = (DOMAttribute)oldAttr;
        if (attr.coreGetOwnerElement() != this) {
            throw DOMExceptionTranslator.newDOMException(DOMException.NOT_FOUND_ERR);
        } else {
            attr.coreRemove(Policies.DETACH_POLICY);
        }
        return attr;
    }

    public final void DOMElement.removeAttribute(String name) throws DOMException {
        // Specs: "If no attribute with this name is found, this method has no effect."
        coreRemoveAttribute(Policies.DOM1_ATTRIBUTE_MATCHER, null, name, Policies.DETACH_POLICY);
    }

    public final void DOMElement.removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        // Specs: "If no attribute with this local name and namespace URI is found, this method has no effect."
        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
            coreRemoveAttribute(Policies.NAMESPACE_DECLARATION_MATCHER, null, localName.equals(XMLConstants.XMLNS_ATTRIBUTE) ? "" : localName, Policies.DETACH_POLICY);
        } else {
            coreRemoveAttribute(Policies.DOM2_ATTRIBUTE_MATCHER, namespaceURI == null ? "" : namespaceURI, localName, Policies.DETACH_POLICY);
        }
    }
    
    public final String DOMElement.getTextContent() {
        return coreGetCharacterData(ElementAction.RECURSE).toString();
    }

    public final void DOMElement.setTextContent(String textContent) {
        coreSetCharacterData(textContent, Policies.DETACH_POLICY);
    }

    public final NodeList DOMElement.getElementsByTagName(String tagname) {
        return new ElementsByTagName(this, tagname);
    }

    public final NodeList DOMElement.getElementsByTagNameNS(String namespaceURI, String localName) {
        return new ElementsByTagNameNS(this, namespaceURI, localName);
    }
}
