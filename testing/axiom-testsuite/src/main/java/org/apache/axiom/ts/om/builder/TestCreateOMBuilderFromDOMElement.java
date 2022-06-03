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

import java.io.StringReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Tests that {@link OMXMLBuilderFactory#createOMBuilder(Node, boolean)} can be used to create a
 * builder from an {@link Element} and that the resulting Axiom tree corresponds to a the subtree
 * defined by that element.
 */
public class TestCreateOMBuilderFromDOMElement extends AxiomTestCase {
    public TestCreateOMBuilderFromDOMElement(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document =
                DOMImplementation.XERCES.parse(
                        new InputSource(new StringReader("<a><b><c/></b><b2/></a>")));
        Element domB = (Element) document.getElementsByTagNameNS(null, "b").item(0);
        OMElement omB =
                OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), domB, true)
                        .getDocumentElement();
        assertEquals("b", omB.getLocalName());
        assertNull(omB.getNextOMSibling());
    }
}
