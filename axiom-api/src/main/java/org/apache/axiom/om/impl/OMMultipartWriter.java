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
import java.util.List;

import org.apache.axiom.attachments.ConfigurableDataHandler;
import org.apache.axiom.blob.Blob;
import org.apache.axiom.mime.ContentTransferEncoding;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.mime.Header;
import org.apache.axiom.mime.MediaType;
import org.apache.axiom.mime.MultipartBodyWriter;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.format.xop.CombinedContentTransferEncodingPolicy;
import org.apache.axiom.om.format.xop.ContentTransferEncodingPolicy;
import org.apache.axiom.om.format.xop.ContentTypeProvider;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.util.activation.DataHandlerContentTypeProvider;

/**
 * Writes a MIME multipart package as used by XOP/MTOM and SOAP with Attachments. This class wraps a
 * {@link MultipartBodyWriter}, providing a higher level API. In particular it will configure content
 * types and content transfer encodings based on information from an {@link OMOutputFormat} object.
 */
public class OMMultipartWriter {
    private final OMOutputFormat format;
    private final MultipartBodyWriter writer;
    private final ContentTypeProvider contentTypeProvider;
    private final ContentTransferEncodingPolicy contentTransferEncodingPolicy;
    private final ContentType rootPartContentType;
    
    public OMMultipartWriter(OutputStream out, OMOutputFormat format) {
        this.format = format;
        
        writer = new MultipartBodyWriter(out, format.getMimeBoundary());
        
        // TODO(AXIOM-506): make this configurable in OMOutputFormat
        contentTypeProvider = DataHandlerContentTypeProvider.INSTANCE;
        ContentTransferEncodingPolicy contentTransferEncodingPolicy = ConfigurableDataHandler.CONTENT_TRANSFER_ENCODING_POLICY;
        if (format != null && Boolean.TRUE.equals(
                format.getProperty(OMOutputFormat.USE_CTE_BASE64_FOR_NON_TEXTUAL_ATTACHMENTS))) {
            contentTransferEncodingPolicy = new CombinedContentTransferEncodingPolicy(contentTransferEncodingPolicy, ContentTransferEncodingPolicy.USE_BASE64_FOR_NON_TEXTUAL_PARTS);
        }
        this.contentTransferEncodingPolicy = contentTransferEncodingPolicy;
        
        MediaType soapContentType;
        if (format.isSOAP11()) {
            soapContentType = SOAPVersion.SOAP11.getMediaType();
        } else {
            soapContentType = SOAPVersion.SOAP12.getMediaType();
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

    private ContentTransferEncoding getContentTransferEncoding(Blob blob, ContentType contentType) {
        ContentTransferEncoding cte = contentTransferEncodingPolicy.getContentTransferEncoding(blob, contentType);
        return cte == null ? ContentTransferEncoding.BINARY : cte;
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
        return writer.writePart(contentType, getContentTransferEncoding(null, contentType), contentID, null);
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
        return writer.writePart(contentType, getContentTransferEncoding(null, contentType), contentID, extraHeaders);
    }
    
    /**
     * Write a MIME part. This method delegates to
     * {@link MultipartBodyWriter#writePart(Blob, ContentType, ContentTransferEncoding, String, List)}, but computes the
     * appropriate content transfer encoding from the {@link OMOutputFormat}.
     * 
     * @param blob
     *            the content of the MIME part to write
     * @param contentID
     *            the content ID of the MIME part
     * @param extraHeaders
     *            a list of {@link Header} objects with additional headers to write to the MIME part
     * @throws IOException
     *             if an I/O error occurs when writing the part to the underlying stream
     */
    public void writePart(Blob blob, String contentID, List<Header> extraHeaders) throws IOException {
        ContentType contentType = contentTypeProvider.getContentType(blob);
        writer.writePart(blob, contentType, getContentTransferEncoding(blob, contentType), contentID, extraHeaders);
    }
    
    /**
     * Write a MIME part. This method delegates to
     * {@link MultipartBodyWriter#writePart(Blob, ContentType, ContentTransferEncoding, String, List)}, but computes the appropriate
     * content transfer encoding from the {@link OMOutputFormat}.
     * 
     * @param blob
     *            the content of the MIME part to write
     * @param contentID
     *            the content ID of the MIME part 
     * @throws IOException
     *             if an I/O error occurs when writing the part to the underlying stream
     */
    public void writePart(Blob blob, String contentID) throws IOException {
        writePart(blob, contentID, null);
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
