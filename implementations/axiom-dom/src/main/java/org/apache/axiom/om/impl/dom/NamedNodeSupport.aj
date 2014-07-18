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
package org.apache.axiom.om.impl.dom;

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.w3c.dom.DOMException;

aspect NamedNodeSupport {
    public final void NamedNode.setPrefix(String prefix) throws DOMException {
        if (prefix == null) {
            prefix = "";
        }
        OMNamespace ns = getNamespace();
        if (ns == null) {
            if (prefix.length() > 0) {
                throw newDOMException(DOMException.NAMESPACE_ERR);
            } else {
                // No need to set a new OMNamespace in this case
            }
        } else {
            internalSetNamespace(new OMNamespaceImpl(ns.getNamespaceURI(), prefix == null ? "" : prefix));
        }
    }
}
