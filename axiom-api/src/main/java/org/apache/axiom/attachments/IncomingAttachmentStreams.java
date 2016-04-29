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
import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMException;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.MimeTokenStream;

/**
 * Container for AttachmentStream s. This class provides an SwA like access mechanism, allowing
 * applications to access the streams directly. Access it intentionally restrictred to either SwA
 * like (stream access), or MTOM like (part/data handler access via blob id), not both.
 */
public final class IncomingAttachmentStreams {
    private final MimeTokenStream parser;

    /**
     * Boolean indicating weather or not the next stream can be read (next stream cannot be read until
     * previous is consumed
     */
    private boolean readyToGetNextStream = true;

    IncomingAttachmentStreams(MimeTokenStream parser) {
        this.parser = parser;
    }

    /** @return True if the next stream can be read, false otherwise. */
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
        IncomingAttachmentInputStream stream;

        if (!readyToGetNextStream) {
            throw new IllegalStateException("nextStreamNotReady");
        }

        try {
            if (parser.getState() == EntityState.T_BODY) {
                if (parser.next() != EntityState.T_END_BODYPART) {
                    throw new IllegalStateException();
                }
                parser.next();
            }
            
            if (parser.getState() != EntityState.T_START_BODYPART) {
                return null;
            }
            
            if (parser.next() != EntityState.T_START_HEADER) {
                throw new IllegalStateException();
            }
            
            List<Field> fields = new ArrayList<Field>();
            while (parser.next() == EntityState.T_FIELD) {
                fields.add(parser.getField());
            }
            
            if (parser.next() != EntityState.T_BODY) {
                throw new IllegalStateException();
            }
            
            stream = new IncomingAttachmentInputStream(parser.getInputStream(), this);
    
            for (Field field : fields) {
                String name = field.getName();
                String value = field.getBody();
                if (IncomingAttachmentInputStream.HEADER_CONTENT_ID.equals(name)
                        || IncomingAttachmentInputStream.HEADER_CONTENT_TYPE.equals(name)
                        || IncomingAttachmentInputStream.HEADER_CONTENT_LOCATION.equals(name)) {
                    value = value.trim();
                }
                stream.addHeader(name, value);
            }
        } catch (MimeException ex) {
            throw new OMException(ex);
        } catch (IOException ex) {
            throw new OMException(ex);
        }

        readyToGetNextStream = false;
        return stream;
    }
}