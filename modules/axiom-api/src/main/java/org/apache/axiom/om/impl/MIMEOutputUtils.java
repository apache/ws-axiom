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

package org.apache.axiom.om.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.attachments.ConfigurableDataHandler;
import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMText;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class used to write out XML with Attachments
 * @See MTOMXMLStreamWriter
 *
 */
public class MIMEOutputUtils {
    
    private static Log log = LogFactory.getLog(MIMEOutputUtils.class);
    private static boolean isDebugEnabled = log.isDebugEnabled();

    private static byte[] CRLF = { 13, 10 };

    /**
     * @deprecated is anyone really using this?
     * 
     * Invoked by MTOMXMLStreamWriter to write the SOAP Part and the attachemts
     * @param outStream OutputStream target
     * @param bufferedXML String containing XML of SOAPPart
     * @param binaryNodeList Text nodes with the attachment Data Handlers
     * @param boundary Boundary String
     * @param contentId Content-ID of SOAPPart
     * @param charSetEncoding Character Encoding of SOAPPart
     * @param SOAPContentType Content-Type of SOAPPart
     */
    public static void complete(OutputStream outStream, 
                                String bufferedXML,
                                LinkedList binaryNodeList, 
                                String boundary, 
                                String contentId,
                                String charSetEncoding, 
                                String SOAPContentType) {
        try {
            // TODO: Instead of buffering the SOAPPart contents, it makes more
            // sense to split this method in two.  Write out the SOAPPart headers
            // and later write out the attachments.  This will avoid the cost and
            // space of buffering.
            
            // Write out the mime boundary
            startWritingMime(outStream, boundary);

            javax.activation.DataHandler dh = 
                new javax.activation.DataHandler(bufferedXML,
                                                 "text/xml; charset=" + charSetEncoding);
            MimeBodyPart rootMimeBodyPart = new MimeBodyPart();
            rootMimeBodyPart.setDataHandler(dh);

            rootMimeBodyPart.addHeader("Content-Type",
                                       "application/xop+xml; charset=" + charSetEncoding +
                                               "; type=\"" + SOAPContentType + "\"");
            rootMimeBodyPart.addHeader("Content-Transfer-Encoding", "binary");
            rootMimeBodyPart.addHeader("Content-ID", "<" + contentId + ">");

            // Write out the SOAPPart
            writeBodyPart(outStream, rootMimeBodyPart, boundary);

            // Now write out the Attachment parts (which are represented by the
            // text nodes int the binary node list)
            Iterator binaryNodeIterator = binaryNodeList.iterator();
            while (binaryNodeIterator.hasNext()) {
                OMText binaryNode = (OMText) binaryNodeIterator.next();
                writeBodyPart(outStream, createMimeBodyPart(binaryNode
                        .getContentID(), (DataHandler) binaryNode
                        .getDataHandler()), boundary);
            }
            finishWritingMime(outStream);
            outStream.flush();
        } catch (IOException e) {
            throw new OMException("Error while writing to the OutputStream.", e);
        } catch (MessagingException e) {
            throw new OMException("Problem writing Mime Parts.", e);
        }
    }
    
    /**
     * Invoked by MTOMXMLStreamWriter to write the SOAP Part and the attachements. 
     * 
     * @param outStream OutputStream target
     * @param bufferedXML String containing XML of SOAPPart
     * @param binaryNodeList Text nodes with the attachment Data Handlers
     * @param boundary Boundary String
     * @param contentId Content-ID of SOAPPart
     * @param charSetEncoding Character Encoding of SOAPPart
     * @param SOAPContentType Content-Type of SOAPPart
     */
    public static void complete(OutputStream outStream, 
                                byte[] xmlData,
                                LinkedList binaryNodeList, 
                                String boundary, 
                                String contentId,
                                String charSetEncoding, 
                                String SOAPContentType) {
        try {
            if (isDebugEnabled) {
                log.debug("Start: write the SOAPPart and the attachments");
            }
            // TODO: Instead of buffering the SOAPPart contents, it makes more
            // sense to split this method in two.  Write out the SOAPPart headers
            // and later write out the attachments.  This will avoid the cost and
            // space of buffering.
            
            // Write out the mime boundary
            startWritingMime(outStream, boundary);

            javax.activation.DataHandler dh = 
                new javax.activation.DataHandler(new ByteArrayDataSource(xmlData,
                                                 "text/xml; charset=" + charSetEncoding));
            MimeBodyPart rootMimeBodyPart = new MimeBodyPart();
            rootMimeBodyPart.setDataHandler(dh);

            rootMimeBodyPart.addHeader("Content-Type",
                                       "application/xop+xml; charset=" + charSetEncoding +
                                               "; type=\"" + SOAPContentType + "\"");
            rootMimeBodyPart.addHeader("Content-Transfer-Encoding", "binary");
            rootMimeBodyPart.addHeader("Content-ID", "<" + contentId + ">");

            // Write out the SOAPPart
            writeBodyPart(outStream, rootMimeBodyPart, boundary);

            // Now write out the Attachment parts (which are represented by the
            // text nodes int the binary node list)
            Iterator binaryNodeIterator = binaryNodeList.iterator();
            while (binaryNodeIterator.hasNext()) {
                OMText binaryNode = (OMText) binaryNodeIterator.next();
                writeBodyPart(outStream, createMimeBodyPart(binaryNode
                        .getContentID(), (DataHandler) binaryNode
                        .getDataHandler()), boundary);
            }
            finishWritingMime(outStream);
            outStream.flush();
            if (isDebugEnabled) {
                log.debug("End: write the SOAPPart and the attachments");
            }
        } catch (IOException e) {
            throw new OMException("Error while writing to the OutputStream.", e);
        } catch (MessagingException e) {
            throw new OMException("Problem writing Mime Parts.", e);
        }
    }

