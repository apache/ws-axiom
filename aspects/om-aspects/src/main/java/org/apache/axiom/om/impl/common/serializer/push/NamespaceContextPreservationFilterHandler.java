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
package org.apache.axiom.om.impl.common.serializer.push;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.stream.StreamException;
import org.apache.axiom.om.impl.stream.XmlHandler;
import org.apache.axiom.om.impl.stream.XmlHandlerWrapper;

public final class NamespaceContextPreservationFilterHandler extends XmlHandlerWrapper {
    private final OMElement contextElement;
    // Maintain a set of the prefixes we have already seen. This is required to take into
    // account that a namespace mapping declared on an element can hide another one declared
    // for the same prefix on an ancestor of the element.
    private Set<String> prefixesAlreadyBound;
    private boolean done = false;
    
    public NamespaceContextPreservationFilterHandler(XmlHandler parent, OMElement contextElement) {
        super(parent);
        this.contextElement = contextElement;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        super.startElement(namespaceURI, localName, prefix);
        if (!done) {
            prefixesAlreadyBound = new HashSet<String>();
        }
    }

    @Override
    public void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        super.processNamespaceDeclaration(prefix, namespaceURI);
        if (!done) {
            prefixesAlreadyBound.add(prefix);
        }
    }

    @Override
    public void attributesCompleted() throws StreamException {
        if (!done) {
            OMElement current = contextElement;
            while (true) {
                for (Iterator<OMNamespace> it = current.getAllDeclaredNamespaces(); it.hasNext(); ) {
                    OMNamespace ns = it.next();
                    if (prefixesAlreadyBound.add(ns.getPrefix())) {
                        super.processNamespaceDeclaration(ns.getPrefix(), ns.getNamespaceURI());
                    }
                }
                OMContainer parent = current.getParent();
                if (!(parent instanceof OMElement)) {
                    break;
                }
                current = (OMElement)parent;
            }
            prefixesAlreadyBound = null;
            done = true;
        }
        super.attributesCompleted();
    }
}
