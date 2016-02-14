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

import org.apache.axiom.om.impl.stream.StreamException;
import org.apache.axiom.om.impl.stream.XmlHandler;
import org.apache.axiom.om.impl.stream.XmlHandlerWrapper;

final class NamespaceHelper extends XmlHandlerWrapper {
    private final SerializerImpl serializer;

    NamespaceHelper(SerializerImpl serializer, XmlHandler handler) {
        super(handler);
        this.serializer = serializer;
    }

    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        super.startElement(namespaceURI, localName, prefix);
        mapNamespace(prefix, namespaceURI, false);
    }
    
    public void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException {
        mapNamespace(prefix, namespaceURI, true);
        super.processAttribute(namespaceURI, localName, prefix, value, type, specified);
    }
    
    public void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        mapNamespace(prefix, namespaceURI, false);
    }
    
    /**
     * Method used internally to report usage of a given namespace binding. This method will
     * generate a namespace declaration if required by the configured namespace repairing policy.
     * 
     * @param prefix
     *            the namespace prefix; must not be <code>null</code>
     * @param namespaceURI
     *            the namespace URI; must not be <code>null</code>
     * @param attr
     */
    private void mapNamespace(String prefix, String namespaceURI, boolean attr) throws StreamException {
        // If the prefix and namespace are already associated, no generation is needed
        if (serializer.isAssociated(prefix, namespaceURI)) {
            return;
        }
        
        // Attributes without a prefix always are associated with the unqualified namespace
        // according to the schema specification.  No generation is needed.
        if (prefix.length() == 0 && namespaceURI.length() == 0 && attr) {
            return;
        }
        
        // Add the namespace if the prefix is not associated.
        super.processNamespaceDeclaration(prefix, namespaceURI);
    }
}
