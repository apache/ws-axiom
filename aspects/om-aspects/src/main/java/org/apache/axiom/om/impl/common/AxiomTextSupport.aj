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
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axiom.util.base64.Base64Utils;

public aspect AxiomTextSupport {
    declare parents: (InformationItem+ && OMText+) implements AxiomText;

    // TODO: should be private
    public String AxiomText.value;
    
    private String AxiomText.mimeType;
    
    /** Field contentID for the mime part used when serializing Binary stuff as MTOM optimized. */
    private String AxiomText.contentID;
    
    /**
     * Contains a {@link DataHandler} or {@link DataHandlerProvider} object if the text node
     * represents base64 encoded binary data.
     */
    private Object AxiomText.dataHandlerObject;

    private boolean AxiomText.optimize;
    private boolean AxiomText.binary;

    public final boolean AxiomText.isBinary() {
        return binary;
    }

    public final void AxiomText.setBinary(boolean binary) {
        this.binary = binary;
    }

    public final boolean AxiomText.isOptimized() {
        return optimize;
    }

    public final void AxiomText.setOptimize(boolean optimize) {
        this.optimize = optimize;
        if (optimize) {
            binary = true;
        }
    }
    
    public final String AxiomText.getText() throws OMException {
        if (dataHandlerObject != null) {
            try {
                return Base64Utils.encode((DataHandler)getDataHandler());
            } catch (Exception e) {
                throw new OMException(e);
            }
        } else {
            return value;
        }
    }

    public final char[] AxiomText.getTextCharacters() {
        if (dataHandlerObject != null) {
            try {
                return Base64Utils.encodeToCharArray((DataHandler)getDataHandler());
            } catch (IOException ex) {
                throw new OMException(ex);
            }
        } else {
            return value.toCharArray();
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
        if (dataHandlerObject != null) {
            if (dataHandlerObject instanceof DataHandlerProvider) {
                try {
                    dataHandlerObject = ((DataHandlerProvider)dataHandlerObject).getDataHandler();
                } catch (IOException ex) {
                    throw new OMException(ex);
                }
            }
            return dataHandlerObject;
        } else if (isBinary()) {
            return new DataHandler(new ByteArrayDataSource(
                    Base64Utils.decode(value), mimeType));
        } else {
            throw new OMException("No DataHandler available");
        }
    }

    public final String AxiomText.getContentID() {
        if (contentID == null) {
            contentID = UIDGenerator.generateContentId();
        }
        return this.contentID;
    }

    public final void AxiomText.internalSerialize(Serializer serializer, OMOutputFormat format, boolean cache) throws OutputException {
        if (!isBinary()) {
            serializer.writeText(getType(), getText());
        } else if (dataHandlerObject instanceof DataHandlerProvider) {
            serializer.writeDataHandler((DataHandlerProvider)dataHandlerObject, contentID, isOptimized());
        } else {
            serializer.writeDataHandler((DataHandler)getDataHandler(), contentID, isOptimized());
        }
    }

    public final void AxiomText.buildWithAttachments() {
        if (isOptimized()) {
            // The call to getDataSource ensures that the MIME part is completely read
            ((DataHandler)this.getDataHandler()).getDataSource();
        }
    }

    public final void AxiomText.setContentID(String cid) {
        this.contentID = cid;
    }

    public final void AxiomText.internalSetValue(String value) {
        this.value = value;
    }

    public final void AxiomText.internalSetMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public final void AxiomText.internalSetDataHandlerObject(Object dataHandlerObject) {
        this.dataHandlerObject = dataHandlerObject;
    }
    
    public final String AxiomText.coreGetData() {
        return value;
    }

    public final void AxiomText.coreSetData(String data) {
        this.value = data;
    }
    
    public final AxiomText AxiomText.doClone() {
        AxiomText clone = createInstanceOfSameType();
        clone.value = value;
        
        // Copy the optimized related settings.
        clone.optimize = optimize;
        clone.mimeType = mimeType;
        clone.binary = binary;
        
        clone.contentID = contentID;
        clone.dataHandlerObject = dataHandlerObject;
        
        return clone;
    }
}
