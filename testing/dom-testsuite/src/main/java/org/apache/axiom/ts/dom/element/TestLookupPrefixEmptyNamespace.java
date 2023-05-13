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
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Tests that {@link Node#lookupPrefix(String)} always returns <code>null</code> if the given
 * namespace URI is the empty string.
 */
public class TestLookupPrefixEmptyNamespace extends DOMTestCase {
    public TestLookupPrefixEmptyNamespace(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document =
                dbf.newDocumentBuilder()
                        .parse(
                                new InputSource(
                                        new StringReader("<a xmlns='urn:test'><b xmlns=''/></a>")));
        assertNull(document.getDocumentElement().getFirstChild().lookupPrefix(""));
    }
}
