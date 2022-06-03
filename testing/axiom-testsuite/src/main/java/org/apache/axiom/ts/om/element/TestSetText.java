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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.ts.AxiomTestCase;

/** Tests the behavior of {@link OMElement#setText(String)} when invoked on an empty element. */
public class TestSetText extends AxiomTestCase {
    public TestSetText(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement element = metaFactory.getOMFactory().createOMElement("test", null);
        String text = "The quick brown fox jumps over the lazy dog";
        element.setText(text);

        // Check that OMElement#getText() returns a matching value
        assertEquals("Text value mismatch", text, element.getText());

        // Check that OMElement#setText() has created the expected nodes
        OMNode child = element.getFirstOMChild();
        assertTrue(child instanceof OMText);
        assertSame(element, child.getParent());
        assertEquals(text, ((OMText) child).getText());
        assertNull(child.getNextOMSibling());
    }
}
