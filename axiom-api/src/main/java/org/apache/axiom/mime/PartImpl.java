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

import org.apache.axiom.blob.Blob;
import org.apache.axiom.blob.OverflowableBlob;
import org.apache.axiom.blob.WritableBlob;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.MimeTokenStream;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

/** Actual implementation of the {@link Part} interface. */
final class PartImpl implements Part {
    /**
     * The part has not been read yet. In this case the parser is in state {@link
     * EntityState#T_BODY}.
     */
    private static final int STATE_UNREAD = 0;

    /** The part has been read into a memory or file based buffer. */
    private static final int STATE_BUFFERED = 1;

    /**
     * The part content is being streamed, i.a. the application code consumes the part content
     * without buffering.
     */
    private static final int STATE_STREAMING = 2;

    /**
     * The part content has been discarded and can no longer be read. This state is reached either
     * when the content has been streamed or when it is discarded explicitly after being buffered.
     */
    private static final int STATE_DISCARDED = 3;

    private static final Log log = LogFactory.getLog(PartImpl.class);

    private final MultipartBody message;
    private final WritableBlobFactory<?> blobFactory;

    private final String contentID;
    private final List<Header> headers;
    private ContentType contentType;

    private int state = STATE_UNREAD;

    /**
     * The MIME parser from which the content of this part is read. This is only set if the state is
     * {@link #STATE_UNREAD} or {@link #STATE_STREAMING}.
     */
    private MimeTokenStream parser;

    /** The content of this part. This is only set if the state is {@link #STATE_BUFFERED}. */
    private WritableBlob content;

    private PartBlob blob;

    private PartInputStream partInputStream;

    private PartImpl nextPart;

    PartImpl(
            MultipartBody message,
            WritableBlobFactory<?> blobFactory,
            String contentID,
            List<Header> headers,
            MimeTokenStream parser) {
        this.message = message;
        this.blobFactory = blobFactory;
        this.contentID = contentID;
        this.headers = headers;
        this.parser = parser;
    }

    @Override
    public String getHeader(String name) {
        String value = null;
        for (int i = 0, l = headers.size(); i < l; i++) {
            Header header = headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                value = header.getValue();
                break;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("getHeader name=(" + name + ") value=(" + value + ")");
        }
        return value;
    }

    @Override
    public List<Header> getHeaders() {
        return Collections.unmodifiableList(headers);
    }

    @Override
    public String getContentID() {
        return contentID;
    }

    @Override
    public ContentType getContentType() {
        if (contentType == null) {
            try {
                contentType = new ContentType(getHeader("content-type"));
            } catch (ParseException ex) {
                throw new MIMEException(ex);
            }
        }
        return contentType;
    }

    @Override
    public PartBlob getPartBlob() {
        if (blob == null) {
            blob = message.getPartBlobFactory().createBlob(this);
        }
        return blob;
    }

    private WritableBlob getContent() {
        switch (state) {
            case STATE_UNREAD:
                fetch();
            // Fall through
            case STATE_BUFFERED:
                return content;
            default:
                throw new IllegalStateException(
                        "The content of the MIME part has already been consumed");
        }
    }

    @Override
    public Blob getBlob() {
        WritableBlob blob = getContent();
        if (blob instanceof OverflowableBlob) {
            WritableBlob overflowBlob = ((OverflowableBlob) blob).getOverflowBlob();
            if (overflowBlob != null) {
                blob = overflowBlob;
            }
        }
        return blob;
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

    private InputStream getDecodedInputStream() {
        InputStream in = parser.getDecodedInputStream();
        if (log.isDebugEnabled()) {
            in = new DebugInputStream(in, log);
        }
        return in;
    }

    @Override
    public void fetch() {
        switch (state) {
            case STATE_UNREAD -> {
                checkParserState(parser.getState(), EntityState.T_BODY);

                content = blobFactory.createBlob();
                if (log.isDebugEnabled()) {
                    log.debug("Using blob of type " + content.getClass().getName());
                }
                try {
                    content.readFrom(getDecodedInputStream());
                } catch (StreamCopyException ex) {
                    if (ex.getOperation() == StreamCopyException.READ) {
                        throw new MIMEException(
                                "Failed to fetch the MIME part content", ex.getCause());
                    } else {
                        throw new MIMEException(
                                "Failed to write the MIME part content to temporary storage",
                                ex.getCause());
                    }
                }
                moveToNextPart();
                state = STATE_BUFFERED;
            }
            case STATE_STREAMING -> {
                // If the stream is still open, buffer the remaining content
                try {
                    partInputStream.detach();
                } catch (IOException ex) {
                    throw new MIMEException(ex);
                }
                partInputStream = null;
                moveToNextPart();
                state = STATE_DISCARDED;
            }
        }
    }

    private void moveToNextPart() {
        try {
            checkParserState(parser.next(), EntityState.T_END_BODYPART);
            EntityState state = parser.next();
            if (state == EntityState.T_EPILOGUE) {
                while (parser.next() != EntityState.T_END_MULTIPART) {
                    // Just loop
                }
            } else if (state != EntityState.T_START_BODYPART
                    && state != EntityState.T_END_MULTIPART) {
                throw new IllegalStateException("Internal error: unexpected parser state " + state);
            }
        } catch (IOException ex) {
            throw new MIMEException(ex);
        } catch (MimeException ex) {
            throw new MIMEException(ex);
        }
        parser = null;
    }

    @Override
    public InputStream getInputStream(boolean preserve) {
        if (!preserve && state == STATE_UNREAD) {
            checkParserState(parser.getState(), EntityState.T_BODY);
            state = STATE_STREAMING;
            partInputStream = new PartInputStream(getDecodedInputStream(), blobFactory);
            return partInputStream;
        } else {
            WritableBlob content = getContent();
            try {
                if (preserve) {
                    return content.getInputStream();
                } else {
                    return new PartInputStream(content);
                }
            } catch (IOException ex) {
                throw new MIMEException("Failed to retrieve part content from blob", ex);
            }
        }
    }

    @Override
    public void discard() {
        try {
            switch (state) {
                case STATE_UNREAD -> {
                    EntityState parserState;
                    do {
                        parserState = parser.next();
                    } while (parserState != EntityState.T_START_BODYPART
                            && parserState != EntityState.T_END_MULTIPART);
                    state = STATE_DISCARDED;
                }
                case STATE_BUFFERED -> content.release();
            }
        } catch (MimeException ex) {
            throw new MIMEException(ex);
        } catch (IOException ex) {
            throw new MIMEException(ex);
        }
    }

    PartImpl getNextPart() {
        if (nextPart == null) {
            message.getNextPart();
        }
        return nextPart;
    }

    void setNextPart(PartImpl nextPart) {
        this.nextPart = nextPart;
    }
}
