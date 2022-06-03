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

import static com.google.common.truth.Truth.assertThat;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * Tests that after calling {@link Element#removeAttributeNode(Attr)} the removed attribute has the
 * same owner document as the element. This test case uses a scenario where the element is created
 * using the Axiom API, i.e. where the owner document is created lazily.
 */
public class TestRemoveAttributeNode extends AxiomTestCase {
    public TestRemoveAttributeNode(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = factory.createOMElement("test", null);
        OMAttribute attr = element.addAttribute("attr", "value", null);
        ((Element) element).removeAttributeNode((Attr) attr);
        assertThat(((Attr) attr).getOwnerDocument())
                .isSameInstanceAs(((Element) element).getOwnerDocument());
    }
}
