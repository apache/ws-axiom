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
package org.apache.axiom.ts.om.factory;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;

/**
 * Tests that {@link OMFactory} forbids creating prefixed elements with an empty namespace name.
 * Neither XML 1.0 nor XML 1.1 allow binding a prefix to the empty namespace name.
 */
public class TestCreateOMElementWithInvalidNamespace extends CreateOMElementTestCase {
    public TestCreateOMElementWithInvalidNamespace(
            OMMetaFactory metaFactory,
            CreateOMElementVariant variant,
            CreateOMElementParentSupplier parentSupplier) {
        super(metaFactory, variant, parentSupplier);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        try {
            variant.createOMElement(factory, parentSupplier.createParent(factory), "test", "", "p");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // Expected
        }
    }
}
