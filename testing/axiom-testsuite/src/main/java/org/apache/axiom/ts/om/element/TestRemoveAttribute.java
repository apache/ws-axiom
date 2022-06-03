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

import java.util.Iterator;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;

/** Tests the behavior of {@link OMElement#removeAttribute(OMAttribute)}. */
public class TestRemoveAttribute extends AxiomTestCase {
    public TestRemoveAttribute(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = factory.createOMElement("test", null);
        OMAttribute attr1 = element.addAttribute("attr1", "value1", null);
        OMAttribute attr2 = element.addAttribute("attr2", "value2", null);
        element.removeAttribute(attr1);
        assertNull(attr1.getOwner());
        Iterator<OMAttribute> it = element.getAllAttributes();
        assertTrue(it.hasNext());
        assertSame(attr2, it.next());
        assertFalse(it.hasNext());
    }
}
