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
package org.apache.axiom.ts;

import java.util.HashSet;
import java.util.Set;

import org.apache.axiom.om.OMMetaFactory;

import junit.framework.TestSuite;

public class AxiomTestSuiteBuilder {
    private final OMMetaFactory metaFactory;
    private final Set/*<Class>*/ excludedTests = new HashSet();
    private TestSuite suite;
    
    public AxiomTestSuiteBuilder(OMMetaFactory metaFactory) {
        this.metaFactory = metaFactory;
    }
    
    public void exclude(Class testClass) {
        excludedTests.add(testClass);
    }
    
    public TestSuite build() {
        suite = new TestSuite();
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueWithXmlPrefix1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueWithXmlPrefix2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeWithXmlPrefix1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeWithXmlPrefix2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSetTextQName(metaFactory));
        return suite;
    }
    
    private void addTest(AxiomTestCase test) {
        if (!excludedTests.contains(test.getClass())) {
            suite.addTest(test);
        }
    }
}
