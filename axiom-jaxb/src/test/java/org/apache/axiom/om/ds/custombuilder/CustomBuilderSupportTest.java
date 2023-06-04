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

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

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
import org.apache.axiom.testutils.activation.TestDataSource;
import org.apache.axiom.testutils.blob.RandomBlob;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.apache.axiom.util.activation.DataHandlerUtils;
import org.junit.jupiter.api.Test;

public class CustomBuilderSupportTest {
    private OMElement createTestDocument(DataHandler dh) {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement document = factory.createOMElement(new QName("urn:test", "document"));
        OMElement name = factory.createOMElement(new QName("name"));
        name.setText("some name");
        document.addChild(name);
        OMElement content = factory.createOMElement(new QName("content"));
        content.addChild(factory.createOMText(DataHandlerUtils.toBlob(dh), true));
        document.addChild(content);
        return document;
    }

    private void test(DataHandler dh, OMXMLParserWrapper builder, boolean same) throws Exception {
        JAXBCustomBuilder customBuilder =
                new JAXBCustomBuilder(JAXBContext.newInstance(MyDocument.class));
        ((CustomBuilderSupport) builder)
                .registerCustomBuilder(CustomBuilder.Selector.PAYLOAD, customBuilder);
        builder.getDocumentElement().build();
        MyDocument myDocument = (MyDocument) customBuilder.getJaxbObject();
        if (same) {
            assertThat(myDocument.getContent()).isSameAs(dh);
        } else {
            assertThat(myDocument.getContent()).isNotSameAs(dh);
            IOTestUtils.compareStreams(
                    dh.getInputStream(),
                    "expected",
                    myDocument.getContent().getInputStream(),
                    "actual");
        }
    }

    @Test
    public void testRegisterCustomBuilderForPayloadJAXBPlain() throws Exception {
        DataHandler dh = DataHandlerUtils.toDataHandler(new RandomBlob(10000));
        MemoryBlob blob = Blobs.createMemoryBlob();
        OutputStream out = blob.getOutputStream();
        createTestDocument(dh).serialize(out);
        out.close();
        test(dh, OMXMLBuilderFactory.createOMBuilder(blob.getInputStream()), false);
    }

    @Test
    public void testRegisterCustomBuilderForPayloadJAXBWithDataHandlerReaderExtension()
            throws Exception {
        DataHandler dh = new DataHandler(new TestDataSource('X', Integer.MAX_VALUE));
        OMElement document = createTestDocument(dh);
        test(dh, OMXMLBuilderFactory.createStAXOMBuilder(document.getXMLStreamReader()), true);
    }

    @Test
    public void testRegisterCustomBuilderForPayloadJAXBWithXOP() throws Exception {
        DataHandler dh = DataHandlerUtils.toDataHandler(new RandomBlob(10000));
        MemoryBlob blob = Blobs.createMemoryBlob();
        OutputStream out = blob.getOutputStream();
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        createTestDocument(dh).serialize(out, format);
        out.close();
        MultipartBody mb =
                MultipartBody.builder()
                        .setInputStream(blob.getInputStream())
                        .setContentType(format.getContentType())
                        .build();
        test(dh, OMXMLBuilderFactory.createOMBuilder(StAXParserConfiguration.DEFAULT, mb), false);
    }
}
