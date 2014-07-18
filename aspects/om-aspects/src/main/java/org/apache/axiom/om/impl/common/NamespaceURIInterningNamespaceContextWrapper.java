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

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

class NamespaceURIInterningNamespaceContextWrapper implements NamespaceContext {
    private final NamespaceContext parent;

    NamespaceURIInterningNamespaceContextWrapper(NamespaceContext parent) {
        this.parent = parent;
    }

    NamespaceContext getParent() {
        return parent;
    }

    private static String intern(String s) {
        return s == null ? null : s.intern();
    }
    
    public String getNamespaceURI(String prefix) {
        return intern(parent.getNamespaceURI(prefix));
    }

    public String getPrefix(String namespaceURI) {
        return parent.getPrefix(namespaceURI);
    }

    public Iterator getPrefixes(String namespaceURI) {
        return parent.getPrefixes(namespaceURI);
    }
}
