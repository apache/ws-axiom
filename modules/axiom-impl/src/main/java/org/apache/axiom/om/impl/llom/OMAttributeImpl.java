/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

import javax.xml.namespace.QName;

/** Class OMAttributeImpl */
public class OMAttributeImpl implements OMAttribute {
    /** Field localName */
    private String localName;

    /** Field value */
    private String value;

    /** Field namespace */
    private OMNamespace namespace;

    /** <code>OMFactory</code> that created this <code>OMAttribute</code> */
    private OMFactory factory;

    /**
     * Constructor OMAttributeImpl.
     *
     * @param localName
     * @param ns
     * @param value
     */
    public OMAttributeImpl(String localName, OMNamespace ns, String value, OMFactory factory) 
    {
        if (localName == null || localName.trim().length() == 0)
            throw new IllegalArgumentException("Local name may not be null or empty");

        this.localName = localName;
        this.value = value;
        this.namespace = ns;
        this.factory = factory;
    }

    /** @return Returns QName. */
    public QName getQName() {
        if (namespace != null) {
            // Guard against QName implementation sillyness.
            if (namespace.getPrefix() == null) {
                return new QName(namespace.getNamespaceURI(), localName);
            } else {
                return new QName(namespace.getNamespaceURI(), localName, namespace.getPrefix());
            }
        } else {
            return new QName(localName);
        }
    }

    // -------- Getters and Setters

    /**
     * Method getLocalName.
     *
     * @return Returns local name.
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Method setLocalName.
     *
     * @param localName
     */
    public void setLocalName(String localName) {
        if (localName == null || localName.trim().length() == 0)
            throw new IllegalArgumentException("Local name may not be null or empty");
        this.localName = localName;
    }

    /**
     * Method getAttributeValue.
     *
     * @return Returns value.
     */
    public String getAttributeValue() {
        return value;
    }

    /**
     * Method setAttributeValue.
     *
     * @param value
     */
    public void setAttributeValue(String value) {
        this.value = value;
    }

    /**
     * Method setOMNamespace.
     *
     * @param omNamespace
     */
    public void setOMNamespace(OMNamespace omNamespace) {
        this.namespace = omNamespace;
    }

    /**
     * Method getNamespace.
     *
     * @return Returns namespace.
     */
    public OMNamespace getNamespace() {
        return namespace;
    }

    public OMFactory getOMFactory() {
        return this.factory;
    }

}
