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

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.core.stream.stax.StAX;
import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMContainer#serializeAndConsume(XMLStreamWriter)} throws an appropriate
 * exception if the part of the tree has already been consumed using {@link
 * OMContainer#getXMLStreamReaderWithoutCaching()}.
 */
// TODO: in this scenario we should trigger a NodeUnavailableException as well; fix this with
// AXIOM-288
public class TestSerializeAndConsumeConsumed extends AxiomTestCase {
    public TestSerializeAndConsumeConsumed(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMXMLParserWrapper builder =
                OMXMLBuilderFactory.createOMBuilder(
                        metaFactory.getOMFactory(),
                        TestGetChildElementsConsumed.class.getResourceAsStream(
                                "purchase-order.xml"));

        OMElement documentElement = builder.getDocumentElement();

        XMLStreamReader reader = documentElement.getXMLStreamReaderWithoutCaching();

        // consume the parser. this should force the xml stream to be exhausted without
        // building the tree
        while (reader.hasNext()) {
            reader.next();
        }

        // try to find the children of the document element. This should produce an
        // error since the underlying stream is fully consumed without building the object tree
        try {
            documentElement.serializeAndConsume(StAX.createNullXMLStreamWriter());
            fail("Expected NodeUnavailableException");
        } catch (NodeUnavailableException e) {
            // wea re cool
        }

        documentElement.close(false);
    }
}
