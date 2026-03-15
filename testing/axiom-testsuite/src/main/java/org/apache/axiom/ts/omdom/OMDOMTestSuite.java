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

import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.InjectorNode;
import org.apache.axiom.testutils.suite.MatrixTest;
import org.apache.axiom.testutils.suite.ParentNode;
import org.apache.axiom.testutils.suite.ParameterFanOutNode;
import org.apache.axiom.ts.xml.XMLSample;

import com.google.common.collect.ImmutableList;
import com.google.inject.name.Names;

/**
 * Builds a test suite for Axiom implementations that also implement DOM. Note that this test suite
 * only contains tests that depend on Axiom specific features. Pure DOM tests (that are executable
 * with a standard DOM implementation) should go to <code>dom-testsuite</code>.
 */
public class OMDOMTestSuite {
    public static InjectorNode create(DOMMetaFactory metaFactory) {
        return new InjectorNode(
                binder -> binder.bind(DOMMetaFactory.class).toInstance(metaFactory),
                new ParentNode(
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.attr.TestGetNamespaceNormalized.class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.attr.TestSetValueOnNamespaceDeclaration
                                        .class),
                        new ParameterFanOutNode<>(
                                ImmutableList.of(true, false),
                                (binder, value) ->
                                        binder.bind(Boolean.class)
                                                .annotatedWith(Names.named("build"))
                                                .toInstance(value),
                                "build",
                                String::valueOf,
                                new ParentNode(
                                        new MatrixTest(
                                                org.apache.axiom.ts.omdom.document
                                                        .TestAppendChildForbidden.class),
                                        new MatrixTest(
                                                org.apache.axiom.ts.omdom.document
                                                        .TestInsertBeforeForbidden.class))),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.document
                                        .TestCreateDocumentFragmentInterfaces.class),
                        new MatrixTest(org.apache.axiom.ts.omdom.document.TestGetOMFactory1.class),
                        new MatrixTest(org.apache.axiom.ts.omdom.document.TestGetOMFactory2.class),
                        new ParameterFanOutNode<>(
                                Multiton.getInstances(XMLSample.class),
                                (binder, value) -> binder.bind(XMLSample.class).toInstance(value),
                                "file",
                                XMLSample::getName,
                                new MatrixTest(
                                        org.apache.axiom.ts.omdom.document.TestImportNode.class)),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.element.TestAddAttributeReplace.class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.element.TestAddChildFromForeignDocument
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.element.TestAppendChildIncomplete.class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.element.TestCloneNodeIncomplete.class),
                        new MatrixTest(org.apache.axiom.ts.omdom.element.TestCloneOMElement.class),
                        new MatrixTest(org.apache.axiom.ts.omdom.element.TestDetach.class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.element.TestGetNamespaceNormalized.class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.element.TestInsertBeforeIncomplete.class),
                        new MatrixTest(org.apache.axiom.ts.omdom.element.TestRemoveAttribute.class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.element.TestRemoveAttributeNode.class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.element
                                        .TestRemoveAttributeNSNamespaceDeclaration.class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.element.TestRemoveChildIncomplete.class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.element.TestReplaceChildFirstIncomplete
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.element.TestReplaceChildMiddleIncomplete
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.factory.TestCreateOMAttribute.class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.factory.TestCreateOMTextCDATASection
                                        .class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.factory
                                        .TestCreateOMTextCDATASectionWithParent.class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.node
                                        .TestInsertSiblingAfterFromForeignDocument.class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.node
                                        .TestInsertSiblingBeforeFromForeignDocument.class),
                        new MatrixTest(org.apache.axiom.ts.omdom.text.TestCloneNodeBinary.class),
                        new MatrixTest(
                                org.apache.axiom.ts.omdom.text.TestGetNodeValueBinary.class)));
    }
}
