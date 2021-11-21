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

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;

public final class DeferredNamespace implements OMNamespace {
    private final AxiomSourcedElement element;
    
    final String uri;
    
    public DeferredNamespace(AxiomSourcedElement element, String ns) {
        this.element = element;
        this.uri = ns;
    }

    @Override
    public boolean equals(String uri, String prefix) {
        String thisPrefix = getPrefix();
        return (this.uri.equals(uri) &&
                (thisPrefix == null ? prefix == null :
                        thisPrefix.equals(prefix)));
    }

    @Override
    public String getName() {
        return uri;
    }

    @Override
    public String getNamespaceURI() {
        return uri;
    }

    @Override
    public String getPrefix() {
        if (!element.isExpanded()) {
            element.forceExpand();
        }
        OMNamespace actualNS = element.getNamespace();
        return actualNS == null ? "" : actualNS.getPrefix();
    }
    
    @Override
    public int hashCode() {
        String thisPrefix = getPrefix();
        return uri.hashCode() ^ (thisPrefix != null ? thisPrefix.hashCode() : 0);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OMNamespace)) {
            return false;
        }
        OMNamespace other = (OMNamespace)obj;
        String otherPrefix = other.getPrefix();
        String thisPrefix = getPrefix();
        return (uri.equals(other.getNamespaceURI()) &&
                (thisPrefix == null ? otherPrefix == null :
                        thisPrefix.equals(otherPrefix)));
    }
}