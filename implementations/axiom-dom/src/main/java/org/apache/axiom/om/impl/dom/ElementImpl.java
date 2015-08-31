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

import static org.apache.axiom.dom.DOMExceptionTranslator.newDOMException;

import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.NodeMigrationException;
import org.apache.axiom.core.NodeMigrationPolicy;
import org.apache.axiom.dom.DOMAttribute;
import org.apache.axiom.dom.DOMElement;
import org.apache.axiom.dom.DOMExceptionTranslator;
import org.apache.axiom.dom.Policies;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;

public abstract class ElementImpl extends ParentNode implements DOMElement {
    public ElementImpl(OMFactory factory) {
        super(factory);
    }

    private final String checkNamespaceIsDeclared(String prefix, String namespaceURI, boolean allowDefaultNamespace, boolean declare) {
        if (prefix == null) {
            if (namespaceURI.length() == 0) {
                prefix = "";
                declare = false;
            } else {
                prefix = coreLookupPrefix(namespaceURI, true);
                if (prefix != null && (allowDefaultNamespace || prefix.length() != 0)) {
                    declare = false;
                } else {
                    prefix = OMSerializerUtil.getNextNSPrefix();
                }
            }
        } else {
            String existingNamespaceURI = coreLookupNamespaceURI(prefix, true);
            declare = declare && !namespaceURI.equals(existingNamespaceURI);
        }
        if (declare) {
            coreSetAttribute(Policies.NAMESPACE_DECLARATION_MATCHER, null, prefix, null, namespaceURI);
        }
        return prefix;
    }

    final <T> ParentNode shallowClone(T options, ParentNode targetParent, ClonePolicy<T> policy) {
        ElementImpl clone = createClone(options, targetParent, policy);
        NamedNodeMap attributes = getAttributes();
        for (int i=0, l=attributes.getLength(); i<l; i++) {
            AttrImpl attr = (AttrImpl)attributes.item(i);
            AttrImpl clonedAttr = (AttrImpl)attr.clone(options, null, policy);
            clonedAttr.coreSetSpecified(attr.coreGetSpecified());
            if (policy.repairNamespaces(options) && attr instanceof NSAwareAttribute) {
                NSAwareAttribute nsAwareAttr = (NSAwareAttribute)attr;
                String namespaceURI = nsAwareAttr.coreGetNamespaceURI();
                if (namespaceURI.length() != 0) {
                    clone.checkNamespaceIsDeclared(nsAwareAttr.coreGetPrefix(), namespaceURI, false, true);
                }
            }
            try {
                clone.coreAppendAttribute(clonedAttr, NodeMigrationPolicy.MOVE_ALWAYS);
            } catch (NodeMigrationException ex) {
                DOMExceptionTranslator.translate(ex);
            }
        }
        return clone;
    }

    abstract <T> ElementImpl createClone(T options, ParentNode targetParent, ClonePolicy<T> policy);
    
    /*
     * DOM-Level 3 methods
     */

    public final void setIdAttribute(String name, boolean isId) throws DOMException {
        //find the attr
        AttrImpl tempAttr = (AttrImpl) this.getAttributeNode(name);
        if (tempAttr == null) {
            throw newDOMException(DOMException.NOT_FOUND_ERR);
        }

        this.updateIsId(isId, tempAttr);
    }

    public final void setIdAttributeNS(String namespaceURI, String localName, boolean isId)
            throws DOMException {
        //find the attr
        AttrImpl tempAttr = (AttrImpl) this.getAttributeNodeNS(namespaceURI, localName);
        if (tempAttr == null) {
            throw newDOMException(DOMException.NOT_FOUND_ERR);
        }

        this.updateIsId(isId, tempAttr);
    }

    public final void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        //find the attr
        if (((DOMAttribute)idAttr).coreGetOwnerElement() != this) {
            throw newDOMException(DOMException.NOT_FOUND_ERR);
        }
        this.updateIsId(isId, (AttrImpl)idAttr);
    }

    /**
     * Updates the id state of the attr and notifies the document
     *
     * @param isId
     * @param tempAttr
     */
    private void updateIsId(boolean isId, AttrImpl tempAttr) {
        if (tempAttr.isId != isId) {
            tempAttr.isId = isId;
            if (isId) {
                ownerDocument().addIdAttr(tempAttr);
            } else {
                ownerDocument().removeIdAttr(tempAttr);
            }
        }
    }
}
