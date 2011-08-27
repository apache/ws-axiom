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
package org.apache.axiom.om;

import javax.xml.namespace.QName;

/**
 * Represents an information item that has a name, more precisely a namespace URI, a local name and
 * a prefix. This applies to elements and attributes.
 */
public interface OMNamedInformationItem extends OMInformationItem {
    /**
     * Get the local name of the information item.
     *
     * @return the local name of the information item
     */
    String getLocalName();

    /**
     * Method setLocalName
     *
     * @param localName
     */
    void setLocalName(String localName);

    /**
     * Get the namespace this information item is part of.
     * 
     * @return the namespace of this information item, or <code>null</code> if the information item
     *         has no namespace
     */
    OMNamespace getNamespace();

    /**
     * Get the QName of this information item.
     * 
     * @return the {@link QName} for the information item
     */
    QName getQName();

    /**
     * Get the namespace URI of the information item. Note that the contract of this method is
     * identical to DOM's {@link org.w3c.dom.Node#getNamespaceURI()} (when called on an
     * {@link org.w3c.dom.Element} or {@link org.w3c.dom.Attr}).
     * 
     * @return the namespace URI of the information item or <code>null</code> if the element has no
     *         namespace
     */
    String getNamespaceURI();
}
