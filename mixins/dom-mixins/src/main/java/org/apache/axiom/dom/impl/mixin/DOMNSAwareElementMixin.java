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
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNamespaceDeclaration;
import org.apache.axiom.dom.DOMConfigurationImpl;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMNSAwareElement;
import org.apache.axiom.dom.DOMSemantics;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin
public abstract class DOMNSAwareElementMixin implements DOMNSAwareElement {
    @Override
    public final void normalize(DOMConfigurationImpl config) {
        if (config.isEnabled(DOMConfigurationImpl.NAMESPACES)) {
            try {
                String namespaceURI = coreGetNamespaceURI();
                if (namespaceURI.isEmpty()) {
                    // Walk up from this element looking for the first explicit default namespace
                    // declaration. If it maps to a non-empty URI, add xmlns="" to override it.
                    CoreElement current = this;
                    outer:
                    while (current != null) {
                        for (CoreAttribute a = current.coreGetFirstAttribute();
                                a != null;
                                a = a.coreGetNextAttribute()) {
                            if (a instanceof CoreNamespaceDeclaration decl
                                    && decl.coreGetDeclaredPrefix().isEmpty()) {
                                if (!decl.coreGetCharacterData().toString().isEmpty()) {
                                    coreSetAttribute(DOMSemantics.NAMESPACE_DECLARATION_MATCHER, null, "", null, "");
                                }
                                break outer;
                            }
                        }
                        current = current.coreGetParentElement();
                    }
                } else {
                    // Check only this element's own explicit namespace declarations.
                    String prefix = coreGetPrefix();
                    boolean declared = false;
                    for (CoreAttribute a = coreGetFirstAttribute(); a != null; a = a.coreGetNextAttribute()) {
                        if (a instanceof CoreNamespaceDeclaration decl && prefix.equals(decl.coreGetDeclaredPrefix())) {
                            declared = decl.coreGetCharacterData().toString().equals(namespaceURI);
                            break;
                        }
                    }
                    if (!declared) {
                        coreSetAttribute(DOMSemantics.NAMESPACE_DECLARATION_MATCHER, null, prefix, null, namespaceURI);
                    }
                }
            } catch (CoreModelException ex) {
                throw DOMExceptionUtil.toUncheckedException(ex);
            }
        }
    }
}
