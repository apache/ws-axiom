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
package org.apache.axiom.ts.om.xop;

import static com.google.common.truth.Truth.assertThat;
import static org.apache.axiom.ts.xml.XOPSample.XOP_SPEC_SAMPLE;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;

import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.ts.AxiomTestCase;

public class TestSetOptimize extends AxiomTestCase {
    private final boolean optimize;

    public TestSetOptimize(OMMetaFactory metaFactory, boolean optimize) {
        super(metaFactory);
        this.optimize = optimize;
        addTestParameter("optimize", optimize);
    }

    @Override
    protected void runTest() throws Throwable {
        InputStream in = XOP_SPEC_SAMPLE.getInputStream();
        try {
            OMDocument document =
                    OMXMLBuilderFactory.createOMBuilder(
                                    metaFactory.getOMFactory(),
                                    StAXParserConfiguration.DEFAULT,
                                    MultipartBody.builder()
                                            .setInputStream(in)
                                            .setContentType(XOP_SPEC_SAMPLE.getContentType())
                                            .build())
                            .getDocument();
            for (Iterator<OMSerializable> it = document.getDescendants(false); it.hasNext(); ) {
                OMSerializable node = it.next();
                if (node instanceof OMText) {
                    OMText text = (OMText) node;
                    if (text.isBinary()) {
                        text.setOptimize(optimize);
                    }
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OMOutputFormat format = new OMOutputFormat();
            format.setDoOptimize(true);
            document.serialize(out, format);

            Multipart mp =
                    new MimeMultipart(
                            new ByteArrayDataSource(out.toByteArray(), format.getContentType()));
            assertThat(mp.getCount()).isEqualTo(optimize ? 3 : 1);
        } finally {
            in.close();
        }
    }
}
