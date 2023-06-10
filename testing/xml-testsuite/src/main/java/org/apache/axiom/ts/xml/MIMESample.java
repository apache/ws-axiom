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
import java.io.OutputStream;

import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.ParseException;

public abstract class MIMESample extends MessageSample {
    private final String contentType;
    private MimeMultipart multipart;

    protected MIMESample(MessageContent content, String name, String contentType) {
        super(content, name);
        this.contentType = contentType;
    }

    @Override
    public final String getContentType() {
        return contentType;
    }

    private String getParameter(String name) {
        try {
            return new ContentType(contentType).getParameter(name);
        } catch (ParseException ex) {
            // MIMEResource objects are only defined as constants. Therefore we
            // will never get here under normal conditions.
            throw new Error(ex);
        }
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

    private final synchronized MimeMultipart getMultipart() {
        if (multipart == null) {
            try {
                multipart =
                        new MimeMultipart(
                                new DataSource() {
                                    @Override
                                    public OutputStream getOutputStream() throws IOException {
                                        throw new UnsupportedOperationException();
                                    }

                                    @Override
                                    public String getName() {
                                        return null;
                                    }

                                    @Override
                                    public InputStream getInputStream() throws IOException {
                                        return MIMESample.this.getInputStream();
                                    }

                                    @Override
                                    public String getContentType() {
                                        return MIMESample.this.getContentType();
                                    }
                                });
                // Force the implementation to parse the message
                multipart.getCount();
            } catch (MessagingException ex) {
                throw new Error(ex);
            }
        }
        return multipart;
    }

    public final InputStream getPart(int part) {
        try {
            return getMultipart().getBodyPart(part).getInputStream();
        } catch (IOException ex) {
            throw new Error(ex);
        } catch (MessagingException ex) {
            throw new Error(ex);
        }
    }

    public final InputStream getPart(String cid) {
        try {
            MimeMultipart mp = getMultipart();
            BodyPart part = mp.getBodyPart(cid);
            if (part == null) {
                part = mp.getBodyPart("<" + cid + ">");
            }
            if (part == null) {
                throw new IllegalArgumentException("Part " + cid + " not found");
            }
            return part.getInputStream();
        } catch (IOException ex) {
            throw new Error(ex);
        } catch (MessagingException ex) {
            throw new Error(ex);
        }
    }
}
