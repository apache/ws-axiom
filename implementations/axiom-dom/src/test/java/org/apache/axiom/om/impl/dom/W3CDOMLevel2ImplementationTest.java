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
package org.apache.axiom.om.impl.dom;

import java.util.stream.Stream;

import org.apache.axiom.testutils.suite.MatrixTestFilters;
import org.apache.axiom.ts.dom.w3c.level2.W3CDOMLevel2TestSuite;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

public class W3CDOMLevel2ImplementationTest {
    @TestFactory
    Stream<DynamicNode> tests() {
        return W3CDOMLevel2TestSuite.create(DOMTests.FACTORY, DOMTests.UNSUPPORTED_FEATURES)
                .toDynamicNodes(
                        MatrixTestFilters.builder()
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocument03)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocument04)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocumentType01)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocumentType02)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocumentType03)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocumentType04)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/documentimportnode14)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/documentimportnode21)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/documenttypeinternalSubset01)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/documenttypepublicid01)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/documenttypesystemid01)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/domimplementationcreatedocumenttype01)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/domimplementationcreatedocumenttype02)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/domimplementationcreatedocumenttype04)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/getElementsByTagNameNS01)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/getElementsByTagNameNS08)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/hc_nodedocumentfragmentnormalize1)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/hc_nodedocumentfragmentnormalize2)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/importNode07)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/importNode10)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/importNode16)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/namednodemapremovenameditemns02)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/nodegetownerdocument01)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/nodegetownerdocument02)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/nodenormalize01)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/nodesetprefix06)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/nodesetprefix07)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/nodesetprefix09)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/prefix05)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/prefix06)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/prefix07)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/removeAttributeNS02)")
                                .build());
    }
}
