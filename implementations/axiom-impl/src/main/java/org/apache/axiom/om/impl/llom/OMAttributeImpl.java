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

package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.AxiomAttribute;

/** Class OMAttributeImpl */
public class OMAttributeImpl extends OMInformationItemImpl implements AxiomAttribute {
    private String value;

    private String type;

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
        
        if (ns != null) {
            if (ns.getNamespaceURI().length() == 0) {
                if (ns.getPrefix().length() > 0) {
                    throw new IllegalArgumentException("Cannot create a prefixed attribute with an empty namespace name");
                } else {
                    ns = null;
                }
            } else if (ns.getPrefix().length() == 0) {
                throw new IllegalArgumentException("Cannot create an unprefixed attribute with a namespace");
            }
        }

        internalSetLocalName(localName);
        this.value = value;
        internalSetNamespace(ns);
        this.type = OMConstants.XMLATTRTYPE_CDATA;
        this.factory = factory;
    }

    // -------- Getters and Setters

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

    public String getAttributeType() {
        return type;
    }

    /**
     * Method setAttributeType.
     *
     * @param type
     */
    public void setAttributeType(String type) {
        this.type = type;
    }

    public OMFactory getOMFactory() {
        return this.factory;
    }

    public OMInformationItem clone(OMCloneOptions options) {
        return new OMAttributeImpl(getLocalName(), getNamespace(), value, factory);
    }
}
