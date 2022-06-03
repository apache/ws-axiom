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

import static com.google.common.truth.Truth.assertThat;
import static org.apache.axiom.truth.AxiomTruth.assertThat;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.dimension.AddAttributeStrategy;

/**
 * Test that adding an attribute has the expected effect when an attribute with the same name and
 * namespace URI already exists.
 */
public class TestAddAttributeReplace extends AxiomTestCase {
    private final AddAttributeStrategy strategy;

    public TestAddAttributeReplace(OMMetaFactory metaFactory, AddAttributeStrategy strategy) {
        super(metaFactory);
        this.strategy = strategy;
        strategy.addTestParameters(this);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        // Use same namespace URI but different prefixes
        OMNamespace ns1 = factory.createOMNamespace("urn:ns", "p1");
        OMNamespace ns2 = factory.createOMNamespace("urn:ns", "p2");
        OMElement element = factory.createOMElement(new QName("test"));
        OMAttribute att1 = strategy.addAttribute(element, "test", ns1, "value1");
        OMAttribute att2 = strategy.addAttribute(element, "test", ns2, "value2");
        Iterator<OMAttribute> it = element.getAllAttributes();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameInstanceAs(att2);
        assertThat(it.hasNext()).isFalse();
        assertThat(att1.getOwner()).isNull();
        assertThat(att2.getOwner()).isSameInstanceAs(element);
        assertThat(att1).hasValue("value1");
        assertThat(att2).hasValue("value2");
    }
}
