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
package org.apache.axiom.ts.dom;

import java.util.stream.Stream;

import org.apache.axiom.testutils.suite.MatrixTestFilters;
import org.apache.axiom.ts.dom.document.TestCreateElementNSWithSupplementaryCharacter;
import org.apache.axiom.ts.dom.document.TestCreateElementWithSupplementaryCharacter;
import org.apache.axiom.ts.dom.element.TestLookupNamespaceURIXercesJ1586;
import org.apache.axiom.ts.dom.element.TestSetPrefixWithSupplementaryCharacter;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

public class XercesTest {
    @TestFactory
    public Stream<DynamicNode> tests() {
        return DOMTestSuite.create(DocumentBuilderFactoryImpl::new)
                .toDynamicNodes(
                        MatrixTestFilters.builder()
                                .add(TestCreateElementWithSupplementaryCharacter.class)
                                .add(TestCreateElementNSWithSupplementaryCharacter.class)
                                .add(TestSetPrefixWithSupplementaryCharacter.class)
                                // XERCESJ-1586
                                .add(TestLookupNamespaceURIXercesJ1586.class)
                                .build());
    }
}
