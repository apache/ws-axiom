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

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.mime.DataHandlerFactory;
import org.apache.axiom.mime.Header;
import org.apache.axiom.mime.Part;
import org.apache.axiom.om.OMException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.MimeConfig;
import org.apache.james.mime4j.stream.MimeTokenStream;
import org.apache.james.mime4j.stream.RecursionMode;

/**
 * {@link AttachmentsDelegate} implementation that represents a MIME multipart message read from a
 * stream.
 */
class MIMEMessage implements Iterable<Part> {
    private static final Log log = LogFactory.getLog(MIMEMessage.class);
    
    /** <code>ContentType</code> of the MIME message */
    private final ContentType contentType;
    private final String rootPartContentID;
    private final MimeTokenStream parser;
    
    /**
     * Stores the already parsed MIME parts by Content IDs.
     */
    private final Map<String,PartImpl> partMap = new HashMap<String,PartImpl>();

    /**
     * The MIME part currently being processed.
     */
    private PartImpl currentPart;
    
    private PartImpl firstPart;
    private PartImpl rootPart;

    private final WritableBlobFactory<?> attachmentBlobFactory;
    private final DataHandlerFactory dataHandlerFactory;
    
    MIMEMessage(InputStream inStream, String contentTypeString,
            WritableBlobFactory<?> attachmentBlobFactory,
            DataHandlerFactory dataHandlerFactory) throws OMException {
        this.attachmentBlobFactory = attachmentBlobFactory;
        this.dataHandlerFactory = dataHandlerFactory;
        try {
            contentType = new ContentType(contentTypeString);
        } catch (ParseException e) {
            throw new OMException(
                    "Invalid Content Type Field in the Mime Message"
                    , e);
        }

        String start = contentType.getParameter("start");
        rootPartContentID = start == null ? null : normalizeContentID(start);

        MimeConfig config = new MimeConfig();
        config.setStrictParsing(true);
        parser = new MimeTokenStream(config);
        parser.setRecursionMode(RecursionMode.M_NO_RECURSE);
        parser.parseHeadless(inStream, contentTypeString);
        
        // Move the parser to the beginning of the first part
        while (parser.getState() != EntityState.T_START_BODYPART) {
            try {
                parser.next();
            } catch (IOException ex) {
                throw new OMException(ex);
            } catch (MimeException ex) {
                throw new OMException(ex);
            }
        }
    }

    private static String normalizeContentID(String contentID) {
        contentID = contentID.trim();
        if (contentID.length() >= 2 && contentID.charAt(0) == '<'
                && contentID.charAt(contentID.length()-1) == '>') {
            contentID = contentID.substring(1, contentID.length()-1);
        }
        // There is some evidence that some broken MIME implementations add
        // a "cid:" prefix to the Content-ID; remove it if necessary.
        if (contentID.length() > 4 && contentID.startsWith("cid:")) {
            contentID = contentID.substring(4);
        }
        return contentID;
    }

    DataHandlerFactory getDataHandlerFactory() {
        return dataHandlerFactory;
    }

    ContentType getContentType() {
        return contentType;
    }

    DataHandler getDataHandler(String contentID) {
        do {
            PartImpl part = partMap.get(contentID);
            if (part != null) {
                return part.getDataHandler();
            }
        } while (getNextPart() != null);
        return null;
    }

    PartImpl getFirstPart() {
        if (firstPart == null) {
            getNextPart();
        }
        return firstPart;
    }

    Part getRootPart() {
        do {
            if (rootPart != null) {
                return rootPart;
            }
        } while (getNextPart() != null);
        throw new OMException(
                "Mandatory root MIME part is missing");
    }

    PartImpl getNextPart() throws OMException {
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
                    
                    if (log.isDebugEnabled()){
                        log.debug("addHeader: (" + name + ") value=(" + value +")");
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
                
                PartImpl part = new PartImpl(this, isRootPart ? MemoryBlob.FACTORY : attachmentBlobFactory, partContentID, headers, parser);
                if (currentPart == null) {
                    firstPart = part;
                } else {
                    currentPart.setNextPart(part);
                }
                currentPart = part;
            } catch (IOException ex) {
                throw new OMException(ex);
            } catch (MimeException ex) {
                throw new OMException(ex);
            }

            if (partContentID != null) {
                if (partMap.containsKey(partContentID)) {
                    throw new OMException(
                            "Two MIME parts with the same Content-ID not allowed.");
                }
                partMap.put(partContentID, currentPart);
            }
            if (isRootPart) {
                rootPart = currentPart;
            }
        }
        return currentPart;
    }

    private static void checkParserState(EntityState state, EntityState expected) throws IllegalStateException {
        if (expected != state) {
            throw new IllegalStateException("Internal error: expected parser to be in state "
                    + expected + ", but got " + state);
        }
    }

    @Override
    public Iterator<Part> iterator() {
        return new PartIterator(this);
    }
}
