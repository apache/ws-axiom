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
package org.apache.axiom.ts.dom.builder;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * Test that whitespace around the document element is discarded (or not visible). Indeed, DOM
 * doesn't allow text nodes as children of a document.
 */
public class TestWhitespaceAroundDocumentElement extends DOMTestCase {
    public TestWhitespaceAroundDocumentElement(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document doc =
                dbf.newDocumentBuilder()
                        .parse(new InputSource(new StringReader(" <!-- --> <root/> ")));
        Node child = doc.getFirstChild();
        do {
            assertThat(child).isNotInstanceOf(Text.class);
            child = child.getNextSibling();
        } while (child != null);

        child = doc.getLastChild();
        do {
            assertThat(child).isNotInstanceOf(Text.class);
            child = child.getPreviousSibling();
        } while (child != null);

        NodeList children = doc.getChildNodes();
        assertThat(children.getLength()).isEqualTo(2);
        assertThat(children.item(1)).isSameInstanceAs(doc.getDocumentElement());
    }
}
