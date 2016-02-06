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

package org.apache.axiom.om.impl.common.factory;

import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.common.Handler;
import org.apache.axiom.om.impl.common.OMContentHandler;
import org.apache.axiom.om.impl.common.builder.AbstractPushBuilder;
import org.apache.axiom.om.impl.common.builder.BuilderUtil;
import org.apache.axiom.om.impl.common.builder.Model;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.transform.sax.SAXSource;

public final class SAXOMBuilder extends AbstractPushBuilder implements Handler {
    private final boolean expandEntityReferences;
    private final SAXSource source;
    
    public SAXOMBuilder(NodeFactory nodeFactory, Model model, SAXSource source, boolean expandEntityReferences) {
        super(nodeFactory, model);
        this.expandEntityReferences = expandEntityReferences;
        this.source = source;
    }
    
    public void doStartDocument() {
        handler.startDocument(null, "1.0", null, true);
    }

    public void doEndDocument() {
        if (handler.target != handler.document) {
            throw new IllegalStateException();
        }
        handler.target = null;
        ((AxiomContainer)handler.document).setComplete(true);
    }

    public OMDocument getDocument() {
        if (handler.document == null && source != null) {
            XMLReader reader = source.getXMLReader();
            OMContentHandler contentHandler = new OMContentHandler(this, expandEntityReferences);
            reader.setContentHandler(contentHandler);
            reader.setDTDHandler(contentHandler);
            try {
                reader.setProperty("http://xml.org/sax/properties/lexical-handler", contentHandler);
            } catch (SAXException ex) {
                // Ignore
            }
            try {
                reader.setProperty("http://xml.org/sax/properties/declaration-handler", contentHandler);
            } catch (SAXException ex) {
                // Ignore
            }
            try {
                reader.parse(source.getInputSource());
            } catch (IOException ex) {
                throw new OMException(ex);
            } catch (SAXException ex) {
                throw new OMException(ex);
            }
        }
        if (handler.document != null && handler.document.isComplete()) {
            return handler.document;
        } else {
            throw new OMException("Tree not complete");
        }
    }

    public void createOMDocType(String rootName, String publicId,
            String systemId, String internalSubset) {
        handler.createDocumentTypeDeclaration(rootName, publicId, systemId, internalSubset);
    }

    public OMElement createOMElement(String localName,
            String namespaceURI, String prefix, String[] namespaces, int namespaceCount) {
        AxiomElement element = handler.startElement(null, localName, null);
        for (int i = 0; i < namespaceCount; i++) {
            element.addNamespaceDeclaration(namespaces[2*i+1], namespaces[2*i]);
        }
        BuilderUtil.setNamespace(element, namespaceURI, prefix);
        return element;
    }

    public void completed() {
        handler.endElement();
    }

    public void createOMText(String text, int type) {
        switch (type) {
            case XMLStreamConstants.CHARACTERS:
                handler.processCharacterData(text, false);
                break;
            case XMLStreamConstants.SPACE:
                handler.processCharacterData(text, true);
                break;
            case XMLStreamConstants.CDATA:
                handler.createCDATASection(text);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void createOMProcessingInstruction(String piTarget, String piData) {
        handler.createProcessingInstruction(piTarget, piData);
    }

    public void createOMComment(String content) {
        handler.createComment(content);
    }

    public void createOMEntityReference(String name, String replacementText) {
        handler.createEntityReference(name, replacementText);
    }
}
