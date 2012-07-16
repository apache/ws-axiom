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

import java.io.StringReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.custommonkey.xmlunit.XMLAssert;

/**
 * Tests {@link OMContainer#removeChildren()} on an {@link OMElement}.
 */
public class TestRemoveChildren extends AxiomTestCase {
    private final boolean complete;
    
    public TestRemoveChildren(OMMetaFactory metaFactory, boolean complete) {
        super(metaFactory);
        this.complete = complete;
        addTestProperty("complete", Boolean.toString(complete));
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = OMXMLBuilderFactory.createOMBuilder(factory,
                new StringReader("<root><a>A</a><b>B</b></root>")).getDocumentElement();
        if (complete) {
            element.build();
        }
        OMElement firstChild = (OMElement)element.getFirstOMChild();
        assertEquals(complete, element.isComplete());
        assertEquals(complete, firstChild.isComplete());
        element.removeChildren();
        // We still need to be able to get the content of the child we retrieved before
        // calling removeChildren.
        assertEquals("A", firstChild.getText());
        // Test that the child has been detached correctly.
        assertNull(firstChild.getParent());
        assertNull(firstChild.getPreviousOMSibling());
        assertNull(firstChild.getNextOMSibling());
        // Test that the element is now empty.
        assertNull(element.getFirstOMChild());
        // Check that the element is in a clean state and that we are able to add
        // new children.
        element.addChild(factory.createOMElement("c", null));
        XMLAssert.assertXMLEqual("<root><c/></root>", element.toString());
    }
}
