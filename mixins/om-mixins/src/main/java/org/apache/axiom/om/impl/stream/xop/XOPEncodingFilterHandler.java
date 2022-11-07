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
package org.apache.axiom.om.impl.stream.xop;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.xop.AbstractXOPEncodingFilterHandler;
import org.apache.axiom.ext.stax.BlobProvider;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.util.activation.DataHandlerUtils;

public final class XOPEncodingFilterHandler extends AbstractXOPEncodingFilterHandler
        implements XOPHandler, OMAttachmentAccessor {
    private final Map<String, Object> blobObjects = new LinkedHashMap<String, Object>();
    private final ContentIDGenerator contentIDGenerator;
    private final OptimizationPolicy optimizationPolicy;

    public XOPEncodingFilterHandler(
            XmlHandler parent,
            ContentIDGenerator contentIDGenerator,
            OptimizationPolicy optimizationPolicy) {
        super(parent);
        this.contentIDGenerator = contentIDGenerator;
        this.optimizationPolicy = optimizationPolicy;
    }

    @Override
    public String prepareBlob(Blob blob) {
        boolean doOptimize;
        try {
            doOptimize = optimizationPolicy.isOptimized(DataHandlerUtils.toDataHandler(blob), true);
        } catch (IOException ex) {
            doOptimize = true;
        }
        if (doOptimize) {
            String contentID = contentIDGenerator.generateContentID(null);
            blobObjects.put(contentID, blob);
            return contentID;
        } else {
            return null;
        }
    }

    /**
     * Get the set of content IDs referenced in {@code xop:Include} element information items
     * produced by this wrapper.
     *
     * @return The set of content IDs in their order of appearance in the infoset. If no {@code
     *     xop:Include} element information items have been produced yet, an empty set will be
     *     returned.
     */
    public Set<String> getContentIDs() {
        return Collections.unmodifiableSet(blobObjects.keySet());
    }

    @Override
    public Blob getBlob(String contentID) {
        Object blobObject = blobObjects.get(contentID);
        if (blobObject == null) {
            return null;
        } else if (blobObject instanceof Blob) {
            return (Blob) blobObject;
        } else {
            try {
                return ((BlobProvider) blobObject).getBlob();
            } catch (IOException ex) {
                throw new OMException(ex);
            }
        }
    }

    @Override
    protected String processCharacterData(Object data) throws StreamException {
        if (data instanceof TextContent) {
            TextContent textContent = (TextContent) data;
            if (textContent.isBinary()) {
                Object blobObject = textContent.getBlobObject();
                boolean optimize;
                try {
                    if (blobObject instanceof BlobProvider) {
                        optimize =
                                optimizationPolicy.isOptimized(
                                        (BlobProvider) blobObject, textContent.isOptimize());
                    } else {
                        optimize =
                                optimizationPolicy.isOptimized(
                                        DataHandlerUtils.toDataHandler((Blob) blobObject),
                                        textContent.isOptimize());
                    }
                } catch (IOException ex) {
                    throw new StreamException(ex);
                }
                if (optimize) {
                    String contentID =
                            contentIDGenerator.generateContentID(textContent.getContentID());
                    blobObjects.put(contentID, blobObject);
                    return contentID;
                }
            }
        }
        return null;
    }
}
