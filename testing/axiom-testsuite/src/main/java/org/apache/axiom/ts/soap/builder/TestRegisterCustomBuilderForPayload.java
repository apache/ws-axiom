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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.BlobOMDataSource;
import org.apache.axiom.om.ds.custombuilder.BlobOMDataSourceCustomBuilder;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder;
import org.apache.axiom.om.ds.custombuilder.CustomBuilderSupport;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.soap.SOAPSample;
import org.apache.axiom.ts.soap.SOAPSampleAdapter;
import org.xml.sax.InputSource;

public class TestRegisterCustomBuilderForPayload extends AxiomTestCase {
    private final SOAPSample message;

    public TestRegisterCustomBuilderForPayload(OMMetaFactory metaFactory, SOAPSample message) {
        super(metaFactory);
        this.message = message;
        addTestParameter("message", message.getName());
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPModelBuilder builder = message.getAdapter(SOAPSampleAdapter.class).getBuilder(metaFactory);
        ((CustomBuilderSupport)builder).registerCustomBuilder(
                CustomBuilder.Selector.PAYLOAD,
                new BlobOMDataSourceCustomBuilder(MemoryBlob.FACTORY, "utf-8"));
        OMElement payload = builder.getSOAPEnvelope().getBody().getFirstElement();
        if (message.getPayload() == null) {
            assertNull(payload);
        } else if (message.getPayload().getLocalName().equals("Fault")) {
            assertTrue(payload instanceof SOAPFault);
        } else {
            assertTrue(payload instanceof OMSourcedElement);
            BlobOMDataSource.Data data = (BlobOMDataSource.Data)((OMSourcedElement)payload).getObject(BlobOMDataSource.class);
            assertNotNull(data);
            InputSource is = new InputSource(data.getBlob().getInputStream());
            is.setEncoding(data.getEncoding());
            assertAbout(xml())
                    .that(is)
                    .ignoringNamespaceDeclarations()
                    .hasSameContentAs(message.getPayloadInputSource());
        }
    }
}
