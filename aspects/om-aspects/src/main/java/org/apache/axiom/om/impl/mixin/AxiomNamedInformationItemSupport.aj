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
package org.apache.axiom.om.impl.mixin;

import javax.xml.namespace.QName;

import org.apache.axiom.core.CoreNamedNode;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.common.builder.OMNamespaceCache;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomNamedInformationItem;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;

public aspect AxiomNamedInformationItemSupport {
    /**
     * The namespace of the information item. Possible values:
     * <ul>
     * <li><code>null</code> (if the information item has no namespace)
     * <li>any {@link OMNamespace} instance, with the following exceptions:
     * <ul>
     * <li>an {@link OMNamespace} instance with a <code>null</code> prefix
     * <li>for elements: an {@link OMNamespace} instance with both prefix and namespace URI set to
     * the empty string
     * <li>for attributes: an {@link OMNamespace} instance with an empty prefix (because an
     * unprefixed attribute never has a namespace)
     * </ul>
     * </ul>
     */
    private OMNamespace AxiomNamedInformationItem.namespace;
    
    private String AxiomNamedInformationItem.localName;
    private QName AxiomNamedInformationItem.qName;
    
    public final void AxiomNamedInformationItem.initName(String namespaceURI, String localName, String prefix, Object namespaceHelper) {
        this.localName = localName;
        namespace = ((OMNamespaceCache)namespaceHelper).getOMNamespace(namespaceURI, prefix);
    }
    
    /**
     * Set the namespace of the node without adding a corresponding namespace declaration.
     * 
     * @param namespace
     */
    public final void AxiomNamedInformationItem.internalSetNamespace(OMNamespace namespace) {
        this.namespace = namespace;
        qName = null;
    }

    public final String AxiomNamedInformationItem.internalGetLocalName() {
        return localName;
    }

    public final void AxiomNamedInformationItem.internalSetLocalName(String localName) {
        this.localName = localName;
    }

    public OMNamespace AxiomNamedInformationItem.getNamespace() {
        return defaultGetNamespace();
    }
    
    public final OMNamespace AxiomNamedInformationItem.defaultGetNamespace() {
        return namespace;
    }

    abstract void AxiomNamedInformationItem.beforeSetLocalName();
    
    public final void AxiomNamedInformationItem.setLocalName(String localName) {
        beforeSetLocalName();
        this.localName = localName;
        qName = null;
    }

    public QName AxiomNamedInformationItem.getQName() {
        return defaultGetQName();
    }
    
    public final QName AxiomNamedInformationItem.defaultGetQName() {
        if (qName != null) {
            return qName;
        }

        if (namespace != null) {
            qName = new QName(namespace.getNamespaceURI(), localName, namespace.getPrefix());
        } else {
            qName = new QName(localName);
        }
        return qName;
    }
    
    public final boolean AxiomNamedInformationItem.hasName(QName name) {
        if (name.getLocalPart().equals(getLocalName())) {
            OMNamespace ns = getNamespace();
            return ns == null && name.getNamespaceURI().length() == 0
                    || ns != null && name.getNamespaceURI().equals(ns.getNamespaceURI());
        } else {
            return false;
        }
    }

    public final String AxiomNamedInformationItem.coreGetNamespaceURI() {
        OMNamespace namespace = getNamespace();
        return namespace == null ? "" : namespace.getNamespaceURI();
    }
    
    public final String AxiomNamedInformationItem.coreGetPrefix() {
        OMNamespace namespace = getNamespace();
        return namespace == null ? "" : namespace.getPrefix();
    }
    
    public final void AxiomNamedInformationItem.coreSetName(String namespaceURI, String localName, String prefix) {
        this.localName = localName;
        namespace = namespaceURI.length() == 0 && prefix.length() == 0 ? null : new OMNamespaceImpl(namespaceURI, prefix);
        // TODO: need unit test to assert this
        qName = null;
    }

    public final void AxiomNamedInformationItem.initName(CoreNamedNode other) {
        AxiomNamedInformationItem o = (AxiomNamedInformationItem)other;
        if (o instanceof AxiomSourcedElement && ((AxiomElement)this).isExpanded()) {
            localName = o.coreGetLocalName();
            namespace = o.getNamespace();
            qName = o.getQName();
        } else {
            localName = o.localName;
            namespace = o.namespace;
            qName = o.qName;
        }
    }
    
    public void AxiomNamedInformationItem.updateLocalName() {
        throw new IllegalStateException();
    }
    
    public final String AxiomNamedInformationItem.coreGetLocalName() {
        if (localName == null) {
            updateLocalName();
        }
        return localName;
    }
    
    public final void AxiomNamedInformationItem.coreSetPrefix(String prefix) {
        OMNamespace ns = getNamespace();
        if (ns == null) {
            if (prefix.length() > 0) {
                throw new OMException("Cannot set prefix on an information item without namespace");
            } else {
                // No need to set a new OMNamespace in this case
            }
        } else {
            internalSetNamespace(new OMNamespaceImpl(ns.getNamespaceURI(), prefix));
        }
    }
}
