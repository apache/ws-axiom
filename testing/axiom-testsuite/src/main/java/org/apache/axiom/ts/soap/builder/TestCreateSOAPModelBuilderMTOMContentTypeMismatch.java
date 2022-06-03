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
package org.apache.axiom.ts.soap.builder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.ts.soap.SOAPSample;
import org.apache.axiom.ts.soap.SOAPSampleSet;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;
import org.apache.axiom.util.UIDGenerator;

/**
 * Tests that {@link OMXMLBuilderFactory#createSOAPModelBuilder(OMMetaFactory, MultipartBody)}
 * produces an error if the SOAP version used in the root part doesn't match the Content-Type of the
 * message.
 */
public class TestCreateSOAPModelBuilderMTOMContentTypeMismatch extends SOAPTestCase {
    public TestCreateSOAPModelBuilderMTOMContentTypeMismatch(
            OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        final SOAPSample sample = SOAPSampleSet.NO_HEADER.getMessage(spec);
        // Generate an MTOM message with the wrong content type
        MimeMessage message = new MimeMessage((Session) null);
        MimeMultipart mp = new MimeMultipart("related");
        MimeBodyPart bp = new MimeBodyPart();
        String contentID = "<" + UIDGenerator.generateContentId() + ">";
        bp.setDataHandler(
                new DataHandler(
                        new DataSource() {
                            @Override
                            public String getContentType() {
                                return "application/xop+xml; charset=\""
                                        + sample.getEncoding()
                                        + "\"; type=\""
                                        + spec.getAltSpec().getContentType()
                                        + "\"";
                            }

                            @Override
                            public InputStream getInputStream() throws IOException {
                                return sample.getInputStream();
                            }

                            @Override
                            public String getName() {
                                return null;
                            }

                            @Override
                            public OutputStream getOutputStream() {
                                throw new UnsupportedOperationException();
                            }
                        }));
        bp.addHeader("Content-Transfer-Encoding", "binary");
        bp.addHeader("Content-ID", contentID);
        mp.addBodyPart(bp);
        message.setContent(mp);
        message.saveChanges();
        ContentType contentType =
                new ContentType(message.getContentType())
                        .toBuilder()
                                .setParameter("type", "application/xop+xml")
                                .setParameter("start", contentID)
                                .setParameter("start-info", spec.getAltSpec().getContentType())
                                .build();
        MemoryBlob blob = Blobs.createMemoryBlob();
        OutputStream out = blob.getOutputStream();
        mp.writeTo(out);
        out.close();
        // Now attempt to create an Axiom builder
        try {
            OMXMLBuilderFactory.createSOAPModelBuilder(
                    metaFactory,
                    MultipartBody.builder()
                            .setInputStream(blob.getInputStream())
                            .setContentType(contentType)
                            .build());
            fail("Expected SOAPProcessingException");
        } catch (SOAPProcessingException ex) {
            // Expected
        }
    }
}
