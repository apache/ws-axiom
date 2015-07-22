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

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.AxiomAttribute;
import org.apache.axiom.om.impl.common.Policies;

/** Class OMAttributeImpl */
public class OMAttributeImpl extends Attribute implements AxiomAttribute {
    /**
     * Constructor OMAttributeImpl.
     *
     * @param localName
     * @param ns
     * @param value
     */
    public OMAttributeImpl(String localName, OMNamespace ns, String value, OMFactory factory) 
    {
        super(factory);
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
        coreSetCharacterData(value, Policies.DETACH_POLICY);
        internalSetNamespace(ns);
        coreSetType(OMConstants.XMLATTRTYPE_CDATA);
    }

    public OMInformationItem clone(OMCloneOptions options) {
        return new OMAttributeImpl(getLocalName(), getNamespace(), getAttributeValue(), getOMFactory());
    }
}
