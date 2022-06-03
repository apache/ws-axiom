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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tests the behavior of {@link Node#replaceChild(Node, Node)} on an element that has not been built
 * completely. This test covers the case where the child being replaced is the first child.
 */
public class TestReplaceChildFirstIncomplete extends AxiomTestCase {
    public TestReplaceChildFirstIncomplete(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        Element element =
                (Element)
                        OMXMLBuilderFactory.createOMBuilder(
                                        factory, new StringReader("<root><a/><b/><c/></root>"))
                                .getDocumentElement();
        Element a = (Element) element.getFirstChild();
        Element a2 = element.getOwnerDocument().createElementNS(null, "a2");
        element.replaceChild(a2, a);
        Element b = (Element) a2.getNextSibling();
        assertNotNull(b);
        Element c = (Element) b.getNextSibling();
        assertNotNull(c);
        // Check the other sibling relations
        assertNull(a2.getPreviousSibling());
        assertSame(a2, b.getPreviousSibling());
        assertSame(b, c.getPreviousSibling());
        assertNull(c.getNextSibling());
        // Check parent-child relations
        assertSame(a2, element.getFirstChild());
        assertSame(c, element.getLastChild());
        assertSame(element, a2.getParentNode());
        assertSame(element, b.getParentNode());
        assertSame(element, c.getParentNode());
        NodeList children = element.getChildNodes();
        assertEquals(3, children.getLength());
        assertSame(a2, children.item(0));
        assertSame(b, children.item(1));
        assertSame(c, children.item(2));
        // Check that a has been detached properly
        assertNull(a.getPreviousSibling());
        assertNull(a.getNextSibling());
        assertNull(a.getParentNode());
    }
}
