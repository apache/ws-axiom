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

package org.apache.axiom.util.stax;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.ext.stax.BlobProvider;
import org.apache.axiom.ext.stax.BlobWriter;
import org.apache.axiom.util.base64.Base64EncodingWriterOutputStream;

/**
 * Contains utility methods to work with {@link XMLStreamWriter} objects.
 */
public class XMLStreamWriterUtils {
    /**
     * Write base64 encoded data to a stream writer. This will result in one or more
     * {@link javax.xml.stream.XMLStreamConstants#CHARACTERS} events to be written
     * to the stream (or zero events if the data handler produces an empty byte sequence),
     * i.e. the data is streamed from the data handler directly to the stream writer.
     * Since no in-memory base64 representation of the entire binary data is built, this
     * method is suitable for very large amounts of data.
     * <p>
     * Note that this method will always serialize the data as base64 encoded character data.
     * Serialization code should prefer using
     * {@link #writeBlob(XMLStreamWriter, Blob, String, boolean)} or
     * {@link #writeBlob(XMLStreamWriter, BlobProvider, String, boolean)} to
     * enable optimization (if supported by the {@link XMLStreamWriter}).
     * 
     * @param writer the stream writer to write the data to
     * @param blob the blob containing the data to encode
     * @throws IOException if an error occurs when reading the data from the data handler
     * @throws XMLStreamException if an error occurs when writing the base64 encoded data to
     *         the stream
     */
    public static void writeBase64(XMLStreamWriter writer, Blob blob)
            throws IOException, XMLStreamException {
        
        Base64EncodingWriterOutputStream out = new Base64EncodingWriterOutputStream(
                new XMLStreamWriterWriter(writer), 4096, true);
        try {
            blob.writeTo(out);
            out.close();
        } catch (XMLStreamIOException ex) {
            throw ex.getXMLStreamException();
        }
    }

    private static BlobWriter internalGetBlobWriter(XMLStreamWriter writer) {
        try {
            return (BlobWriter)writer.getProperty(BlobWriter.PROPERTY);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Get the {@link BlobWriter} extension for a given {@link XMLStreamWriter}. If the
     * writer exposes the extension, a reference to the extension interface implementation is
     * returned. If the writer doesn't expose the extension, this method returns an instance of the
     * extension interface that emulates the extension (by writing the binary data as base64
     * character data to the stream).
     * 
     * @param writer
     *            the stream for which the method should return the {@link BlobWriter}
     *            extension
     * @return a reference to the extension interface exposed by the writer or an implementation the
     *         emulates the extension; the return value is never <code>null</code>
     */
    public static BlobWriter getBlobWriter(final XMLStreamWriter writer) {
        BlobWriter blobWriter = internalGetBlobWriter(writer);
        if (blobWriter == null) {
            return new BlobWriter() {
                @Override
                public void writeBlob(Blob blob, String contentID,
                        boolean optimize) throws IOException, XMLStreamException {
                    writeBase64(writer, blob);
                }

                @Override
                public void writeBlob(BlobProvider blobProvider,
                        String contentID, boolean optimize) throws IOException, XMLStreamException {
                    writeBase64(writer, blobProvider.getBlob());
                }
            };
        } else {
            return blobWriter;
        }
    }

    /**
     * Write binary content to the stream. Depending on the supplied {@link XMLStreamWriter},
     * the content will be written as base64 encoded character data or using an optimization
     * scheme such as XOP/MTOM. The method attempts to submit the binary content using the
     * {@link BlobWriter} extension. If the writer doesn't expose this extension,
     * the method will fall back to {@link #writeBase64(XMLStreamWriter, Blob)}.
     * <p>
     * Please refer to the documentation of {@link BlobWriter} for a more
     * detailed description of the semantics of the different arguments.
     * 
     * @param writer
     *            the stream writer to write the data to
     * @param blob
     *            the binary content to write
     * @param contentID
     *            an existing content ID for the binary data
     * @param optimize
     *            indicates whether the content is eligible for optimization
     * @throws IOException
     *             if an error occurs while reading from the data handler
     * @throws XMLStreamException
     *             if an error occurs while writing to the underlying stream
     */
    public static void writeBlob(XMLStreamWriter writer, Blob blob,
            String contentID, boolean optimize) throws IOException, XMLStreamException {
        BlobWriter blobWriter = internalGetBlobWriter(writer);
        if (blobWriter != null) {
            blobWriter.writeBlob(blob, contentID, optimize);
        } else {
            writeBase64(writer, blob);
        }
    }
    
    /**
     * Write binary content to the stream. This method is similar to
     * {@link #writeBlob(XMLStreamWriter, Blob, String, boolean)},
     * but supports deferred loading of the data handler.
     * 
     * @param writer
     *            the stream writer to write the data to
     * @param blobProvider
     *            the binary content to write
     * @param contentID
     *            an existing content ID for the binary data
     * @param optimize
     *            indicates whether the content is eligible for optimization
     * @throws IOException
     *             if an error occurs while reading from the data handler
     * @throws XMLStreamException
     *             if an error occurs while writing to the underlying stream
     */
    public static void writeBlob(XMLStreamWriter writer, BlobProvider blobProvider,
            String contentID, boolean optimize) throws IOException, XMLStreamException {
        BlobWriter blobWriter = internalGetBlobWriter(writer);
        if (blobWriter != null) {
            blobWriter.writeBlob(blobProvider, contentID, optimize);
        } else {
            writeBase64(writer, blobProvider.getBlob());
        }
    }
    
    /**
     * Prepare the {@code DOCTYPE} declaration using the provided information and output it using
     * {@link XMLStreamWriter#writeDTD(String)}.
     * 
     * @param writer
     *            the stream writer to write the {@code DOCTYPE} declaration to
     * @param rootName
     *            the root name, i.e. the name immediately following the {@code DOCTYPE} keyword
     * @param publicId
     *            the public ID of the external subset, or <code>null</code> if there is no external
     *            subset or no public ID has been specified for the external subset
     * @param systemId
     *            the system ID of the external subset, or <code>null</code> if there is no external
     *            subset
     * @param internalSubset
     *            the internal subset, or <code>null</code> if there is none
     * @throws XMLStreamException
     *             if an error occurs while writing to the stream
     */
    public static void writeDTD(XMLStreamWriter writer, String rootName, String publicId,
            String systemId, String internalSubset) throws XMLStreamException {
        StringBuilder buffer = new StringBuilder("<!DOCTYPE ");
        buffer.append(rootName);
        if (publicId != null) {
            buffer.append(" PUBLIC \"");
            buffer.append(publicId);
            buffer.append("\" \"");
            buffer.append(systemId);
            buffer.append("\"");
        } else if (systemId != null) {
            buffer.append(" SYSTEM \"");
            buffer.append(systemId);
            buffer.append("\"");
        }
        if (internalSubset != null) {
            buffer.append(" [");
            buffer.append(internalSubset);
            buffer.append("]");
        }
        buffer.append(">");
        writer.writeDTD(buffer.toString());
    }
}
