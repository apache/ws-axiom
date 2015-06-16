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
package org.apache.axiom.ts.om.sourcedelement;

import static org.apache.axiom.truth.AxiomTestVerb.ASSERT;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.dimension.AddAttributeStrategy;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;

/**
 * Tests that adding an attribute to an {@link OMSourcedElement} overrides a corresponding attribute
 * that may be produced during expansion.
 */
public class TestAddAttribute extends AxiomTestCase {
    private final AddAttributeStrategy strategy;
    
    public TestAddAttribute(OMMetaFactory metaFactory, AddAttributeStrategy strategy) {
        super(metaFactory);
        this.strategy = strategy;
        strategy.addTestParameters(this);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMSourcedElement element = factory.createOMElement(
                new PullOMDataSource("<root attr='orgvalue'><child/></root>"), "root", null);
        // Add an attribute before expansion
        OMAttribute attr = strategy.addAttribute(element, "attr", null, "newvalue");
        // Force expansion; this should not overwrite the attribute we just added
        ASSERT.that(element.getFirstOMChild()).isNotNull();
        OMAttribute attr2 = element.getAttribute(new QName("attr"));
        ASSERT.that(attr2).isSameAs(attr);
        ASSERT.that(attr2.getAttributeValue()).isEqualTo("newvalue");
        Iterator it = element.getAllAttributes();
        ASSERT.that(it.hasNext()).isTrue();
        ASSERT.that(it.next()).isSameAs(attr);
        ASSERT.that(it.hasNext()).isFalse();
    }
}
