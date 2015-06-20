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

import org.apache.axiom.core.NonDeferringParentNode;
import org.apache.axiom.dom.DOMAttribute;
import org.apache.axiom.om.OMFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

/** Implementation of <code>org.w3c.dom.Attr</code> and <code>org.apache.axiom.om.OMAttribute</code> */
public abstract class AttrImpl extends RootNode implements DOMAttribute, NonDeferringParentNode {
    /** Flag used to mark an attribute as per the DOM Level 3 specification */
    protected boolean isId;

    AttrImpl(DocumentImpl ownerDocument, OMFactory factory) {
        super(factory);
        coreSetOwnerDocument(ownerDocument);
    }

    // /
    // /org.w3c.dom.Node methods
    // /

    /**
     * Returns the value of this attribute.
     *
     * @see org.w3c.dom.Attr#getValue()
     */
    public String getValue() {
        String value = null;
        StringBuffer buffer = null;
        Node child = getFirstChild();

        while (child != null) {
            String textValue = ((Text)child).getData();
            if (textValue != null && textValue.length() != 0) {
                if (value == null) {
                    // This is the first non empty text node. Just save the string.
                    value = textValue;
                } else {
                    // We've already seen a non empty text node before. Concatenate using
                    // a StringBuffer.
                    if (buffer == null) {
                        // This is the first text node we need to append. Initialize the
                        // StringBuffer.
                        buffer = new StringBuffer(value);
                    }
                    buffer.append(textValue);
                }
            }
            child = child.getNextSibling();
        }

        if (value == null) {
            // We didn't see any text nodes. Return an empty string.
            return "";
        } else if (buffer != null) {
            return buffer.toString();
        } else {
            return value;
        }
    }

    // /
    // /org.w3c.dom.Attr methods
    // /

    /**
     * Returns the owner element.
     *
     * @see org.w3c.dom.Attr#getOwnerElement()
     */
    public Element getOwnerElement() {
        return (Element)coreGetOwnerElement();
    }

    void setOwnerElement(ElementImpl element, boolean useDomSemantics) {
        if (element == null) {
            internalUnsetOwnerElement(useDomSemantics ? coreGetOwnerDocument(true) : null);
        } else {
            internalSetOwnerElement(element);
        }
    }
    
    public boolean getSpecified() {
        return coreGetSpecified();
    }

    public void setSpecified(boolean specified) {
        coreSetSpecified(specified);
    }

    /**
     * Sets the value of the attribute.
     *
     * @see org.w3c.dom.Attr#setValue(String)
     */
    public void setValue(String value) throws DOMException {
        setTextContent(value);
    }

    public final String coreGetValue() {
        return getValue();
    }
    
    public final void coreSetValue(String value) {
        setValue(value);
    }

    /*
     * DOM-Level 3 methods
     */
    public TypeInfo getSchemaTypeInfo() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isId() {
        return isId;
    }

    public final boolean isComplete() {
        return true;
    }

    public final void setComplete(boolean state) {
        if (state != true) {
            throw new IllegalStateException();
        }
    }

    public final void build() {
        // An attribute node doesn't have a builder
    }

    public final String lookupNamespaceURI(String specifiedPrefix) {
        Element ownerElement = getOwnerElement();
        return ownerElement == null ? null : ownerElement.lookupNamespaceURI(specifiedPrefix);
    }

    public final String lookupPrefix(String namespaceURI) {
        Element ownerElement = getOwnerElement();
        return ownerElement == null ? null : ownerElement.lookupPrefix(namespaceURI);
    }
}
