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

import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.common.builder.OMNamespaceCache;
import org.apache.axiom.om.impl.intf.AxiomNamespaceDeclaration;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin(AxiomNamespaceDeclaration.class)
public abstract class AxiomNamespaceDeclarationMixin implements AxiomNamespaceDeclaration {
    private static final OMNamespace DEFAULT_NS = new OMNamespaceImpl("", "");
    
    private OMNamespace declaredNamespace;
    
    public final void init(String prefix, String namespaceURI, Object namespaceHelper) {
        OMNamespace ns = ((OMNamespaceCache)namespaceHelper).getOMNamespace(namespaceURI, prefix);
        setDeclaredNamespace(ns == null ? DEFAULT_NS : ns);
    }

    public final String coreGetDeclaredPrefix() {
        return declaredNamespace.getPrefix();
    }

    public final OMNamespace getDeclaredNamespace() {
        try {
            String namespaceURI = coreGetCharacterData().toString();
            if (!namespaceURI.equals(declaredNamespace.getNamespaceURI())) {
                declaredNamespace = new OMNamespaceImpl(namespaceURI, declaredNamespace.getPrefix());
            }
            return declaredNamespace;
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }
    
    public final void coreSetDeclaredNamespace(String prefix, String namespaceURI) {
        setDeclaredNamespace(new OMNamespaceImpl(namespaceURI, prefix));
    }
    
    public final void setDeclaredNamespace(OMNamespace declaredNamespace) {
        try {
            this.declaredNamespace = declaredNamespace;
            coreSetCharacterData(declaredNamespace.getNamespaceURI(), AxiomSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }
    
    public final void build() {
        // TODO
    }
}
