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

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests the behavior of {@link OMFactory#createOMAttribute(String, OMNamespace, String)} if an
 * {@link OMNamespace} object with a null prefix and an empty namespace URI is given. Since it is
 * not allowed to bind a prefix to the empty namespace URI and an unprefixed attribute has no
 * namespace, this should give the same result as specifying an empty prefix.
 */
public class TestCreateOMAttributeNullPrefixNoNamespace extends AxiomTestCase {
    public TestCreateOMAttributeNullPrefixNoNamespace(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("", null);
        OMAttribute attr = factory.createOMAttribute("attr", ns, "value");
        assertNull(attr.getNamespace());
        
        // An OMAttribute is neither an OMNode nor an OMContainer. For the latter this is in
        // contrast to DOM where an Attr node is a parent node (containing Text and EntityReference
        // nodes).
        assertFalse(attr instanceof OMNode);
        assertFalse(attr instanceof OMContainer);
    }
}
