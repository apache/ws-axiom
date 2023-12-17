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

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests that the DOM implementation is not affected by <a
 * href="https://issues.apache.org/jira/browse/XERCESJ-1586">XERCESJ-1586</a>.
 */
public class TestLookupNamespaceURIXercesJ1586 extends DOMTestCase {
    public TestLookupNamespaceURIXercesJ1586(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Element root = document.createElementNS("urn:test", "root");
        document.appendChild(root);
        Element child = document.createElementNS(null, "child");
        root.appendChild(child);
        assertThat(child.lookupNamespaceURI(null)).isNull();
    }
}
