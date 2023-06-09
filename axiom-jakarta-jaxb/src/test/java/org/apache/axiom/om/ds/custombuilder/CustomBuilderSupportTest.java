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
package org.apache.axiom.om.ds.custombuilder;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.OutputStream;

import javax.xml.namespace.QName;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.testutils.blob.RandomBlob;
import org.apache.axiom.testutils.blob.TestBlob;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.apache.axiom.util.activation.DataHandlerUtils;
import org.junit.jupiter.api.Test;

import jakarta.xml.bind.JAXBContext;

public class CustomBuilderSupportTest {
    private OMElement createTestDocument(Blob blob) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement document = factory.createOMElement(new QName("urn:test", "document"));
        OMElement name = factory.createOMElement(new QName("name"));
        name.setText("some name");
        document.addChild(name);
        OMElement content = factory.createOMElement(new QName("content"));
        content.addChild(factory.createOMText(blob, true));
        document.addChild(content);
        return document;
    }

    private void test(Blob blob, OMXMLParserWrapper builder, boolean same) throws Exception {
        JAXBCustomBuilder customBuilder =
                new JAXBCustomBuilder(JAXBContext.newInstance(MyDocument.class));
        ((CustomBuilderSupport) builder)
                .registerCustomBuilder(CustomBuilder.Selector.PAYLOAD, customBuilder);
        builder.getDocumentElement().build();
        MyDocument myDocument = (MyDocument) customBuilder.getJaxbObject();
        Blob actualBlob = DataHandlerUtils.toBlob(myDocument.getContent());
        if (same) {
            assertThat(actualBlob).isSameAs(blob);
        } else {
            assertThat(actualBlob).isNotSameAs(blob);
            IOTestUtils.compareStreams(
                    blob.getInputStream(),
                    "expected",
                    myDocument.getContent().getInputStream(),
                    "actual");
        }
    }

    @Test
    public void testRegisterCustomBuilderForPayloadJAXBPlain() throws Exception {
        Blob contentBlob = new RandomBlob(10000);
        MemoryBlob blob = Blobs.createMemoryBlob();
        OutputStream out = blob.getOutputStream();
        createTestDocument(contentBlob).serialize(out);
        out.close();
        test(contentBlob, OMXMLBuilderFactory.createOMBuilder(blob.getInputStream()), false);
    }

    @Test
    public void testRegisterCustomBuilderForPayloadJAXBWithDataHandlerReaderExtension()
            throws Exception {
        Blob contentBlob = new TestBlob('X', Integer.MAX_VALUE);
        OMElement document = createTestDocument(contentBlob);
        test(
                contentBlob,
                OMXMLBuilderFactory.createStAXOMBuilder(document.getXMLStreamReader()),
                true);
    }

    @Test
    public void testRegisterCustomBuilderForPayloadJAXBWithXOP() throws Exception {
        Blob contentBlob = new RandomBlob(10000);
        MemoryBlob blob = Blobs.createMemoryBlob();
        OutputStream out = blob.getOutputStream();
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        createTestDocument(contentBlob).serialize(out, format);
        out.close();
        MultipartBody mb =
                MultipartBody.builder()
                        .setInputStream(blob.getInputStream())
                        .setContentType(format.getContentType())
                        .build();
        test(
                contentBlob,
                OMXMLBuilderFactory.createOMBuilder(StAXParserConfiguration.DEFAULT, mb),
                false);
    }
}
