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
package org.apache.axiom.dom.impl.mixin;

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import java.util.Hashtable;
import java.util.Objects;

import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.dom.DOMDocument;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMNode;
import org.apache.axiom.dom.DOMSemantics;
import org.apache.axiom.weaver.annotation.Mixin;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;

@Mixin
public abstract class DOMNodeMixin implements DOMNode {
    /** Holds the user data objects */
    private Hashtable userData; // Will be initialized in setUserData()

    @Override
    public final boolean isSupported(String feature, String version) {
        return getDOMNodeFactory().hasFeature(feature, version);
    }

    @Override
    public final String lookupNamespaceURI(String prefix) {
        try {
            CoreElement context = getNamespaceContext();
            if (context == null) {
                return null;
            }
            if (prefix == null) {
                prefix = "";
            } else if (prefix.length() == 0) {
                return null;
            }
            String namespaceURI = context.coreLookupNamespaceURI(prefix, DOMSemantics.INSTANCE);
            return namespaceURI == null || namespaceURI.length() == 0 ? null : namespaceURI;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final String lookupPrefix(String namespaceURI) {
        try {
            CoreElement context = getNamespaceContext();
            if (context == null) {
                return null;
            }
            if (namespaceURI == null) {
                return null;
            } else {
                String prefix = context.coreLookupPrefix(namespaceURI, DOMSemantics.INSTANCE);
                return prefix == null || prefix.length() == 0 ? null : prefix;
            }
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final boolean isDefaultNamespace(String namespaceURI) {
        try {
            CoreElement context = getNamespaceContext();
            if (context == null) {
                return false;
            }
            if (namespaceURI == null) {
                namespaceURI = "";
            }
            return namespaceURI.equals(context.coreLookupNamespaceURI("", DOMSemantics.INSTANCE));
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final Node cloneNode(boolean deep) {
        try {
            DOMNode clone =
                    (DOMNode)
                            coreClone(
                                    deep ? DOMSemantics.DEEP_CLONE : DOMSemantics.SHALLOW_CLONE,
                                    null);
            if (!(clone instanceof DOMDocument)) {
                clone.coreSetOwnerDocument(coreGetOwnerDocument(true));
            }
            return clone;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public void normalize() {
        // Parent node should override this
    }

    /*
     * DOM-Level 3 methods
     */

    @Override
    public String getBaseURI() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        // This is not yet implemented. In the meantime, we throw a DOMException
        // and not an UnsupportedOperationException, since this works better with
        // some other libraries (such as Saxon 8.9).
        throw newDOMException(DOMException.NOT_SUPPORTED_ERR);
    }

    @Override
    public boolean isSameNode(Node node) {
        // TODO : check
        return this == node;
    }

    // TODO : sumedha, complete
    @Override
    public boolean isEqualNode(Node other) {
        if (getNodeType() != other.getNodeType()
                || !Objects.equals(getNodeName(), other.getNodeName())
                || !Objects.equals(getLocalName(), other.getLocalName())
                || !Objects.equals(getNamespaceURI(), other.getNamespaceURI())
                || !Objects.equals(getPrefix(), other.getPrefix())
                || !Objects.equals(getNodeValue(), other.getNodeValue())) {
            return false;
        }
        NamedNodeMap attributes = getAttributes();
        NamedNodeMap otherAttributes = other.getAttributes();
        if (attributes == null ^ otherAttributes == null) {
            return false;
        }
        if (attributes != null) {
            if (attributes.getLength() != otherAttributes.getLength()) {
                return false;
            }
            for (int a = 0, itemCount = attributes.getLength(); a < itemCount; a++) {
                Node attribute = attributes.item(a);
                Node otherAttribute = otherAttributes.getNamedItem(attribute.getNodeName());
                if (otherAttribute == null || !attribute.isEqualNode(otherAttribute)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Object getFeature(String feature, String version) {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Object setUserData(String key, Object value, UserDataHandler userDataHandler) {
        if (userData == null) {
            userData = new Hashtable();
        }
        return userData.put(key, value);
    }

    @Override
    public Object getUserData(String key) {
        if (userData != null) {
            return userData.get(key);
        }
        return null;
    }

    /**
     * Get the owner document of this node. In contrast to {@link Node#getOwnerDocument()}, this
     * method returns a non null value when invoked on a {@link Document} instance.
     *
     * @return the owner document
     */
    final DOMDocument ownerDocument() {
        return (DOMDocument) coreGetOwnerDocument(true);
    }
}
