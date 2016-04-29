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
package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.om.OMException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class MIMEMessageAdapter extends AttachmentsDelegate {
    private static final Log log = LogFactory.getLog(MIMEMessageAdapter.class);

    private final MIMEMessage message;
    private final Map<String,DataHandler> addedDataHandlers = new LinkedHashMap<String,DataHandler>();
    private final Set<String> removedDataHandlers = new HashSet<String>();
    private final int contentLength;
    private final CountingInputStream filterIS;

    /** <code>boolean</code> Indicating if any streams have been directly requested */
    private boolean streamsRequested;

    /** <code>boolean</code> Indicating if any data handlers have been directly requested */
    private boolean partsRequested;

    /** Container to hold streams for direct access */
    private IncomingAttachmentStreams streams;

    MIMEMessageAdapter(InputStream inStream, String contentTypeString,
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

        this.message = new MIMEMessage(inStream, contentTypeString, attachmentBlobFactory);
    }

    private void requestParts() {
        if (streamsRequested) {
            throw new IllegalStateException("The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
        }
        partsRequested = true;
    }

    @Override
    ContentType getContentType() {
        return message.getContentType();
    }

    @Override
    DataHandler getDataHandler(String contentID) {
        requestParts();
        DataHandler dh = addedDataHandlers.get(contentID);
        if (dh != null) {
            return dh;
        } else if (removedDataHandlers.contains(contentID)) {
            return null;
        } else {
            return message.getDataHandler(contentID);
        }
    }

    @Override
    void addDataHandler(String contentID, DataHandler dataHandler) {
        requestParts();
        addedDataHandlers.put(contentID, dataHandler);
    }

    @Override
    void removeDataHandler(String blobContentID) {
        requestParts();
        if (addedDataHandlers.remove(blobContentID) == null) {
            removedDataHandlers.add(blobContentID);
        }
    }

    @Override
    InputStream getRootPartInputStream(boolean preserve) {
        try {
            return message.getRootPart().getInputStream(preserve);
        } catch (IOException ex) {
            throw new OMException("Problem fetching the root part", ex);
        }
    }

    @Override
    String getRootPartContentID() {
        return message.getRootPartContentID();
    }

    @Override
    String getRootPartContentType() {
        return message.getRootPart().getContentType();
    }

    @Override
    IncomingAttachmentStreams getIncomingAttachmentStreams() {
        if (partsRequested) {
            throw new IllegalStateException(
                    "The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a " +
                            "collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
        }
        
        streamsRequested = true;
        
        if (streams == null) {
            streams = new IncomingAttachmentStreams(message);
        }
        
        return streams;
    }


    @Override
    Set<String> getContentIDs(boolean fetchAll) {
        requestParts();
        Set<String> result = new LinkedHashSet<String>(message.getContentIDs(fetchAll));
        result.removeAll(removedDataHandlers);
        result.addAll(addedDataHandlers.keySet());
        return result;
    }

    @Override
    Map<String,DataHandler> getMap() {
        requestParts();
        Map<String,DataHandler> result = new LinkedHashMap<String,DataHandler>();
        for (Map.Entry<String,Part> entry : message.getMap().entrySet()) {
            String contentID = entry.getKey();
            if (!removedDataHandlers.contains(contentID)) {
                result.put(contentID, entry.getValue().getDataHandler());
            }
        }
        result.putAll(addedDataHandlers);
        return Collections.unmodifiableMap(result);
    }

    @Override
    long getContentLength() throws IOException {
        if (contentLength > 0) {
            return contentLength;
        } else {
            requestParts();
            // Ensure all parts are read
            message.fetchAllParts();
            // Now get the count from the filter
            return filterIS.getCount();
        }
    }
}
