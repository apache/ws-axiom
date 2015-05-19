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

import org.apache.axiom.attachments.Part;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.blob.OverflowableBlob;
import org.apache.axiom.blob.WritableBlob;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.mime.Header;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.util.DetachableInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.MimeTokenStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Actual implementation of the {@link Part} interface.
 */
final class PartImpl implements Part {
    /**
     * The part has not been read yet. In this case the parser is in state
     * {@link EntityState#T_BODY}.
     */
    private static final int STATE_UNREAD = 0;
    
    /**
     * The part has been read into a memory or file based buffer.
     */
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
    
    private final WritableBlobFactory blobFactory;
    
    private List/*<Header>*/ headers;
    
    private int state = STATE_UNREAD;
    
    /**
     * The MIME parser from which the content of this part is read. This is only set if the state is
     * {@link #STATE_UNREAD} or {@link #STATE_STREAMING}.
     */
    private MimeTokenStream parser;
    
    /**
     * The content of this part. This is only set if the state is {@link #STATE_BUFFERED}.
     */
    private WritableBlob content;
    
    private final DataHandler dataHandler;
    
    private DetachableInputStream detachableInputStream;
    
    /**
     * The actual parts are constructed with the PartFactory.
     * @see org.apache.axiom.attachments.PartContentFactory
     * @param headers
     */
    PartImpl(WritableBlobFactory blobFactory, List headers, MimeTokenStream parser) {
        this.blobFactory = blobFactory;;
        this.headers = headers;
        this.parser = parser;
        this.dataHandler = new PartDataHandler(this);
    }
    
    public String getHeader(String name) {
        String value = null;
        for (int i=0, l=headers.size(); i<l; i++) {
            Header header = (Header)headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                value = header.getValue();
                break;
            }
        }
        if(log.isDebugEnabled()){
            log.debug("getHeader name=(" + name + ") value=(" + value +")");
        }
        return value;
    }

    public String getContentID() {
        return getHeader("content-id");
    }

    public String getContentType() {
        return getHeader("content-type");
    }
    
    /**
     * Get the content type that should be reported by {@link DataSource} instances created for this
     * part.
     * 
     * @return the content type
     */
    String getDataSourceContentType() {
        String ct = getContentType();
        return ct == null ? "application/octet-stream" : ct;
    }
    
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public long getSize() {
        return getContent().getSize();
    }

    private WritableBlob getContent() {
        switch (state) {
            case STATE_UNREAD:
                fetch();
                // Fall through
            case STATE_BUFFERED:
                return content;
            default:
                throw new IllegalStateException("The content of the MIME part has already been consumed");
        }
    }
    
    private static void checkParserState(EntityState state, EntityState expected) throws IllegalStateException {
        if (expected != state) {
            throw new IllegalStateException("Internal error: expected parser to be in state "
                    + expected + ", but got " + state);
        }
    }
    
    private InputStream getDecodedInputStream() {
        InputStream in;
        if ("quoted-printable".equalsIgnoreCase(getHeader("Content-Transfer-Encoding"))) {
            // Temporary workaround for AXIOM-467 while waiting for MIME4J 0.7.3:
            // use a copy of QuotedPrintableInputStream from the 0.7 branch.
            in = new QuotedPrintableInputStream(parser.getInputStream(), true);
        } else {
            in = parser.getDecodedInputStream();
        }
        if (log.isDebugEnabled()) {
            in = new DebugInputStream(in, log);
        }
        return in;
    }
    
    /**
     * Make sure that the MIME part has been fully read from the parser. If the part has not been
     * read yet, then it will be buffered. This method prepares the parser for reading the next part
     * in the stream.
     */
    void fetch() {
        switch (state) {
            case STATE_UNREAD:
                checkParserState(parser.getState(), EntityState.T_BODY);

                content = blobFactory.createBlob();
                if (log.isDebugEnabled()) {
                    log.debug("Using blob of type " + content.getClass().getName());
                }
                try {
                    content.readFrom(getDecodedInputStream());
                } catch (StreamCopyException ex) {
                    if (ex.getOperation() == StreamCopyException.READ) {
                        throw new OMException("Failed to fetch the MIME part content", ex.getCause());
                    } else {
                        throw new OMException("Failed to write the MIME part content to temporary storage", ex.getCause());
                    }
                }
                moveToNextPart();
                state = STATE_BUFFERED;
                break;
            case STATE_STREAMING:
                // If the stream is still open, buffer the remaining content
                try {
                    detachableInputStream.detach();
                } catch (IOException ex) {
                    throw new OMException(ex);
                }
                detachableInputStream = null;
                moveToNextPart();
                state = STATE_DISCARDED;
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
            } else if (state != EntityState.T_START_BODYPART && state != EntityState.T_END_MULTIPART) {
                throw new IllegalStateException("Internal error: unexpected parser state " + state);
            }
        } catch (IOException ex) {
            throw new OMException(ex);
        } catch (MimeException ex) {
            throw new OMException(ex);
        }
        parser = null;
    }
    
    InputStream getInputStream(boolean preserve) throws IOException {
        if (!preserve && state == STATE_UNREAD) {
            checkParserState(parser.getState(), EntityState.T_BODY);
            state = STATE_STREAMING;
            detachableInputStream = new DetachableInputStream(getDecodedInputStream());
            return detachableInputStream;
        } else {
            WritableBlob content = getContent();
            if (preserve) {
                return content.getInputStream();
            } else if (content instanceof MemoryBlob) {
                return ((MemoryBlob)content).readOnce();
            } else {
                return new ReadOnceInputStreamWrapper(this, content.getInputStream());
            }
        }
    }
    
    DataSource getDataSource() {
        WritableBlob blob = getContent();
        if (blob instanceof OverflowableBlob) {
            WritableBlob overflowBlob = ((OverflowableBlob)blob).getOverflowBlob();
            if (overflowBlob != null) {
                blob = overflowBlob;
            }
        }
        if (blob instanceof LegacyTempFileBlob) {
            return ((LegacyTempFileBlob)blob).getDataSource(getDataSourceContentType());
        } else {
            return null;
        }
    }

    void writeTo(OutputStream out) throws IOException {
        getContent().writeTo(out);
    }

    void releaseContent() throws IOException {
        switch (state) {
            case STATE_UNREAD:
                try {
                    EntityState state;
                    do {
                        state = parser.next();
                    } while (state != EntityState.T_START_BODYPART && state != EntityState.T_END_MULTIPART);
                } catch (MimeException ex) {
                    throw new OMException(ex);
                }
                state = STATE_DISCARDED;
                break;
            case STATE_BUFFERED:
                content.release();
        }
    }
}
