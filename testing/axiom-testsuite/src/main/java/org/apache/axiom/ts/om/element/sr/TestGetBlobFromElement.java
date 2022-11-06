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
package org.apache.axiom.ts.om.element.sr;

import java.io.StringReader;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.testutils.activation.RandomDataSource;
import org.apache.axiom.testutils.io.IOTestUtils;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.util.activation.DataHandlerUtils;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;

/**
 * Tests {@link XMLStreamReaderUtils#getBlobFromElement(XMLStreamReader)} on an {@link
 * XMLStreamReader} returned by {@link OMElement#getXMLStreamReader(boolean)}.
 */
public class TestGetBlobFromElement extends AxiomTestCase {
    private final boolean cache;

    public TestGetBlobFromElement(OMMetaFactory metaFactory, boolean cache) {
        super(metaFactory);
        this.cache = cache;
        addTestParameter("cache", cache);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        RandomDataSource orgDS = new RandomDataSource(64 * 1024);
        OMElement orgRoot = factory.createOMElement(new QName("root"));
        OMElement orgChild = factory.createOMElement(new QName("child"), orgRoot);
        orgChild.addChild(
                factory.createOMText(DataHandlerUtils.toBlob(new DataHandler(orgDS)), false));
        OMElement root =
                OMXMLBuilderFactory.createOMBuilder(factory, new StringReader(orgRoot.toString()))
                        .getDocumentElement();
        XMLStreamReader reader = root.getXMLStreamReader(cache);
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        assertEquals(XMLStreamReader.START_ELEMENT, reader.next());
        Blob blob = XMLStreamReaderUtils.getBlobFromElement(reader);
        IOTestUtils.compareStreams(orgDS.getInputStream(), blob.getInputStream());
    }
}
