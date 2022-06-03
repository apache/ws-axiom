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

import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMNode#insertSiblingBefore(OMNode)} if the node is already a
 * sibling.
 */
public class TestInsertSiblingBeforeSameParent extends AxiomTestCase {
    public TestInsertSiblingBeforeSameParent(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory fac = metaFactory.getOMFactory();
        OMElement parent = fac.createOMElement("test", null);
        OMText text1 = fac.createOMText("text1");
        OMText text2 = fac.createOMText("text2");
        OMText text3 = fac.createOMText("text3");
        parent.addChild(text1);
        parent.addChild(text2);
        parent.addChild(text3);
        text2.insertSiblingBefore(text3);
        assertSame(parent, text3.getParent());
        Iterator<OMNode> it = parent.getChildren();
        assertSame(text1, it.next());
        assertSame(text3, it.next());
        assertSame(text2, it.next());
        assertFalse(it.hasNext());
    }
}
