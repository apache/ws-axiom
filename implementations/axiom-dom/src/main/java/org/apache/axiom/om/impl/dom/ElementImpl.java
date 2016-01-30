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

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import org.apache.axiom.dom.DOMAttribute;
import org.apache.axiom.dom.DOMElement;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

public abstract class ElementImpl extends ParentNode implements DOMElement {
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
