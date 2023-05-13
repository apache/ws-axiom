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
 * Tests the behavior of {@link Element#setAttributeNS(String, String, String)} if the element
 * already has an attribute with the same namespace URI and local name.
 */
public class TestSetAttributeNSExisting extends DOMTestCase {
    public TestSetAttributeNSExisting(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Element element = document.createElementNS(null, "test");

        // Add the original attribute
        Attr attr = document.createAttributeNS("urn:test", "p1:attr");
        attr.setValue("value1");
        element.setAttributeNodeNS(attr);

        // Now change the attribute using setAttributeNS (using a different prefix and value)
        element.setAttributeNS("urn:test", "p2:attr", "value2");

        // DOM is expected to change the original attribute, not to create a new one
        assertEquals("value2", attr.getValue());
        assertEquals("p2", attr.getPrefix());
    }
}
