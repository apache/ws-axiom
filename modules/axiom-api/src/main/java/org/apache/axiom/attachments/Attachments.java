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

import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.MTOMConstants;

import javax.activation.DataHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class Attachments implements OMAttachmentAccessor {
    private final AttachmentsImpl impl;
   
    /**
     * <code>applicationType</code> used to distinguish between MTOM & SWA If the message is MTOM
     * optimised type is application/xop+xml If the message is SWA, type is ??have to find out
     */
    private String applicationType;

    public LifecycleManager getLifecycleManager() {
        return impl.getLifecycleManager();
    }

    public void setLifecycleManager(LifecycleManager manager) {
        impl.setLifecycleManager(manager);
    }

    /**
     * Moves the pointer to the beginning of the first MIME part. Reads till first MIME boundary is
     * found or end of stream is reached.
     *
     * @param inStream
     * @param contentTypeString
     * @param fileCacheEnable
     * @param attachmentRepoDir
     * @throws OMException
     */
    public Attachments(LifecycleManager manager, InputStream inStream, String contentTypeString,
                       boolean fileCacheEnable, String attachmentRepoDir,
                       String fileThreshold) throws OMException {
        this(manager, inStream, contentTypeString, fileCacheEnable, attachmentRepoDir, fileThreshold, 0);
    }
        
        /**
     * Moves the pointer to the beginning of the first MIME part. Reads
     * till first MIME boundary is found or end of stream is reached.
     *
     * @param inStream
     * @param contentTypeString
     * @param fileCacheEnable
     * @param attachmentRepoDir
     * @param fileThreshold
     * @param contentLength
     * @throws OMException
     */
    public Attachments(LifecycleManager manager, InputStream inStream, String contentTypeString, boolean fileCacheEnable,
            String attachmentRepoDir, String fileThreshold, int contentLength) throws OMException {
        int fileStorageThreshold;
        if (fileThreshold != null && (!"".equals(fileThreshold))) {
            fileStorageThreshold = Integer.parseInt(fileThreshold);
        } else {
            fileStorageThreshold = 1;
        }
        impl = new MIMEMessage(manager, inStream, contentTypeString, fileCacheEnable,
                attachmentRepoDir, fileStorageThreshold, contentLength);
    }

    /**
     * Moves the pointer to the beginning of the first MIME part. Reads till first MIME boundary is
     * found or end of stream is reached.
     *
     * @param inStream
     * @param contentTypeString
     * @param fileCacheEnable
     * @param attachmentRepoDir
     * @throws OMException
     */
    public Attachments(InputStream inStream, String contentTypeString,
                       boolean fileCacheEnable, String attachmentRepoDir,
                       String fileThreshold) throws OMException {
        this(null, inStream, contentTypeString, fileCacheEnable, attachmentRepoDir, fileThreshold, 0);
    }
        
        /**
     * Moves the pointer to the beginning of the first MIME part. Reads
     * till first MIME boundary is found or end of stream is reached.
     *
     * @param inStream
     * @param contentTypeString
     * @param fileCacheEnable
     * @param attachmentRepoDir
     * @param fileThreshold
     * @param contentLength
     * @throws OMException
     */
    public Attachments(InputStream inStream, String contentTypeString, boolean fileCacheEnable,
            String attachmentRepoDir, String fileThreshold, int contentLength) throws OMException {
            this(null, inStream, contentTypeString, fileCacheEnable,
            attachmentRepoDir, fileThreshold, contentLength);
    }
    /**
     * Sets file cache to false.
     *
     * @param inStream
     * @param contentTypeString
     * @throws OMException
     */
    public Attachments(InputStream inStream, String contentTypeString)
            throws OMException {
        this(null, inStream, contentTypeString, false, null, null);
    }

    /**
     * Use this constructor when instantiating this to store the attachments set programatically
     * through the SwA API.
     */
    public Attachments() {
        impl = new AttachmentSet();
    }

    /**
     * Identify the type of message (MTOM or SOAP with attachments) represented by this
     * object.
     * 
     * @return One of the {@link MTOMConstants#MTOM_TYPE}, {@link MTOMConstants#SWA_TYPE}
     *         or {@link MTOMConstants#SWA_TYPE_12} constants.
     * @throws OMException if the message doesn't have one of the supported types, i.e. is
     *         neither MTOM nor SOAP with attachments
     */
    public String getAttachmentSpecType() {
        if (this.applicationType == null) {
            applicationType = impl.getContentType().getParameter("type");
            if ((MTOMConstants.MTOM_TYPE).equalsIgnoreCase(applicationType)) {
                this.applicationType = MTOMConstants.MTOM_TYPE;
            } else if ((MTOMConstants.SWA_TYPE).equalsIgnoreCase(applicationType)) {
                this.applicationType = MTOMConstants.SWA_TYPE;
            } else if ((MTOMConstants.SWA_TYPE_12).equalsIgnoreCase(applicationType)) {
                this.applicationType = MTOMConstants.SWA_TYPE_12;
            } else {
                throw new OMException(
                        "Invalid Application type. Support available for MTOM & SwA only.");
            }
        }
        return this.applicationType;
    }

    /**
     * Get the {@link DataHandler} object for the MIME part with a given content ID.
     * 
     * @param contentID
     *            the raw content ID (without the surrounding angle brackets and <tt>cid:</tt>
     *            prefix) of the MIME part
     * @return the {@link DataHandler} of the MIME part referred by the content ID or
     *         <code>null</code> if the MIME part referred by the content ID does not exist
     */
    public DataHandler getDataHandler(String contentID) {
        return impl.getDataHandler(contentID);
    }

    /**
     * Programatically adding an SOAP with Attachments(SwA) Attachment. These attachments will get
     * serialized only if SOAP with Attachments is enabled.
     *
     * @param contentID
     * @param dataHandler
     */
    public void addDataHandler(String contentID, DataHandler dataHandler) {
        impl.addDataHandler(contentID, dataHandler);
    }

    /**
     * Removes the DataHandler corresponding to the given contenID. If it is not present, then
     * trying to find it calling the getNextPart() till the required part is found.
     *
     * @param blobContentID
     */
    public void removeDataHandler(String blobContentID) {
        impl.removeDataHandler(blobContentID);
    }

    /**
     *  @deprecated Use {@link #getRootPartInputStream()} instead.
     */
    public InputStream getSOAPPartInputStream() throws OMException {
        return getRootPartInputStream();
    }
    
    /**
     * @deprecated Use {@link #getRootPartContentID()} instead.
     */
    public String getSOAPPartContentID() {
        return getRootPartContentID();
    }
    
    /**
     * @deprecated Use {@link #getRootPartContentType()} instead.
     */
    public String getSOAPPartContentType() {
        return getRootPartContentType();
    }
    
    /**
     * Get an input stream for the root part of the MIME message. The root part is located as
     * described in the documentation of the {@link #getRootPartContentID()} method.
     * 
     * @return the input stream for the root part
     */
    public InputStream getRootPartInputStream() throws OMException {
        return impl.getRootPartInputStream();
    }

    /**
     * Get the content ID of the root part of the MIME message. This content ID is determined as
     * follows:
     * <ul>
     * <li>If the content type of the MIME message has a <tt>start</tt> parameter, then the content
     * ID will be extracted from that parameter.
     * <li>Otherwise the content ID of the first MIME part of the MIME message is returned.
     * </ul>
     * 
     * @return the content ID of the root part (without the surrounding angle brackets)
     */
    public String getRootPartContentID() {
        return impl.getRootPartContentID();
    }

    /**
     * Get the content type of the root part of the MIME message. The root part is located as
     * described in the documentation of the {@link #getRootPartContentID()} method.
     * 
     * @return the content type of the root part
     * @throws OMException
     *             if the content type could not be determined
     */
    public String getRootPartContentType() {
        return impl.getRootPartContentType();
    }

    /**
     * Stream based access
     *
     * @return The stream container of type <code>IncomingAttachmentStreams</code>
     * @throws IllegalStateException if application has alreadt started using Part's directly
     */
    public IncomingAttachmentStreams getIncomingAttachmentStreams()
            throws IllegalStateException {
        return impl.getIncomingAttachmentStreams();
    }

    /**
     * Get the content IDs of all MIME parts in the message. This includes the content ID of the
     * SOAP part as well as the content IDs of the attachments. Note that if this object has been
     * created from a stream, a call to this method will force reading of all MIME parts that
     * have not been fetched from the stream yet.
     * 
     * @return an array with the content IDs in order of appearance in the message
     */
    public String[] getAllContentIDs() {
        Set cids = impl.getContentIDs(true);
        return (String[]) cids.toArray(new String[cids.size()]);
    }

    /**
     * Get the content IDs of all MIME parts in the message. This includes the content ID of the
     * SOAP part as well as the content IDs of the attachments. Note that if this object has been
     * created from a stream, a call to this method will force reading of all MIME parts that
     * have not been fetched from the stream yet.
     * 
     * @return the set of content IDs
     */
    public Set getContentIDSet() {
        return impl.getContentIDs(true);
    }
    
    /**
     * Get a map of all MIME parts in the message. This includes the SOAP part as well as the
     * attachments. Note that if this object has been created from a stream, a call to this
     * method will force reading of all MIME parts that have not been fetched from the stream yet.
     * 
     * @return A map of all MIME parts in the message, with content IDs as keys and
     *         {@link DataHandler} objects as values.
     */
    public Map getMap() {
        return impl.getMap();
    }

    /**
     * Get the content IDs of the already loaded MIME parts in the message. This includes the
     * content ID of the SOAP part as well as the content IDs of the attachments. If this
     * object has been created from a stream, only the content IDs of the MIME parts that
     * have already been fetched from the stream are returned. If this is not the desired
     * behavior, {@link #getAllContentIDs()} or {@link #getContentIDSet()} should be used
     * instead.
     * 
     * @return List of content IDs in order of appearance in message
     */
    public List getContentIDList() {
        return new ArrayList(impl.getContentIDs(false));
    }
    
    /**
     * If the Attachments is backed by an InputStream, then this
     * method returns the length of the message contents
     * (Length of the entire message - Length of the Transport Headers)
     * @return length of message content or -1 if Attachments is not
     * backed by an InputStream
     */
    public long getContentLength() throws IOException {
        return impl.getContentLength();
    }

    /**
     * @deprecated As of Axiom 1.2.13, this method is no longer supported.
     */
    public InputStream getIncomingAttachmentsAsSingleStream() throws IllegalStateException {
        throw new UnsupportedOperationException();
    }
}