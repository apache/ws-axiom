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

import org.apache.axiom.dom.DOMAttribute;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;

/** Implementation of <code>org.w3c.dom.Attr</code> and <code>org.apache.axiom.om.OMAttribute</code> */
public abstract class AttrImpl extends ParentNode implements DOMAttribute {
    /** Flag used to mark an attribute as per the DOM Level 3 specification */
    protected boolean isId;

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

    public boolean getSpecified() {
        return coreGetSpecified();
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
}
