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
package org.apache.axiom.ts.omdom;

import static org.apache.axiom.testing.multiton.Multiton.getInstances;

import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
import org.apache.axiom.ts.xml.XMLSample;

/**
 * Builds a test suite for Axiom implementations that also implement DOM. Note that this test suite
 * only contains tests that depend on Axiom specific features. Pure DOM tests (that are executable
 * with a standard DOM implementation) should go to <code>dom-testsuite</code>.
 */
public class OMDOMTestSuiteBuilder extends MatrixTestSuiteBuilder {
    private final DOMMetaFactory metaFactory;

    public OMDOMTestSuiteBuilder(DOMMetaFactory metaFactory) {
        this.metaFactory = metaFactory;
    }

    @Override
    protected void addTests() {
        addTest(new org.apache.axiom.ts.omdom.attr.TestGetNamespaceNormalized(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.attr.TestSetValueOnNamespaceDeclaration(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.document.TestAppendChildForbidden(metaFactory, true));
        addTest(
                new org.apache.axiom.ts.omdom.document.TestAppendChildForbidden(
                        metaFactory, false));
        addTest(
                new org.apache.axiom.ts.omdom.document.TestCreateDocumentFragmentInterfaces(
                        metaFactory));
        addTest(new org.apache.axiom.ts.omdom.document.TestGetOMFactory1(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.document.TestGetOMFactory2(metaFactory));
        for (XMLSample sample : getInstances(XMLSample.class)) {
            addTest(new org.apache.axiom.ts.omdom.document.TestImportNode(metaFactory, sample));
        }
        addTest(
                new org.apache.axiom.ts.omdom.document.TestInsertBeforeForbidden(
                        metaFactory, true));
        addTest(
                new org.apache.axiom.ts.omdom.document.TestInsertBeforeForbidden(
                        metaFactory, false));
        addTest(new org.apache.axiom.ts.omdom.element.TestAddAttributeReplace(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.element.TestAddChildFromForeignDocument(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.element.TestAppendChildIncomplete(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.element.TestCloneNodeIncomplete(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.element.TestCloneOMElement(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.element.TestDetach(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.element.TestGetNamespaceNormalized(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.element.TestInsertBeforeIncomplete(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.element.TestRemoveAttribute(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.element.TestRemoveAttributeNode(metaFactory));
        addTest(
                new org.apache.axiom.ts.omdom.element.TestRemoveAttributeNSNamespaceDeclaration(
                        metaFactory));
        addTest(new org.apache.axiom.ts.omdom.element.TestRemoveChildIncomplete(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.element.TestReplaceChildFirstIncomplete(metaFactory));
        addTest(
                new org.apache.axiom.ts.omdom.element.TestReplaceChildMiddleIncomplete(
                        metaFactory));
        addTest(new org.apache.axiom.ts.omdom.factory.TestCreateOMAttribute(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.factory.TestCreateOMTextCDATASection(metaFactory));
        addTest(
                new org.apache.axiom.ts.omdom.factory.TestCreateOMTextCDATASectionWithParent(
                        metaFactory));
        addTest(
                new org.apache.axiom.ts.omdom.node.TestInsertSiblingAfterFromForeignDocument(
                        metaFactory));
        addTest(
                new org.apache.axiom.ts.omdom.node.TestInsertSiblingBeforeFromForeignDocument(
                        metaFactory));
        addTest(new org.apache.axiom.ts.omdom.text.TestCloneNodeBinary(metaFactory));
        addTest(new org.apache.axiom.ts.omdom.text.TestGetNodeValueBinary(metaFactory));
    }
}
