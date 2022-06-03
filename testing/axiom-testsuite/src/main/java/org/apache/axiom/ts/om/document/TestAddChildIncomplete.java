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

import java.io.StringReader;
import java.util.Iterator;

import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMContainer#addChild(OMNode)} if the parent has not been built
 * completely. In this case, the parent must be built before the new child is added (so that the
 * result of {@link OMContainer#addChild(OMNode)} is independent of the completeness of the parent).
 *
 * @see org.apache.axiom.ts.om.element.TestAddChildIncomplete
 */
public class TestAddChildIncomplete extends AxiomTestCase {
    public TestAddChildIncomplete(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMDocument parent =
                OMXMLBuilderFactory.createOMBuilder(
                                factory, new StringReader("<!--a--><b/><!--c-->"))
                        .getDocument();
        parent.addChild(factory.createOMComment(null, "d"));
        Iterator<OMNode> it = parent.getChildren();
        assertEquals("a", ((OMComment) it.next()).getValue());
        assertEquals("b", ((OMElement) it.next()).getLocalName());
        assertEquals("c", ((OMComment) it.next()).getValue());
        assertEquals("d", ((OMComment) it.next()).getValue());
        assertFalse(it.hasNext());
    }
}
