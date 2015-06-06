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

import java.io.ByteArrayInputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.ByteArrayDataSource;
import org.apache.axiom.om.ds.ByteArrayDataSource.ByteArray;
import org.apache.axiom.om.ds.custombuilder.ByteArrayCustomBuilder;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
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
        ((StAXSOAPModelBuilder)builder).registerCustomBuilderForPayload(new ByteArrayCustomBuilder("utf-8"));
        OMElement payload = builder.getSOAPEnvelope().getBody().getFirstElement();
        if (message.getPayload() == null) {
            assertNull(payload);
        } else {
            assertTrue(payload instanceof OMSourcedElement);
            ByteArray byteArray = (ByteArray)((OMSourcedElement)payload).getObject(ByteArrayDataSource.class);
            assertNotNull(byteArray);
            InputSource is = new InputSource(new ByteArrayInputStream(byteArray.bytes));
            is.setEncoding(byteArray.encoding);
            assertAbout(xml())
                    .that(xml(is))
                    .ignoringNamespaceDeclarations()
                    .hasSameContentAs(xml(message.getPayloadInputSource()));
        }
    }
}
