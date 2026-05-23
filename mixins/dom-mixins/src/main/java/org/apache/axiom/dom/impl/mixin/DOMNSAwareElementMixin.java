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
package org.apache.axiom.dom.impl.mixin;

import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNamespaceDeclaration;
import org.apache.axiom.dom.DOMConfigurationImpl;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMNSAwareElement;
import org.apache.axiom.dom.DOMSemantics;
import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin
public abstract class DOMNSAwareElementMixin implements DOMNSAwareElement {
    @Override
    public final void normalize(DOMConfigurationImpl config, ScopedNamespaceContext nsContext) {
        if (config.isEnabled(DOMConfigurationImpl.NAMESPACES)) {
            try {
                // Add existing namespace declarations on this element to the context so that
                // they are visible to descendants and so we can check them below.
                for (CoreAttribute a = coreGetFirstAttribute(); a != null; a = a.coreGetNextAttribute()) {
                    if (a instanceof CoreNamespaceDeclaration decl) {
                        nsContext.setPrefix(
                                decl.coreGetDeclaredPrefix(),
                                decl.coreGetCharacterData().toString());
                    }
                }
                String namespaceURI = coreGetNamespaceURI();
                if (namespaceURI.isEmpty()) {
                    // If the default namespace is bound to a non-empty URI in the context,
                    // add xmlns="" to override it.
                    if (!nsContext.getNamespaceURI("").isEmpty()) {
                        coreSetAttribute(DOMSemantics.NAMESPACE_DECLARATION_MATCHER, null, "", null, "");
                        nsContext.setPrefix("", "");
                    }
                } else {
                    String prefix = coreGetPrefix();
                    if (!namespaceURI.equals(nsContext.getNamespaceURI(prefix))) {
                        coreSetAttribute(DOMSemantics.NAMESPACE_DECLARATION_MATCHER, null, prefix, null, namespaceURI);
                        nsContext.setPrefix(prefix, namespaceURI);
                    }
                }
            } catch (CoreModelException ex) {
                throw DOMExceptionUtil.toUncheckedException(ex);
            }
        }
    }
}
