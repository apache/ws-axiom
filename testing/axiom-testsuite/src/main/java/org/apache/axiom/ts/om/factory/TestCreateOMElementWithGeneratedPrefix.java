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
 * requested to create an element with a namespace but no namespace prefix is given and no matching
 * namespace declaration is in scope. In this case, <code>createOMElement</code> is expected to
 * generate a prefix.
 */
public class TestCreateOMElementWithGeneratedPrefix extends CreateOMElementTestCase {
    @Inject
    private OMFactory factory;

    @Inject
    public TestCreateOMElementWithGeneratedPrefix(
            CreateOMElementVariant variant, CreateOMElementParentSupplier parentSupplier) {
        super(variant, parentSupplier);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement element =
                variant.createOMElement(factory, parentSupplier.createParent(factory), "test", "urn:test", null);
        assertThat(element.getLocalName()).isEqualTo("test");
        OMNamespace ns = element.getNamespace();
        assertThat(ns).isNotNull();
        assertThat(ns.getNamespaceURI()).isEqualTo("urn:test");
        // Axiom auto-generates a prefix here
        assertThat(ns.getPrefix()).isNotEmpty();
        Iterator<OMNamespace> it = element.getAllDeclaredNamespaces();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isEqualTo(ns);
        assertThat(it.hasNext()).isFalse();
    }
}
