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

public class TestLookupNamespaceURI extends DOMTestCase {

    public TestLookupNamespaceURI(DocumentBuilderFactory dbf) {
        super(dbf);
    }

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
        parent.appendChild(element3);

        assertEquals("Incorrect default namespace returned for the element", ns1,
                element1.lookupNamespaceURI(null));
        assertEquals("Incorrect namespace returned for the element", ns2,
                element2.lookupNamespaceURI(pref2));
        assertEquals("Incorrect namespace returned for the given prefix", nsParent,
                element3.lookupNamespaceURI(prefParent));
    }

}