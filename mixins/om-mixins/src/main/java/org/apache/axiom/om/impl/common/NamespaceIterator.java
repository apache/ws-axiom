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

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;

/**
 * Iterator implementation used by {@link OMElement#getNamespacesInScope()}.
 */
public final class NamespaceIterator implements Iterator<OMNamespace> {
    private final Set<String> seenPrefixes = new HashSet<String>();
    private OMElement element;
    private Iterator<OMNamespace> declaredNamespaces;
    private boolean hasNextCalled;
    private OMNamespace next;

    public NamespaceIterator(OMElement element) {
        this.element = element;
    }

    @Override
    public boolean hasNext() {
        if (!hasNextCalled) {
            while (true) {
                if (declaredNamespaces == null) {
                    declaredNamespaces = element.getAllDeclaredNamespaces();
                } else if (declaredNamespaces.hasNext()) {
                    OMNamespace namespace = declaredNamespaces.next();
                    // We only return a namespace declaration if it has not been overridden (i.e. if
                    // we have not seen another declaration with the same prefix yet) and if the namespace
                    // URI is not empty. The second part of the condition covers the case of namespace
                    // declarations such as xmlns="" (for which no OMNamespace object is returned, as
                    // described in the Javadoc of the getNamespacesInScope method) as well as undeclared
                    // prefixes (XML 1.1 only).
                    if (seenPrefixes.add(namespace.getPrefix()) && namespace.getNamespaceURI().length() > 0) {
                        next = namespace;
                        break;
                    }
                } else {
                    declaredNamespaces = null;
                    OMContainer parent = element.getParent();
                    if (parent instanceof OMElement) {
                        element = (OMElement)parent;
                    } else {
                        next = null;
                        break;
                    }
                }
            }
            hasNextCalled = true;
        }
        return next != null;
    }

    @Override
    public OMNamespace next() {
        if (hasNext()) {
            OMNamespace result = next;
            hasNextCalled = false;
            next = null;
            return result;
        } else {
            throw new NoSuchElementException();
        }
    }

    // TODO: document that remove is not supported
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
