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
package org.apache.axiom.ts.dom.document;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;

public class TestCreateAttributeNS extends DOMTestCase {
    public TestCreateAttributeNS(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    protected void runTest() throws Throwable {
        String localName = "attrIdentifier";
        String uri = "http://ws.apache.org/axis2/ns";
        String prefix = "axis2";
        String name = prefix + ":" + localName;

        Document doc = dbf.newDocumentBuilder().newDocument();

        Attr attr = doc.createAttributeNS(uri, name);
        
        // Check name
        assertEquals("Attr name mismatch", localName, attr.getLocalName());
        assertEquals("NamsspaceURI mismatch", uri, attr.getNamespaceURI());
        assertEquals("namespace prefix mismatch", prefix, attr.getPrefix());
        assertEquals(name, attr.getName());

        // Check defaults
        assertNull(attr.getFirstChild());
        assertEquals("", attr.getValue());
    }
}
