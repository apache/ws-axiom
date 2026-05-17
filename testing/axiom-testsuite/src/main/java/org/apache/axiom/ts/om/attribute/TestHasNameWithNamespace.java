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
package org.apache.axiom.ts.om.attribute;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.testutils.suite.MatrixTestCase;

/**
 * Tests that {@link OMAttribute#hasName(QName)} returns the correct value for an attribute (with
 * namespace) created by {@link OMFactory#createOMAttribute(String, OMNamespace, String)}.
 */
public class TestHasNameWithNamespace implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Override
    public void runTest() throws Throwable {
        String localName = "attr";
        String uri = "urn:test";
        OMNamespace ns = factory.createOMNamespace(uri, "p");
        OMAttribute attr = factory.createOMAttribute(localName, ns, "value");
        assertThat(attr.hasName(new QName(uri, localName, "p"))).isTrue();
        assertThat(attr.hasName(new QName(uri, localName, "q"))).isTrue();
        assertThat(attr.hasName(new QName("http://example.org", localName, "p")))
                .isFalse();
        assertThat(attr.hasName(new QName(uri, "otherName", "p"))).isFalse();
    }
}
