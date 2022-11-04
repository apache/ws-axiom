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
import java.text.ParseException;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.axiom.attachments.ConfigurableDataHandler;
import org.apache.axiom.mime.ContentTransferEncoding;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.mime.Header;
import org.apache.axiom.mime.MediaType;
import org.apache.axiom.mime.MultipartBodyWriter;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;

/**
 * Writes a MIME multipart package as used by XOP/MTOM and SOAP with Attachments. This class wraps a
 * {@link MultipartBodyWriter}, providing a higher level API. In particular it will configure content
 * types and content transfer encodings based on information from an {@link OMOutputFormat} object.
 */
public class OMMultipartWriter {
    private final OMOutputFormat format;
    private final MultipartBodyWriter writer;
    private final boolean useCTEBase64;
    private final ContentType rootPartContentType;
    
    public OMMultipartWriter(OutputStream out, OMOutputFormat format) {
        this.format = format;
        
        writer = new MultipartBodyWriter(out, format.getMimeBoundary());
        
        useCTEBase64 = format != null && Boolean.TRUE.equals(
                format.getProperty(OMOutputFormat.USE_CTE_BASE64_FOR_NON_TEXTUAL_ATTACHMENTS));
        
        MediaType soapContentType;
        if (format.isSOAP11()) {
            soapContentType = SOAP11Constants.SOAP_11_CONTENT_TYPE;
        } else {
            soapContentType = SOAP12Constants.SOAP_12_CONTENT_TYPE;
        }
        if (format.isOptimized()) {
            rootPartContentType = ContentType.builder()
                    .setMediaType(MediaType.APPLICATION_XOP_XML)
                    .setParameter("charset", format.getCharSetEncoding())
                    .setParameter("type", soapContentType.toString())
                    .build();
        } else {
            rootPartContentType = ContentType.builder()
                    .setMediaType(soapContentType)
                    .setParameter("charset", format.getCharSetEncoding())
                    .build();
        }
    }

    private ContentTransferEncoding getContentTransferEncoding(ContentType contentType) {
        if (useCTEBase64 && !contentType.isTextual()) {
            return ContentTransferEncoding.BASE64;
        } else {
            return ContentTransferEncoding.BINARY;
        }
    }
    
    /**
     * Get the content type of the root part, as determined by the {@link OMOutputFormat} passed
     * to the constructor of this object.
     * 
     * @return the content type of the root part
     */
    public ContentType getRootPartContentType() {
        return rootPartContentType;
    }

    /**
     * Start writing the root part of the MIME package. This method delegates to
     * {@link MultipartBodyWriter#writePart(ContentType, ContentTransferEncoding, String, List)}, but computes the content type,
     * content transfer encoding and content ID from the {@link OMOutputFormat}.
     * 
     * @return an output stream to write the content of the MIME part
     * @throws IOException
     *             if an I/O error occurs when writing to the underlying stream
     */
    public OutputStream writeRootPart() throws IOException {
        return writer.writePart(rootPartContentType, ContentTransferEncoding.BINARY, format.getRootContentId(), null);
    }

    /**
     * Start writing an attachment part of the MIME package. This method delegates to
     * {@link MultipartBodyWriter#writePart(ContentType, ContentTransferEncoding, String, List)}, but computes the content transfer
     * encoding based on the content type and the {@link OMOutputFormat}.
     * 
     * @param contentType
     *            the content type of the MIME part to write
     * @param contentID
     *            the content ID of the MIME part
     * @return an output stream to write the content of the MIME part
     * @throws IOException
     *             if an I/O error occurs when writing to the underlying stream
     */
    public OutputStream writePart(ContentType contentType, String contentID) throws IOException {
        return writer.writePart(contentType, getContentTransferEncoding(contentType), contentID, null);
    }
    
    /**
     * Start writing an attachment part of the MIME package. This method delegates to
     * {@link MultipartBodyWriter#writePart(ContentType, ContentTransferEncoding, String, List)}, but computes the content
     * transfer encoding based on the content type and the {@link OMOutputFormat}.
     * 
     * @param contentType
     *            the content type of the MIME part to write
     * @param contentID
     *            the content ID of the MIME part
     * @param extraHeaders
     *            a list of {@link Header} objects with additional headers to write to the MIME part
     * @return an output stream to write the content of the MIME part
     * @throws IOException
     *             if an I/O error occurs when writing to the underlying stream
     */
    public OutputStream writePart(ContentType contentType, String contentID, List<Header> extraHeaders) throws IOException {    
        return writer.writePart(contentType, getContentTransferEncoding(contentType), contentID, extraHeaders);
    }
    
    /**
     * Write a MIME part. This method delegates to
     * {@link MultipartBodyWriter#writePart(DataHandler, ContentTransferEncoding, String, List)}, but computes the
     * appropriate content transfer encoding from the {@link OMOutputFormat}.
     * 
     * @param dataHandler
     *            the content of the MIME part to write
     * @param contentID
     *            the content ID of the MIME part
     * @param extraHeaders
     *            a list of {@link Header} objects with additional headers to write to the MIME part
     * @throws IOException
     *             if an I/O error occurs when writing the part to the underlying stream
     */
    public void writePart(DataHandler dataHandler, String contentID, List<Header> extraHeaders) throws IOException {
        ContentTransferEncoding contentTransferEncoding = null;
        if (dataHandler instanceof ConfigurableDataHandler) {
            switch (((ConfigurableDataHandler)dataHandler).getTransferEncoding()) {
                case "8bit":
                    contentTransferEncoding = ContentTransferEncoding.EIGHT_BIT;
                    break;
                case "binary":
                    contentTransferEncoding = ContentTransferEncoding.BINARY;
                    break;
                default:
                    contentTransferEncoding = ContentTransferEncoding.BASE64;
            }
        }
        if (contentTransferEncoding == null) {
            String contentTypeString = dataHandler.getContentType();
            if (contentTypeString != null) {
                ContentType contentType;
                try {
                    contentType = new ContentType(contentTypeString);
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
                contentTransferEncoding = getContentTransferEncoding(contentType);
            }
        }
        if (contentTransferEncoding == null) {
            contentTransferEncoding = ContentTransferEncoding.BINARY;
        }
        writer.writePart(dataHandler, contentTransferEncoding, contentID, extraHeaders);
    }
    
    /**
     * Write a MIME part. This method delegates to
     * {@link MultipartBodyWriter#writePart(DataHandler, ContentTransferEncoding, String, List)}, but computes the appropriate
     * content transfer encoding from the {@link OMOutputFormat}.
     * 
     * @param dataHandler
     *            the content of the MIME part to write
     * @param contentID
     *            the content ID of the MIME part 
     * @throws IOException
     *             if an I/O error occurs when writing the part to the underlying stream
     */
    public void writePart(DataHandler dataHandler, String contentID) throws IOException {
        writePart(dataHandler, contentID, null);
    }

    /**
     * Complete writing of the MIME multipart package. This method delegates to
     * {@link MultipartBodyWriter#complete()}.
     * 
     * @throws IOException
     *             if an I/O error occurs when writing to the underlying stream
     */
    public void complete() throws IOException {
        writer.complete();
    }
}
