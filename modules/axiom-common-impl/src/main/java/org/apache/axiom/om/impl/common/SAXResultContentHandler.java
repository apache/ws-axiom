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

public class SAXResultContentHandler extends OMContentHandler {
    private final OMContainer root;
    private final OMFactory factory;

    public SAXResultContentHandler(OMContainer root) {
        super(true);
        this.root = root;
        factory = root.getOMFactory();
    }
    
    protected OMContainer doStartDocument() {
        return root;
    }

    protected void doEndDocument() {
    }

    protected void createOMDocType(OMContainer parent, String rootName, String publicId,
            String systemId, String internalSubset) {
        if (parent instanceof OMDocument) {
            factory.createOMDocType(parent, rootName, publicId, systemId, internalSubset);
        }
    }

    protected OMElement createOMElement(OMContainer parent, String localName, String namespaceURI,
            String prefix, String[] namespaces, int namespaceCount) {
        // TODO: inefficient: we should not create a new OMNamespace instance every time
        OMElement element = factory.createOMElement(localName, factory.createOMNamespace(namespaceURI, prefix), parent);
        for (int i=0; i<namespaceCount; i++) {
            String nsPrefix = namespaces[2*i];
            String nsURI = namespaces[2*i+1];
            if (nsPrefix.length() == 0) {
                element.declareDefaultNamespace(nsURI);
            } else {
                element.declareNamespace(nsURI, nsPrefix);
            }
        }
        return element;
    }

    protected void completed(OMElement element) {
    }

    protected void createOMText(OMContainer parent, String text, int type) {
        factory.createOMText(parent, text, type);
    }

    protected void createOMProcessingInstruction(OMContainer parent, String piTarget, String piData) {
        factory.createOMProcessingInstruction(parent, piTarget, piData);
    }

    protected void createOMComment(OMContainer parent, String content) {
        factory.createOMComment(parent, content);
    }

    protected void createOMEntityReference(OMContainer parent, String name, String replacementText) {
        if (replacementText == null) {
            factory.createOMEntityReference(parent, name);
        } else {
            // Since we set expandEntityReferences=true, we should never get here
            throw new UnsupportedOperationException();
        }
    }
}
