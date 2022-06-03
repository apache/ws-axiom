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

import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;

/**
 * Tests the behavior of the <code>createOMElement</code> methods in {@link OMFactory} when
 * requested to create an element without namespace as a child of an element with a {@code xmlns=""}
 * declaration. In this case, no namespace declaration is added to the created element.
 */
public class TestCreateOMElementWithoutNamespace3 extends CreateOMElementTestCase {
    public TestCreateOMElementWithoutNamespace3(
            OMMetaFactory metaFactory, CreateOMElementVariant variant) {
        super(metaFactory, variant, null);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement grandparent =
                factory.createOMElement("grandparent", factory.createOMNamespace("urn:ns1", ""));
        OMElement parent =
                factory.createOMElement(
                        "parent", factory.createOMNamespace("urn:ns2", "p"), grandparent);
        parent.declareDefaultNamespace("");
        OMElement child = variant.createOMElement(factory, parent, "test", "", "");
        assertEquals("test", child.getLocalName());
        assertNull(child.getNamespace());
        Iterator<OMNamespace> it = child.getAllDeclaredNamespaces();
        assertFalse(it.hasNext());
    }
}
