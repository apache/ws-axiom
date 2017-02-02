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

import org.apache.axiom.mime.ContentType;
import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMException;

/**
 * {@link Attachments} delegate. An {@link Attachments} object may actually represent two fairly
 * different things (depending on the constructor that is used):
 * <ul>
 * <li>A MIME multipart message that comprises a root part and a set of attachment parts. Axiom uses
 * deferred parsing to process the message, i.e. the parts and their content are loaded on-demand.
 * <li>A programmatically created set of attachment parts. In that case, the root part is not
 * included.
 * </ul>
 * Since the behavior of the {@link Attachments} instance is fairly different in the two cases, this
 * should be considered a flaw in the API design. Unfortunately it is not possible to fix this
 * without breaking existing code. In particular, in Axis2 the {@link Attachments} API is heavily
 * used by application code. Therefore a delegation pattern is used so that internally these two
 * cases can be represented using distinct classes.
 * <p>
 * Note that this class is intentionally not public. It is for <b>internal use only</b>. However, in
 * a later Axiom version we may want to refactor this API to make it public, in which case
 * {@link Attachments} would simply become a legacy adapter.
 */
abstract class AttachmentsDelegate {
    abstract ContentType getContentType();
    abstract DataHandler getDataHandler(String contentID);
    abstract void addDataHandler(String contentID, DataHandler dataHandler);
    abstract void removeDataHandler(String blobContentID);
    abstract InputStream getRootPartInputStream(boolean preserve) throws OMException;
    abstract String getRootPartContentID();
    abstract String getRootPartContentType();
    abstract IncomingAttachmentStreams getIncomingAttachmentStreams();
    abstract Set<String> getContentIDs(boolean fetchAll);
    abstract Map<String,DataHandler> getMap();
    abstract long getContentLength() throws IOException;
    abstract MultipartBody getMultipartBody();
}
