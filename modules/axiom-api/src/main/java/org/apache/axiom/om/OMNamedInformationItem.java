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
     * Set the local name of this information item.
     * 
     * @param localName
     *            the new local name of the information item
     */
    void setLocalName(String localName);

    /**
     * Get the namespace this information item is part of.
     * 
     * @return The namespace of this information item, or <code>null</code> if the information item
     *         has no namespace. Note that this implies that the method never returns an
     *         {@link OMNamespace} object with both prefix and namespace URI set to the empty
     *         string. In addition, the prefix of the returned {@link OMNamespace} object (if any)
     *         is never <code>null</code>: if a <code>null</code> prefix was specified when creating
     *         this information item, then a prefix has been automatically assigned and the assigned
     *         prefix is returned.
     */
    OMNamespace getNamespace();

    /**
     * Get the QName of this information item.
     * <p>
     * Note that if you simply need to check if the information item has a given QName, then you
     * should use {@link #hasName(QName)} instead of this method.
     * 
     * @return the {@link QName} for the information item
     */
    QName getQName();

    /**
     * Get the prefix of this information item. Note that the contract of this method is identical
     * to DOM's {@link org.w3c.dom.Node#getPrefix()} (when called on an {@link org.w3c.dom.Element}
     * or {@link org.w3c.dom.Attr}).
     * 
     * @return the prefix of the information item or <code>null</code> if the information item has
     *         no prefix
     */
    String getPrefix();

    /**
     * Get the namespace URI of this information item. Note that the contract of this method is
     * identical to DOM's {@link org.w3c.dom.Node#getNamespaceURI()} (when called on an
     * {@link org.w3c.dom.Element} or {@link org.w3c.dom.Attr}).
     * 
     * @return the namespace URI of the information item or <code>null</code> if the information
     *         item has no namespace
     */
    String getNamespaceURI();
    
    /**
     * Determine if this information item has the given name. Note that only the namespace URI and
     * local part will be compared, the prefix is ignored.
     * <p>
     * The result of the expression <code>node.hasName(name)</code> is the same as
     * <code>node.getQName().equals(name)</code>. However, the former expression is generally more
     * efficient than the latter because it avoids the creation of the {@link QName} object. In
     * addition, for an {@link OMSourcedElement} it avoids the expansion of the element if the
     * prefix is unknown.
     * 
     * @param name
     *            the QName to compare with the QName of this information item
     * @return <code>true</code> if the information item has the given name, <code>false</code>
     *         otherwise
     */
    boolean hasName(QName name);
}
