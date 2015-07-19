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
package org.apache.axiom.dom;

import org.apache.axiom.core.CoreElement;

public aspect DOMNodeSupport {
    // TODO: should eventually have package access
    public void DOMNode.normalize(DOMConfigurationImpl config) {
        // Default: do nothing
    }

    public final boolean DOMNode.isSupported(String feature, String version) {
        return ((DOMNodeFactory)coreGetNodeFactory()).hasFeature(feature, version);
    }
    
    public final String DOMNode.lookupNamespaceURI(String prefix) {
        CoreElement context = getNamespaceContext();
        if (context == null) {
            return null;
        }
        if (prefix == null) {
            prefix = "";
        } else if (prefix.length() == 0) {
            return null;
        }
        String namespaceURI = context.coreLookupNamespaceURI(prefix, false);
        return namespaceURI == null || namespaceURI.length() == 0 ? null : namespaceURI;
    }

    public final String DOMNode.lookupPrefix(String namespaceURI) {
        CoreElement context = getNamespaceContext();
        if (context == null) {
            return null;
        }
        if (namespaceURI == null) {
            return null;
        } else {
            String prefix = context.coreLookupPrefix(namespaceURI, false);
            return prefix == null || prefix.length() == 0 ? null : prefix;
        }
    }

    public final boolean DOMNode.isDefaultNamespace(String namespaceURI) {
        CoreElement context = getNamespaceContext();
        if (context == null) {
            return false;
        }
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        return namespaceURI.equals(context.coreLookupNamespaceURI("", false));
    }
}
