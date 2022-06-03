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

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder;
import org.apache.axiom.om.ds.custombuilder.CustomBuilderSupport;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.apache.axiom.ts.AxiomTestCase;

public abstract class RegisterCustomBuilderForPayloadJAXBTestCase extends AxiomTestCase {
    public RegisterCustomBuilderForPayloadJAXBTestCase(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected final OMElement createTestDocument(DataHandler dh) {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement document = factory.createOMElement(new QName("urn:test", "document"));
        OMElement name = factory.createOMElement(new QName("name"));
        name.setText("some name");
        document.addChild(name);
        OMElement content = factory.createOMElement(new QName("content"));
        content.addChild(factory.createOMText(dh, true));
        document.addChild(content);
        return document;
    }

    protected final void test(DataHandler dh, OMXMLParserWrapper builder, boolean same)
            throws Exception {
        JAXBCustomBuilder customBuilder =
                new JAXBCustomBuilder(JAXBContext.newInstance(MyDocument.class));
        ((CustomBuilderSupport) builder)
                .registerCustomBuilder(CustomBuilder.Selector.PAYLOAD, customBuilder);
        builder.getDocumentElement().build();
        MyDocument myDocument = (MyDocument) customBuilder.getJaxbObject();
        if (same) {
            assertSame(dh, myDocument.getContent());
        } else {
            assertNotSame(dh, myDocument.getContent());
            IOTestUtils.compareStreams(
                    dh.getInputStream(),
                    "expected",
                    myDocument.getContent().getInputStream(),
                    "actual");
        }
    }
}
