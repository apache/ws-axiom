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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.soap.MTOMSample;

public class TestSerialize extends AxiomTestCase {
    private final boolean base64;

    public TestSerialize(OMMetaFactory metaFactory, boolean base64) {
        super(metaFactory);
        this.base64 = base64;
        addTestParameter("base64", base64);
    }

    @Override
    protected void runTest() throws Throwable {
        MTOMSample testMessage = MTOMSample.SAMPLE1;

        // Read in message: SOAPPart and 2 image attachments
        InputStream inStream = testMessage.getInputStream();
        MultipartBody mb =
                MultipartBody.builder()
                        .setInputStream(inStream)
                        .setContentType(testMessage.getContentType())
                        .build();

        OMOutputFormat oof = new OMOutputFormat();
        oof.setDoOptimize(true);
        oof.setMimeBoundary(testMessage.getBoundary());
        oof.setRootContentId(testMessage.getStart());
        if (base64) {
            oof.setProperty(
                    OMOutputFormat.USE_CTE_BASE64_FOR_NON_TEXTUAL_ATTACHMENTS, Boolean.TRUE);
        }

        // Write out the message
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        OMXMLParserWrapper builder =
                OMXMLBuilderFactory.createOMBuilder(
                        metaFactory.getOMFactory(), StAXParserConfiguration.DEFAULT, mb);
        OMElement om = builder.getDocumentElement();
        om.serialize(baos, oof);
        om.close(false);
        String out = baos.toString();

        if (base64) {
            // Do a quick check to see if the data is base64 and is
            // writing base64 compliant code.
            assertTrue(out.indexOf("base64") != -1);
            assertTrue(out.indexOf("GBgcGBQgHBwcJCQgKDBQNDAsL") != -1);
        } else {
            assertTrue(out.indexOf("base64") == -1);
        }
    }
}
