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
package org.apache.axiom.ts.dom.text;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Tests that a call to {@link Node#lookupNamespaceURI(String)} on a {@link Text} node locates
 * namespace declarations present on an ancestor element of the node.
 */
public class TestLookupNamespaceURIWithNSDeclInScope extends DOMTestCase {
    public TestLookupNamespaceURIWithNSDeclInScope(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Element root = document.createElementNS("urn:test", "ns:root");
        root.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:ns", "urn:test");
        Element child = document.createElementNS(null, "child");
        root.appendChild(child);
        Text text = document.createTextNode("test");
        child.appendChild(text);
        assertEquals("urn:test", text.lookupNamespaceURI("ns"));
    }
}
