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
package org.apache.axiom.om.impl.common.serializer.push.sax;

import java.util.Stack;

import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

final class SAXHelper {
    private Stack elementNameStack = new Stack();
    private String elementURI;
    private String elementLocalName;
    private String elementQName;
    private final AttributesImpl attributes = new AttributesImpl();

    private static String getQName(String prefix, String localName) {
        if (prefix.length() == 0) {
            return localName;
        } else {
            return prefix + ":" + localName;
        }
    }
    
    void beginStartElement(String prefix, String namespaceURI, String localName) {
        elementURI = namespaceURI;
        elementLocalName = localName;
        elementQName = getQName(prefix, localName);
    }

    void addAttribute(String prefix, String namespaceURI, String localName, String type, String value) {
        attributes.addAttribute(namespaceURI, localName, getQName(prefix, localName), type, value);
    }

    void finishStartElement(ContentHandler contentHandler) throws SAXException {
        contentHandler.startElement(elementURI, elementLocalName, elementQName, attributes);
        elementNameStack.push(elementURI);
        elementNameStack.push(elementLocalName);
        elementNameStack.push(elementQName);
        elementURI = null;
        elementLocalName = null;
        elementQName = null;
        attributes.clear();
    }

    boolean isInStartElement() {
        return elementLocalName != null;
    }
    
    void writeEndElement(ContentHandler contentHandler, ScopedNamespaceContext nsContext) throws SAXException {
        String elementQName = (String)elementNameStack.pop();
        String elementLocalName = (String)elementNameStack.pop();
        String elementURI = (String)elementNameStack.pop();
        contentHandler.endElement(elementURI, elementLocalName, elementQName);
        if (nsContext != null) {
            for (int i=nsContext.getBindingsCount()-1; i>=nsContext.getFirstBindingInCurrentScope(); i--) {
                contentHandler.endPrefixMapping(nsContext.getPrefix(i));
            }
            nsContext.endScope();
        }
    }
}
