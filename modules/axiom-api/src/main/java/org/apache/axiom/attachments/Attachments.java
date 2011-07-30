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

import org.apache.axiom.attachments.impl.PartFactory;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.attachments.lifecycle.impl.LifecycleManagerImpl;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.MTOMConstants;
import org.apache.axiom.om.util.DetachableInputStream;
import org.apache.axiom.util.UIDGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Collections;

public class Attachments implements OMAttachmentAccessor {

    /** <code>ContentType</code> of the MIME message */
    private final ContentType contentType;
    
    private final int contentLength; // Content Length

    /** Mime <code>boundary</code> which separates mime parts */
    private final byte[] boundary;

    /**
     * <code>applicationType</code> used to distinguish between MTOM & SWA If the message is MTOM
     * optimised type is application/xop+xml If the message is SWA, type is ??have to find out
     */
    private String applicationType;

    /**
     * <code>pushbackInStream</code> stores the reference to the incoming stream A PushbackStream
     * has the ability to "push back" or "unread" one byte.
     */
    private final PushbackInputStream pushbackInStream;
    private static final int PUSHBACK_SIZE = 4 * 1024;
    private final DetachableInputStream filterIS;

    /**
     * Stores the Data Handlers of the already parsed Mime Body Parts in the order that the attachments
     * occur in the message. This map is keyed using the content-ID's.
     */
    private final Map attachmentsMap = new LinkedHashMap();

    /** <code>partIndex</code>- Number of Mime parts parsed */
    private int partIndex = 0;

    /** Container to hold streams for direct access */
    private IncomingAttachmentStreams streams;

    /** <code>boolean</code> Indicating if any streams have been directly requested */
    private boolean streamsRequested;

    /** <code>boolean</code> Indicating if any data handlers have been directly requested */
    private boolean partsRequested;

    /**
     * <code>endOfStreamReached</code> flag which is to be set by MIMEBodyPartStream when MIME
     * message terminator is found.
     */
    private boolean endOfStreamReached;


    /**
     * <code>noStreams</code> flag which is to be set when this class is instantiated by the SwA API
     * to handle programatic added attachements. An InputStream with attachments is not present at
     * that occation.
     */
    private final boolean noStreams;

    private String firstPartId;

    private final boolean fileCacheEnable;

    private final String attachmentRepoDir;

    private final int fileStorageThreshold;
    
    private LifecycleManager manager;
    
    private static final Log log = LogFactory.getLog(Attachments.class);
   
