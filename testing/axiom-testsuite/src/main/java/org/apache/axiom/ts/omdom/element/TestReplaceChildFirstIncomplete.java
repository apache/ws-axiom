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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.omdom.OMDOMTestCase;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tests the behavior of {@link Node#replaceChild(Node, Node)} on an element that has not been built
 * completely. This test covers the case where the child being replaced is the first child.
 */
public class TestReplaceChildFirstIncomplete extends OMDOMTestCase {
    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        Element element =
                (Element) OMXMLBuilderFactory.createOMBuilder(factory, new StringReader("<root><a/><b/><c/></root>"))
                        .getDocumentElement();
        Element a = (Element) element.getFirstChild();
        Element a2 = element.getOwnerDocument().createElementNS(null, "a2");
        element.replaceChild(a2, a);
        Element b = (Element) a2.getNextSibling();
        assertThat(b).isNotNull();
        Element c = (Element) b.getNextSibling();
        assertThat(c).isNotNull();
        // Check the other sibling relations
        assertThat(a2.getPreviousSibling()).isNull();
        assertThat(b.getPreviousSibling()).isSameAs(a2);
        assertThat(c.getPreviousSibling()).isSameAs(b);
        assertThat(c.getNextSibling()).isNull();
        // Check parent-child relations
        assertThat(element.getFirstChild()).isSameAs(a2);
        assertThat(element.getLastChild()).isSameAs(c);
        assertThat(a2.getParentNode()).isSameAs(element);
        assertThat(b.getParentNode()).isSameAs(element);
        assertThat(c.getParentNode()).isSameAs(element);
        NodeList children = element.getChildNodes();
        assertThat(children.getLength()).isEqualTo(3);
        assertThat(children.item(0)).isSameAs(a2);
        assertThat(children.item(1)).isSameAs(b);
        assertThat(children.item(2)).isSameAs(c);
        // Check that a has been detached properly
        assertThat(a.getPreviousSibling()).isNull();
        assertThat(a.getNextSibling()).isNull();
        assertThat(a.getParentNode()).isNull();
    }
}
