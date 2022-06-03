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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.StringReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMNode#discard()} on an element that is partially built, more
 * precisely in a situation where the builder is building a descendant that is not an immediate
 * child of the element.
 */
public class TestDiscardPartiallyBuilt extends AxiomTestCase {
    public TestDiscardPartiallyBuilt(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement root =
                OMXMLBuilderFactory.createOMBuilder(
                                factory,
                                new StringReader(
                                        "<root><element><a><b>text</b></a><c/></element><sibling/></root>"))
                        .getDocumentElement();
        OMElement element = root.getFirstElement();

        // Navigate to the text node so that the element is partially built
        OMElement b = element.getFirstElement().getFirstElement();
        OMText text = (OMText) b.getFirstOMChild();
        assertEquals("text", text.getText());

        element.discard();
        assertAbout(xml())
                .that(xml(OMElement.class, root))
                .hasSameContentAs("<root><sibling/></root>");

        // Force the builder to complete the document. If the discard method didn't adjust the
        // element depth correctly, then an exception will occur here
        assertNull(root.getNextOMSibling());
    }
}