    public LifecycleManager getLifecycleManager() {
        if(manager == null) {
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
        this.contentLength = contentLength;
        this.attachmentRepoDir = attachmentRepoDir;
        this.fileCacheEnable = fileCacheEnable;
        noStreams = false;
        if (log.isDebugEnabled()) {
            log.debug("Attachments contentLength=" + contentLength + ", contentTypeString=" + contentTypeString);
        }
        if (fileThreshold != null && (!"".equals(fileThreshold))) {
            this.fileStorageThreshold = Integer.parseInt(fileThreshold);
        } else {
            this.fileStorageThreshold = 1;
        }
        try {
            contentType = new ContentType(contentTypeString);
        } catch (ParseException e) {
            throw new OMException(
                    "Invalid Content Type Field in the Mime Message"
                    , e);
        }
        // REVIEW: This conversion is hard-coded to UTF-8.
        // The complete solution is to respect the charset setting of the message.
        // However this may cause problems in BoundaryDelimittedStream and other
        // lower level classes.

        // Boundary always have the prefix "--".
        try {
            String encoding = contentType.getParameter("charset");
            if(encoding == null || encoding.length()==0){
                encoding = "UTF-8";
            }
            String boundaryParam = contentType.getParameter("boundary");
            if (boundaryParam == null) {
                throw new OMException("Content-type has no 'boundary' parameter");
            }
            this.boundary = ("--" + boundaryParam).getBytes(encoding);
            if (log.isDebugEnabled()) {
                log.debug("boundary=" + new String(this.boundary));
            }
        } catch (UnsupportedEncodingException e) {
            throw new OMException(e);
        }

        // If the length is not known, install a TeeInputStream
        // so that we can retrieve it later.
        InputStream is = inStream;
        if (contentLength <= 0) {
            filterIS = new DetachableInputStream(inStream);
            is = filterIS;
        } else {
            filterIS = null;
        }
        pushbackInStream = new PushbackInputStream(is,
                                                   PUSHBACK_SIZE);

        // Move the read pointer to the beginning of the first part
        // read till the end of first boundary
        while (true) {
            int value;
            try {
                value = pushbackInStream.read();
                if ((byte) value == boundary[0]) {
                    int boundaryIndex = 0;
                    while ((boundaryIndex < boundary.length)
                            && ((byte) value == boundary[boundaryIndex])) {
                        value = pushbackInStream.read();
                        if (value == -1) {
                            throw new OMException(
                                    "Unexpected End of Stream while searching for first Mime Boundary");
                        }
                        boundaryIndex++;
                    }
                    if (boundaryIndex == boundary.length) { // boundary found
                        pushbackInStream.read();
                        break;
                    }
                } else if (value == -1) {
                    throw new OMException(
                            "Mime parts not found. Stream ended while searching for the boundary");
                }
            } catch (IOException e1) {
                throw new OMException("Stream Error" + e1.toString(), e1);
            }
        }

        // Read the SOAP part and cache it
        getDataHandler(getSOAPPartContentID());

        // Now reset partsRequested. SOAP part is a special case which is always 
        // read beforehand, regardless of request.
        partsRequested = false;
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
        noStreams = true;
        contentType = null;
        contentLength = 0;
        boundary = null;
        pushbackInStream = null;
        filterIS = null;
        fileCacheEnable = false;
        attachmentRepoDir = null;
        fileStorageThreshold = 0;
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
     * Get the {@link DataHandler} object for the MIME part with a given content ID.
     * 
     * @param contentID
     *            the raw content ID (without the surrounding angle brackets and <tt>cid:</tt>
     *            prefix) of the MIME part
     * @return the {@link DataHandler} of the MIME part referred by the content ID or
     *         <code>null</code> if the MIME part referred by the content ID does not exist
     */
    public DataHandler getDataHandler(String contentID) {
        // Check whether the MIME part is already parsed by checking the attachments HashMap. If it is
        // not parsed yet then call the getNextPart() till the required part is found.
        DataHandler dataHandler;
        if (attachmentsMap.containsKey(contentID)) {
            dataHandler = (DataHandler) attachmentsMap.get(contentID);
            return dataHandler;
        } else if (!noStreams) {
            //This loop will be terminated by the Exceptions thrown if the Mime
            // part searching was not found
            while ((dataHandler = this.getNextPartDataHandler()) != null) {
                if (attachmentsMap.containsKey(contentID)) {
                    dataHandler = (DataHandler) attachmentsMap.get(contentID);
                    return dataHandler;
                }
            }
        }
        return null;
    }

    /**
     * Programatically adding an SOAP with Attachments(SwA) Attachment. These attachments will get
     * serialized only if SOAP with Attachments is enabled.
     *
     * @param contentID
     * @param dataHandler
     */
    public void addDataHandler(String contentID, DataHandler dataHandler) {
        attachmentsMap.put(contentID, dataHandler);
    }

    /**
     * Removes the DataHandler corresponding to the given contenID. If it is not present, then
     * trying to find it calling the getNextPart() till the required part is found.
     *
     * @param blobContentID
     */
    public void removeDataHandler(String blobContentID) {
        if (attachmentsMap.containsKey(blobContentID)) {
            attachmentsMap.remove(blobContentID);
        } else if (!noStreams) {
            //This loop will be terminated by the Exceptions thrown if the Mime
            // part searching was not found
            while (this.getNextPartDataHandler() != null) {
                if (attachmentsMap.containsKey(blobContentID)) {
                    attachmentsMap.remove(blobContentID);
                }
            }
        }
    }

    /**
     * @return the InputStream which includes the SOAP Envelope. It assumes that the root mime part
     *         is always pointed by "start" parameter in content-type.
     */
    public InputStream getSOAPPartInputStream() throws OMException {
        DataHandler dh;
        if (noStreams) {
            throw new OMException("Invalid operation. Attachments are created programatically.");
        }
        try {
            dh = getDataHandler(getSOAPPartContentID());
            if (dh == null) {
                throw new OMException(
                        "Mandatory Root MIME part containing the SOAP Envelope is missing");
            }
            return dh.getInputStream();
        } catch (IOException e) {
            throw new OMException(
                    "Problem with DataHandler of the Root Mime Part. ", e);
        }
    }

    /**
     * Get the content ID of the SOAP part or the MIME message. This content ID is determined as
     * follows:
     * <ul>
     * <li>If the content type of the MIME message has a <tt>start</tt> parameter, then the content
     * ID will be extracted from that parameter.
     * <li>Otherwise the content ID of the first MIME part of the MIME message is returned.
     * </ul>
     * 
     * @return the content ID of the SOAP part (without the surrounding angle brackets)
     */
    public String getSOAPPartContentID() {
        if(contentType == null) {
            return null;
        }
        String rootContentID = contentType.getParameter("start");
        if (log.isDebugEnabled()) {
            log.debug("getSOAPPartContentID rootContentID=" + rootContentID);
        }

        // to handle the Start parameter not mentioned situation
        if (rootContentID == null) {
            if (partIndex == 0) {
                getNextPartDataHandler();
            }
            rootContentID = firstPartId;
        } else {
            rootContentID = rootContentID.trim();

            if ((rootContentID.indexOf("<") > -1)
                    & (rootContentID.indexOf(">") > -1)) {
                rootContentID = rootContentID.substring(1, (rootContentID
                        .length() - 1));
            }
        }
        // Strips off the "cid:" part from content-id
        if (rootContentID.length() > 4
                && "cid:".equalsIgnoreCase(rootContentID.substring(0, 4))) {
            rootContentID = rootContentID.substring(4);
        }
        return rootContentID;
    }

    /**
     * Get the content type of the SOAP part of the MIME message.
     * 
     * @return the content type of the SOAP part
     * @throws OMException
     *             if the content type could not be determined
     */
    public String getSOAPPartContentType() {
        if (!noStreams) {
            String soapPartContentID = getSOAPPartContentID();
            if (soapPartContentID == null) {
                throw new OMException("Unable to determine the content ID of the SOAP part");
            }
            DataHandler soapPart = getDataHandler(soapPartContentID);
            if (soapPart == null) {
                throw new OMException("Unable to locate the SOAP part; content ID was " + soapPartContentID);
            }
            return soapPart.getContentType();
        } else {
            throw new OMException(
                    "The attachments map was created programatically. Unsupported operation.");
        }
    }

    /**
     * Stream based access
     *
     * @return The stream container of type <code>IncomingAttachmentStreams</code>
     * @throws IllegalStateException if application has alreadt started using Part's directly
     */
    public IncomingAttachmentStreams getIncomingAttachmentStreams()
            throws IllegalStateException {
        if (partsRequested) {
            throw new IllegalStateException(
                    "The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a " +
                            "collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
        }
        if (noStreams) {
            throw new IllegalStateException(
                    "The attachments map was created programatically. No streams are available.");
        }

        streamsRequested = true;

        if (this.streams == null) {
            BoundaryDelimitedStream boundaryDelimitedStream =
                    new BoundaryDelimitedStream(pushbackInStream,
                                                boundary, 1024);

            this.streams = new MultipartAttachmentStreams(boundaryDelimitedStream);
        }

        return this.streams;
    }

    /**
     * Force reading of all attachments.
     */
    private void fetchAllParts() {
        if (!noStreams) {
            while (getNextPartDataHandler() != null) {
                // Just loop until getNextPartDataHandler returns null
            }
        }
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
        fetchAllParts();
        Set cids = attachmentsMap.keySet();
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
        fetchAllParts();
        return attachmentsMap.keySet();
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
        fetchAllParts();
        return Collections.unmodifiableMap(attachmentsMap);
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
        return new ArrayList(attachmentsMap.keySet());
    }
    
    /**
     * If the Attachments is backed by an InputStream, then this
     * method returns the length of the message contents
     * (Length of the entire message - Length of the Transport Headers)
     * @return length of message content or -1 if Attachments is not
     * backed by an InputStream
     */
    public long getContentLength() throws IOException {
        if (contentLength > 0) {
            return contentLength;
        } else if (filterIS != null) {
            // Ensure all parts are read
            this.getContentIDSet();
            // Now get the count from the filter
            return filterIS.length();
        } else {
            return -1; // not backed by an input stream
        }
    }
    
    /**
     * endOfStreamReached will be set to true if the message ended in MIME Style having "--" suffix
     * with the last mime boundary
     *
     * @param value
     */
    void setEndOfStream(boolean value) {
        this.endOfStreamReached = value;
    }

    /**
     * Returns the rest of mime stream. It will contain all attachments without
     * soappart (first attachment) with headers and mime boundary. Raw content! 
     */
    public InputStream getIncomingAttachmentsAsSingleStream() throws IllegalStateException {
        if (partsRequested) {
            throw new IllegalStateException(
                    "The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a " +
                            "collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
        }
        if (noStreams) {
            throw new IllegalStateException(
                    "The attachments map was created programatically. No streams are available.");
        }

        streamsRequested = true;

    	return this.pushbackInStream;
    }

    /**
     * @return the Next valid MIME part + store the Part in the Parts List
     * @throws OMException throw if content id is null or if two MIME parts contain the same
     *                     content-ID & the exceptions throws by getPart()
     */
    private DataHandler getNextPartDataHandler() throws OMException {
        if (endOfStreamReached) {
            return null;
        } else {
            Part nextPart = getPart();
            try {
                long size = nextPart.getSize();
                String partContentID;
                DataHandler dataHandler;
                try {
                    partContentID = nextPart.getContentID();

                    if (partContentID == null & partIndex == 1) {
                        String id = "firstPart_" + UIDGenerator.generateContentId();
                        firstPartId = id;
                        if (size > 0) {
                            dataHandler = nextPart.getDataHandler();
                        } else {
                            // Either the mime part is empty or the stream ended without having 
                            // a MIME message terminator
                            dataHandler = new DataHandler(new ByteArrayDataSource(new byte[]{}));
                        }
                        addDataHandler(id, dataHandler);
                        return dataHandler;
                    }
                    if (partContentID == null) {
                        throw new OMException(
                                "Part content ID cannot be blank for non root MIME parts");
                    }
                    if ((partContentID.indexOf("<") > -1)
                            & (partContentID.indexOf(">") > -1)) {
                        partContentID = partContentID.substring(1, (partContentID
                                .length() - 1));

                    }
                    if (partIndex == 1) {
                        firstPartId = partContentID;
                    }
                    if (attachmentsMap.containsKey(partContentID)) {
                        throw new OMException(
                                "Two MIME parts with the same Content-ID not allowed.");
                    }
                    if (size > 0) {
                        dataHandler = nextPart.getDataHandler();
                    } else {
                        // Either the mime part is empty or the stream ended without having 
                        // a MIME message terminator
                        dataHandler = new DataHandler(new ByteArrayDataSource(new byte[]{}));
                    }
                    addDataHandler(partContentID, dataHandler);
                    return dataHandler;
                } catch (MessagingException e) {
                    throw new OMException("Error reading Content-ID from the Part."
                            + e);
                }
            } catch (MessagingException e) {
                throw new OMException(e);
            }
        }
    }

    /**
     * @return This will return the next available MIME part in the stream.
     * @throws OMException if Stream ends while reading the next part...
     */
    private Part getPart() throws OMException {

        if (streamsRequested) {
            throw new IllegalStateException("The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
        }

        partsRequested = true;

        boolean isSOAPPart = (partIndex == 0);
        int threshhold = (fileCacheEnable) ? fileStorageThreshold : 0;

        // Create a MIMEBodyPartInputStream that simulates a single stream for this MIME body part
        MIMEBodyPartInputStream partStream =
            new MIMEBodyPartInputStream(pushbackInStream,
                                        boundary,
                                        this,
                                        PUSHBACK_SIZE);

        // The PartFactory will determine which Part implementation is most appropriate.
        Part part = PartFactory.createPart(getLifecycleManager(), partStream, 
                                      isSOAPPart, 
                                      threshhold, 
                                      attachmentRepoDir, 
                                      contentLength);  // content-length for the whole message
        partIndex++;
        return part;
    }
}