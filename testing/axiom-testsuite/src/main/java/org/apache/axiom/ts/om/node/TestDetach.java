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
package org.apache.axiom.ts.om.node;

import java.io.StringReader;

import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/** Tests the behavior of {@link OMNode#detach()}. */
public class TestDetach extends AxiomTestCase {
    private final boolean document;
    private final boolean build;

    public TestDetach(OMMetaFactory metaFactory, boolean document, boolean build) {
        super(metaFactory);
        this.document = document;
        this.build = build;
        addTestParameter("document", document);
        addTestParameter("build", build);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMContainer root;
        if (document) {
            root =
                    OMXMLBuilderFactory.createOMBuilder(
                                    factory, new StringReader("<!--a--><b/><!--c-->"))
                            .getDocument();
        } else {
            root =
                    OMXMLBuilderFactory.createOMBuilder(
                                    factory, new StringReader("<root><!--a--><b/><!--c--></root>"))
                            .getDocumentElement();
        }
        if (build) {
            root.build();
        } else {
            assertFalse(root.isComplete());
        }
        OMComment a = (OMComment) root.getFirstOMChild();
        assertEquals("a", a.getValue());
        OMElement b = (OMElement) a.getNextOMSibling();
        assertEquals("b", b.getLocalName());
        OMNode returnValue = b.detach();
        assertSame(b, returnValue); // Detach is expected to do a "return this"
        assertNull(b.getParent());
        assertNull(b.getPreviousOMSibling());
        assertNull(b.getNextOMSibling());
        OMComment c = (OMComment) a.getNextOMSibling();
        assertEquals("c", c.getValue());
        assertSame(c, a.getNextOMSibling());
        assertSame(a, c.getPreviousOMSibling());
        root.close(false);
    }
}
