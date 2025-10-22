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
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.MimeConfig;
import org.apache.james.mime4j.stream.MimeTokenStream;
import org.apache.james.mime4j.stream.RecursionMode;

/**
 * A MIME multipart message read from a stream. This class exposes an API that represents the
 * message as a sequence of {@link Part} instances. It implements the {@link Iterable} interface to
 * enable access to all parts. In addition, it supports lookup by content ID. Data is read from the
 * stream on demand. This means that the stream must be kept open until all parts have been
 * processed or {@link #detach()} has been called. It also means that any invocation of a method on
 * an instance of this class or an individual {@link Part} instance may trigger a {@link
 * MIMEException} if there is an I/O error on the stream or a MIME parsing error.
 *
 * <p>Instances of this class are created using a fluent builder; see {@link #builder()}.
 */
public final class MultipartBody implements Iterable<Part> {
    public interface PartCreationListener {
        void partCreated(Part part);
    }

    public static final class Builder {
        private InputStream inputStream;
        private ContentType contentType;
        private WritableBlobFactory<?> attachmentBlobFactory;
        private PartBlobFactory partBlobFactory;
        private PartCreationListener partCreationListener;

        Builder() {}

        public Builder setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Builder setContentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder setContentType(String contentType) {
            try {
                this.contentType = new ContentType(contentType);
            } catch (ParseException ex) {
                throw new MIMEException(ex);
            }
            return this;
        }

        public Builder setAttachmentBlobFactory(WritableBlobFactory<?> attachmentBlobFactory) {
            this.attachmentBlobFactory = attachmentBlobFactory;
            return this;
        }

        public Builder setPartBlobFactory(PartBlobFactory partBlobFactory) {
            this.partBlobFactory = partBlobFactory;
            return this;
        }

        public Builder setPartCreationListener(PartCreationListener partCreationListener) {
            this.partCreationListener = partCreationListener;
            return this;
        }

        public MultipartBody build() {
            if (inputStream == null) {
                throw new IllegalArgumentException("inputStream is mandatory");
            }
            if (contentType == null) {
                throw new IllegalArgumentException("contentType is mandatory");
            }
            return new MultipartBody(
                    inputStream,
                    contentType,
                    attachmentBlobFactory == null ? MemoryBlob.FACTORY : attachmentBlobFactory,
                    partBlobFactory == null ? PartBlobFactory.DEFAULT : partBlobFactory,
                    partCreationListener);
        }
    }

    private static final Log log = LogFactory.getLog(MultipartBody.class);

    private static final MimeConfig config = MimeConfig.custom().setStrictParsing(true).build();

    /** <code>ContentType</code> of the MIME message */
    private final ContentType contentType;

    private final String rootPartContentID;
    private final MimeTokenStream parser;

    /** Stores the already parsed MIME parts by Content IDs. */
    private final Map<String, PartImpl> partMap = new HashMap<String, PartImpl>();

    /** The MIME part currently being processed. */
    private PartImpl currentPart;

    private PartImpl firstPart;
    private PartImpl rootPart;

    private int partCount;

    private final WritableBlobFactory<?> attachmentBlobFactory;
    private final PartBlobFactory partBlobFactory;
    private final PartCreationListener partCreationListener;

