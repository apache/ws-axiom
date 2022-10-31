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
package org.apache.axiom.om.impl.intf;

import java.io.IOException;

import javax.activation.DataHandler;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CloneableCharacterData;
import org.apache.axiom.core.stream.CharacterData;
import org.apache.axiom.core.stream.CharacterDataSink;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMException;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axiom.util.activation.BlobDataSource;
import org.apache.axiom.util.base64.AbstractBase64EncodingOutputStream;
import org.apache.axiom.util.base64.Base64EncodingStringBufferOutputStream;
import org.apache.axiom.util.base64.Base64Utils;

public final class TextContent implements CloneableCharacterData {
    private final String value;

    private final String mimeType;

    /** Field contentID for the mime part used when serializing Binary stuff as MTOM optimized. */
    private String contentID;

    /**
     * Contains a {@link DataHandler} or {@link DataHandlerProvider} object if the text node
     * represents base64 encoded binary data.
     */
    private Object dataHandlerObject;

    private boolean optimize;
    private boolean binary;

    public TextContent(String value) {
        this.value = value;
        this.mimeType = null;
    }

    public TextContent(String value, String mimeType, boolean optimize) {
        this.value = value;
        this.mimeType = mimeType;
        binary = true;
        this.optimize = optimize;
    }

    public TextContent(String contentID, Object dataHandlerObject, boolean optimize) {
        this.value = null;
        mimeType = null;
        this.contentID = contentID;
        this.dataHandlerObject = dataHandlerObject;
        binary = true;
        this.optimize = optimize;
    }

    private TextContent(TextContent other) {
        this.value = other.value;
        this.mimeType = other.mimeType;
        this.contentID = other.contentID;
        this.dataHandlerObject = other.dataHandlerObject;
        this.optimize = other.optimize;
        this.binary = other.binary;
    }

    public boolean isOptimize() {
        return optimize;
    }

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
        if (optimize) {
            binary = true;
        }
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public String getContentID() {
        if (contentID == null) {
            contentID = UIDGenerator.generateContentId();
        }
        return contentID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }

    public Object getDataHandlerObject() {
        return dataHandlerObject;
    }

    public DataHandler getDataHandler() {
        if (dataHandlerObject != null) {
            if (dataHandlerObject instanceof DataHandlerProvider) {
                try {
                    dataHandlerObject = ((DataHandlerProvider) dataHandlerObject).getDataHandler();
                } catch (IOException ex) {
                    throw new OMException(ex);
                }
            }
            return (DataHandler) dataHandlerObject;
        } else if (binary) {
            return new DataHandler(
                    new BlobDataSource(Blobs.createBlob(Base64Utils.decode(value)), mimeType));
        } else {
            throw new OMException("No DataHandler available");
        }
    }

    @Override
    public String toString() {
        if (dataHandlerObject != null) {
            try {
                return Base64Utils.encode(getDataHandler());
            } catch (Exception e) {
                throw new OMException(e);
            }
        } else {
            return value;
        }
    }

    public char[] toCharArray() {
        if (dataHandlerObject != null) {
            try {
                return Base64Utils.encodeToCharArray(getDataHandler());
            } catch (IOException ex) {
                throw new OMException(ex);
            }
        } else {
            return value.toCharArray();
        }
    }

    @Override
    public <T> CharacterData clone(ClonePolicy<T> policy, T options) {
        if (binary
                && options instanceof OMCloneOptions
                && ((OMCloneOptions) options).isFetchDataHandlers()) {
            // Force loading of the reference to the DataHandler and ensure that its content is
            // completely fetched into memory (or temporary storage).
            getDataHandler().getDataSource();
        }
        return new TextContent(this);
    }

    @Override
    public void writeTo(CharacterDataSink sink) throws IOException {
        if (binary) {
            AbstractBase64EncodingOutputStream out = sink.getBase64EncodingOutputStream();
            getDataHandler().writeTo(out);
            out.complete();
        } else {
            // TODO: there must be a better way to just write a String
            sink.getWriter().write(value);
        }
    }

    @Override
    public void appendTo(StringBuilder buffer) {
        if (binary) {
            Base64EncodingStringBufferOutputStream out =
                    new Base64EncodingStringBufferOutputStream(buffer);
            try {
                getDataHandler().writeTo(out);
                out.complete();
            } catch (IOException ex) {
                throw new OMException(ex);
            }
        } else {
            buffer.append(value);
        }
    }
}
