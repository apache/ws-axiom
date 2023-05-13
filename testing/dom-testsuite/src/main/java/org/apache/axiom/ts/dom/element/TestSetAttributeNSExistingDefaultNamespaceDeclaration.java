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

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests the behavior of {@link Element#setAttributeNS(String, String, String)} when used to modify
 * a namespace declaration for the default namespace.
 */
public class TestSetAttributeNSExistingDefaultNamespaceDeclaration extends DOMTestCase {
    public TestSetAttributeNSExistingDefaultNamespaceDeclaration(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Element element = document.createElementNS(null, "test");

        // Add the original default namespace declaration
        Attr attr =
                document.createAttributeNS(
                        XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE);
        attr.setValue("urn:ns1");
        element.setAttributeNodeNS(attr);

        // Now change the attribute using setAttributeNS
        element.setAttributeNS(
                XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE, "urn:ns2");

        // DOM is expected to change the original attribute, not to create a new one
        assertEquals("urn:ns2", attr.getValue());
        assertNull(attr.getPrefix());
    }
}