    MultipartBody(
            InputStream inStream,
            ContentType contentType,
            WritableBlobFactory<?> attachmentBlobFactory,
            PartBlobFactory partBlobFactory,
            PartCreationListener partCreationListener) {
        this.attachmentBlobFactory = attachmentBlobFactory;
        this.partBlobFactory = partBlobFactory;
        this.partCreationListener = partCreationListener;
        this.contentType = contentType;

        String start = contentType.getParameter("start");
        rootPartContentID = start == null ? null : normalizeContentID(start);

        parser = new MimeTokenStream(config);
        parser.setRecursionMode(RecursionMode.M_NO_RECURSE);
        parser.parseHeadless(inStream, contentType.toString());

        // Move the parser to the beginning of the first part
        while (parser.getState() != EntityState.T_START_BODYPART) {
            try {
                parser.next();
            } catch (IOException ex) {
                throw new MIMEException(ex);
            } catch (MimeException ex) {
                throw new MIMEException(ex);
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private static String normalizeContentID(String contentID) {
        contentID = contentID.trim();
        if (contentID.length() >= 2
                && contentID.charAt(0) == '<'
                && contentID.charAt(contentID.length() - 1) == '>') {
            contentID = contentID.substring(1, contentID.length() - 1);
        }
        // There is some evidence that some broken MIME implementations add
        // a "cid:" prefix to the Content-ID; remove it if necessary.
        if (contentID.length() > 4 && contentID.startsWith("cid:")) {
            contentID = contentID.substring(4);
        }
        return contentID;
    }

    PartBlobFactory getPartBlobFactory() {
        return partBlobFactory;
    }

    public ContentType getContentType() {
        return contentType;
    }

    /**
     * Get the MIME part with the given content ID.
     *
     * @param contentID the content ID of the part to retrieve
     * @return the MIME part, or {@code null} if the message doesn't have a part with the given
     *     content ID
     */
    public Part getPart(String contentID) {
        do {
            PartImpl part = partMap.get(contentID);
            if (part != null) {
                return part;
            }
        } while (getNextPart() != null);
        return null;
    }

    /**
     * Get the number of parts in this multipart.
     *
     * @return the number of parts
     */
    public int getPartCount() {
        detach();
        return partCount;
    }

    PartImpl getFirstPart() {
        if (firstPart == null) {
            getNextPart();
        }
        return firstPart;
    }

    public Part getRootPart() {
        do {
            if (rootPart != null) {
                return rootPart;
            }
        } while (getNextPart() != null);
        throw new MIMEException("Mandatory root MIME part is missing");
    }

    PartImpl getNextPart() {
        if (currentPart != null) {
            currentPart.fetch();
        }
        if (parser.getState() == EntityState.T_END_MULTIPART) {
            currentPart = null;
        } else {
            String partContentID = null;
            boolean isRootPart;

            try {
                checkParserState(parser.next(), EntityState.T_START_HEADER);

                List<Header> headers = new ArrayList<Header>();
                while (parser.next() == EntityState.T_FIELD) {
                    Field field = parser.getField();
                    String name = field.getName();
                    String value = field.getBody();

                    if (log.isDebugEnabled()) {
                        log.debug("addHeader: (" + name + ") value=(" + value + ")");
                    }
                    headers.add(new Header(name, value));
                    if (partContentID == null && name.equalsIgnoreCase("Content-ID")) {
                        partContentID = normalizeContentID(value);
                    }
                }

                checkParserState(parser.next(), EntityState.T_BODY);

                if (rootPartContentID == null) {
                    isRootPart = firstPart == null;
                } else {
                    isRootPart = rootPartContentID.equals(partContentID);
                }

                PartImpl part =
                        new PartImpl(
                                this,
                                isRootPart ? MemoryBlob.FACTORY : attachmentBlobFactory,
                                partContentID,
                                headers,
                                parser);
                if (currentPart == null) {
                    firstPart = part;
                } else {
                    currentPart.setNextPart(part);
                }
                currentPart = part;
            } catch (IOException ex) {
                throw new MIMEException(ex);
            } catch (MimeException ex) {
                throw new MIMEException(ex);
            }

            partCount++;
            if (partContentID != null) {
                if (partMap.containsKey(partContentID)) {
                    throw new MIMEException("Two MIME parts with the same Content-ID not allowed.");
                }
                partMap.put(partContentID, currentPart);
            }
            if (isRootPart) {
                rootPart = currentPart;
            }
            if (partCreationListener != null) {
                partCreationListener.partCreated(currentPart);
            }
        }
        return currentPart;
    }

    private static void checkParserState(EntityState state, EntityState expected)
            throws IllegalStateException {
        if (expected != state) {
            throw new IllegalStateException(
                    "Internal error: expected parser to be in state "
                            + expected
                            + ", but got "
                            + state);
        }
    }

    @Override
    public Iterator<Part> iterator() {
        return new PartIterator(this);
    }

    public void detach() {
        while (getNextPart() != null) {
            // Just loop
        }
    }
}
