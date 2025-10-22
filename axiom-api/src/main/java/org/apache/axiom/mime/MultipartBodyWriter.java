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
package org.apache.axiom.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.util.UIDGenerator;

/**
 * Writes a MIME multipart body as used by XOP/MTOM and SOAP with Attachments. MIME parts are
 * written using {@link #writePart(ContentType, ContentTransferEncoding, String, List)} or {@link
 * #writePart(Blob, ContentType, ContentTransferEncoding, String, List)}. Calls to both methods can
 * be mixed, i.e. it is not required to use the same method for all MIME parts. Instead, the caller
 * should choose the most convenient method for each part (depending on the form in which the
 * content is available). After all parts have been written, {@link #complete()} must be called to
 * write the final MIME boundary.
 *
 * <p>The following semantics are defined for the {@code contentTransferEncoding} and {@code
 * contentID} arguments of the two write methods:
 *
 * <ul>
 *   <li>The content transfer encoding specified by the {@code contentTransferEncoding} argument is
 *       applied by the write method; the caller only provides the unencoded data. The
 *       implementation ensures that the MIME part has a {@code Content-Transfer-Encoding} header
 *       appropriate for the applied encoding.
 *   <li>The content ID passed as argument is always the raw ID (without the angle brackets). The
 *       implementation translates this into a properly formatted {@code Content-ID} header.
 * </ul>
 */
public final class MultipartBodyWriter {
    class PartOutputStream extends OutputStream {
        private final OutputStream parent;

        public PartOutputStream(OutputStream parent) {
            this.parent = parent;
        }

        @Override
        public void write(int b) throws IOException {
            parent.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            parent.write(b, off, len);
        }

        @Override
        public void write(byte[] b) throws IOException {
            parent.write(b);
        }

        @Override
        public void close() throws IOException {
            writeAscii("\r\n");
        }
    }

    private final OutputStream out;
    private final String boundary;
    private final byte[] buffer = new byte[256];

    /**
     * Constructor.
     *
     * @param out the output stream to write the multipart body to
     * @param boundary the MIME boundary
     * @see UIDGenerator#generateMimeBoundary()
     */
    public MultipartBodyWriter(OutputStream out, String boundary) {
        this.out = out;
        this.boundary = boundary;
    }

    void writeAscii(String s) throws IOException {
        int count = 0;
        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            if (c >= 128) {
                throw new IOException("Illegal character '" + c + "'");
            }
            buffer[count++] = (byte) c;
            if (count == buffer.length) {
                out.write(buffer);
                count = 0;
            }
        }
        if (count > 0) {
            out.write(buffer, 0, count);
        }
    }

    /**
     * Start writing a MIME part. The methods returns an {@link OutputStream} that the caller can
     * use to write the content of the MIME part. After writing the content, {@link
     * OutputStream#close()} must be called to complete the writing of the MIME part.
     *
     * @param contentType the content type of the MIME part; may be {@code null}
     * @param contentTransferEncoding the content transfer encoding to be used (see above); must not
     *     be <code>null</code>
     * @param contentID the content ID of the MIME part (see above); may be {@code null}
     * @param extraHeaders a list of {@link Header} objects with additional headers to write to the
     *     MIME part; may be {@code null}
     * @return an output stream to write the content of the MIME part
     * @throws IOException if an I/O error occurs when writing to the underlying stream
     */
    public OutputStream writePart(
            ContentType contentType,
            ContentTransferEncoding contentTransferEncoding,
            String contentID,
            List<Header> extraHeaders)
            throws IOException {
        writeAscii("--");
        writeAscii(boundary);
        // RFC 2046 explicitly says that Content-Type is not mandatory (and defaults to
        // text/plain; charset=us-ascii).
        if (contentType != null) {
            writeAscii("\r\nContent-Type: ");
            writeAscii(contentType.toString());
        }
        writeAscii("\r\nContent-Transfer-Encoding: ");
        writeAscii(contentTransferEncoding.toString());
        if (contentID != null) {
            writeAscii("\r\nContent-ID: <");
            writeAscii(contentID);
            out.write('>');
        }
        if (extraHeaders != null) {
            for (Header header : extraHeaders) {
                writeAscii("\r\n");
                writeAscii(header.getName());
                writeAscii(": ");
                writeAscii(header.getValue());
            }
        }
        writeAscii("\r\n\r\n");
        return contentTransferEncoding.encode(new PartOutputStream(out));
    }

    /**
     * Write a MIME part.
     *
     * @param blob the content of the MIME part to write
     * @param contentType the content type; may be {@code null}
     * @param contentTransferEncoding the content transfer encoding to be used (see above); must not
     *     be <code>null</code>
     * @param contentID the content ID of the MIME part (see above)
     * @param extraHeaders a list of {@link Header} objects with additional headers to write to the
     *     MIME part
     * @throws IOException if an I/O error occurs when writing the part to the underlying stream
     */
    public void writePart(
            Blob blob,
            ContentType contentType,
            ContentTransferEncoding contentTransferEncoding,
            String contentID,
            List<Header> extraHeaders)
            throws IOException {
        OutputStream partOutputStream =
                writePart(contentType, contentTransferEncoding, contentID, extraHeaders);
        blob.writeTo(partOutputStream);
        partOutputStream.close();
    }

    /**
     * Complete writing of the MIME multipart package. This method does <b>not</b> close the
     * underlying stream.
     *
     * @throws IOException if an I/O error occurs when writing to the underlying stream
     */
    public void complete() throws IOException {
        writeAscii("--");
        writeAscii(boundary);
        writeAscii("--\r\n");
    }
}
