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
package org.apache.axiom.om.impl.common;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.util.OMSerializerUtil;

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
        return namespace;
    }

    public final String AxiomNamedInformationItem.getPrefix() {
        OMNamespace namespace = getNamespace();
        if (namespace == null) {
            return null;
        } else {
            String prefix = namespace.getPrefix();
            return prefix.length() == 0 ? null : prefix;
        }
    }
    
    public String AxiomNamedInformationItem.getLocalName() {
        return localName;
    }

    public void AxiomNamedInformationItem.setLocalName(String localName) {
        this.localName = localName;
        qName = null;
    }

    public QName AxiomNamedInformationItem.getQName() {
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
    
    public boolean AxiomNamedInformationItem.hasName(QName name) {
        if (name.getLocalPart().equals(getLocalName())) {
            OMNamespace ns = getNamespace();
            return ns == null && name.getNamespaceURI().length() == 0
                    || ns != null && name.getNamespaceURI().equals(ns.getNamespaceURI());
        } else {
            return false;
        }
    }

    public final OMNamespace AxiomNamedInformationItem.handleNamespace(AxiomElement context, OMNamespace ns, boolean attr, boolean decl) {
        String namespaceURI = ns == null ? "" : ns.getNamespaceURI();
        String prefix = ns == null ? "" : ns.getPrefix();
        if (namespaceURI.length() == 0) {
            if (prefix != null && prefix.length() != 0) {
                throw new IllegalArgumentException("Cannot bind a prefix to the empty namespace name");
            }
            if (!attr && decl) {
                // Special case: no namespace; we need to generate a namespace declaration only if
                // there is a conflicting namespace declaration (i.e. a declaration for the default
                // namespace with a non empty URI) is in scope
                if (context.getDefaultNamespace() != null) {
                    context.declareDefaultNamespace("");
                }
            }
            return null;
        } else {
            if (attr && prefix != null && prefix.length() == 0) {
                throw new IllegalArgumentException("An attribute with a namespace must be prefixed");
            }
            boolean addNSDecl = false;
            if (context != null && (decl || prefix == null)) {
                OMNamespace existingNSDecl = context.findNamespace(namespaceURI, prefix);
                if (existingNSDecl == null
                        || (prefix != null && !existingNSDecl.getPrefix().equals(prefix))
                        || (prefix == null && attr && existingNSDecl.getPrefix().length() == 0)) {
                    addNSDecl = decl;
                } else {
                    prefix = existingNSDecl.getPrefix();
                    ns = existingNSDecl;
                }
            }
            if (prefix == null) {
                prefix = OMSerializerUtil.getNextNSPrefix();
                ns = new OMNamespaceImpl(namespaceURI, prefix);
            }
            if (addNSDecl) {
                context.addNamespaceDeclaration(ns);
            }
            return ns;
        }
    }
}
