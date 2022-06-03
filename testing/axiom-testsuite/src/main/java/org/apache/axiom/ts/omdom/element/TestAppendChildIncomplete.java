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
package org.apache.axiom.ts.omdom.element;

import java.io.StringReader;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tests the behavior of {@link Node#appendChild(Node)} if the parent has not been built completely.
 * In this case, the parent must be built before the new child is added.
 *
 * @see TestInsertBeforeIncomplete
 */
public class TestAppendChildIncomplete extends AxiomTestCase {
    public TestAppendChildIncomplete(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        Document document =
                (Document)
                        OMXMLBuilderFactory.createOMBuilder(
                                        factory, new StringReader("<root><a/><b/></root>"))
                                .getDocument();
        Element parent = document.getDocumentElement();
        parent.appendChild(document.createElementNS(null, "c"));
        NodeList children = parent.getChildNodes();
        assertEquals(3, children.getLength());
        assertEquals("a", children.item(0).getLocalName());
        assertEquals("b", children.item(1).getLocalName());
        assertEquals("c", children.item(2).getLocalName());
    }
}
