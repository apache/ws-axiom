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
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TestCloneNodeWithAttributeHavingMultipleChildren extends DOMTestCase {
    public TestCloneNodeWithAttributeHavingMultipleChildren(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Attr attr = document.createAttributeNS(null, "attr");
        attr.appendChild(document.createTextNode("foo"));
        attr.appendChild(document.createTextNode("bar"));
        Element element = document.createElementNS(null, "test");
        element.setAttributeNodeNS(attr);
        Element clonedElement = (Element) element.cloneNode(false);
        Attr clonedAttr = clonedElement.getAttributeNodeNS(null, "attr");
        NodeList children = clonedAttr.getChildNodes();
        assertEquals(2, children.getLength());
        assertEquals("foo", children.item(0).getNodeValue());
        assertEquals("bar", children.item(1).getNodeValue());
    }
}
