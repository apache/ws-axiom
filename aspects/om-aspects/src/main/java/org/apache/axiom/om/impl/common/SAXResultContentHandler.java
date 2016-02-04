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

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;

public class SAXResultContentHandler extends OMContentHandler {
    private final OMContainer root;
    private final OMFactory factory;
    private OMContainer target;

    public SAXResultContentHandler(OMContainer root) {
        super(true);
        this.root = root;
        factory = root.getOMFactory();
    }
    
    protected void doStartDocument() {
        target = root;
    }

    protected void doEndDocument() {
    }

    protected void createOMDocType(String rootName, String publicId,
            String systemId, String internalSubset) {
        if (target instanceof OMDocument) {
            factory.createOMDocType(target, rootName, publicId, systemId, internalSubset);
        }
    }

    protected OMElement createOMElement(String localName, String namespaceURI,
            String prefix, String[] namespaces, int namespaceCount) {
        // TODO: inefficient: we should not create a new OMNamespace instance every time
        OMElement element = factory.createOMElement(localName, factory.createOMNamespace(namespaceURI, prefix), target);
        for (int i=0; i<namespaceCount; i++) {
            String nsPrefix = namespaces[2*i];
            String nsURI = namespaces[2*i+1];
            if (nsPrefix.length() == 0) {
                element.declareDefaultNamespace(nsURI);
            } else {
                element.declareNamespace(nsURI, nsPrefix);
            }
        }
        target = element;
        return element;
    }

    protected void completed() {
        target = ((OMNode)target).getParent();
    }

    protected void createOMText(String text, int type) {
        factory.createOMText(target, text, type);
    }

    protected void createOMProcessingInstruction(String piTarget, String piData) {
        factory.createOMProcessingInstruction(target, piTarget, piData);
    }

    protected void createOMComment(String content) {
        factory.createOMComment(target, content);
    }

    protected void createOMEntityReference(String name, String replacementText) {
        if (replacementText == null) {
            factory.createOMEntityReference(target, name);
        } else {
            // Since we set expandEntityReferences=true, we should never get here
            throw new UnsupportedOperationException();
        }
    }
}
