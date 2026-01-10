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

import java.util.Iterator;

import org.apache.axiom.mime.Header;
import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.mime.Part;
import org.apache.axiom.om.OMException;

/**
 * Container for AttachmentStream s. This class provides an SwA like access mechanism, allowing
 * applications to access the streams directly. Access it intentionally restrictred to either SwA
 * like (stream access), or MTOM like (part/data handler access via blob id), not both.
 */
public final class IncomingAttachmentStreams {
    private final Part rootPart;
    private final Iterator<Part> partIterator;

    /**
     * Boolean indicating weather or not the next stream can be read (next stream cannot be read
     * until previous is consumed
     */
    private boolean readyToGetNextStream = true;

    IncomingAttachmentStreams(MultipartBody message) {
        rootPart = message.getRootPart();
        partIterator = message.iterator();
    }

    /**
     * @return True if the next stream can be read, false otherwise.
     */
    public final boolean isReadyToGetNextStream() {
        return readyToGetNextStream;
    }

    /**
     * Set the ready flag. Intended for the inner class to use.
     *
     * @param ready
     */
    void setReadyToGetNextStream(boolean ready) {
        readyToGetNextStream = ready;
    }

    /**
     * Returns the next attachment stream in sequence.
     *
     * @return The next stream or null if no additional streams are left.
     */
    public IncomingAttachmentInputStream getNextStream() throws OMException {
        if (!readyToGetNextStream) {
            throw new IllegalStateException("nextStreamNotReady");
        }

        Part part = null;
        while (part == null && partIterator.hasNext()) {
            part = partIterator.next();
            // Skip the root part
            if (part == rootPart) {
                part = null;
            }
        }

        if (part != null) {
            IncomingAttachmentInputStream stream =
                    new IncomingAttachmentInputStream(part.getInputStream(false), this);

            for (Header header : part.getHeaders()) {
                String name = header.getName();
                String value = header.getValue();
                if (IncomingAttachmentInputStream.HEADER_CONTENT_ID.equals(name)
                        || IncomingAttachmentInputStream.HEADER_CONTENT_TYPE.equals(name)
                        || IncomingAttachmentInputStream.HEADER_CONTENT_LOCATION.equals(name)) {
                    value = value.trim();
                }
                stream.addHeader(name, value);
            }

            readyToGetNextStream = false;
            return stream;
        } else {
            return null;
        }
    }
}
