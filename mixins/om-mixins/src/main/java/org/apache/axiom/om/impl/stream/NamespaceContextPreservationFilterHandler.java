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
package org.apache.axiom.om.impl.stream;

import java.util.HashSet;
import java.util.Set;

import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNamespaceDeclaration;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlHandlerWrapper;

public final class NamespaceContextPreservationFilterHandler extends XmlHandlerWrapper {
    private final CoreElement contextElement;
    // Maintain a set of the prefixes we have already seen. This is required to take into
    // account that a namespace mapping declared on an element can hide another one declared
    // for the same prefix on an ancestor of the element.
    private Set<String> prefixesAlreadyBound;
    private boolean done = false;

    public NamespaceContextPreservationFilterHandler(
            XmlHandler parent, CoreElement contextElement) {
        super(parent);
        this.contextElement = contextElement;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
            throws StreamException {
        super.startElement(namespaceURI, localName, prefix);
        if (!done) {
            prefixesAlreadyBound = new HashSet<String>();
        }
    }

    @Override
    public void processNamespaceDeclaration(String prefix, String namespaceURI)
            throws StreamException {
        super.processNamespaceDeclaration(prefix, namespaceURI);
        if (!done) {
            prefixesAlreadyBound.add(prefix);
        }
    }

    @Override
    public void attributesCompleted() throws StreamException {
        if (!done) {
            try {
                CoreElement current = contextElement;
                while (true) {
                    CoreAttribute attr = current.coreGetFirstAttribute();
                    while (attr != null) {
                        if (attr instanceof CoreNamespaceDeclaration) {
                            CoreNamespaceDeclaration decl = (CoreNamespaceDeclaration) attr;
                            String prefix = decl.coreGetDeclaredPrefix();
                            if (prefixesAlreadyBound.add(prefix)) {
                                super.processNamespaceDeclaration(
                                        prefix, decl.coreGetCharacterData().toString());
                            }
                        }
                        attr = attr.coreGetNextAttribute();
                    }
                    CoreParentNode parent = current.coreGetParent();
                    if (parent instanceof CoreElement element) {
                        current = element;
                    } else {
                        break;
                    }
                }
                prefixesAlreadyBound = null;
                done = true;
            } catch (CoreModelException ex) {
                throw new StreamException(ex);
            }
        }
        super.attributesCompleted();
    }
}
