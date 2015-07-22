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
package org.apache.axiom.dom;

import javax.xml.XMLConstants;

import org.apache.axiom.core.CoreElement;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public aspect DOMDocumentSupport {
    private final DOMConfigurationImpl DOMDocument.domConfig = new DOMConfigurationImpl();

    public final Document DOMDocument.getOwnerDocument() {
        return null;
    }

    public final String DOMDocument.getNodeName() {
        return "#document";
    }

    public final short DOMDocument.getNodeType() {
        return Node.DOCUMENT_NODE;
    }

    public final String DOMDocument.getNodeValue() {
        return null;
    }

    public final void DOMDocument.setNodeValue(String nodeValue) {
    }

    public final String DOMDocument.getPrefix() {
        return null;
    }

    public final String DOMDocument.getNamespaceURI() {
        return null;
    }

    public final String DOMDocument.getLocalName() {
        return null;
    }

    public final boolean DOMDocument.hasAttributes() {
        return false;
    }

    public final NamedNodeMap DOMDocument.getAttributes() {
        return null;
    }

    public final String DOMDocument.getTextContent() {
        return null;
    }

    public final void DOMDocument.setTextContent(String textContent) {
        // no-op
    }

    public final Element DOMDocument.getDocumentElement() {
        return (Element)coreGetDocumentElement();
    }
    
    public final CoreElement DOMDocument.getNamespaceContext() {
        return coreGetDocumentElement();
    }

    public final DOMImplementation DOMDocument.getImplementation() {
        return (DOMNodeFactory)coreGetNodeFactory();
    }

    public final DOMConfiguration DOMDocument.getDomConfig() {
        return domConfig;
    }

    public final void DOMDocument.normalizeDocument() {
        if (domConfig.isEnabled(DOMConfigurationImpl.SPLIT_CDATA_SECTIONS)
                || domConfig.isEnabled(DOMConfigurationImpl.WELLFORMED)) {
            throw new UnsupportedOperationException("TODO");
        } else {
            normalize(domConfig);
        }
    }
    
    public final Text DOMDocument.createTextNode(String data) {
        DOMText text = (DOMText)coreGetNodeFactory().createCharacterDataNode();
        text.coreSetOwnerDocument(this);
        text.coreSetCharacterData(data);
        return text;
    }

    public final CDATASection DOMDocument.createCDATASection(String data) throws DOMException {
        DOMCDATASection cdataSection = (DOMCDATASection)coreGetNodeFactory().createCDATASection();
        cdataSection.coreSetOwnerDocument(this);
        cdataSection.coreSetCharacterData(data);
        return cdataSection;
    }
    
    public final Attr DOMDocument.createAttribute(String name) {
        NSUtil.validateName(name);
        return (DOMAttribute)coreGetNodeFactory().createAttribute(this, name, "", "CDATA");
    }

    public final Attr DOMDocument.createAttributeNS(String namespaceURI, String qualifiedName) {
        int i = NSUtil.validateQualifiedName(qualifiedName);
        String prefix;
        String localName;
        if (i == -1) {
            prefix = "";
            localName = qualifiedName;
        } else {
            prefix = qualifiedName.substring(0, i);
            localName = qualifiedName.substring(i+1);
        }
        if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceURI)) {
            return (DOMAttribute)coreGetNodeFactory().createNamespaceDeclaration(this, NSUtil.getDeclaredPrefix(localName, prefix), null);
        } else {
            namespaceURI = NSUtil.normalizeNamespaceURI(namespaceURI);
            NSUtil.validateAttributeName(namespaceURI, localName, prefix);
            return (DOMAttribute)coreGetNodeFactory().createAttribute(this, namespaceURI, localName, prefix, null, null);
        }
    }

    public final ProcessingInstruction DOMDocument.createProcessingInstruction(String target, String data) {
        DOMProcessingInstruction pi = (DOMProcessingInstruction)coreGetNodeFactory().createProcessingInstruction();
        pi.coreSetOwnerDocument(this);
        pi.coreSetTarget(target);
        pi.coreSetCharacterData(data);
        return pi;
    }
}
