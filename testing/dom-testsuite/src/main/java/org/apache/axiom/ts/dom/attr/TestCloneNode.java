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
package org.apache.axiom.ts.dom.attr;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class TestCloneNode extends DOMTestCase {
    private final boolean deep;

    public TestCloneNode(DocumentBuilderFactory dbf, boolean deep) {
        super(dbf);
        this.deep = deep;
        addTestParameter("deep", deep);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Attr attr = document.createAttributeNS(null, "attr");
        attr.appendChild(document.createTextNode("foo"));
        attr.appendChild(document.createTextNode("bar"));
        // Note that for an attribute, cloneNode always copies the children, even if deep=false
        Attr clone = (Attr) attr.cloneNode(deep);
        Node child = clone.getFirstChild();
        assertNotNull(child);
        assertEquals(Node.TEXT_NODE, child.getNodeType());
        assertEquals("foo", child.getNodeValue());
        child = child.getNextSibling();
        assertEquals(Node.TEXT_NODE, child.getNodeType());
        assertEquals("bar", child.getNodeValue());
        assertNull(child.getNextSibling());
    }
}
