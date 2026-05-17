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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

/**
 * Tests the behavior of the <code>createOMElement</code> methods in {@link OMFactory} when
 * requested to create an element without namespace as a child of an element that has a default
 * namespace with a non empty namespace URI. In this case, a namespace declaration is added to the
 * created element to override the default namespace.
 *
 * <p>This is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-400">AXIOM-400</a>.
 */
public class TestCreateOMElementWithoutNamespace2 extends CreateOMElementTestCase {
    @Inject
    private OMFactory factory;

    @Inject
    public TestCreateOMElementWithoutNamespace2(CreateOMElementVariant variant) {
        super(variant, null);
    }

    @Override
    public void runTest() throws Throwable {
        OMElement parent = factory.createOMElement("parent", factory.createOMNamespace("urn:test", ""));
        OMElement child = variant.createOMElement(factory, parent, "test", "", "");
        assertThat(child.getLocalName()).isEqualTo("test");
        assertThat(child.getNamespace()).isNull();
        Iterator<OMNamespace> it = child.getAllDeclaredNamespaces();
        assertThat(it.hasNext()).isTrue();
        OMNamespace decl = it.next();
        assertThat(decl.getPrefix()).isEqualTo("");
        assertThat(decl.getNamespaceURI()).isEqualTo("");
        assertThat(it.hasNext()).isFalse();
    }
}
