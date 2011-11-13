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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamedInformationItem;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMNamedInformationItem#getNamespace()} returns <code>null</code> for an element
 * with no namespace. The case considered in this test is a programmatically created element without
 * namespace that is added as a child to another element that has a default namespace. Earlier
 * versions of Axiom returned a non null value in this case to work around an issue in the
 * serialization code.
 * <p>
 * This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-398">AXIOM-398</a>.
 */
public class TestGetNamespaceNormalized extends AxiomTestCase {
    public TestGetNamespaceNormalized(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMElement parent = factory.createOMElement("parent", "urn:test", "");
        OMElement child = factory.createOMElement("child", null);
        parent.addChild(child);
        assertNull(child.getNamespace());
    }
}
