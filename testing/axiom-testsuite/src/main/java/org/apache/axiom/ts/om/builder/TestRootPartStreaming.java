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
package org.apache.axiom.ts.om.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.testutils.io.InstrumentedInputStream;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that the content of the root part of an XOP/MTOM message is not buffered (i.e. read
 * entirely into memory) unless an attachment part is accessed. This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-403">AXIOM-403</a>.
 */
public class TestRootPartStreaming extends AxiomTestCase {
    public TestRootPartStreaming(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();

        // Programmatically create the message
        OMElement orgRoot = factory.createOMElement("root", null);
        for (int i = 0; i < 10000; i++) {
            factory.createOMElement("child", null, orgRoot).setText("Some text content");
        }

        // Serialize the message as XOP even if there will be no attachment parts
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        orgRoot.serialize(baos, format);

        // Parse the message and monitor the number of bytes read
        InstrumentedInputStream in =
                new InstrumentedInputStream(new ByteArrayInputStream(baos.toByteArray()));
        OMXMLParserWrapper builder =
                OMXMLBuilderFactory.createOMBuilder(
                        factory,
                        StAXParserConfiguration.DEFAULT,
                        MultipartBody.builder()
                                .setInputStream(in)
                                .setContentType(format.getContentType())
                                .build());
        OMElement root = builder.getDocumentElement();
        long count1 = in.getCount();
        XMLStreamReader reader = root.getXMLStreamReader(false);
        while (reader.hasNext()) {
            reader.next();
        }
        long count2 = in.getCount();

        // We expect that after requesting the document element, only a small part (corresponding to
        // the size of the parser buffer) should have been read:
        assertTrue(count1 < count2 / 2);
    }
}
