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
package org.apache.axiom.datatype;

import org.w3c.dom.Element;

final class DOMContextAccessor implements ContextAccessor<Element,Void> {
    static final DOMContextAccessor INSTANCE = new DOMContextAccessor();

    public String lookupNamespaceURI(Element element, Void options, String prefix) {
        String namespaceURI = element.lookupNamespaceURI(prefix.length() == 0 ? null : prefix);
        if (namespaceURI != null) {
            return namespaceURI;
        } else {
            return prefix.length() == 0 ? "" : null;
        }
    }

    public String lookupPrefix(Element element, Void options, String namespaceURI) {
        return element.lookupPrefix(namespaceURI.length() == 0 ? null : namespaceURI);
    }
}
