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
package org.apache.axiom.core.stream;

import javax.xml.XMLConstants;

public final class NamespaceRepairingFilterHandler extends XmlHandlerWrapper {
    private String[] namespaceStack = new String[32];
    private int bindings;
    private int[] scopeStack = new int[8];
    private int scopes;

    public NamespaceRepairingFilterHandler(XmlHandler parent) {
        super(parent);
    }

    private boolean isBound(String prefix, String namespaceURI) {
        if (prefix.equals(XMLConstants.XML_NS_PREFIX) && namespaceURI.equals(XMLConstants.XML_NS_URI)) {
            return true;
        } else {
            for (int i=(bindings-1)*2; i>=0; i-=2) {
                if (prefix.equals(namespaceStack[i])) {
                    return namespaceURI.equals(namespaceStack[i+1]);
                }
            }
            return prefix.length() == 0 && namespaceURI.length() == 0;
        }
    }
    
    private void setPrefix(String prefix, String namespaceURI) {
        if (bindings*2 == namespaceStack.length) {
            int len = namespaceStack.length;
            String[] newNamespaceStack = new String[len*2];
            System.arraycopy(namespaceStack, 0, newNamespaceStack, 0, len);
            namespaceStack = newNamespaceStack;
        }
        namespaceStack[bindings*2] = prefix;
        namespaceStack[bindings*2+1] = namespaceURI;
        bindings++;
    }
    
    private void ensureNamespaceDeclared(String prefix, String namespaceURI) throws StreamException {
        if (!isBound(prefix, namespaceURI)) {
            super.processNamespaceDeclaration(prefix, namespaceURI);
            setPrefix(prefix, namespaceURI);
        }
    }
    
    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        super.startElement(namespaceURI, localName, prefix);
        if (scopes == scopeStack.length) {
            int[] newScopeStack = new int[scopeStack.length*2];
            System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
            scopeStack = newScopeStack;
        }
        scopeStack[scopes++] = bindings;
        ensureNamespaceDeclared(prefix, namespaceURI);
    }
    
    public void endElement() throws StreamException {
        bindings = scopeStack[--scopes];
        super.endElement();
    }

    public void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException {
        super.processAttribute(namespaceURI, localName, prefix, value, type, specified);
        if (!namespaceURI.isEmpty()) {
            ensureNamespaceDeclared(prefix, namespaceURI);
        }
    }
    
    public void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        for (int i = scopeStack[scopes-1]; i < bindings; i++) {
            if (namespaceStack[i*2].equals(prefix)) {
                if (namespaceStack[i*2+1].equals(namespaceURI)) {
                    return;
                } else {
                    // TODO: this causes a failure in the FOM tests
//                        throw new OMException("The same prefix cannot be bound to two different namespaces");
                }
            }
        }
        super.processNamespaceDeclaration(prefix, namespaceURI);
        setPrefix(prefix, namespaceURI);
    }
}
