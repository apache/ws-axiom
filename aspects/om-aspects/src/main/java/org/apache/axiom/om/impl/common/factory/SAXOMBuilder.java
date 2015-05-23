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

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMElementEx;
import org.apache.axiom.om.impl.builder.BuilderUtil;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.om.impl.common.OMContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;

import javax.xml.transform.sax.SAXSource;

public class SAXOMBuilder extends OMContentHandler implements OMXMLParserWrapper {
    private final SAXSource source;
    
    private OMDocument document;
    
    private final OMFactoryEx factory;

    public SAXOMBuilder(OMFactory factory, SAXSource source, boolean expandEntityReferences) {
        super(expandEntityReferences);
        this.factory = (OMFactoryEx)factory;
        this.source = source;
    }
    
    protected OMContainer doStartDocument() {
        document = factory.createOMDocument(this);
        return document;
    }

    protected void doEndDocument() {
        ((OMContainerEx)document).setComplete(true);
    }

    public OMDocument getDocument() {
        if (document == null && source != null) {
            XMLReader reader = source.getXMLReader();
            reader.setContentHandler(this);
            reader.setDTDHandler(this);
            try {
                reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
            } catch (SAXException ex) {
                // Ignore
            }
            try {
                reader.setProperty("http://xml.org/sax/properties/declaration-handler", this);
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
        if (document != null && document.isComplete()) {
            return document;
        } else {
            throw new OMException("Tree not complete");
        }
    }
    
    public int next() throws OMException {
        throw new UnsupportedOperationException();
    }

    public void discard(OMElement el) throws OMException {
        throw new UnsupportedOperationException();
    }

    public void setCache(boolean b) throws OMException {
        throw new UnsupportedOperationException();
    }

    public boolean isCache() {
        throw new UnsupportedOperationException();
    }

    public Object getParser() {
        throw new UnsupportedOperationException();
    }

    public boolean isCompleted() {
        return document != null && document.isComplete();
    }

    public OMElement getDocumentElement() {
        return getDocument().getOMDocumentElement();
    }

    public OMElement getDocumentElement(boolean discardDocument) {
        OMElement documentElement = getDocument().getOMDocumentElement();
        if (discardDocument) {
            documentElement.detach();
        }
        return documentElement;
    }

    public short getBuilderType() {
        throw new UnsupportedOperationException();
    }

    public void registerExternalContentHandler(Object obj) {
        throw new UnsupportedOperationException();
    }

    public Object getRegisteredContentHandler() {
        throw new UnsupportedOperationException();
    }

    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    public void close() {
        // This is a no-op
    }

    protected void createOMDocType(OMContainer parent, String rootName, String publicId,
            String systemId, String internalSubset) {
        factory.createOMDocType(parent, rootName, publicId, systemId, internalSubset, true);
    }

    protected OMElement createOMElement(OMContainer parent, String localName,
            String namespaceURI, String prefix, String[] namespaces, int namespaceCount) {
        OMElement element = factory.createOMElement(localName, parent, this);
        for (int i = 0; i < namespaceCount; i++) {
            ((OMElementEx)element).addNamespaceDeclaration(namespaces[2*i+1], namespaces[2*i]);
        }
        BuilderUtil.setNamespace(element, namespaceURI, prefix, false);
        return element;
    }

    protected void completed(OMElement element) {
        ((OMElementEx)element).setComplete(true);
    }

    protected void createOMText(OMContainer parent, String text, int type) {
        factory.createOMText(parent, text, type, true);
    }

    protected void createOMProcessingInstruction(OMContainer parent,
            String piTarget, String piData) {
        factory.createOMProcessingInstruction(parent, piTarget, piData, true);
    }

    protected void createOMComment(OMContainer parent, String content) {
        factory.createOMComment(parent, content, true);
    }

    protected void createOMEntityReference(OMContainer parent, String name,
            String replacementText) {
        factory.createOMEntityReference(parent, name, replacementText, true);
    }
    
    public void detach() {
        // Force processing of the SAX source
        getDocument();
    }
}
