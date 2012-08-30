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
import org.w3c.dom.NamedNodeMap;

public class TestSetAttributeNS extends DOMTestCase {
    private final String namespaceURI;
    private final String prefix;
    private final String localName;
    private final String name;
    private final String value;
    
    public TestSetAttributeNS(DocumentBuilderFactory dbf, String namespaceURI, String prefix, String localName, String value) {
        super(dbf);
        this.namespaceURI = namespaceURI;
        this.prefix = prefix;
        this.localName = localName;
        name = prefix == null ? localName : prefix + ":" + localName;
        this.value = value;
        addTestProperty("uri", namespaceURI == null ? "" : namespaceURI);
        addTestProperty("name", name);
    }

    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Element element = document.createElementNS("urn:ns1", "p:element");
        element.setAttributeNS(namespaceURI, name, value);
        assertTrue(element.hasAttributes());
        NamedNodeMap attributes = element.getAttributes();
        assertEquals(1, attributes.getLength());
        Attr attr = (Attr)attributes.item(0);
        assertSame(document, attr.getOwnerDocument());
        assertSame(element, attr.getOwnerElement());
        assertEquals(namespaceURI, attr.getNamespaceURI());
        assertEquals(prefix, attr.getPrefix());
        assertEquals(localName, attr.getLocalName());
        assertEquals(name, attr.getName());
        assertEquals(value, attr.getValue());
        assertSame(attr, element.getAttributeNodeNS(namespaceURI, localName));
    }
}
