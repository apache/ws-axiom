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

/**
 * Tests the behavior of {@link Element#setAttributeNodeNS(Attr)} when the element already has an
 * attribute with the same local name and namespace URI, i.e. if the call will replace an existing
 * attribute.
 */
public class TestSetAttributeNodeNSReplace extends DOMTestCase {
    public TestSetAttributeNodeNSReplace(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();

        // Initialize element with original attribute
        Element element = document.createElementNS("urn:ns1", "test");
        Attr attr1 = document.createAttributeNS("urn:ns2", "p:attr");
        attr1.setValue("value1");
        element.setAttributeNodeNS(attr1);

        // Replace attribute
        Attr attr2 = document.createAttributeNS("urn:ns2", "q:attr");
        attr2.setValue("value2");
        element.setAttributeNodeNS(attr2);

        assertNull(attr1.getOwnerElement());
        assertSame(document, attr1.getOwnerDocument());
        assertSame(element, attr2.getOwnerElement());
        assertSame(document, attr2.getOwnerDocument());
        assertEquals("value2", element.getAttributeNS("urn:ns2", "attr"));
    }
}
