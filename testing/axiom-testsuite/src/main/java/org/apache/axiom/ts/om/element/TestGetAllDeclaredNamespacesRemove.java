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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Tests that {@link Iterator#remove()} can be used on the iterator returned by {@link
 * OMElement#getAllDeclaredNamespaces()} to remove a namespace declaration.
 */
public class TestGetAllDeclaredNamespacesRemove extends AxiomTestCase {
    public TestGetAllDeclaredNamespacesRemove(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        List<String> prefixes = Arrays.asList("a", "b", "c");
        for (String prefixToRemove : prefixes) {
            OMElement element = factory.createOMElement("test", null);
            for (String prefix : prefixes) {
                element.declareNamespace("urn:" + prefix, prefix);
            }
            Set<String> seenPrefixes = new HashSet<String>();
            for (Iterator<OMNamespace> it = element.getAllDeclaredNamespaces(); it.hasNext(); ) {
                OMNamespace ns = it.next();
                String prefix = ns.getPrefix();
                if (prefix.equals(prefixToRemove)) {
                    it.remove();
                }
                seenPrefixes.add(prefix);
            }
            assertThat(seenPrefixes).containsAtLeastElementsIn(prefixes);
            for (String prefix : prefixes) {
                OMNamespace ns = element.findNamespaceURI(prefix);
                if (prefix.equals(prefixToRemove)) {
                    assertThat(ns).isNull();
                } else {
                    assertThat(ns).isNotNull();
                    assertThat(ns.getNamespaceURI()).isEqualTo("urn:" + prefix);
                }
            }
        }
    }
}
