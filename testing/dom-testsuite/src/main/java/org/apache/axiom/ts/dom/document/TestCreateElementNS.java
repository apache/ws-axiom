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

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestCreateElementNS extends DOMTestCase {
    public TestCreateElementNS(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        String tagName = "LocalName";
        String namespace = "http://ws.apache.org/axis2/ns";
        Document doc = dbf.newDocumentBuilder().newDocument();
        Element elem = doc.createElementNS(namespace, "axis2:" + tagName);
        assertEquals("Local name misnatch", tagName, elem.getLocalName());
        assertEquals("Namespace misnatch", namespace, elem.getNamespaceURI());
        // In contrast to Axiom, DOM doesn't generate namespace declarations automatically
        assertEquals(0, elem.getAttributes().getLength());
    }
}
