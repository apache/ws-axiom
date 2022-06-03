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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMSourcedElement;

public class TestGetNamespace extends LazyNameTestCase {
    public TestGetNamespace(
            OMMetaFactory metaFactory, OMSourcedElementVariant variant, QName qname) {
        super(metaFactory, variant, qname);
    }

    @Override
    protected void runTest(OMSourcedElement element) throws Throwable {
        OMNamespace ns = element.getNamespace();
        if (qname.getNamespaceURI().length() == 0) {
            assertNull(ns);
        } else {
            assertEquals(qname.getNamespaceURI(), ns.getNamespaceURI());
            assertEquals(qname.getPrefix(), ns.getPrefix());
        }
        if (variant.isNamespaceURIRequiresExpansion() || variant.isPrefixRequiresExpansion(qname)) {
            assertTrue(element.isExpanded());
        } else {
            assertFalse(element.isExpanded());
        }
    }
}
