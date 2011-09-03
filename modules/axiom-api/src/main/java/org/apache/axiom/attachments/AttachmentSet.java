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

import javax.activation.DataHandler;
import javax.mail.internet.ContentType;

import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.om.OMException;

class AttachmentSet extends AttachmentsImpl {
    private final Map attachmentsMap = new LinkedHashMap();

    ContentType getContentType() {
        return null;
    }

    LifecycleManager getLifecycleManager() {
        return null;
    }

    void setLifecycleManager(LifecycleManager manager) {
        // Ignore; only stream based attachments need a lifecycle manager
    }

    DataHandler getDataHandler(String contentID) {
        return (DataHandler)attachmentsMap.get(contentID);
    }

    void addDataHandler(String contentID, DataHandler dataHandler) {
        attachmentsMap.put(contentID, dataHandler);
    }
    
    void removeDataHandler(String blobContentID) {
        attachmentsMap.remove(blobContentID);
    }

    InputStream getRootPartInputStream() throws OMException {
        throw new OMException("Invalid operation. Attachments are created programatically.");
    }

    String getRootPartContentID() {
        return null;
    }

    String getRootPartContentType() {
        throw new OMException(
                "The attachments map was created programatically. Unsupported operation.");
    }

    IncomingAttachmentStreams getIncomingAttachmentStreams() {
        throw new IllegalStateException(
                "The attachments map was created programatically. No streams are available.");
    }

    Set getContentIDs(boolean fetchAll) {
        return attachmentsMap.keySet();
    }
    
    Map getMap() {
        return Collections.unmodifiableMap(attachmentsMap);
    }
    
    long getContentLength() throws IOException {
        return -1;
    }
}
