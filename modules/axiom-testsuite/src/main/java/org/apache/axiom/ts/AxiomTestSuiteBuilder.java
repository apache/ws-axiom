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

import org.apache.axiom.om.AbstractTestCase;
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
        String[] conformanceFiles = AbstractTestCase.getConformanceTestFiles();
        suite = new TestSuite();
        addTest(new org.apache.axiom.ts.om.attribute.TestGetQName(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestGetDocumentElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.builder.TestGetDocumentElementWithDiscardDocument(metaFactory));
        for (int i=0; i<conformanceFiles.length; i++) {
            addTest(new org.apache.axiom.ts.om.document.TestGetXMLStreamReader(metaFactory, conformanceFiles[i], true));
            addTest(new org.apache.axiom.ts.om.document.TestGetXMLStreamReader(metaFactory, conformanceFiles[i], false));
        }
        addTest(new org.apache.axiom.ts.om.document.TestIsCompleteAfterAddingIncompleteChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSerializeAndConsume(metaFactory));
        addTest(new org.apache.axiom.ts.om.document.TestSerializeAndConsumeWithIncompleteDescendant(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithExistingNamespaceDeclarationInScope(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithExistingNamespaceDeclarationOnSameElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithMaskedNamespaceDeclaration(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestAddAttributeWithoutExistingNamespaceDeclaration(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueNonExisting(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueWithXmlPrefix1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeValueWithXmlPrefix2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeWithXmlPrefix1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetAttributeWithXmlPrefix2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildElements(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildren(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenRemove1(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenRemove2(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenRemove3(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenRemove4(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenWithLocalName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetChildrenWithName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestGetFirstChildWithName(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestIsCompleteAfterAddingIncompleteChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "D", "D",
                "<person xmlns=\"urn:ns\"><name>John</name><age>34</age><weight>50</weight></person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "D", "U",
                "<person xmlns=\"urn:ns\"><name xmlns=\"\">John</name><age xmlns=\"\">34</age><weight xmlns=\"\">50</weight></person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "D", "Q",
                "<person xmlns=\"urn:ns\"><p:name xmlns:p=\"urn:ns\">John</p:name><p:age xmlns:p=\"urn:ns\">34</p:age><p:weight xmlns:p=\"urn:ns\">50</p:weight></person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "Q", "Q",
                "<p:person xmlns:p=\"urn:ns\"><p:name>John</p:name><p:age>34</p:age><p:weight>50</p:weight></p:person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "Q", "U",
                "<p:person xmlns:p=\"urn:ns\"><name>John</name><age>34</age><weight>50</weight></p:person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "Q", "D",
                "<p:person xmlns:p=\"urn:ns\"><name xmlns=\"urn:ns\">John</name><age xmlns=\"urn:ns\">34</age><weight xmlns=\"urn:ns\">50</weight></p:person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "U", "U",
                "<person><name>John</name><age>34</age><weight>50</weight></person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "U", "Q",
                "<person><p:name xmlns:p=\"urn:ns\">John</p:name><p:age xmlns:p=\"urn:ns\">34</p:age><p:weight xmlns:p=\"urn:ns\">50</p:weight></person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerialization(metaFactory, "U", "D",
                "<person><name xmlns=\"urn:ns\">John</name><age xmlns=\"urn:ns\">34</age><weight xmlns=\"urn:ns\">50</weight></person>"));
        addTest(new org.apache.axiom.ts.om.element.TestSerializationWithTwoNonBuiltOMElements(metaFactory));
        addTest(new org.apache.axiom.ts.om.element.TestSerializeAndConsumeWithIncompleteDescendant(metaFactory));
        for (int i=0; i<conformanceFiles.length; i++) {
            addTest(new org.apache.axiom.ts.om.element.TestSerializeToOutputStream(metaFactory, conformanceFiles[i], true));
            addTest(new org.apache.axiom.ts.om.element.TestSerializeToOutputStream(metaFactory, conformanceFiles[i], false));
        }
        addTest(new org.apache.axiom.ts.om.element.TestSetTextQName(metaFactory));
        addTest(new org.apache.axiom.ts.om.factory.TestCreateOMElement(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestDetach(metaFactory, true));
        addTest(new org.apache.axiom.ts.om.node.TestDetach(metaFactory, false));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfter(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterLastChild(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingAfterOnOrphan(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBefore(metaFactory));
        addTest(new org.apache.axiom.ts.om.node.TestInsertSiblingBeforeOnOrphan(metaFactory));
        addTest(new org.apache.axiom.ts.om.text.TestBase64Streaming(metaFactory));
        return suite;
    }
    
    private void addTest(AxiomTestCase test) {
        if (!excludedTests.contains(test.getClass())) {
            suite.addTest(test);
        }
    }
}
