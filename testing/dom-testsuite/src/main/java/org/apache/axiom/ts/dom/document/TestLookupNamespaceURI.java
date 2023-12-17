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
package org.apache.axiom.ts.dom.document;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests that a call to {@link Node#lookupNamespaceURI(String)} on a {@link Document} actually
 * performs a lookup on the document element as described in appendix B.4 of the DOM Level 3 Core
 * specification.
 */
public class TestLookupNamespaceURI extends DOMTestCase {
    public TestLookupNamespaceURI(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Element documentElement = document.createElementNS("urn:test", "ns:root");
        documentElement.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:ns", "urn:test");
        document.appendChild(documentElement);
        // Note: this part is also tested by the W3C test suite (see nodelookupnamespaceuri02)
        assertThat(document.lookupNamespaceURI("ns")).isEqualTo("urn:test");
        // The following assertion is important to check that there is no infinite recursion
        // (Document delegates the lookup to its document element, but the document element must
        // not delegate to its parent if no matching declaration is found).
        // This is not tested by the W3C test suite.
        assertThat(document.lookupNamespaceURI("p")).isNull();
    }
}
