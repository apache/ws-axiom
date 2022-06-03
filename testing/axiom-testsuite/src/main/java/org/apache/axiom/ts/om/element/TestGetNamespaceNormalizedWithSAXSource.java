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

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamedInformationItem;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.xml.sax.InputSource;

/**
 * Tests that {@link OMNamedInformationItem#getNamespace()} returns <code>null</code> for an element
 * with no namespace. The case considered in this test is an element created using a {@link
 * SAXSource} and that has an explicit namespace declaration for the default namespace, i.e. {@code
 * xmlns=""}. This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-398">AXIOM-398</a>.
 */
public class TestGetNamespaceNormalizedWithSAXSource extends AxiomTestCase {
    public TestGetNamespaceNormalizedWithSAXSource(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        SAXParser parser = factory.newSAXParser();
        SAXSource source =
                new SAXSource(
                        parser.getXMLReader(),
                        new InputSource(new StringReader("<root xmlns=''/>")));
        OMElement element =
                OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), source)
                        .getDocumentElement();
        assertNull(element.getNamespace());
    }
}
