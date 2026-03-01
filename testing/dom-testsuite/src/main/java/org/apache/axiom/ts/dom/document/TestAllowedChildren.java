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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

public class TestAllowedChildren extends DOMTestCase {
    public TestAllowedChildren(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document doc = dbf.newDocumentBuilder().newDocument();

        doc.appendChild(doc.createComment("some comment"));
        doc.appendChild(doc.createProcessingInstruction("pi", "data"));

        // Document Object Model (DOM) Level 3 Core Specification, section 1.1.1
        // says that text nodes are not allowed as children of a document.
        assertThatThrownBy(() -> doc.appendChild(doc.createTextNode("    ")))
                .isInstanceOfSatisfying(
                        DOMException.class,
                        ex -> assertThat(ex.code).isEqualTo(DOMException.HIERARCHY_REQUEST_ERR));

        doc.appendChild(doc.createElement("root1"));

        // Multiple document elements are not allowed
        assertThatThrownBy(() -> doc.appendChild(doc.createElement("root2")))
                .isInstanceOfSatisfying(
                        DOMException.class,
                        ex -> assertThat(ex.code).isEqualTo(DOMException.HIERARCHY_REQUEST_ERR));

        // PIs and comments after the document element are allowed
        doc.appendChild(doc.createProcessingInstruction("pi", "data"));
        doc.appendChild(doc.createComment("some comment"));

        // Again, text nodes are not allowed
        assertThatThrownBy(() -> doc.appendChild(doc.createTextNode("    ")))
                .isInstanceOfSatisfying(
                        DOMException.class,
                        ex -> assertThat(ex.code).isEqualTo(DOMException.HIERARCHY_REQUEST_ERR));
    }
}
