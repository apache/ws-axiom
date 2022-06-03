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
package org.apache.axiom.ts.om.element;

import java.io.ByteArrayInputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.testutils.activation.RandomDataSource;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.commons.codec.binary.Base64;

/**
 * Tests that {@link OMElement#getText()} returns the expected value (i.e. base64 encoded data) for
 * an element that has an {@link OMText} child constructed from a {@link DataHandler}.
 */
public class TestGetTextBinary extends AxiomTestCase {
    private final boolean compact;

    public TestGetTextBinary(OMMetaFactory metaFactory, boolean compact) {
        super(metaFactory);
        this.compact = compact;
        addTestParameter("compact", compact);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        DataSource ds = new RandomDataSource(99999, 1000);
        OMElement element = factory.createOMElement("elem", null);
        element.addChild(factory.createOMText(new DataHandler(ds), false));
        if (compact) {
            // Only the builder can create a compact element containing a DataHandler
            element =
                    OMXMLBuilderFactory.createStAXOMBuilder(factory, element.getXMLStreamReader())
                            .getDocumentElement();
            element.build();
        }
        IOTestUtils.compareStreams(
                ds.getInputStream(),
                new ByteArrayInputStream(Base64.decodeBase64(element.getText())));
    }
}
