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
package org.apache.axiom.om.impl.mixin;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.intf.AxiomText;
import org.apache.axiom.om.impl.intf.TextContent;

public aspect AxiomTextSupport {
    private TextContent AxiomText.getTextContent(boolean force) {
        try {
            Object content = coreGetCharacterData();
            if (content instanceof TextContent) {
                return (TextContent)content;
            } else if (force) {
                TextContent textContent = new TextContent((String)content);
                coreSetCharacterData(textContent, AxiomSemantics.INSTANCE);
                return textContent;
            } else {
                return null;
            }
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }
    
    public final boolean AxiomText.isBinary() {
        TextContent textContent = getTextContent(false);
        return textContent != null && textContent.isBinary();
    }

    public final void AxiomText.setBinary(boolean binary) {
        TextContent textContent = getTextContent(binary);
        if (textContent != null) {
            textContent.setBinary(binary);
        }
    }

    public final boolean AxiomText.isOptimized() {
        TextContent textContent = getTextContent(false);
        return textContent != null && textContent.isOptimize();
    }

    public final void AxiomText.setOptimize(boolean optimize) {
        TextContent textContent = getTextContent(optimize);
        if (textContent != null) {
            textContent.setOptimize(optimize);
        }
    }
    
    public final String AxiomText.getText() throws OMException {
        try {
            return coreGetCharacterData().toString();
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    public final char[] AxiomText.getTextCharacters() {
        try {
            Object content = coreGetCharacterData();
            if (content instanceof TextContent) {
                return ((TextContent)content).toCharArray();
            } else {
                return ((String)content).toCharArray();
            }
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    public final boolean AxiomText.isCharacters() {
        return false;
    }

    public final QName AxiomText.getTextAsQName() throws OMException {
        return ((OMElement)getParent()).resolveQName(getText());
    }

    public final OMNamespace AxiomText.getNamespace() {
        // Note: efficiency is not important here; the method is deprecated anyway
        QName qname = getTextAsQName();
        if (qname == null) {
            return null;
        } else {
            String namespaceURI = qname.getNamespaceURI();
            return namespaceURI.length() == 0 ? null : new OMNamespaceImpl(namespaceURI, qname.getPrefix());
        }
    }

    public final DataHandler AxiomText.getDataHandler() {
        try {
            Object content = coreGetCharacterData();
            if (content instanceof TextContent) {
                return ((TextContent)content).getDataHandler();
            } else {
                throw new OMException("No DataHandler available");
            }
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    public final String AxiomText.getContentID() {
        return getTextContent(true).getContentID();
    }

    public final void AxiomText.buildWithAttachments() {
        if (isOptimized()) {
            // The call to getDataSource ensures that the MIME part is completely read
            getDataHandler().getDataSource();
        }
    }

    public final void AxiomText.setContentID(String cid) {
        getTextContent(true).setContentID(cid);
    }
}
