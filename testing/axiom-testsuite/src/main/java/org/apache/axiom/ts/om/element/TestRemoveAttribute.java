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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.util.Iterator;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;

/** Tests the behavior of {@link OMElement#removeAttribute(OMAttribute)}. */
public class TestRemoveAttribute extends AxiomTestCase {
    @Inject
    private OMMetaFactory metaFactory;

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement element = factory.createOMElement("test", null);
        OMAttribute attr1 = element.addAttribute("attr1", "value1", null);
        OMAttribute attr2 = element.addAttribute("attr2", "value2", null);
        element.removeAttribute(attr1);
        assertThat(attr1.getOwner()).isNull();
        Iterator<OMAttribute> it = element.getAllAttributes();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameAs(attr2);
        assertThat(it.hasNext()).isFalse();
    }
}
