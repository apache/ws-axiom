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

import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

public class TestAttributes3 extends DOMTestCase {
    public TestAttributes3(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document doc =
                dbf.newDocumentBuilder()
                        .parse(
                                new InputSource(
                                        new StringReader(
                                                "<root><child xmlns:p=\"urn:ns1\"/></root>")));
        Element element = (Element) doc.getDocumentElement().getFirstChild();
        assertTrue(element.hasAttributes());
        NamedNodeMap attributes = element.getAttributes();
        assertEquals(1, attributes.getLength());
        Attr attr = (Attr) attributes.item(0);
        assertEquals("xmlns:p", attr.getName());
        assertEquals("xmlns", attr.getPrefix());
        assertEquals("p", attr.getLocalName());
        assertEquals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, attr.getNamespaceURI());
        assertEquals("urn:ns1", attr.getValue());
    }
}
