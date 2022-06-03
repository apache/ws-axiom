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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
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
 * containing a {@link DataHandler} is correctly serialized.
 */
public class TestDataHandlerSerializationWithoutMTOM extends AxiomTestCase {
    public TestDataHandlerSerializationWithoutMTOM(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPFactory factory = metaFactory.getSOAP11Factory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);

        // Construct the original message
        DocumentBean orgObject = new DocumentBean();
        orgObject.setId("123456");
        orgObject.setContent(new DataHandler(new TextDataSource("some content", "utf-8", "plain")));
        SOAPEnvelope orgEnvelope = factory.getDefaultEnvelope();
        OMSourcedElement element =
                factory.createOMElement(new JAXBOMDataSource(context, orgObject));
        orgEnvelope.getBody().addChild(element);

        // Serialize the message
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        orgEnvelope.serialize(out);
        assertFalse(element.isExpanded());

        SOAPEnvelope envelope =
                OMXMLBuilderFactory.createSOAPModelBuilder(
                                metaFactory, new ByteArrayInputStream(out.toByteArray()), null)
                        .getSOAPEnvelope();
        DocumentBean object =
                (DocumentBean)
                        context.createUnmarshaller()
                                .unmarshal(
                                        envelope.getBody()
                                                .getFirstElement()
                                                .getXMLStreamReader(false));
        assertEquals(
                "some content", IOUtils.toString(object.getContent().getInputStream(), "utf-8"));
    }
}
