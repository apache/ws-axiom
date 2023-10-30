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

import org.apache.axiom.jakarta.attachments.lifecycle.DataHandlerExt;
import org.apache.axiom.jakarta.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.jakarta.attachments.lifecycle.impl.LifecycleManagerImpl;
import org.apache.axiom.blob.Blob;
import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.axiom.ext.activation.SizeAwareDataSource;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.MTOMConstants;
import org.apache.axiom.util.activation.DataHandlerUtils;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class Attachments implements OMAttachmentAccessor {
    private final AttachmentsDelegate delegate;
   
    /**
     * <code>applicationType</code> used to distinguish between MTOM & SWA If the message is MTOM
     * optimised type is application/xop+xml If the message is SWA, type is ??have to find out
     */
    private String applicationType;

    private LifecycleManager manager;
    
    public LifecycleManager getLifecycleManager() {
        if (manager == null) {
            manager = new LifecycleManagerImpl();
        }
        return manager;
    }

    public void setLifecycleManager(LifecycleManager manager) {
        this.manager = manager;
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
        this.manager = manager;
        final int fileStorageThreshold;
        if (fileThreshold != null && (!"".equals(fileThreshold))) {
            fileStorageThreshold = Integer.parseInt(fileThreshold);
        } else {
            fileStorageThreshold = 0;
        }
        WritableBlobFactory<?> attachmentBlobFactory;
        if (fileCacheEnable) {
            WritableBlobFactory<?> tempFileBlobFactory = new LegacyTempFileBlobFactory(this, attachmentRepoDir);
            if (fileStorageThreshold > 0) {
                attachmentBlobFactory = () -> Blobs.createOverflowableBlob(fileStorageThreshold, tempFileBlobFactory);
            } else {
                attachmentBlobFactory = tempFileBlobFactory;
            }
        } else {
            attachmentBlobFactory = MemoryBlob.FACTORY;
        }
        
        delegate = new MultipartBodyAdapter(inStream, contentTypeString, attachmentBlobFactory,
                contentLength);
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
        delegate = new AttachmentSet();
    }

    /**
     * Identify the type of message (MTOM or SOAP with attachments) represented by this object. Note
     * that this method is only meaningful if the instance was created from a stream.
     * 
     * @return One of the {@link MTOMConstants#MTOM_TYPE}, {@link MTOMConstants#SWA_TYPE} or
     *         {@link MTOMConstants#SWA_TYPE_12} constants.
     * @throws OMException
     *             if the message doesn't have one of the supported types (i.e. is neither MTOM nor
     *             SOAP with attachments) or if the instance was not created from a stream
     */
    public String getAttachmentSpecType() {
        if (this.applicationType == null) {
            ContentType contentType = delegate.getContentType();
            if (contentType == null) {
                throw new OMException("Unable to determine the attachment spec type because the " +
                		"Attachments object doesn't have a known content type");
            }
            applicationType = contentType.getParameter("type");
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
     * Get the {@link DataHandler} object for the MIME part with a given content ID. The returned
     * instance MAY implement {@link DataHandlerExt} in which case the caller can use that API to
     * stream the content of the part. In addition, the {@link DataSource} linked to the returned
     * {@link DataHandler} MAY be of type {@link SizeAwareDataSource} in which case the caller can
     * use that interface to determine the size of the MIME part.
     * 
     * @param contentID
     *            the raw content ID (without the surrounding angle brackets and {@code cid:}
     *            prefix) of the MIME part
     * @return the {@link DataHandler} of the MIME part referred by the content ID or
     *         <code>null</code> if the MIME part referred by the content ID does not exist
     */
    public DataHandler getDataHandler(String contentID) {
        return delegate.getDataHandler(contentID);
    }

    /**
     * Programatically adding an SOAP with Attachments(SwA) Attachment. These attachments will get
     * serialized only if SOAP with Attachments is enabled.
     *
     * @param contentID
     * @param dataHandler
     */
    public void addDataHandler(String contentID, DataHandler dataHandler) {
        delegate.addDataHandler(contentID, dataHandler);
    }

    /**
     * Removes the DataHandler corresponding to the given contenID. If it is not present, then
     * trying to find it calling the getNextPart() till the required part is found.
     *
     * @param blobContentID
     */
    public void removeDataHandler(String blobContentID) {
        delegate.removeDataHandler(blobContentID);
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
     * described in the documentation of the {@link #getRootPartContentID()} method. Note that a new
     * stream is returned each time this method is called, i.e. the method does not consume the root
     * part. Instead it loads the root part into memory so that it can be read several times.
     * 
     * @return the input stream for the root part
     */
    public InputStream getRootPartInputStream() throws OMException {
        return delegate.getRootPartInputStream(true);
    }

    /**
     * Get an input stream for the root part of the MIME message. This method is similar to
     * {@link #getRootPartInputStream()}, but can be instructed to consume the root part. This
     * allows streaming of the root part. If that feature is used, the root part will not be loaded
     * into memory unless an attempt is made to access another part of the MIME message, in which
     * case the remaining (i.e. unconsumed) content of the root part will be buffered. If the
     * feature is not enabled, then this method behaves in the same way as
     * {@link #getRootPartInputStream()}.
     * 
     * @param preserve
     *            <code>true</code> if the content of the root part should be fetched into memory so
     *            that it can be read several times, <code>false</code> if the root part should be
     *            consumed
     * @return the input stream for the root part
     */
    public InputStream getRootPartInputStream(boolean preserve) throws OMException {
        return delegate.getRootPartInputStream(preserve);
    }

    /**
     * Get the content ID of the root part of the MIME message. This content ID is determined as
     * follows:
     * <ul>
     * <li>If the content type of the MIME message has a {@code start} parameter, then the content
     * ID will be extracted from that parameter.
     * <li>Otherwise the content ID of the first MIME part of the MIME message is returned.
     * </ul>
     * 
     * @return the content ID of the root part (without the surrounding angle brackets)
     */
    public String getRootPartContentID() {
        return delegate.getRootPartContentID();
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
        return delegate.getRootPartContentType();
    }

    /**
     * Stream based access
     *
     * @return The stream container of type <code>IncomingAttachmentStreams</code>
     * @throws IllegalStateException if application has alreadt started using Part's directly
     */
    public IncomingAttachmentStreams getIncomingAttachmentStreams()
            throws IllegalStateException {
        return delegate.getIncomingAttachmentStreams();
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
        Set<String> cids = delegate.getContentIDs(true);
        return cids.toArray(new String[cids.size()]);
    }

    /**
     * Get the content IDs of all MIME parts in the message. This includes the content ID of the
     * SOAP part as well as the content IDs of the attachments. Note that if this object has been
     * created from a stream, a call to this method will force reading of all MIME parts that
     * have not been fetched from the stream yet.
     * 
     * @return the set of content IDs
     */
    public Set<String> getContentIDSet() {
        return delegate.getContentIDs(true);
    }
    
    /**
     * Get a map of all MIME parts in the message. This includes the SOAP part as well as the
     * attachments. Note that if this object has been created from a stream, a call to this
     * method will force reading of all MIME parts that have not been fetched from the stream yet.
     * 
     * @return A map of all MIME parts in the message, with content IDs as keys and
     *         {@link DataHandler} objects as values.
     */
    public Map<String,DataHandler> getMap() {
        return delegate.getMap();
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
    public List<String> getContentIDList() {
        return new ArrayList<String>(delegate.getContentIDs(false));
    }
    
    /**
     * If the Attachments is backed by an InputStream, then this
     * method returns the length of the message contents
     * (Length of the entire message - Length of the Transport Headers)
     * @return length of message content or -1 if Attachments is not
     * backed by an InputStream
     */
    public long getContentLength() throws IOException {
        return delegate.getContentLength();
    }

    /**
     * @deprecated As of Axiom 1.2.13, this method is no longer supported.
     */
    public InputStream getIncomingAttachmentsAsSingleStream() throws IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public MultipartBody getMultipartBody() {
        return delegate.getMultipartBody();
    }

    @Override
    public Blob getBlob(String contentID) {
        DataHandler dh = getDataHandler(contentID);
        return dh == null ? null : DataHandlerUtils.toBlob(dh);
    }
}
