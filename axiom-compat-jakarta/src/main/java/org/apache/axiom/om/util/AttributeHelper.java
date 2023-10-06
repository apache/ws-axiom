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

package org.apache.axiom.om.util;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNamespace;

/** Helper class for attributes. */
public class AttributeHelper {
    /**
     * In Axiom, a single tree should always contain objects created from the same type of factory
     * (eg: LinkedListImplFactory, DOMFactory, etc.,). This method will convert omAttribute to the
     * given omFactory.
     *
     * @see ElementHelper#importOMElement(OMElement, OMFactory) to convert instances of OMElement
     * 
     * @deprecated Use {@link OMFactory#importInformationItem(OMInformationItem)} instead.
     */
    public static void importOMAttribute(OMAttribute omAttribute, OMElement omElement) {
        // first check whether the given OMAttribute has the same OMFactory
        if (omAttribute.getOMFactory().getMetaFactory() == omElement.getOMFactory().getMetaFactory()) {
            omElement.addAttribute(omAttribute);
        } else {
            OMNamespace ns = omAttribute.getNamespace();
            omElement.addAttribute(omAttribute.getLocalName(), omAttribute.getAttributeValue(),
                                   omElement.getOMFactory().createOMNamespace(ns.getNamespaceURI(), ns.getPrefix()));
        }
    }
}
