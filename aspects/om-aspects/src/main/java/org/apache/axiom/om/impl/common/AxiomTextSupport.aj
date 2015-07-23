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

import java.io.IOException;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axiom.util.base64.Base64Utils;

public aspect AxiomTextSupport {
    /**
     * Either a {@link String} or a {@link TextContent} object.
     */
    private Object AxiomText.content;

    private TextContent AxiomText.getTextContent(boolean force) {
        if (content instanceof TextContent) {
            return (TextContent)content;
        } else if (force) {
            TextContent textContent = new TextContent((String)content);
            content = textContent;
            return textContent;
        } else {
            return null;
        }
    }
    
    public final boolean AxiomText.isBinary() {
        return content instanceof TextContent ? ((TextContent)content).binary : false;
    }

    public final void AxiomText.setBinary(boolean binary) {
        TextContent textContent = getTextContent(binary);
        if (textContent != null) {
            textContent.binary = binary;
        }
    }

    public final boolean AxiomText.isOptimized() {
        return content instanceof TextContent ? ((TextContent)content).optimize : false;
    }

    public final void AxiomText.setOptimize(boolean optimize) {
        TextContent textContent = getTextContent(optimize);
        if (textContent != null) {
            textContent.optimize = optimize;
            if (optimize) {
                textContent.binary = true;
            }
        }
    }
    
    public final String AxiomText.getText() throws OMException {
        if (content instanceof TextContent) {
            TextContent textContent = (TextContent)content;
            if (textContent.dataHandlerObject != null) {
                try {
                    return Base64Utils.encode((DataHandler)getDataHandler());
                } catch (Exception e) {
                    throw new OMException(e);
                }
            } else {
                return textContent.value;
            }
        } else {
            return (String)content;
        }
    }

    public final char[] AxiomText.getTextCharacters() {
        if (content instanceof TextContent) {
            TextContent textContent = (TextContent)content;
            if (textContent.dataHandlerObject != null) {
                try {
                    return Base64Utils.encodeToCharArray((DataHandler)getDataHandler());
                } catch (IOException ex) {
                    throw new OMException(ex);
                }
            } else {
                return textContent.value.toCharArray();
            }
        } else {
            return ((String)content).toCharArray();
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

    // TODO: should be final, but Abdera overrides this method
    public Object AxiomText.getDataHandler() {
        if (content instanceof TextContent) {
            TextContent textContent = (TextContent)content;
            if (textContent.dataHandlerObject != null) {
                if (textContent.dataHandlerObject instanceof DataHandlerProvider) {
                    try {
                        textContent.dataHandlerObject = ((DataHandlerProvider)textContent.dataHandlerObject).getDataHandler();
                    } catch (IOException ex) {
                        throw new OMException(ex);
                    }
                }
                return textContent.dataHandlerObject;
            } else if (textContent.binary) {
                return new DataHandler(new ByteArrayDataSource(
                        Base64Utils.decode(textContent.value), textContent.mimeType));
            }
        }
        throw new OMException("No DataHandler available");
    }

    public final String AxiomText.getContentID() {
        TextContent textContent = getTextContent(true);
        if (textContent.contentID == null) {
            textContent.contentID = UIDGenerator.generateContentId();
        }
        return textContent.contentID;
    }

    public final void AxiomText.internalSerialize(Serializer serializer, OMOutputFormat format, boolean cache) throws OutputException {
        if (content instanceof TextContent) {
            TextContent textContent = (TextContent)content;
            if (!textContent.binary) {
                serializer.writeText(getType(), textContent.value);
            } else if (textContent.dataHandlerObject instanceof DataHandlerProvider) {
                serializer.writeDataHandler((DataHandlerProvider)textContent.dataHandlerObject, textContent.contentID, textContent.optimize);
            } else {
                serializer.writeDataHandler((DataHandler)getDataHandler(), textContent.contentID, textContent.optimize);
            }
        } else {
            serializer.writeText(getType(), (String)content);
        }
    }

    public final void AxiomText.buildWithAttachments() {
        if (isOptimized()) {
            // The call to getDataSource ensures that the MIME part is completely read
            ((DataHandler)this.getDataHandler()).getDataSource();
        }
    }

    public final void AxiomText.setContentID(String cid) {
        getTextContent(true).contentID = cid;
    }

    public final void AxiomText.internalSetMimeType(String mimeType) {
        getTextContent(true).mimeType = mimeType;
    }
    
    public final void AxiomText.internalSetDataHandlerObject(Object dataHandlerObject) {
        getTextContent(true).dataHandlerObject = dataHandlerObject;
    }
    
    public final String AxiomText.coreGetCharacterData() {
        return getText();
    }

    public final void AxiomText.coreSetCharacterData(String data) {
        content = data;
    }
    
    public final AxiomText AxiomText.doClone() {
        AxiomText clone = createInstanceOfSameType();
        if (content instanceof TextContent) {
            TextContent textContent = (TextContent)content;
            TextContent clonedTextContent = new TextContent(textContent.value);
            clonedTextContent.optimize = textContent.optimize;
            clonedTextContent.mimeType = textContent.mimeType;
            clonedTextContent.binary = textContent.binary;
            clonedTextContent.contentID = textContent.contentID;
            clonedTextContent.dataHandlerObject = textContent.dataHandlerObject;
            clone.content = clonedTextContent;
        } else {
            clone.content = content;
        }
        return clone;
    }
}
