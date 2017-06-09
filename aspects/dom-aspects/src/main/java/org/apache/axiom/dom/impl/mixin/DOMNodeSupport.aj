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

import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.dom.DOMConfigurationImpl;
import org.apache.axiom.dom.DOMDocument;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMNode;
import org.apache.axiom.dom.DOMNodeFactory;
import org.apache.axiom.dom.DOMSemantics;
import org.w3c.dom.Node;

public aspect DOMNodeSupport {
    // TODO: should eventually have package access
    public void DOMNode.normalize(DOMConfigurationImpl config) {
        // Default: do nothing
    }

    public final boolean DOMNode.isSupported(String feature, String version) {
        return ((DOMNodeFactory)coreGetNodeFactory()).getDOMImplementation().hasFeature(feature, version);
    }
    
    public final String DOMNode.lookupNamespaceURI(String prefix) {
        try {
            CoreElement context = getNamespaceContext();
            if (context == null) {
                return null;
            }
            if (prefix == null) {
                prefix = "";
            } else if (prefix.length() == 0) {
                return null;
            }
            String namespaceURI = context.coreLookupNamespaceURI(prefix, DOMSemantics.INSTANCE);
            return namespaceURI == null || namespaceURI.length() == 0 ? null : namespaceURI;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    public final String DOMNode.lookupPrefix(String namespaceURI) {
        try {
            CoreElement context = getNamespaceContext();
            if (context == null) {
                return null;
            }
            if (namespaceURI == null) {
                return null;
            } else {
                String prefix = context.coreLookupPrefix(namespaceURI, DOMSemantics.INSTANCE);
                return prefix == null || prefix.length() == 0 ? null : prefix;
            }
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    public final boolean DOMNode.isDefaultNamespace(String namespaceURI) {
        try {
            CoreElement context = getNamespaceContext();
            if (context == null) {
                return false;
            }
            if (namespaceURI == null) {
                namespaceURI = "";
            }
            return namespaceURI.equals(context.coreLookupNamespaceURI("", DOMSemantics.INSTANCE));
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }

    public final Node DOMNode.cloneNode(boolean deep) {
        try {
            DOMNode clone = (DOMNode)coreClone(deep ? DOMSemantics.DEEP_CLONE : DOMSemantics.SHALLOW_CLONE, null);
            if (!(clone instanceof DOMDocument)) {
                clone.coreSetOwnerDocument(coreGetOwnerDocument(true));
            }
            return clone;
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.toUncheckedException(ex);
        }
    }
}
