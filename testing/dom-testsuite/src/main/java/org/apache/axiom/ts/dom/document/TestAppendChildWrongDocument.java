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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests that {@link Node#appendChild(Node)} throws an appropriate exception if an attempt is made
 * to append a child that has a different owner document.
 */
public class TestAppendChildWrongDocument extends DOMTestCase {
    public TestAppendChildWrongDocument(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
        Document document1 = documentBuilder.newDocument();
        Document document2 = documentBuilder.newDocument();
        Element element = document2.createElementNS(null, "element");
        try {
            document1.appendChild(element);
            fail("Expected DOMException");
        } catch (DOMException ex) {
            assertEquals(DOMException.WRONG_DOCUMENT_ERR, ex.code);
        }
    }
}
