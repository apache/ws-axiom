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

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.apache.axiom.ts.dom.DOMUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class TestSetAttributeNS extends DOMTestCase {
    private final QName qname;
    private final String value;
    
    public TestSetAttributeNS(DocumentBuilderFactory dbf, QName qname, String value) {
        super(dbf);
        this.qname = qname;
        this.value = value;
        addTestParameter("ns", qname.getNamespaceURI());
        addTestParameter("name", DOMUtils.getQualifiedName(qname));
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Element element = document.createElementNS("urn:ns1", "p:element");
        element.setAttributeNS(DOMUtils.getNamespaceURI(qname), DOMUtils.getQualifiedName(qname), value);
        assertTrue(element.hasAttributes());
        NamedNodeMap attributes = element.getAttributes();
        assertEquals(1, attributes.getLength());
        Attr attr = (Attr)attributes.item(0);
        assertSame(document, attr.getOwnerDocument());
        assertSame(element, attr.getOwnerElement());
        assertEquals(DOMUtils.getNamespaceURI(qname), attr.getNamespaceURI());
        assertEquals(DOMUtils.getPrefix(qname), attr.getPrefix());
        assertEquals(qname.getLocalPart(), attr.getLocalName());
        assertEquals(DOMUtils.getQualifiedName(qname), attr.getName());
        assertEquals(value, attr.getValue());
        assertSame(attr, element.getAttributeNodeNS(DOMUtils.getNamespaceURI(qname), qname.getLocalPart()));
    }
}
