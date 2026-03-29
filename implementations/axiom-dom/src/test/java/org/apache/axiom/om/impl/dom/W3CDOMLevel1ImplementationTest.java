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

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.axiom.ts.dom.w3c.level1.W3CDOMLevel1TestSuiteBuilder;

public class W3CDOMLevel1ImplementationTest extends TestCase {
    public static TestSuite suite() {
        W3CDOMLevel1TestSuiteBuilder builder =
                new W3CDOMLevel1TestSuiteBuilder(DOMTests.FACTORY, DOMTests.UNSUPPORTED_FEATURES);

        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/attrspecifiedvalueremove)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/documentcreateelementdefaultattr)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/documentgetelementsbytagnametotallength)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/documentinvalidcharacterexceptioncreateentref)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/documentinvalidcharacterexceptioncreateentref1)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/documentinvalidcharacterexceptioncreatepi)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/documentinvalidcharacterexceptioncreatepi1)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/elementremoveattribute)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/elementremoveattributerestoredefaultvalue)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/elementsetattributenomodificationallowederr)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/namednodemapremovenameditem)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/namednodemapremovenameditemgetvalue)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/processinginstructionsetdatanomodificationallowederr)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_elementnormalize)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_elementnormalize2)");
        builder.exclude("(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_attrgetvalue2)");
        builder.exclude(
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_attrinsertbefore7)");
        builder.exclude("(id=http://www.w3.org/2001/DOM-Test-Suite/level1/core/hc_attrnormalize)");

        return builder.build();
    }
}
