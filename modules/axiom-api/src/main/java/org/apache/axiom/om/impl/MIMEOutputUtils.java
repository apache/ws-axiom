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

package org.apache.axiom.om.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.attachments.ConfigurableDataHandler;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMText;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;

public class MIMEOutputUtils {

    private static byte[] CRLF =  { 13, 10 };

    public static void complete(OutputStream outStream,
                                StringWriter writer, LinkedList binaryNodeList,
                                String boundary, String contentId, String charSetEncoding,String SOAPContentType) {
        try {
            startWritingMime(outStream, boundary);

            javax.activation.DataHandler dh = new javax.activation.DataHandler(writer.toString(),
                    "text/xml; charset=" + charSetEncoding);
            MimeBodyPart rootMimeBodyPart = new MimeBodyPart();
            rootMimeBodyPart.setDataHandler(dh);

            rootMimeBodyPart.addHeader("content-type",
                    "application/xop+xml; charset=" + charSetEncoding +
                    "; type=\""+SOAPContentType+"\";");
            rootMimeBodyPart.addHeader("content-transfer-encoding", "binary");
            rootMimeBodyPart.addHeader("content-id","<"+contentId+">");

            writeBodyPart(outStream, rootMimeBodyPart, boundary);

            Iterator binaryNodeIterator = binaryNodeList.iterator();
            while (binaryNodeIterator.hasNext()) {
				OMText binaryNode = (OMText) binaryNodeIterator.next();
				writeBodyPart(outStream, createMimeBodyPart(binaryNode
						.getContentID(), (DataHandler) binaryNode
						.getDataHandler()), boundary);
			}
			finishWritingMime(outStream);
        } catch (IOException e) {
            throw new OMException("Error while writing to the OutputStream.", e);
        } catch (MessagingException e) {
            throw new OMException("Problem writing Mime Parts.", e);
        }
    }

    public static MimeBodyPart createMimeBodyPart(String contentID,
			DataHandler dataHandler) throws MessagingException {
		String encoding = null;
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setDataHandler(dataHandler);
		mimeBodyPart.addHeader("content-id", "<" + contentID + ">");
		mimeBodyPart.addHeader("content-type", dataHandler.getContentType());
		if (dataHandler instanceof ConfigurableDataHandler) {
			ConfigurableDataHandler configurableDataHandler = (ConfigurableDataHandler) dataHandler;
			encoding = configurableDataHandler.getTransferEncoding();
		}
		if (encoding == null) {
			encoding = "binary";
		}
		mimeBodyPart.addHeader("content-transfer-encoding", encoding);
		return mimeBodyPart;
	}

    /**
	 * @throws IOException
	 *             This will write the boundary to output Stream
	 */
    public static void writeMimeBoundary(OutputStream outStream,
                                         String boundary) throws IOException {
        outStream.write(new byte[]{45, 45});
        outStream.write(boundary.getBytes());
    }

    /**
     * @throws IOException This will write the boundary with CRLF
     */
    public static void startWritingMime(OutputStream outStream,
                                        String boundary)
            throws IOException {
        writeMimeBoundary(outStream, boundary);
        //outStream.write(CRLF);
    }

    /**
     * Writes a CRLF for the earlier boundary then the BodyPart data
     * with headers followed by boundary. Writes only the boundary. No more
     * CRLF's are written after that.
     *
     * @throws IOException
     * @throws MessagingException
     */
    public static void writeBodyPart(OutputStream outStream,
                                     MimeBodyPart part,
                                     String boundary) throws IOException,
            MessagingException {
        outStream.write(CRLF);
        part.writeTo(outStream);
        outStream.write(CRLF);
        writeMimeBoundary(outStream, boundary);
    }

    /**
     * @throws IOException This will write "--" to the end of last boundary
     */
    public static void finishWritingMime(OutputStream outStream)
            throws IOException {
        outStream.write(new byte[]{45, 45});
    }

    public static void writeSOAPWithAttachmentsMessage(StringWriter writer,
			OutputStream outputStream, Attachments attachments, OMOutputFormat format) {
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

			rootMimeBodyPart.addHeader("content-type",
					SOAPContentType+"; charset="
							+ format.getCharSetEncoding());
			rootMimeBodyPart.addHeader("content-transfer-encoding", "8bit");
			rootMimeBodyPart.addHeader("content-id", "<"
					+ format.getRootContentId() + ">");

			writeBodyPart(outputStream, rootMimeBodyPart, format
					.getMimeBoundary());

			Iterator attachmentIDIterator = attachments.getContentIDSet().iterator();
			while (attachmentIDIterator.hasNext()) {
				String contentID = (String) attachmentIDIterator.next();
				DataHandler dataHandler = attachments.getDataHandler(contentID);
				writeBodyPart(outputStream, createMimeBodyPart(contentID,
						dataHandler), format.getMimeBoundary());
			}
			finishWritingMime(outputStream);
		} catch (IOException e) {
			throw new OMException("Error while writing to the OutputStream.", e);
		} catch (MessagingException e) {
			throw new OMException("Problem writing Mime Parts.", e);
		}
	}
    
    /**
	 * Pack all the attachments in to a multipart/related MIME part and attachs
	 * it as the second MIME Part of MIME message
	 * 
	 * @param writer
	 * @param outputStream
	 * @param attachments
	 * @param format
	 * @param innerBoundary
	 */
    public static void writeMM7Message(StringWriter writer,
			OutputStream outputStream, Attachments attachments, OMOutputFormat format, String innerPartCID,String innerBoundary) {
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
					SOAPContentType+"; charset="+ format.getCharSetEncoding());
//			rootMimeBodyPart.addHeader("content-transfer-encoding", "quoted-printable");
			rootMimeBodyPart.addHeader("content-id", "<"
					+ format.getRootContentId() + ">");

			writeBodyPart(outputStream, rootMimeBodyPart, format
					.getMimeBoundary());

			if (attachments.getContentIDSet().size()!=0){
			outputStream.write(CRLF);  
		    StringBuffer sb = new StringBuffer();
		    sb.append("Content-Type: multipart/related");
		    sb.append("; ");
		    sb.append("boundary=");
		    sb.append("\""+innerBoundary+"\"");
		    outputStream.write(sb.toString().getBytes());
		    outputStream.write(CRLF); 
		    StringBuffer sb1 = new StringBuffer();
		    sb1.append("content-id: ");
		    sb1.append("<");
		    sb1.append(innerPartCID);
		    sb1.append(">");
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
		} catch (IOException e) {
			throw new OMException("Error while writing to the OutputStream.", e);
		} catch (MessagingException e) {
			throw new OMException("Problem writing Mime Parts.", e);
		}
	}
}
