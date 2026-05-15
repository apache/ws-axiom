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
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;

/** Test {@link OMElement#getAllAttributes()} on a programmatically created document. */
public class TestGetAllAttributes1 extends AxiomTestCase {
    @Inject
    public TestGetAllAttributes1(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement element = metaFactory.getOMFactory().createOMElement("test", null);
        element.addAttribute("attr1", "value1", null);
        element.addAttribute("attr2", "value2", null);
        Iterator<OMAttribute> it = element.getAllAttributes();
        assertThat(it.hasNext()).isTrue();
        OMAttribute attr = it.next();
        assertThat(attr.getLocalName()).isEqualTo("attr1");
        assertThat(it.hasNext()).isTrue();
        attr = it.next();
        assertThat(attr.getLocalName()).isEqualTo("attr2");
        assertThat(it.hasNext()).isFalse();
    }
}
