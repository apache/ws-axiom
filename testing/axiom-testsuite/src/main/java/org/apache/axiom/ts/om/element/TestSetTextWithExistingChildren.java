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
package org.apache.axiom.ts.om.element;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMElement#setText(String)} when invoked on an element that has
 * children.
 */
public class TestSetTextWithExistingChildren extends AxiomTestCase {
    public TestSetTextWithExistingChildren(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = factory.createOMElement("test", null);

        // Add some children of various types
        factory.createOMText(element, "some text");
        factory.createOMText(element, "cdata section", OMNode.CDATA_SECTION_NODE);
        factory.createOMComment(element, "comment");
        factory.createOMProcessingInstruction(element, "piTarget", "piData");
        factory.createOMElement("child", null, element);

        // Set the text; this should remove all child nodes
        element.setText("test");

        // Check that OMElement#getText() returns a matching value
        assertEquals("Text value mismatch", "test", element.getText());

        // Check that OMElement#setText() has created the expected nodes
        OMNode child = element.getFirstOMChild();
        assertTrue(child instanceof OMText);
        assertSame(element, child.getParent());
        assertEquals("test", ((OMText) child).getText());
        assertNull(child.getNextOMSibling());
    }
}