    /**
     * @deprecated - is anyone really using this?
     * 
     * Write the SOAPPart and attachments
     * @param outStream
     * @param writer
     * @param binaryNodeList
     * @param boundary
     * @param contentId
     * @param charSetEncoding
     * @param SOAPContentType
     */
    public static void complete(OutputStream outStream, StringWriter writer,
                                LinkedList binaryNodeList, String boundary, String contentId,
                                String charSetEncoding, String SOAPContentType) {
        complete(outStream, 
                 writer.toString(), 
                 binaryNodeList, 
                 boundary, 
                 contentId, 
                 charSetEncoding, 
                 SOAPContentType);
    }

    public static MimeBodyPart createMimeBodyPart(String contentID,
                                                  DataHandler dataHandler)
            throws MessagingException {
        if (isDebugEnabled) {
            log.debug("Create MimeBodyPart for " + contentID);
        }
        String encoding = null;
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDataHandler(dataHandler);
        mimeBodyPart.addHeader("Content-ID", "<" + contentID + ">");
        mimeBodyPart.addHeader("Content-Type", dataHandler.getContentType());
        if (dataHandler instanceof ConfigurableDataHandler) {
            ConfigurableDataHandler configurableDataHandler = (ConfigurableDataHandler) dataHandler;
            encoding = configurableDataHandler.getTransferEncoding();
        }
        if (encoding == null) {
            encoding = "binary";
        }
        mimeBodyPart.addHeader("Content-Transfer-Encoding", encoding);
        return mimeBodyPart;
    }

    /** @throws IOException This will write the boundary to output Stream */
    public static void writeMimeBoundary(OutputStream outStream,
                                         String boundary) throws IOException {
        // REVIEW: This conversion is hard-coded to UTF-8.
        // The complete solution is to respect the charset setting of the message.
        // However this may cause problems in BoundaryDelimittedStream and other
        // lower level classes.
        outStream.write(new byte[] { 45, 45 });
        outStream.write(boundary.getBytes("UTF-8"));
    }

    /** @throws IOException This will write the boundary with CRLF */
    public static void startWritingMime(OutputStream outStream,
                                        String boundary)
            throws IOException {
        writeMimeBoundary(outStream, boundary);
        //outStream.write(CRLF);
    }

    /**
     * Writes a CRLF for the earlier boundary then the BodyPart data with headers followed by
     * boundary. Writes only the boundary. No more CRLF's are written after that.
     *
     * @throws IOException
     * @throws MessagingException
     */
    public static void writeBodyPart(OutputStream outStream,
                                     MimeBodyPart part,
                                     String boundary) throws IOException,
            MessagingException {
        if (isDebugEnabled) {
            log.debug("Start writeMimeBodyPart for " + part.getContentID());
        }
        outStream.write(CRLF);
        part.writeTo(outStream);
        outStream.write(CRLF);
        writeMimeBoundary(outStream, boundary);
        outStream.flush();
        if (isDebugEnabled) {
            log.debug("End writeMimeBodyPart");
        }
    }

    /** @throws IOException This will write "--" to the end of last boundary */
    public static void finishWritingMime(OutputStream outStream)
            throws IOException {
        if (isDebugEnabled) {
            log.debug("Write --, which indicates the end of the last boundary");
        }
        outStream.write(new byte[] { 45, 45 });
    }

