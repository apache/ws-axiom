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
import org.apache.axiom.ts.dom.w3c.level1.W3CDOMLevel1TestSuite;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

public class W3CDOMLevel1ImplementationTest {
    @TestFactory
    Stream<DynamicNode> tests() {
        return W3CDOMLevel1TestSuite.create(DOMTests.FACTORY, DOMTests.UNSUPPORTED_FEATURES)
                .toDynamicNodes(
                        MatrixTestFilters.builder()
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/attrspecifiedvalueremove)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/documentcreateelementdefaultattr)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/documentgetelementsbytagnametotallength)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/documentinvalidcharacterexceptioncreateentref)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/documentinvalidcharacterexceptioncreateentref1)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/documentinvalidcharacterexceptioncreatepi)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/documentinvalidcharacterexceptioncreatepi1)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/elementremoveattribute)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/elementremoveattributerestoredefaultvalue)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/elementsetattributenomodificationallowederr)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/namednodemapremovenameditem)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/namednodemapremovenameditemgetvalue)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/processinginstructionsetdatanomodificationallowederr)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_elementnormalize)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_elementnormalize2)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_attrgetvalue2)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_attrinsertbefore7)")
                                .add(
                                        "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_attrnormalize)")
                                .build());
    }
}
