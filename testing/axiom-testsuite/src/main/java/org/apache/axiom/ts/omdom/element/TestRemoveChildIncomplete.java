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

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.ts.AxiomTestCase;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Tests the behavior of {@link Node#removeChild(Node)} on an element that has not been built
 * completely.
 */
public class TestRemoveChildIncomplete extends AxiomTestCase {
    public TestRemoveChildIncomplete(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        Element element =
                (Element)
                        AXIOMUtil.stringToOM(
                                metaFactory.getOMFactory(), "<parent><a/><b/><c/></parent>");
        Node b = element.getFirstChild().getNextSibling();
        element.removeChild(b);
        Node child = element.getFirstChild();
        assertEquals("a", child.getLocalName());
        child = child.getNextSibling();
        assertEquals("c", child.getLocalName());
        assertNull(child.getNextSibling());
    }
}