    public static void writeSOAPWithAttachmentsMessage(StringWriter writer,
                                                       OutputStream outputStream,
                                                       Attachments attachments,
                                                       OMOutputFormat format) {
        String SOAPContentType;
        if (format.isSOAP11()) {
            SOAPContentType = SOAP11Constants.SOAP_11_CONTENT_TYPE;
        } else {
            SOAPContentType = SOAP12Constants.SOAP_12_CONTENT_TYPE;
        }
        String contentType = SOAPContentType + "; charset=" + format.getCharSetEncoding();
        javax.activation.DataHandler dh = new javax.activation.DataHandler(
                writer.toString(), "text/xml; charset="
                + format.getCharSetEncoding());
        writeDataHandlerWithAttachmentsMessage(dh, contentType, outputStream, attachments.getMap(), format);
    }

    public static void writeDataHandlerWithAttachmentsMessage(DataHandler rootDataHandler,
                                                       String contentType,
                                                       OutputStream outputStream,
                                                       Map attachments,
                                                       OMOutputFormat format) {
        try {
            startWritingMime(outputStream, format.getMimeBoundary());

            MimeBodyPart rootMimeBodyPart = new MimeBodyPart();
            rootMimeBodyPart.setDataHandler(rootDataHandler);

            rootMimeBodyPart.addHeader("Content-Type", contentType);
            rootMimeBodyPart.addHeader("Content-Transfer-Encoding", "8bit");
            rootMimeBodyPart.addHeader("Content-ID", "<"
                    + format.getRootContentId() + ">");

            writeBodyPart(outputStream, rootMimeBodyPart, format
                    .getMimeBoundary());

            Iterator iterator = attachments.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                MimeBodyPart part = createMimeBodyPart(key,
                        (DataHandler) attachments.get(key));
                writeBodyPart(outputStream, part,
                              format.getMimeBoundary());
            }
            finishWritingMime(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new OMException("Error while writing to the OutputStream.", e);
        } catch (MessagingException e) {
            throw new OMException("Problem writing Mime Parts.", e);
        }
    }

    /**
     * Pack all the attachments in to a multipart/related MIME part and attachs it as the second
     * MIME Part of MIME message
     *
     * @param writer
     * @param outputStream
     * @param attachments
     * @param format
     * @param innerBoundary
     */
    public static void writeMM7Message(StringWriter writer,
                                       OutputStream outputStream, Attachments attachments,
                                       OMOutputFormat format, String innerPartCID,
                                       String innerBoundary) {
        String SOAPContentType;
        try {
            if (format.isSOAP11()) {
                SOAPContentType = SOAP11Constants.SOAP_11_CONTENT_TYPE;
            } else {
                SOAPContentType = SOAP12Constants.SOAP_12_CONTENT_TYPE;
            }
            startWritingMime(outputStream, format.getMimeBoundary());

            javax.activation.DataHandler dh = new javax.activation.DataHandler(
                    writer.toString(), "text/xml; charset="
                    + format.getCharSetEncoding());
            MimeBodyPart rootMimeBodyPart = new MimeBodyPart();
            rootMimeBodyPart.setDataHandler(dh);

            rootMimeBodyPart.addHeader("Content-Type",
                                       SOAPContentType + "; charset=" +
                                               format.getCharSetEncoding());
            rootMimeBodyPart.addHeader("Content-ID", "<"
                    + format.getRootContentId() + ">");

            writeBodyPart(outputStream, rootMimeBodyPart, format
                    .getMimeBoundary());

            if (attachments.getContentIDSet().size() != 0) {
                outputStream.write(CRLF);
                StringBuffer sb = new StringBuffer();
                sb.append("Content-Type: multipart/related");
                sb.append("; ");
                sb.append("boundary=");
                sb.append("\"" + innerBoundary + "\"");
                // REVIEW Should this be getBytes("UTF-8") or getBytes(charset)
                outputStream.write(sb.toString().getBytes());
                outputStream.write(CRLF);
                StringBuffer sb1 = new StringBuffer();
                sb1.append("Content-ID: ");
                sb1.append("<");
                sb1.append(innerPartCID);
                sb1.append(">");
                // REVIEW Should this be getBytes("UTF-8") or getBytes(charset)
                outputStream.write(sb1.toString().getBytes());
                outputStream.write(CRLF);
                outputStream.write(CRLF);
                startWritingMime(outputStream, innerBoundary);
                Iterator attachmentIDIterator = attachments.getContentIDSet().iterator();
                while (attachmentIDIterator.hasNext()) {
                    String contentID = (String) attachmentIDIterator.next();
                    DataHandler dataHandler = attachments.getDataHandler(contentID);
                    writeBodyPart(outputStream, createMimeBodyPart(contentID,
                                                                   dataHandler), innerBoundary);
                }
                finishWritingMime(outputStream);
                outputStream.write(CRLF);
                writeMimeBoundary(outputStream, format.getMimeBoundary());
            }
            finishWritingMime(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new OMException("Error while writing to the OutputStream.", e);
        } catch (MessagingException e) {
            throw new OMException("Problem writing Mime Parts.", e);
        }
    }
}
