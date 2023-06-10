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
package org.apache.axiom.ts.xml;

import java.io.IOException;
import java.io.InputStream;

import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.SingleBody;
import org.apache.james.mime4j.dom.field.FieldName;
import org.apache.james.mime4j.field.ContentTypeFieldLenientImpl;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.MimeConfig;
import org.apache.james.mime4j.stream.RawField;

public abstract class MIMESample extends MessageSample {
    private final String contentType;
    private Multipart multipart;

    protected MIMESample(MessageContent content, String name, String contentType) {
        super(content, name);
        this.contentType = contentType;
    }

    @Override
    public final String getContentType() {
        return contentType;
    }

    private String getParameter(String name) {
        return ContentTypeFieldLenientImpl.PARSER
                .parse(new RawField(FieldName.CONTENT_TYPE, contentType), DecodeMonitor.SILENT)
                .getParameter(name);
    }

    public final String getStart() {
        String start = getParameter("start");
        if (start.startsWith("<") && start.endsWith(">")) {
            return start.substring(1, start.length() - 1);
        } else {
            return start;
        }
    }

    public final String getBoundary() {
        return getParameter("boundary");
    }

    private final synchronized Multipart getMultipart() {
        if (multipart == null) {
            try {
                DefaultMessageBuilder defaultMessageBuilder = new DefaultMessageBuilder();
                defaultMessageBuilder.setMimeEntityConfig(
                        MimeConfig.custom().setHeadlessParsing(contentType).build());
                defaultMessageBuilder.setDecodeMonitor(DecodeMonitor.SILENT);
                multipart =
                        (Multipart) defaultMessageBuilder.parseMessage(getInputStream()).getBody();
            } catch (IOException ex) {
                throw new Error(ex);
            }
        }
        return multipart;
    }

    public final InputStream getPart(int part) {
        try {
            return ((SingleBody) getMultipart().getBodyParts().get(part).getBody())
                    .getInputStream();
        } catch (IOException ex) {
            throw new Error(ex);
        }
    }

    public final InputStream getPart(String cid) {
        try {
            Multipart mp = getMultipart();
            for (Entity entity : mp.getBodyParts()) {
                Field contentId = entity.getHeader().getField(FieldName.CONTENT_ID);
                if (contentId != null
                        && (contentId.getBody().equals(cid)
                                || contentId.getBody().equals("<" + cid + ">"))) {
                    return ((SingleBody) entity.getBody()).getInputStream();
                }
            }
        } catch (IOException ex) {
            throw new Error(ex);
        }
        throw new IllegalArgumentException("Part " + cid + " not found");
    }
}
