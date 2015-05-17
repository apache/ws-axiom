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

import static org.apache.axiom.truth.AxiomTestVerb.ASSERT;

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
 * Test that adding an attribute has the expected effect when an
 * attribute with the same name and namespace URI already exists.
 */
public class TestAddAttributeReplace extends AxiomTestCase {
    private final AddAttributeStrategy strategy;
    
    public TestAddAttributeReplace(OMMetaFactory metaFactory, AddAttributeStrategy strategy) {
        super(metaFactory);
        this.strategy = strategy;
        strategy.addTestParameters(this);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        // Use same namespace URI but different prefixes
        OMNamespace ns1 = factory.createOMNamespace("urn:ns", "p1");
        OMNamespace ns2 = factory.createOMNamespace("urn:ns", "p2");
        OMElement element = factory.createOMElement(new QName("test"));
        OMAttribute att1 = strategy.addAttribute(element, "test", ns1, "value1");
        OMAttribute att2 = strategy.addAttribute(element, "test", ns2, "value2");
        Iterator it = element.getAllAttributes();
        ASSERT.that(it.hasNext()).isTrue();
        ASSERT.that(it.next()).isSameAs(att2);
        ASSERT.that(it.hasNext()).isFalse();
        ASSERT.that(att1.getOwner()).isNull();
        ASSERT.that(att2.getOwner()).isSameAs(element);
        ASSERT.that(att1).hasValue("value1");
        ASSERT.that(att2).hasValue("value2");
    }
}
