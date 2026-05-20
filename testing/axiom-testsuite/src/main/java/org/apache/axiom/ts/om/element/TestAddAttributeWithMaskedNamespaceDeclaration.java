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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.dimension.AddAttributeStrategy;
import org.junit.jupiter.api.function.Executable;

/**
 * Test checking that adding an attribute correctly generates a new namespace declaration if an
 * equivalent namespace declaration exists but is masked. The test attempts to create the following
 * XML:
 *
 * <pre>
 * &lt;a xmlns:p="urn:ns1"&gt;
 *   &lt;b xmlns:p="urn:ns2"&gt;
 *     &lt;c xmlns:p="urn:ns1" p:attr="test"/&gt;
 *   &lt;/b&gt;
 * &lt;/a&gt;</pre>
 *
 * It only explicitly creates the namespace declarations on {@code <a>} and {@code <b>}. When adding
 * the attribute to {@code <c>}, Axiom must generate a new namespace declaration because the
 * declaration on {@code <a>} is masked by the one on {@code <b>}.
 *
 * <p>Note that because of WSTX-202, Axiom will not be able to serialize the resulting XML.
 */
public class TestAddAttributeWithMaskedNamespaceDeclaration implements Executable {
    @Inject
    private OMFactory factory;

    @Inject
    private AddAttributeStrategy strategy;

    @Override
    public void execute() throws Throwable {
        OMNamespace ns1 = factory.createOMNamespace("urn:ns1", "p");
        OMNamespace ns2 = factory.createOMNamespace("urn:ns2", "p");
        OMElement element1 = factory.createOMElement(new QName("a"));
        element1.declareNamespace(ns1);
        OMElement element2 = factory.createOMElement(new QName("b"), element1);
        element2.declareNamespace(ns2);
        OMElement element3 = factory.createOMElement(new QName("c"), element2);
        strategy.addAttribute(element3, "attr", ns1, "test");
        Iterator<OMNamespace> it = element3.getAllDeclaredNamespaces();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isEqualTo(ns1);
        assertThat(it.hasNext()).isFalse();
    }
}
