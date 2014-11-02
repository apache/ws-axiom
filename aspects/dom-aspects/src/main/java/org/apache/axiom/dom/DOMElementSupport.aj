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

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;

public aspect DOMElementSupport {
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

    public final TypeInfo DOMElement.getSchemaTypeInfo() {
        throw new UnsupportedOperationException();
    }

    public final String DOMElement.lookupNamespaceURI(String specifiedPrefix) {
        String namespace = this.getNamespaceURI();
        String prefix = this.getPrefix();
        // First check for namespaces implicitly defined by the namespace prefix/URI of the element
        // TODO: although the namespace != null condition conforms to the specs, it is likely incorrect; see XERCESJ-1586
        if (namespace != null
                && (prefix == null && specifiedPrefix == null
                        || prefix != null && prefix.equals(specifiedPrefix))) {
            return namespace;
        }
        // looking in attributes
        if (this.hasAttributes()) {
            NamedNodeMap map = this.getAttributes();
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                namespace = attr.getNamespaceURI();
                if (namespace != null && namespace.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
                    // At this point we know that either the prefix of the attribute is null and
                    // the local name is "xmlns" or the prefix is "xmlns" and the local name is the
                    // namespace prefix declared by the namespace declaration. We check that constraint
                    // when the attribute is created.
                    String attrPrefix = attr.getPrefix();
                    if ((specifiedPrefix == null && attrPrefix == null)
                            || (specifiedPrefix != null && attrPrefix != null
                                    && attr.getLocalName().equals(specifiedPrefix))) {
                        String value = attr.getNodeValue();
                        return value.length() > 0 ? value : null;
                    }
                }
            }
        }
        // looking in ancestor
        DOMParentNode parent = (DOMParentNode)coreGetParent();
        return parent instanceof Element ? parent.lookupNamespaceURI(specifiedPrefix) : null;
    }

    public final String DOMElement.lookupPrefix(String namespaceURI) {
        return lookupPrefix(namespaceURI, this);
    }
    
    private final String DOMElement.lookupPrefix(String namespaceURI, Element originalElement) {
        if (namespaceURI == null || namespaceURI.length() == 0) {
            return null;
        }
        if (namespaceURI.equals(getNamespaceURI())) {
            String prefix = getPrefix();
            if (namespaceURI.equals(originalElement.lookupNamespaceURI(prefix))) {
                return prefix;
            }
        }
        if (this.hasAttributes()) {
            NamedNodeMap map = this.getAttributes();
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                String attrPrefix = attr.getPrefix();
                if (attrPrefix != null && attrPrefix.equals(XMLConstants.XMLNS_ATTRIBUTE)
                        && attr.getNodeValue().equals(namespaceURI)) {
                    String prefix = attr.getLocalName();
                    if (namespaceURI.equals(originalElement.lookupNamespaceURI(prefix))) {
                        return prefix;
                    }
                }
            }
        }
        DOMParentNode parent = (DOMParentNode)coreGetParent();
        return parent instanceof Element ? ((DOMElement)parent).lookupPrefix(namespaceURI, originalElement) : null;
    }
}
