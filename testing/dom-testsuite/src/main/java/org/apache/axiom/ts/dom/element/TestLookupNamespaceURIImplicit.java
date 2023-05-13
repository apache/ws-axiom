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

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests the behavior of {@link Node#lookupNamespaceURI(String)} on an {@link Element} for
 * namespaces defined implicitly by the namespace prefix/URI of the element and its ancestors, i.e.
 * for namespaces not defined explicitly by attributes representing namespace declarations.
 */
public class TestLookupNamespaceURIImplicit extends DOMTestCase {

    public TestLookupNamespaceURIImplicit(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {

        String ns1 = "http://apache/axiom/dom/ns1";
        String element1Name = "ele1";
        String ns2 = "http://apache/axiom/dom/ns2";
        String element2Name = "ns2:ele2";
        String pref2 = "ns2";
        String element3Name = "ele3";
        String nsParent = "http://apache/axiom/dom/parent";
        String parentName = "p:parent";
        String prefParent = "p";

        Document doc = dbf.newDocumentBuilder().newDocument();
        Element parent = doc.createElementNS(nsParent, parentName);
        // default namespace, no prefix
        Element element1 = doc.createElementNS(ns1, element1Name);
        parent.appendChild(element1);
        // non default namespace, with prefix
        Element element2 = doc.createElementNS(ns2, element2Name);
        parent.appendChild(element2);
        // parent has the prefix
        Element element3 = doc.createElement(element3Name);
        element3.setAttributeNS("urn:test", "ns3:attr", "value");
        parent.appendChild(element3);

        assertEquals(
                "Incorrect default namespace returned for the element",
                ns1,
                element1.lookupNamespaceURI(null));
        assertNull(element1.lookupNamespaceURI("ns0"));

        assertEquals(
                "Incorrect namespace returned for the element",
                ns2,
                element2.lookupNamespaceURI(pref2));
        assertNull(element2.lookupNamespaceURI("ns0"));
        assertNull(element2.lookupNamespaceURI(null));

        assertEquals(
                "Incorrect namespace returned for the given prefix",
                nsParent,
                element3.lookupNamespaceURI(prefParent));
        // This asserts that namespaces can only be defined implicitly by elements, but not
        // attributes
        assertNull(element3.lookupNamespaceURI("ns3"));
    }
}
