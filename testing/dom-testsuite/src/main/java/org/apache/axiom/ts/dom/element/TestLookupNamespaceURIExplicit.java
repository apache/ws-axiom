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
package org.apache.axiom.ts.dom.element;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.XMLConstants;
import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests the behavior of {@link Node#lookupNamespaceURI(String)} on an {@link Element} for
 * namespaces defined explicitly by attributes representing namespace declarations.
 */
public class TestLookupNamespaceURIExplicit extends DOMTestCase {
    @Override
    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();

        Element e1 = document.createElementNS("urn:ns0", "ns0:e1");
        e1.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns", "urn:ns1");
        e1.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:p", "urn:ns2");
        e1.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:q", "urn:ns4");

        Element e2 = document.createElementNS("urn:ns0", "ns0:e2");
        e2.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:p", "urn:ns3");
        // This attribute undeclares the "q" prefix. Note that this is allowed only in XML 1.1.
        e2.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:q", "");
        e1.appendChild(e2);

        Element e3 = document.createElementNS("urn:ns0", "ns0:e3");
        e3.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns", "");
        // Add some attributes to check that lookupNamespaceURI doesn't confuse normal attributes
        // and namespace declarations
        e3.setAttributeNS("urn:test", "ns:p", "value");
        e3.setAttributeNS(null, "q", "value");
        e2.appendChild(e3);

        assertThat(e1.lookupNamespaceURI(null)).isEqualTo("urn:ns1");
        assertThat(e2.lookupNamespaceURI(null)).isEqualTo("urn:ns1");
        assertThat(e3.lookupNamespaceURI(null)).isNull();

        assertThat(e1.lookupNamespaceURI("p")).isEqualTo("urn:ns2");
        assertThat(e2.lookupNamespaceURI("p")).isEqualTo("urn:ns3");
        assertThat(e3.lookupNamespaceURI("p")).isEqualTo("urn:ns3");

        assertThat(e1.lookupNamespaceURI("q")).isEqualTo("urn:ns4");
        assertThat(e2.lookupNamespaceURI("q")).isNull();
        assertThat(e3.lookupNamespaceURI("q")).isNull();
    }
}
