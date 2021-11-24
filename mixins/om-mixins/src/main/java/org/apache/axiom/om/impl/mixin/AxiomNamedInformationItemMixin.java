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
import org.apache.axiom.util.xml.QNameCache;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin
public abstract class AxiomNamedInformationItemMixin implements AxiomNamedInformationItem {
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
    private OMNamespace namespace;
    
    private String localName;
    
    @Override
    public final void initName(String namespaceURI, String localName, String prefix, Object namespaceHelper) {
        this.localName = localName;
        namespace = ((OMNamespaceCache)namespaceHelper).getOMNamespace(namespaceURI, prefix);
    }
    
    /**
     * Set the namespace of the node without adding a corresponding namespace declaration.
     * 
     * @param namespace
     */
    @Override
    public final void internalSetNamespace(OMNamespace namespace) {
        this.namespace = namespace;
    }

    @Override
    public final String internalGetLocalName() {
        return localName;
    }

    @Override
    public final void internalSetLocalName(String localName) {
        this.localName = localName;
    }

    @Override
    public OMNamespace getNamespace() {
        return defaultGetNamespace();
    }
    
    @Override
    public final OMNamespace defaultGetNamespace() {
        return namespace;
    }

    abstract void beforeSetLocalName();
    
    @Override
    public final void setLocalName(String localName) {
        beforeSetLocalName();
        this.localName = localName;
    }

    @Override
    public QName getQName() {
        return defaultGetQName();
    }
    
    @Override
    public final QName defaultGetQName() {
        return QNameCache.getQName(
                namespace == null ? "" : namespace.getNamespaceURI(),
                localName,
                namespace == null ? "" : namespace.getPrefix());
    }
    
    @Override
    public final boolean hasName(QName name) {
        if (name.getLocalPart().equals(getLocalName())) {
            OMNamespace ns = getNamespace();
            return ns == null && name.getNamespaceURI().length() == 0
                    || ns != null && name.getNamespaceURI().equals(ns.getNamespaceURI());
        } else {
            return false;
        }
    }

    @Override
    public final String coreGetNamespaceURI() {
        OMNamespace namespace = getNamespace();
        return namespace == null ? "" : namespace.getNamespaceURI();
    }
    
    @Override
    public final String coreGetPrefix() {
        OMNamespace namespace = getNamespace();
        return namespace == null ? "" : namespace.getPrefix();
    }
    
    @Override
    public final void coreSetName(String namespaceURI, String localName, String prefix) {
        this.localName = localName;
        namespace = namespaceURI.length() == 0 && prefix.length() == 0 ? null : new OMNamespaceImpl(namespaceURI, prefix);
    }

    @Override
    public final void initName(CoreNamedNode other) {
        AxiomNamedInformationItem o = (AxiomNamedInformationItem)other;
        if (o instanceof AxiomSourcedElement && ((AxiomElement)this).isExpanded()) {
            localName = o.coreGetLocalName();
            namespace = o.getNamespace();
        } else {
            localName = o.internalGetLocalName();
            namespace = o.defaultGetNamespace();
        }
    }
    
    public void updateLocalName() {
        throw new IllegalStateException();
    }
    
    @Override
    public final String coreGetLocalName() {
        if (localName == null) {
            updateLocalName();
        }
        return localName;
    }
    
    @Override
    public final void coreSetPrefix(String prefix) {
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
