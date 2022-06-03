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
package org.apache.axiom.ts.om.document;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.StringReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/** Tests {@link OMContainer#removeChildren()} on an {@link OMContainer}. */
public class TestRemoveChildren extends AxiomTestCase {
    private final boolean complete;
    private final boolean accessDocumentElement;

    public TestRemoveChildren(
            OMMetaFactory metaFactory, boolean complete, boolean accessDocumentElement) {
        super(metaFactory);
        this.complete = complete;
        this.accessDocumentElement = accessDocumentElement;
        addTestParameter("complete", complete);
        addTestParameter("accessDocumentElement", accessDocumentElement);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMDocument document =
                OMXMLBuilderFactory.createOMBuilder(
                                factory, new StringReader("<?pi data?><root>text</root>"))
                        .getDocument();
        if (complete) {
            document.build();
        }
        OMProcessingInstruction firstChild = (OMProcessingInstruction) document.getFirstOMChild();
        OMElement documentElement;
        if (accessDocumentElement) {
            documentElement = document.getOMDocumentElement();
            assertEquals(complete, documentElement.isComplete());
        } else {
            documentElement = null;
        }
        document.removeChildren();
        // Test that the child has been detached correctly.
        assertNull(firstChild.getParent());
        assertNull(firstChild.getPreviousOMSibling());
        assertNull(firstChild.getNextOMSibling());
        if (documentElement != null) {
            // Test that the child has been detached correctly.
            assertNull(documentElement.getParent());
            assertNull(documentElement.getPreviousOMSibling());
            assertNull(documentElement.getNextOMSibling());
            // Test that we can still get the content of the document element.
            assertEquals("text", documentElement.getText());
        }
        // Test that the document is now empty.
        assertNull(document.getFirstOMChild());
        // Check that the document is in a clean state and that we are able to add
        // new children.
        document.addChild(factory.createOMElement("newroot", null));
        assertAbout(xml()).that(xml(OMDocument.class, document)).hasSameContentAs("<newroot/>");
    }
}
