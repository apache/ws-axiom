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

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

public class TestSetTextContent extends DOMTestCase {
    public TestSetTextContent(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document doc =
                dbf.newDocumentBuilder()
                        .parse(new InputSource(new StringReader("<a>1<!--c--><b>2</b>3</a>")));
        Element element = doc.getDocumentElement();
        Node oldFirstChild = element.getFirstChild();
        element.setTextContent("test");
        Node firstChild = element.getFirstChild();
        assertTrue(firstChild instanceof Text);
        assertEquals("test", firstChild.getNodeValue());
        assertNull(firstChild.getNextSibling());
        assertNull(oldFirstChild.getParentNode());
        assertSame(doc, oldFirstChild.getOwnerDocument());
    }
}
