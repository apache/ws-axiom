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
import org.w3c.dom.Node;

/** Tests that {@link Node#cloneNode(boolean)} correctly clones the attributes of an element. */
public class TestCloneNodeWithAttributes extends DOMTestCase {
    private final boolean deep;

    public TestCloneNodeWithAttributes(DocumentBuilderFactory dbf, boolean deep) {
        super(dbf);
        this.deep = deep;
        addTestParameter("deep", deep);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Element element = document.createElementNS("urn:ns1", "p:elem");
        element.setAttributeNS(null, "attr1", "value1");
        element.setAttributeNS("urn:ns2", "q:attr2", "value2");
        Element clone = (Element) element.cloneNode(deep);
        assertEquals(2, clone.getAttributes().getLength());
        Attr attr1 = clone.getAttributeNodeNS(null, "attr1");
        Attr attr2 = clone.getAttributeNodeNS("urn:ns2", "attr2");
        assertNotNull(attr1);
        assertNotNull(attr2);
        assertSame(clone, attr1.getOwnerElement());
        assertSame(clone, attr2.getOwnerElement());
        assertEquals("value1", attr1.getValue());
        assertEquals("value2", attr2.getValue());
    }
}
