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
package org.apache.axiom.core;

/**
 * {@link AttributeMatcher} that matches {@link CoreNamespaceDeclaration} attributes based on the
 * declared prefix. Parameters are defined as follows:
 *
 * <dl>
 *   <dt><code>namespaceURI</code>
 *   <dd>Not used.
 *   <dt><code>name</code>
 *   <dd>The prefix declared by the namespace declaration, or the empty string for the default
 *       namespace declaration.
 *   <dt><code>value</code>
 *   <dd>The namespace URI of the namespace declaration.
 *   <dt><code>prefix</code>
 *   <dd>Not used.
 * </dl>
 */
public final class NamespaceDeclarationMatcher implements AttributeMatcher {
    private final Semantics semantics;

    public NamespaceDeclarationMatcher(Semantics semantics) {
        this.semantics = semantics;
    }

    @Override
    public boolean matches(CoreAttribute attr, String namespaceURI, String name) {
        if (attr instanceof CoreNamespaceDeclaration namespaceDeclaration) {
            String prefix = namespaceDeclaration.coreGetDeclaredPrefix();
            return name.equals(prefix);
        } else {
            return false;
        }
    }

    @Override
    public CoreAttribute createAttribute(
            NodeFactory2 nodeFactory,
            String namespaceURI,
            String name,
            String prefix,
            String value) {
        CoreNamespaceDeclaration decl = nodeFactory.createNamespaceDeclaration();
        decl.coreSetDeclaredNamespace(name, value);
        return decl;
    }

    @Override
    public String getNamespaceURI(CoreAttribute attr) {
        return null;
    }

    @Override
    public String getName(CoreAttribute attr) {
        return ((CoreNamespaceDeclaration) attr).coreGetDeclaredPrefix();
    }

    @Override
    public void update(CoreAttribute attr, String prefix, String value) throws CoreModelException {
        attr.coreSetCharacterData(value, semantics);
    }
}
