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
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.internet.ContentType;

import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.om.OMException;

abstract class AttachmentsImpl {
    abstract ContentType getContentType();
    abstract LifecycleManager getLifecycleManager();
    abstract void setLifecycleManager(LifecycleManager manager);
    abstract DataHandler getDataHandler(String contentID);
    abstract void addDataHandler(String contentID, DataHandler dataHandler);
    abstract void removeDataHandler(String blobContentID);
    abstract InputStream getSOAPPartInputStream() throws OMException;
    abstract String getSOAPPartContentID();
    abstract String getSOAPPartContentType();
    abstract IncomingAttachmentStreams getIncomingAttachmentStreams();
    abstract Set getContentIDs(boolean fetchAll);
    abstract Map getMap();
    abstract long getContentLength() throws IOException;
}
