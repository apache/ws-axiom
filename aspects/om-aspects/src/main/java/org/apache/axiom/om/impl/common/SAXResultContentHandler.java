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

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;

public final  class SAXResultContentHandler implements Handler {
    private final OMContainer root;
    private final OMFactory factory;
    private OMContainer target;

    public SAXResultContentHandler(OMContainer root) {
        this.root = root;
        factory = root.getOMFactory();
    }
    
    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding, boolean standalone) {
        target = root;
    }

    public void endDocument() {
    }

    public void createDocumentTypeDeclaration(String rootName, String publicId,
            String systemId, String internalSubset) {
        if (target instanceof OMDocument) {
            factory.createOMDocType(target, rootName, publicId, systemId, internalSubset);
        }
    }

    public void startElement(String namespaceURI, String localName, String prefix) {
        // TODO: inefficient: we should not create a new OMNamespace instance every time
        target = factory.createOMElement(localName, factory.createOMNamespace(namespaceURI, prefix), target);
    }

    public void endElement() {
        target = ((OMNode)target).getParent();
    }

    @Override
    public void createAttribute(String namespaceURI, String localName, String prefix, String value,
            String type, boolean specified) {
        OMElement element = (OMElement)target;
        OMNamespace ns;
        if (namespaceURI.length() > 0) {
            ns = element.findNamespace(namespaceURI, prefix);
            if (ns == null) {
                throw new OMException("Unbound namespace " + namespaceURI);
            }
        } else {
            ns = null;
        }
        OMAttribute attr = element.addAttribute(localName, value, ns);
        attr.setAttributeType(type);
    }

    @Override
    public void createNamespaceDeclaration(String prefix, String namespaceURI) {
        if (prefix.isEmpty()) {
            ((OMElement)target).declareDefaultNamespace(namespaceURI);
        } else {
            ((OMElement)target).declareNamespace(namespaceURI, prefix);
        }
    }

    @Override
    public void attributesCompleted() {
    }

    public void createOMText(String text, int type) {
        factory.createOMText(target, text, type);
    }

    public void createProcessingInstruction(String piTarget, String piData) {
        factory.createOMProcessingInstruction(target, piTarget, piData);
    }

    public void createComment(String content) {
        factory.createOMComment(target, content);
    }

    public void createEntityReference(String name, String replacementText) {
        if (replacementText == null) {
            factory.createOMEntityReference(target, name);
        } else {
            // Since we set expandEntityReferences=true, we should never get here
            throw new UnsupportedOperationException();
        }
    }
}
