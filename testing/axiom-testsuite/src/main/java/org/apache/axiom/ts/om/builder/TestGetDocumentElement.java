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
package org.apache.axiom.ts.om.builder;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.StringReader;

import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.dimension.BuilderFactory;
import org.xml.sax.InputSource;

/**
 * Tests the behavior of {@link OMXMLParserWrapper#getDocumentElement()} and {@link
 * OMXMLParserWrapper#getDocumentElement(boolean)}.
 */
public class TestGetDocumentElement extends AxiomTestCase {
    private final BuilderFactory builderFactory;
    private final Boolean discardDocument;

    public TestGetDocumentElement(
            OMMetaFactory metaFactory, BuilderFactory builderFactory, Boolean discardDocument) {
        super(metaFactory);
        this.builderFactory = builderFactory;
        this.discardDocument = discardDocument;
        builderFactory.addTestParameters(this);
        addTestParameter("discardDocument", String.valueOf(discardDocument));
    }

    @Override
    protected void runTest() throws Throwable {
        OMXMLParserWrapper builder =
                builderFactory.getBuilder(
                        metaFactory,
                        new InputSource(new StringReader("<!--comment1--><root/><!--comment2-->")));
        OMElement element;
        if (discardDocument == null) {
            element = builder.getDocumentElement();
        } else {
            element = builder.getDocumentElement(discardDocument.booleanValue());
        }
        assertNotNull("Document element can not be null", element);
        assertEquals("Name of the document element is wrong", "root", element.getLocalName());
        if (Boolean.TRUE.equals(discardDocument)) {
            if (builderFactory.isDeferredParsing()) {
                assertFalse(element.isComplete());
            }
            assertNull(element.getParent());
            // Note: we can't test getNextOMSibling here because this would build the element
            assertNull(element.getPreviousOMSibling());
            OMElement newParent = element.getOMFactory().createOMElement("newParent", null);
            newParent.addChild(element);
            if (builderFactory.isDeferredParsing()) {
                assertFalse(element.isComplete());
                assertFalse(builder.isCompleted());
            }
            assertAbout(xml())
                    .that(xml(OMElement.class, newParent))
                    .hasSameContentAs("<newParent><root/></newParent>");
            assertTrue(element.isComplete());
            // Since we discarded the document, the nodes in the epilog will not be accessible.
            // Therefore we expect that when the document element changes its completion status,
            // the builder will consume the epilog and change its completion status as well.
            // This gives the underlying parser a chance to release some resources.
            assertTrue(builder.isCompleted());
        } else {
            // The getDocumentElement doesn't detach the document element from the document:
            assertSame(builder.getDocument(), element.getParent());
            assertSame(builder.getDocument().getOMDocumentElement(), element);
            assertTrue(element.getPreviousOMSibling() instanceof OMComment);
            assertTrue(element.getNextOMSibling() instanceof OMComment);
        }
    }
}
