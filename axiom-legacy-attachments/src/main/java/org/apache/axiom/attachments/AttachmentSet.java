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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jakarta.activation.DataHandler;

import org.apache.axiom.mime.ContentType;
import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMException;

/**
 * {@link AttachmentsDelegate} implementation that represents a programmatically created set of
 * attachment parts.
 */
class AttachmentSet extends AttachmentsDelegate {
    private final Map<String, DataHandler> attachmentsMap =
            new LinkedHashMap<String, DataHandler>();

    @Override
    ContentType getContentType() {
        return null;
    }

    @Override
    DataHandler getDataHandler(String contentID) {
        return attachmentsMap.get(contentID);
    }

    @Override
    void addDataHandler(String contentID, DataHandler dataHandler) {
        attachmentsMap.put(contentID, dataHandler);
    }

    @Override
    void removeDataHandler(String blobContentID) {
        attachmentsMap.remove(blobContentID);
    }

    @Override
    InputStream getRootPartInputStream(boolean preserve) throws OMException {
        throw new OMException("Invalid operation. Attachments are created programatically.");
    }

    @Override
    String getRootPartContentID() {
        return null;
    }

    @Override
    String getRootPartContentType() {
        throw new OMException(
                "The attachments map was created programatically. Unsupported operation.");
    }

    @Override
    IncomingAttachmentStreams getIncomingAttachmentStreams() {
        throw new IllegalStateException(
                "The attachments map was created programatically. No streams are available.");
    }

    @Override
    Set<String> getContentIDs(boolean fetchAll) {
        return attachmentsMap.keySet();
    }

    @Override
    Map<String, DataHandler> getMap() {
        return Collections.unmodifiableMap(attachmentsMap);
    }

    @Override
    long getContentLength() throws IOException {
        return -1;
    }

    @Override
    MultipartBody getMultipartBody() {
        throw new IllegalStateException(
                "The attachments map was created programatically. MultipartBody is not available.");
    }
}
