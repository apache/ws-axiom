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

import org.apache.axiom.util.namespace.ScopedNamespaceContext;

public final class NamespaceRepairingFilterHandler extends XmlHandlerWrapper {
    private final ScopedNamespaceContext nsContext = new ScopedNamespaceContext();

    public NamespaceRepairingFilterHandler(XmlHandler parent) {
        super(parent);
    }

    private void ensureNamespaceDeclared(String prefix, String namespaceURI) throws StreamException {
        if (!namespaceURI.equals(nsContext.getNamespaceURI(prefix))) {
            super.processNamespaceDeclaration(prefix, namespaceURI);
            nsContext.setPrefix(prefix, namespaceURI);
        }
    }
    
    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        super.startElement(namespaceURI, localName, prefix);
        nsContext.startScope();
        ensureNamespaceDeclared(prefix, namespaceURI);
    }
    
    public void endElement() throws StreamException {
        nsContext.endScope();
        super.endElement();
    }

    public void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException {
        super.processAttribute(namespaceURI, localName, prefix, value, type, specified);
        if (!namespaceURI.isEmpty()) {
            ensureNamespaceDeclared(prefix, namespaceURI);
        }
    }
    
    public void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        for (int i=nsContext.getFirstBindingInCurrentScope(); i<nsContext.getBindingsCount(); i++) {
            if (nsContext.getPrefix(i).equals(prefix)) {
                if (nsContext.getNamespaceURI(i).equals(namespaceURI)) {
                    return;
                } else {
                    // TODO: this causes a failure in the FOM tests
//                        throw new OMException("The same prefix cannot be bound to two different namespaces");
                }
            }
        }
        super.processNamespaceDeclaration(prefix, namespaceURI);
        nsContext.setPrefix(prefix, namespaceURI);
    }
}
