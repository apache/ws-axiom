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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.util.namespace.AbstractNamespaceContext;

public final class LiveNamespaceContext extends AbstractNamespaceContext {
    private final OMElement element;

    public LiveNamespaceContext(OMElement element) {
        this.element = element;
    }

    @Override
    protected String doGetNamespaceURI(String prefix) {
        OMNamespace ns = element.findNamespaceURI(prefix);
        return ns == null ? XMLConstants.NULL_NS_URI : ns.getNamespaceURI();
    }

    @Override
    protected String doGetPrefix(String namespaceURI) {
        OMNamespace ns = element.findNamespace(namespaceURI, null);
        return ns == null ? null : ns.getPrefix();
    }

    @Override
    protected Iterator<String> doGetPrefixes(String namespaceURI) {
        List<String> prefixes = new ArrayList<String>();
        for (Iterator<OMNamespace> it = element.getNamespacesInScope(); it.hasNext(); ) {
            OMNamespace ns = it.next();
            if (ns.getNamespaceURI().equals(namespaceURI)) {
                prefixes.add(ns.getPrefix());
            }
        }
        return prefixes.iterator();
    }
}
