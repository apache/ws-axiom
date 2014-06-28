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
package org.apache.axiom.ts.w3c.dom;

import junit.framework.TestSuite;

import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;
import org.w3c.domts.DOMTestDocumentBuilderFactory;
import org.w3c.domts.JAXPDOMTestDocumentBuilderFactory;
import org.w3c.domts.JUnitTestSuiteAdapter;
import org.w3c.domts.level1.core.alltests;
import org.w3c.domts.level1.core.hc_attrgetvalue2;

public class XercesDOM1Test extends TestSuite {
    public static TestSuite suite() throws Exception {
        DOMTestDocumentBuilderFactory factory = new JAXPDOMTestDocumentBuilderFactory(
                new DocumentBuilderFactoryImpl(),
                JAXPDOMTestDocumentBuilderFactory.getConfiguration1());

        FilteredDOMTestSuite suite = new FilteredDOMTestSuite(factory, new alltests(factory));
        suite.addExclude(hc_attrgetvalue2.class);
        return new JUnitTestSuiteAdapter(suite);
    }
}
