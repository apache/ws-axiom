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

import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.ElementAction;
import org.apache.axiom.dom.DOMConfigurationImpl;
import org.apache.axiom.dom.DOMDocumentFragment;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMSemantics;
import org.apache.axiom.weaver.annotation.Mixin;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@Mixin
public abstract class DOMDocumentFragmentMixin implements DOMDocumentFragment {
    @Override
    public final Document getOwnerDocument() {
        return (Document)coreGetOwnerDocument(true);
    }

    @Override
    public final short getNodeType() {
        return Node.DOCUMENT_FRAGMENT_NODE;
    }

    @Override
    public final String getNodeName() {
        return "#document-fragment";
    }

    @Override
    public final String getNodeValue() {
        return null;
    }

    @Override
    public final void setNodeValue(String nodeValue) {
    }

    @Override
    public final CoreElement getNamespaceContext() {
        return null;
    }

    @Override
    public final String getPrefix() {
        return null;
    }

    @Override
    public final void setPrefix(String prefix) throws DOMException {
        throw newDOMException(DOMException.NAMESPACE_ERR);
    }

    @Override
    public final String getNamespaceURI() {
        return null;
    }

    @Override
    public final String getLocalName() {
        return null;
    }

    @Override
    public final boolean hasAttributes() {
        return false;
    }

    @Override
    public final NamedNodeMap getAttributes() {
        return null;
    }
    
    @Override
    public final String getTextContent() {
        try {
            return coreGetCharacterData(ElementAction.RECURSE).toString();
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final void setTextContent(String textContent) {
        try {
            coreSetCharacterData(textContent, DOMSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    @Override
    public final void normalize(DOMConfigurationImpl config) {
    }
}
