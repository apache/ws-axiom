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
package org.apache.axiom.ts.om.sourcedelement.jaxb;

import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.ds.jaxb.JAXBOMDataSource;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.testutils.activation.TextDataSource;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.jaxb.beans.DocumentBean;
import org.apache.commons.io.IOUtils;

/**
 * Tests that an {@link OMSourcedElement} backed by a {@link JAXBOMDataSource} with a bean
 * containing a {@link DataHandler} is correctly serialized to MTOM.
 */
public class TestDataHandlerSerializationWithMTOM extends AxiomTestCase {
    public TestDataHandlerSerializationWithMTOM(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPFactory factory = metaFactory.getSOAP11Factory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);

        // Construct the original message
        DocumentBean object = new DocumentBean();
        object.setId("123456");
        object.setContent(new DataHandler(new TextDataSource("some content", "utf-8", "plain")));
        SOAPEnvelope orgEnvelope = factory.getDefaultEnvelope();
        OMSourcedElement element = factory.createOMElement(new JAXBOMDataSource(context, object));
        orgEnvelope.getBody().addChild(element);

        // Serialize the message
        OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        MemoryBlob blob = Blobs.createMemoryBlob();
        OutputStream out = blob.getOutputStream();
        orgEnvelope.serialize(out, format);
        out.close();
        assertFalse(element.isExpanded());

        // Parse the serialized message
        MultipartBody mb =
                MultipartBody.builder()
                        .setInputStream(blob.getInputStream())
                        .setContentType(format.getContentType())
                        .build();
        assertEquals(2, mb.getPartCount());
        SOAPEnvelope envelope =
                OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory, mb).getSOAPEnvelope();
        OMElement contentElement =
                envelope.getBody()
                        .getFirstElement()
                        .getFirstChildWithName(
                                new QName("http://ws.apache.org/axiom/test/jaxb", "content"));
        OMText content = (OMText) contentElement.getFirstOMChild();
        assertTrue(content.isBinary());
        assertTrue(content.isOptimized());
        DataHandler dh = content.getDataHandler();
        assertEquals("some content", IOUtils.toString(dh.getInputStream(), "utf-8"));
    }
}
