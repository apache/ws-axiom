/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.attachments;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Set;
import java.util.TreeMap;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.MTOMConstants;
import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Attachments {

    /**
     * <code>ContentType</code> of the MIME message
     */
    ContentType contentType;

    /**
     * Mime <code>boundary</code> which separates mime parts
     */
    byte[] boundary;

    /**
     * <code>applicationType</code> used to distinguish between MTOM & SWA If
     * the message is MTOM optimised type is application/xop+xml If the message
     * is SWA, type is ??have to find out
     */
    String applicationType;

    /**
     * <code>pushbackInStream</code> stores the reference to the incoming
     * stream A PushbackStream has the ability to "push back" or "unread" one
     * byte.
     */
    PushbackInputStream pushbackInStream;

    /**
     * <code>attachmentsMap</code> stores the Data Handlers of the already parsed Mime Body
     * Parts. This ordered Map is keyed using the content-ID's. 
     */
    TreeMap attachmentsMap;
    
    /**
     * <code>partIndex</code>- Number of Mime parts parsed
     */
    int partIndex = 0;
    
    /** Container to hold streams for direct access */
    IncomingAttachmentStreams streams = null;
    
    /** <code>boolean</code> Indicating if any streams have been directly requested */
    private boolean streamsRequested = false;
    
    /** <code>boolean</code> Indicating if any data handlers have been directly requested */
    private boolean partsRequested = false;

    /**
     * <code>endOfStreamReached</code> flag which is to be set by
     * MIMEBodyPartStream when MIME message terminator is found.
     */
    private boolean endOfStreamReached;
    
    
    /**
	 * <code>noStreams</code> flag which is to be set when this class is
	 * instantiated by the SwA API to handle programatic added attachements. An
	 * InputStream with attachments is not present at that occation.
	 */
    private boolean noStreams = false;

    private String firstPartId;

    private boolean fileCacheEnable;

    private String attachmentRepoDir;

    private int fileStorageThreshold;

    protected Log log = LogFactory.getLog(getClass());


    /**
     * Moves the pointer to the beginning of the first MIME part. Reads
     * till first MIME boundary is found or end of stream is reached.
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
        this.attachmentRepoDir = attachmentRepoDir;
        this.fileCacheEnable = fileCacheEnable;
        if (fileThreshold != null && (!"".equals(fileThreshold))) {
            this.fileStorageThreshold = Integer.parseInt(fileThreshold);
        } else {
            this.fileStorageThreshold = 1;
        }
        attachmentsMap = new TreeMap();
        try {
            contentType = new ContentType(contentTypeString);
        } catch (ParseException e) {
            throw new OMException(
                    "Invalid Content Type Field in the Mime Message"
                    , e);
        }
        // Boundary always have the prefix "--".
        this.boundary = ("--" + contentType.getParameter("boundary"))
                .getBytes();

        // do we need to wrap InputStream from a BufferedInputStream before
        // wrapping from PushbackStream
        pushbackInStream = new PushbackInputStream(inStream,
                                                   (this.boundary.length + 2));

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
                } else if ((byte) value == -1) {
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
     * Sets file cache to false.
     *
     * @param inStream
     * @param contentTypeString
     * @throws OMException
     */
    public Attachments(InputStream inStream, String contentTypeString)
            throws OMException {
        this(inStream, contentTypeString, false, null, null);
    }
    
    /**
     * Use this constructor when instantiating this to store the attachments set programatically through the SwA API.
     */
    public Attachments()
    {
    	attachmentsMap = new TreeMap();
    	noStreams= true;
    }

    /**
     * @return whether Message Type is SOAP with Attachments or MTOM optimized,
     *         by checking the application type parameter in the Content Type.
     */
    public String getAttachmentSpecType() {
        if (this.applicationType == null) {
            applicationType = contentType.getParameter("type");
            if ((MTOMConstants.MTOM_TYPE).equalsIgnoreCase(applicationType)) {
                this.applicationType = MTOMConstants.MTOM_TYPE;
            } else if ((MTOMConstants.SWA_TYPE).equalsIgnoreCase(applicationType)) {
                this.applicationType = MTOMConstants.SWA_TYPE;
            } else {
                throw new OMException(
                        "Invalid Application type. Support available for MTOM & SwA/SOAP 1.l only.");
            }
        }
        return this.applicationType;
    }
    
    /**
     * Checks whether the MIME part is already parsed by checking the
     * attachments HashMap. If it is not parsed yet then call the getNextPart()
     * till the required part is found.
     * 
     * @param blobContentID (without the surrounding angle brackets and "cid:" prefix)
     * @return The DataHandler of the mime part referred by the Content-Id or 
     *   *null* if the mime part referred by the content-id does not exist
     */
    public DataHandler getDataHandler(String blobContentID) {
        DataHandler dataHandler;
        if (attachmentsMap.containsKey(blobContentID)) {
            dataHandler = (DataHandler) attachmentsMap.get(blobContentID);
            return dataHandler;
        } else if (!noStreams){
            //This loop will be terminated by the Exceptions thrown if the Mime
            // part searching was not found
            while ((dataHandler = this.getNextPartDataHandler())!=null) {
                if (attachmentsMap.containsKey(blobContentID)) {
                    dataHandler = (DataHandler) attachmentsMap.get(blobContentID);
                    return dataHandler;
                }
            }
        }
        return null;
    }
    
    /**
	 * Programatically adding an SOAP with Attachments(SwA) Attachment. These
	 * attachments will get serialized only if SOAP with Attachments is enabled.
	 * 
	 * @param contentID
	 * @param dataHandler
	 */
    public void addDataHandler(String contentID, DataHandler dataHandler)
    {
    	attachmentsMap.put(contentID,dataHandler);
    }

    /**
     * @return the InputStream which includes the SOAP Envelope. It assumes that
     *         the root mime part is always pointed by "start" parameter in
     *         content-type.
     */
    public InputStream getSOAPPartInputStream() throws OMException {
        DataHandler dh;
        if (noStreams)
        {
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
     * @return the Content-ID of the SOAP part It'll be the value Start
     *         Parameter of Content-Type header if given in the Content type of
     *         the MIME message. Else it'll be the content-id of the first MIME
     *         part of the MIME message
     */
    private String getSOAPPartContentID() {
        String rootContentID = contentType.getParameter("start");

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
        // Strips off the "cid" part from content-id
        if ("cid".equalsIgnoreCase(rootContentID.substring(0, 3))) {
            rootContentID = rootContentID.substring(4);
        }
        return rootContentID;
    }

    public String getSOAPPartContentType() {
    	if (!noStreams) {
			DataHandler soapPart = getDataHandler(getSOAPPartContentID());
			return soapPart.getContentType();
		}else
		{
			throw new OMException("The attachments map was created programatically. Unsupported operation.");
		}
    }

    /**
	 * Stream based access
	 * 
	 * @return The stream container of type
	 *         <code>IncomingAttachmentStreams</code>
	 * @throws IllegalStateException
	 *             if application has alreadt started using Part's directly
	 */
    public IncomingAttachmentStreams getIncomingAttachmentStreams() throws IllegalStateException {
    	if (partsRequested) {
    		throw new IllegalStateException("The attachments stream can only be accessed once; either by using the IncomingAttachmentStreams class or by getting a collection of AttachmentPart objects. They cannot both be called within the life time of the same service request.");
    	}
    	if (noStreams)
    	{
    		throw new IllegalStateException("The attachments map was created programatically. No streams are available.");
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

    public String[] getAllContentIDs() {
        Set keys = getContentIDSet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }
    
    public Set getContentIDSet() {
        DataHandler dataHandler;
        while (!noStreams & true) {
            dataHandler = this.getNextPartDataHandler();
            if (dataHandler == null) {
                break;
            }
        }
        return attachmentsMap.keySet();
    }

    /**
	 * endOfStreamReached will be set to true if the message ended in MIME Style
	 * having "--" suffix with the last mime boundary
	 * 
	 * @param value
	 */
    protected void setEndOfStream(boolean value) {
        this.endOfStreamReached = value;
    }

    /**
     * @return the Next valid MIME part + store the Part in the Parts List
     * @throws OMException throw if content id is null or if two MIME parts contain the
     *                     same content-ID & the exceptions throws by getPart()
     */
    private DataHandler getNextPartDataHandler() throws OMException {
    	if (endOfStreamReached)
    	{
    		return null;
    	}
        Part nextPart;
        nextPart = getPart();
        if (nextPart==null)
        {
        	return null;
        } else
			try {
				if (nextPart.getSize()>0) {
				    String partContentID;
				    try {
				        partContentID = nextPart.getContentID();

				        if (partContentID == null & partIndex == 1) {
				        	String id = "firstPart_"+UUIDGenerator.getUUID();
				            attachmentsMap.put(id, nextPart.getDataHandler());
				            firstPartId = id;
				            return nextPart.getDataHandler();
				        }
				        if (partContentID == null) {
				            throw new OMException(
				                    "Part content ID cannot be blank for non root MIME parts");
				        }
				        if ((partContentID.indexOf("<") > -1)
				            & (partContentID.indexOf(">") > -1)) {
				            partContentID = partContentID.substring(1, (partContentID
				                    .length() - 1));

				        } else if (partIndex == 1) {
				            firstPartId = partContentID;
				        }
				        if (attachmentsMap.containsKey(partContentID)) {
				            throw new OMException(
				                    "Two MIME parts with the same Content-ID not allowed.");
				        }
				        attachmentsMap.put(partContentID, nextPart.getDataHandler());
				        return nextPart.getDataHandler();
				    } catch (MessagingException e) {
				        throw new OMException("Error reading Content-ID from the Part."
				                              + e);
				    }
				} // This will take care if stream ended without having MIME
				// message terminator
				else {
					return null;
				}
			} catch (MessagingException e) {
				throw new OMException(e);
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

        Part part;

        try {
            if (fileCacheEnable) {
                try {
                    MIMEBodyPartInputStream partStream;
                    byte[] buffer = new byte[fileStorageThreshold];
                    partStream = new MIMEBodyPartInputStream(pushbackInStream,
                                                             boundary, this);
                    int count = 0;
                    int value;
                    // Make sure *not* to modify this to a Short Circuit "&". If
                    // removed a byte will be lost
                    while (count != fileStorageThreshold
                           && (!partStream.getBoundaryStatus())) {
                        value = partStream.read();
                        buffer[count] = (byte) value;
                        count++;
                    }
                    if (count == fileStorageThreshold) {
                        PushbackFilePartInputStream filePartStream = new PushbackFilePartInputStream(
                                partStream, buffer);
                        part = new PartOnFile(filePartStream, attachmentRepoDir);
                    } else {
                        ByteArrayInputStream byteArrayInStream = new ByteArrayInputStream(
                                buffer, 0, count - 1);
                        part = new PartOnMemory(byteArrayInStream);
                    }
                } catch (Exception e) {
                    throw new OMException("Error creating temporary File.", e);
                }
            } else {
                MIMEBodyPartInputStream partStream;
                partStream = new MIMEBodyPartInputStream(pushbackInStream,
                                                         boundary, this);
                part = new PartOnMemory(partStream);
            }

        } catch (MessagingException e) {
            throw new OMException(e);
        }
        partIndex++;
        return part;
    }
}