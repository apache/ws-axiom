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

import org.apache.axiom.om.OMException;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.MimeTokenStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The MultipartAttachmentStreams class is used to create IncomingAttachmentInputStream objects when
 * the HTTP stream shows a marked separation between the SOAP and each attachment parts. Unlike the
 * DIME version, this class will use the BoundaryDelimitedStream to parse data in the SwA format.
 * Another difference between the two is that the MultipartAttachmentStreams class must also provide
 * a way to hold attachment parts parsed prior to where the SOAP part appears in the HTTP stream
 * (i.e. the root part of the multipart-related message). Our DIME counterpart didn't have to worry
 * about this since the SOAP part is guaranteed to be the first in the stream. But since SwA has no
 * such guarantee, we must fall back to caching these first parts. Afterwards, we can stream the
 * rest of the attachments that are after the SOAP part of the request message.
 */
final class MultipartAttachmentStreams extends IncomingAttachmentStreams {
    private final MimeTokenStream parser;

    public MultipartAttachmentStreams(MimeTokenStream parser) throws OMException {
        this.parser = parser;
    }

    public IncomingAttachmentInputStream getNextStream() throws OMException {
        IncomingAttachmentInputStream stream;

        if (!isReadyToGetNextStream()) {
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
            
            List fields = new ArrayList();
            while (parser.next() == EntityState.T_FIELD) {
                fields.add(parser.getField());
            }
            
            if (parser.next() != EntityState.T_BODY) {
                throw new IllegalStateException();
            }
            
            stream = new IncomingAttachmentInputStream(parser.getInputStream(), this);
    
            for (Iterator it = fields.iterator(); it.hasNext(); ) {
                Field field = (Field)it.next();
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

        setReadyToGetNextStream(false);
        return stream;
    }
}
