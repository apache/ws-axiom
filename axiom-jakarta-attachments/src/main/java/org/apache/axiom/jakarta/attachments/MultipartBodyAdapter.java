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
package org.apache.axiom.jakarta.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jakarta.activation.DataHandler;

import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.mime.activation.PartDataHandler;
import org.apache.axiom.mime.activation.PartDataHandlerBlobFactory;
import org.apache.axiom.mime.Header;
import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.mime.MultipartBody.PartCreationListener;
import org.apache.axiom.mime.Part;
import org.apache.axiom.om.OMException;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axiom.util.activation.DataHandlerUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class MultipartBodyAdapter extends AttachmentsDelegate implements PartCreationListener {
    private static final Log log = LogFactory.getLog(MultipartBodyAdapter.class);

    private final MultipartBody message;
    private final Map<String,DataHandler> map = new LinkedHashMap<String,DataHandler>();
    private final int contentLength;
    private final CountingInputStream filterIS;
    private final Part rootPart;
    private final String rootPartContentID;

    private Iterator<Part> partIterator;

    /** Container to hold streams for direct access */
    private IncomingAttachmentStreams streams;

    MultipartBodyAdapter(InputStream inStream, String contentTypeString,
            WritableBlobFactory<?> attachmentBlobFactory, int contentLength) {
        this.contentLength = contentLength;
        if (log.isDebugEnabled()) {
            log.debug("Attachments contentLength=" + contentLength + ", contentTypeString=" + contentTypeString);
        }

        // If the length is not known, install a filter so that we can retrieve it later.
        if (contentLength <= 0) {
            filterIS = new CountingInputStream(inStream);
            inStream = filterIS;
        } else {
            filterIS = null;
        }

        this.message = MultipartBody.builder()
                .setInputStream(inStream)
                .setContentType(contentTypeString)
                .setAttachmentBlobFactory(attachmentBlobFactory)
                .setPartBlobFactory(new PartDataHandlerBlobFactory() {
                        @Override
                        protected PartDataHandler createDataHandler(Part part) {
                            return new LegacyPartDataHandler(part);
                        }
                    })
                .setPartCreationListener(this)
                .build();

        rootPart = message.getRootPart();
        String rootPartContentID = rootPart.getContentID();
        if (rootPartContentID == null) {
            rootPartContentID = "firstPart_" + UIDGenerator.generateContentId();
            map.put(rootPartContentID, DataHandlerUtils.toDataHandler(rootPart.getPartBlob()));
        }
        this.rootPartContentID = rootPartContentID;
    }

    @Override
    public void partCreated(Part part) {
        String contentID = part.getContentID();
        if (contentID != null) {
            map.put(contentID, DataHandlerUtils.toDataHandler(part.getPartBlob()));
        }
    }

    private boolean fetchNext() {
        if (streams != null) {
            throw new IllegalStateException("The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
        }
        if (partIterator == null) {
            partIterator = message.iterator();
        }
        if (partIterator.hasNext()) {
            // This will add the DataHandler to the map (via the PartCreationListener interface)
            Part part = partIterator.next();
            if (part != rootPart) {
                String contentID = part.getContentID();
                if (contentID == null) {
                    throw new OMException(
                            "Part content ID cannot be blank for non root MIME parts");
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private void fetchAll() {
        while (fetchNext()) {
            // Just loop
        }
    }

    @Override
    ContentType getContentType() {
        return message.getContentType();
    }

    @Override
    DataHandler getDataHandler(String contentID) {
        do {
            DataHandler dataHandler = map.get(contentID);
            if (dataHandler != null) {
                return dataHandler;
            }
        } while (fetchNext());
        return null;
    }

    @Override
    void addDataHandler(String contentID, DataHandler dataHandler) {
        fetchAll();
        map.put(contentID, dataHandler);
    }

    @Override
    void removeDataHandler(String contentID) {
        do {
            if (map.remove(contentID) != null) {
                return;
            }
        } while (fetchNext());
    }

    @Override
    InputStream getRootPartInputStream(boolean preserve) {
        return rootPart.getInputStream(preserve);
    }

    @Override
    String getRootPartContentID() {
        return rootPartContentID;
    }

    @Override
    String getRootPartContentType() {
        return rootPart.getHeader(Header.CONTENT_TYPE);
    }

    @Override
    IncomingAttachmentStreams getIncomingAttachmentStreams() {
        if (partIterator != null) {
            throw new IllegalStateException(
                    "The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a " +
                            "collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
        }
        
        if (streams == null) {
            streams = new IncomingAttachmentStreams(message);
        }
        
        return streams;
    }


    @Override
    Set<String> getContentIDs(boolean fetchAll) {
        if (fetchAll) {
            fetchAll();
        }
        return map.keySet();
    }

    @Override
    Map<String,DataHandler> getMap() {
        fetchAll();
        return Collections.unmodifiableMap(map);
    }

    @Override
    long getContentLength() throws IOException {
        if (contentLength > 0) {
            return contentLength;
        } else {
            // Ensure all parts are read
            fetchAll();
            // Now get the count from the filter
            return filterIS.getCount();
        }
    }

    @Override
    MultipartBody getMultipartBody() {
        return message;
    }
}
