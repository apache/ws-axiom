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

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.StringOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link OMElement#getNamespace()} returns <code>null</code> for an {@link
 * OMSourcedElement} created using {@link OMFactory#createOMElement(OMDataSource, QName)} and a
 * {@link QName} without namespace.
 *
 * <p>This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-398">AXIOM-398</a>.
 */
public class TestGetNamespaceNormalized2 extends AxiomTestCase {
    public TestGetNamespaceNormalized2(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMSourcedElement element =
                factory.createOMElement(
                        new StringOMDataSource("<element>content</element>"), new QName("element"));
        assertNull(element.getNamespace());
        // Expand the element
        element.getFirstOMChild();
        assertNull(element.getNamespace());
    }
}
