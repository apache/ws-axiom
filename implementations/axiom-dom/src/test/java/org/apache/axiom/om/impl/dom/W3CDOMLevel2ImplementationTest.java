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

import org.apache.axiom.ts.dom.w3c.W3CTestCase;
import org.apache.axiom.ts.dom.w3c.level2.W3CDOMLevel2TestSuiteBuilder;

public class W3CDOMLevel2ImplementationTest extends TestCase {
    public static TestSuite suite() {
        W3CDOMLevel2TestSuiteBuilder builder =
                new W3CDOMLevel2TestSuiteBuilder(DOMTests.FACTORY, DOMTests.UNSUPPORTED_FEATURES);

        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocument03)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocument04)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocumentType01)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocumentType02)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocumentType03)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocumentType04)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/documentimportnode14)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/documentimportnode21)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/documenttypeinternalSubset01)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/documenttypepublicid01)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/documenttypesystemid01)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/domimplementationcreatedocumenttype01)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/domimplementationcreatedocumenttype02)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/domimplementationcreatedocumenttype04)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/getElementsByTagNameNS01)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/getElementsByTagNameNS08)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/hc_nodedocumentfragmentnormalize1)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/hc_nodedocumentfragmentnormalize2)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/importNode07)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/importNode10)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/importNode16)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/namednodemapremovenameditemns02)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/nodegetownerdocument01)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/nodegetownerdocument02)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/nodenormalize01)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/nodesetprefix06)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/nodesetprefix07)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/nodesetprefix09)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/prefix05)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/prefix06)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/prefix07)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/removeAttributeNS02)");

        return builder.build();
    }
}
