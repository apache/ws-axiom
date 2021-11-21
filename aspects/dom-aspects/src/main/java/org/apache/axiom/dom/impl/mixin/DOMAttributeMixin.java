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

import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.dom.DOMAttribute;
import org.apache.axiom.dom.DOMConfigurationImpl;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMSemantics;
import org.apache.axiom.weaver.annotation.Mixin;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;

@Mixin(DOMAttribute.class)
public abstract class DOMAttributeMixin implements DOMAttribute {
    public final Document getOwnerDocument() {
        return (Document)coreGetOwnerDocument(true);
    }

    public final short getNodeType() {
        return Node.ATTRIBUTE_NODE;
    }

    public final String getNodeValue() throws DOMException {
        return getValue();
    }

    public final void setNodeValue(String nodeValue) throws DOMException {
        setValue(nodeValue);
    }

    public final String getNodeName() {
        return getName();
    }
    
    public final boolean hasAttributes() {
        return false;
    }

    public final NamedNodeMap getAttributes() {
        return null;
    }
    
    public final String getTextContent() {
        return getValue();
    }

    public final void setTextContent(String textContent) {
        setValue(textContent);
    }
    
    public final String getValue() {
        try {
            return coreGetCharacterData().toString();
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }
    
    public final void setValue(String value) {
        try {
            coreSetCharacterData(value, DOMSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }
    
    public final CoreElement getNamespaceContext() {
        return coreGetOwnerElement();
    }

    public final Element getOwnerElement() {
        return (Element)coreGetOwnerElement();
    }

    public final boolean getSpecified() {
        return coreGetSpecified();
    }

    public final TypeInfo getSchemaTypeInfo() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public final void normalize(DOMConfigurationImpl config) {
    }
}
