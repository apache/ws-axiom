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
import static org.junit.Assert.assertThrows;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests that {@link Node#appendChild(Node)} throws an appropriate exception if the invocation would
 * result in a cyclic relationship. This occurs if the node to append is an ancestor of the node to
 * which is appended.
 */
public class TestAppendChildCyclic extends DOMTestCase {
    public TestAppendChildCyclic(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().newDocument();
        Element grandparent = document.createElementNS(null, "grandparent");
        Element parent = document.createElementNS(null, "parent");
        grandparent.appendChild(parent);
        Element element = document.createElementNS(null, "element");
        parent.appendChild(element);
        assertThat(
                        assertThrows(
                                        DOMException.class,
                                        () -> {
                                            element.appendChild(grandparent);
                                        })
                                .code)
                .isEqualTo(DOMException.HIERARCHY_REQUEST_ERR);
    }
}
