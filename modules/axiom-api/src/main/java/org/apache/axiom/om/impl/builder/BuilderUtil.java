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
package org.apache.axiom.om.impl.builder;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.OMElementEx;
import org.apache.axiom.om.impl.util.OMSerializerUtil;

/**
 * For internal use only.
 */
public class BuilderUtil {
    private BuilderUtil() {}
    
    public static void setNamespace(OMElement element, String namespaceURI, String prefix, boolean namespaceURIInterning) {
        if (prefix == null) {
            prefix = "";
        }
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        // Check if there is an existing namespace declaration. This has two purposes:
        //  * Avoid creating a new OMNamespace instance for each OMElement
        //  * Perform namespace repairing
        OMNamespace namespace = element.findNamespaceURI(prefix);
        if (namespace == null && namespaceURI.length() > 0
                || namespace != null && !namespace.getNamespaceURI().equals(namespaceURI)) {
            if (namespaceURIInterning) {
                namespaceURI = namespaceURI.intern();
            }
            // This is actually the place where we perform namespace repairing as specified
            // in the contract of OMXMLBuilderFactory#createStAXOMBuilder:
            namespace = ((OMElementEx)element).addNamespaceDeclaration(namespaceURI, prefix);
        }
        if (namespace != null && namespaceURI.length() > 0) {
            element.setNamespaceWithNoFindInCurrentScope(namespace);
        }
    }
    
    public static void processAttribute(OMElement element, String prefix, String namespaceURI, String localName, String value, String type) {
        OMNamespace namespace = null;
        if (namespaceURI != null && namespaceURI.length() > 0) {

            // prefix being null means this elements has a default namespace or it has inherited
            // a default namespace from its parent
            namespace = element.findNamespace(namespaceURI, prefix);
            if (namespace == null) {
                if (prefix == null || "".equals(prefix)) {
                    prefix = OMSerializerUtil.getNextNSPrefix();
                }
                namespace = element.declareNamespace(namespaceURI, prefix);
            }
        }

        // todo if the attributes are supposed to namespace qualified all the time
        // todo then this should throw an exception here

        OMAttribute attr = element.addAttribute(localName,
                          value, namespace);
        if (type != null) {
            attr.setAttributeType(type);
        }
    }
}
