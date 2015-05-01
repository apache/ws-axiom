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

import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public aspect DOMDocumentSupport {
    private final DOMConfigurationImpl DOMDocument.domConfig = new DOMConfigurationImpl();

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

    public final String DOMDocument.getLocalName() {
        return null;
    }

    public final Element DOMDocument.getDocumentElement() {
        return (Element)coreGetDocumentElement();
    }
    
    public final String DOMDocument.lookupNamespaceURI(String specifiedPrefix) {
        Element documentElement = getDocumentElement();
        return documentElement == null ? null
                : documentElement.lookupNamespaceURI(specifiedPrefix);
    }
    
    public final String DOMDocument.lookupPrefix(String namespaceURI) {
        Element documentElement = getDocumentElement();
        return documentElement == null ? null
                : getDocumentElement().lookupPrefix(namespaceURI);
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
        DOMText text = (DOMText)coreGetNodeFactory().createCharacterData();
        text.coreSetOwnerDocument(this);
        text.coreSetData(data);
        return text;
    }

    public final CDATASection DOMDocument.createCDATASection(String data) throws DOMException {
        DOMCDATASection cdataSection = (DOMCDATASection)coreGetNodeFactory().createCDATASection();
        cdataSection.coreSetOwnerDocument(this);
        cdataSection.coreSetData(data);
        return cdataSection;
    }
}
