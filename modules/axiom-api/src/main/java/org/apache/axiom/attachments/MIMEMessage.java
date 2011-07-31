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
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;

import org.apache.axiom.attachments.impl.PartFactory;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.attachments.lifecycle.impl.LifecycleManagerImpl;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.util.DetachableInputStream;
import org.apache.axiom.util.UIDGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class MIMEMessage extends AttachmentsImpl {
    private static final Log log = LogFactory.getLog(MIMEMessage.class);

    /** <code>ContentType</code> of the MIME message */
    private final ContentType contentType;
    
    private final int contentLength; // Content Length

    /** Mime <code>boundary</code> which separates mime parts */
    private final byte[] boundary;

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

    private String firstPartId;

    private final boolean fileCacheEnable;

    private final String attachmentRepoDir;

    private final int fileStorageThreshold;
    
    private LifecycleManager manager;
    
    MIMEMessage(LifecycleManager manager, InputStream inStream, String contentTypeString, boolean fileCacheEnable,
            String attachmentRepoDir, int fileStorageThreshold, int contentLength) throws OMException {
        this.manager = manager;
        this.contentLength = contentLength;
        this.attachmentRepoDir = attachmentRepoDir;
        this.fileCacheEnable = fileCacheEnable;
        if (log.isDebugEnabled()) {
            log.debug("Attachments contentLength=" + contentLength + ", contentTypeString=" + contentTypeString);
        }
        this.fileStorageThreshold = fileStorageThreshold;
        try {
            contentType = new ContentType(contentTypeString);
        } catch (ParseException e) {
            throw new OMException(
                    "Invalid Content Type Field in the Mime Message"
                    , e);
        }

        // Boundary always have the prefix "--".
        try {
            String boundaryParam = contentType.getParameter("boundary");
            if (boundaryParam == null) {
                throw new OMException("Content-type has no 'boundary' parameter");
            }
            // A MIME boundary only contains ASCII characters (see RFC2046)
            this.boundary = ("--" + boundaryParam).getBytes("ascii");
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

    ContentType getContentType() {
        return contentType;
    }

    LifecycleManager getLifecycleManager() {
        if(manager == null) {
            manager = new LifecycleManagerImpl();   
        }
        return manager;
    }

    void setLifecycleManager(LifecycleManager manager) {
        this.manager = manager;
    }

    DataHandler getDataHandler(String contentID) {
        do {
            DataHandler dataHandler = (DataHandler)attachmentsMap.get(contentID);
            if (dataHandler != null) {
                return dataHandler;
            }
        } while (getNextPartDataHandler() != null);
        return null;
    }

    void addDataHandler(String contentID, DataHandler dataHandler) {
        attachmentsMap.put(contentID, dataHandler);
    }
    
    void removeDataHandler(String blobContentID) {
        do {
            if (attachmentsMap.remove(blobContentID) != null) {
                return;
            }
        } while (getNextPartDataHandler() != null);
    }

    InputStream getSOAPPartInputStream() throws OMException {
        DataHandler dh;
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

    String getSOAPPartContentID() {
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
    
    String getSOAPPartContentType() {
        String soapPartContentID = getSOAPPartContentID();
        if (soapPartContentID == null) {
            throw new OMException("Unable to determine the content ID of the SOAP part");
        }
        DataHandler soapPart = getDataHandler(soapPartContentID);
        if (soapPart == null) {
            throw new OMException("Unable to locate the SOAP part; content ID was " + soapPartContentID);
        }
        return soapPart.getContentType();
    }
    
    IncomingAttachmentStreams getIncomingAttachmentStreams() {
        if (partsRequested) {
            throw new IllegalStateException(
                    "The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a " +
                            "collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
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
        while (getNextPartDataHandler() != null) {
            // Just loop until getNextPartDataHandler returns null
        }
    }

    Set getContentIDs(boolean fetchAll) {
        if (fetchAll) {
            fetchAllParts();
        }
        return attachmentsMap.keySet();
    }
    
    Map getMap() {
        fetchAllParts();
        return Collections.unmodifiableMap(attachmentsMap);
    }
    
    long getContentLength() throws IOException {
        if (contentLength > 0) {
            return contentLength;
        } else {
            // Ensure all parts are read
            fetchAllParts();
            // Now get the count from the filter
            return filterIS.length();
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
    
    InputStream getIncomingAttachmentsAsSingleStream() {
        if (partsRequested) {
            throw new IllegalStateException(
                    "The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a " +
                            "collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
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
