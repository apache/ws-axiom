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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

public class TestGetChildrenWithName4 extends AxiomTestCase {
    private static final String NS_A = "urn://a";
    private static final String NS_B = "urn://b";
    private static final String NS_C = "urn://c";

    public TestGetChildrenWithName4(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        // Create a document with 2 children, each named "sample" but
        // have different namespaces.
        OMFactory factory = metaFactory.getOMFactory();
        OMNamespace a = factory.createOMNamespace(NS_A, "a");
        OMNamespace b = factory.createOMNamespace(NS_B, "b");

        OMElement documentElement = factory.createOMElement("Document", a);
        factory.createOMElement("sample", a, documentElement);
        factory.createOMElement("sample", b, documentElement);

        // Test for fully qualified names
        QName qName = new QName(NS_A, "sample");
        assertThat(getChildrenCount(documentElement.getChildrenWithName(qName))).isEqualTo(1);
        qName = new QName(NS_B, "sample");
        assertThat(getChildrenCount(documentElement.getChildrenWithName(qName))).isEqualTo(1);
        qName = new QName(NS_C, "sample");
        assertThat(getChildrenCount(documentElement.getChildrenWithName(qName))).isEqualTo(0);

        // Test for QName with no namespace.
        // Axiom 1.2.x implemented a legacy behavior where an empty namespace URI was interpreted
        // as a wildcard (see AXIOM-11). In Axiom 1.3.x, the matching is always strict.
        qName = new QName("", "sample");
        assertThat(getChildrenCount(documentElement.getChildrenWithName(qName))).isEqualTo(0);

        // Now add an unqualified sample element to the documentElement
        factory.createOMElement("sample", null, documentElement);

        // Repeat the tests
        qName = new QName(NS_A, "sample");
        assertThat(getChildrenCount(documentElement.getChildrenWithName(qName))).isEqualTo(1);
        qName = new QName(NS_B, "sample");
        assertThat(getChildrenCount(documentElement.getChildrenWithName(qName))).isEqualTo(1);
        qName = new QName(NS_C, "sample");
        assertThat(getChildrenCount(documentElement.getChildrenWithName(qName))).isEqualTo(0);

        // Since there actually is an unqualified element child, the most accurate
        // interpretation of getChildrenWithName should be to return this one
        // child
        qName = new QName("", "sample");
        assertThat(getChildrenCount(documentElement.getChildrenWithName(qName))).isEqualTo(1);
    }
}
