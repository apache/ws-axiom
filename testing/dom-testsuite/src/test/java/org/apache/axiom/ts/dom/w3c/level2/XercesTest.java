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
package org.apache.axiom.ts.dom.w3c.level2;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.axiom.ts.dom.DocumentBuilderFactoryFactory;
import org.apache.axiom.ts.dom.w3c.W3CTestCase;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;

public class XercesTest extends TestCase {
    public static TestSuite suite() {
        W3CDOMLevel2TestSuiteBuilder builder =
                new W3CDOMLevel2TestSuiteBuilder(
                        new DocumentBuilderFactoryFactory() {
                            @Override
                            public DocumentBuilderFactory newInstance() {
                                return new DocumentBuilderFactoryImpl();
                            }
                        });

        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createAttributeNS06)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocument08)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/createDocumentType04)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/getNamedItemNS03)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/getNamedItemNS04)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/namednodemapgetnameditemns01)");
        builder.exclude(
                W3CTestCase.class,
                "(id=http://www.w3.org/2001/DOM-Test-Suite/level2/core/setAttributeNS10)");

        return builder.build();
    }
}
