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
package org.apache.axiom.ts.omdom.node;

import javax.xml.parsers.DocumentBuilder;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Tests that {@link OMNode#insertSiblingBefore(OMNode)} automatically adopts (in the sense of DOM)
 * the sibling if it doesn't have the same owner document.
 */
public class TestInsertSiblingBeforeFromForeignDocument extends AxiomTestCase {
    public TestInsertSiblingBeforeFromForeignDocument(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        DocumentBuilder db =
                ((DOMMetaFactory) metaFactory).newDocumentBuilderFactory().newDocumentBuilder();
        Document document1 = db.newDocument();
        Element element1 = document1.createElementNS(null, "element1");
        Text text = document1.createTextNode("test");
        element1.appendChild(text);
        Document document2 = db.newDocument();
        Element element2 = document2.createElementNS(null, "element2");
        ((OMNode) text).insertSiblingBefore((OMElement) element2);
        // Assert that the new child is not a copy, but the original element
        assertSame(element2, element1.getFirstChild());
        // Assert that the owner document of element2 was changed
        assertSame(document1, element2.getOwnerDocument());
    }
}
