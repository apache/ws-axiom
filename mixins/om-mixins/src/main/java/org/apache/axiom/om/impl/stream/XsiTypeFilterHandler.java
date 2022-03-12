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

import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlHandlerWrapper;
import org.apache.axiom.om.impl.common.AxiomSemantics;

public class XsiTypeFilterHandler extends XmlHandlerWrapper {
    private final CoreElement contextElement;
    private String[] prefixes = new String[16];
    private int prefixCount;
    private int[] scopeStack = new int[8];
    private int scopes;
    private String xsiType;

    public XsiTypeFilterHandler(XmlHandler parent, CoreElement contextElement) {
        super(parent);
        this.contextElement = contextElement;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix)
            throws StreamException {
        super.startElement(namespaceURI, localName, prefix);
        if (scopes == scopeStack.length) {
            int[] newScopeStack = new int[scopeStack.length * 2];
            System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
            scopeStack = newScopeStack;
        }
        scopeStack[scopes++] = prefixCount;
    }

    @Override
    public void endElement() throws StreamException {
        prefixCount = scopeStack[--scopes];
        super.endElement();
    }

    @Override
    public void processNamespaceDeclaration(String prefix, String namespaceURI)
            throws StreamException {
        super.processNamespaceDeclaration(prefix, namespaceURI);
        if (prefixes.length == prefixCount) {
            String[] newPrefixes = new String[prefixes.length * 2];
            System.arraycopy(prefixes, 0, newPrefixes, 0, prefixes.length);
            prefixes = newPrefixes;
        }
        prefixes[prefixCount++] = prefix;
    }

    @Override
    public void processAttribute(
            String namespaceURI,
            String localName,
            String prefix,
            String value,
            String type,
            boolean specified)
            throws StreamException {
        super.processAttribute(namespaceURI, localName, prefix, value, type, specified);
        if (namespaceURI.equals("http://www.w3.org/2001/XMLSchema-instance")
                && localName.equals("type")) {
            xsiType = value.trim();
        }
    }

    @Override
    public void attributesCompleted() throws StreamException {
        try {
            if (xsiType != null) {
                int idx = xsiType.indexOf(':');
                String prefix = idx == -1 ? "" : xsiType.substring(0, idx);
                boolean bound = false;
                for (int i = 0; i < prefixCount; i++) {
                    if (prefixes[i] == prefix) {
                        bound = true;
                        break;
                    }
                }
                if (!bound) {
                    String namespaceURI =
                            contextElement.coreLookupNamespaceURI(prefix, AxiomSemantics.INSTANCE);
                    if (namespaceURI != null && !namespaceURI.isEmpty()) {
                        processNamespaceDeclaration(prefix, namespaceURI);
                    }
                }
            }
        } catch (CoreModelException ex) {
            throw new StreamException(ex);
        }
        super.attributesCompleted();
        xsiType = null;
    }
}
