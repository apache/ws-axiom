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
import org.w3c.dom.NodeList;

public class TestGetElementsByTagNameNS extends DOMTestCase {
    public TestGetElementsByTagNameNS(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        String childElementQN = "test:Child";
        String childElementLN = "Child";
        String childElementNS = "http://ws.apache.org/ns/axis2/dom";

        Document doc = dbf.newDocumentBuilder().newDocument();
        Element docElem = doc.getDocumentElement();
        assertNull("The document element shoudl be null", docElem);

        docElem = doc.createElementNS("http://test.org", "test:Test");

        docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));
        docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));
        docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));
        docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));
        docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));
        docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));
        docElem.appendChild(doc.createElementNS(childElementNS, childElementQN));

        NodeList list = docElem.getElementsByTagNameNS(childElementNS, childElementLN);

        assertEquals("Incorrect number of child elements", 7, list.getLength());
    }
}
