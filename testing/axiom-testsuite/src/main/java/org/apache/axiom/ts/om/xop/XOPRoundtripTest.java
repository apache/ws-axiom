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
package org.apache.axiom.ts.om.xop;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.XOPEncoded;
import org.apache.axiom.testutils.activation.TestDataSource;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.util.activation.DataHandlerUtils;

public class XOPRoundtripTest extends AxiomTestCase {
    public XOPRoundtripTest(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        DataHandler dh = new DataHandler(new TestDataSource('x', Runtime.getRuntime().maxMemory()));
        OMElement element1 = factory.createOMElement(new QName("test"));
        element1.addChild(factory.createOMText(DataHandlerUtils.toBlob(dh), true));
        XOPEncoded<XMLStreamReader> xopEncodedStream = element1.getXOPEncodedStreamReader(true);
        OMElement element2 =
                OMXMLBuilderFactory.createOMBuilder(
                                factory,
                                new StAXSource(xopEncodedStream.getRootPart()),
                                xopEncodedStream.getAttachmentAccessor())
                        .getDocumentElement();
        OMText child = (OMText) element2.getFirstOMChild();
        assertNotNull(child);
        assertTrue(child.isBinary());
        assertTrue(child.isOptimized());
        assertSame(dh, DataHandlerUtils.toDataHandler(child.getBlob()));
    }
}
