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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests that after a node has been removed from its parent using {@link OMNode#detach()}, it will
 * have a new owner document.
 */
public class TestDetach extends AxiomTestCase {
    public TestDetach(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document =
                ((DOMMetaFactory) metaFactory)
                        .newDocumentBuilderFactory()
                        .newDocumentBuilder()
                        .newDocument();
        Element parent = document.createElementNS(null, "parent");
        Element child = document.createElementNS(null, "child");
        parent.appendChild(child);
        ((OMElement) child).detach();
        assertNotSame(document, child.getOwnerDocument());
    }
}
