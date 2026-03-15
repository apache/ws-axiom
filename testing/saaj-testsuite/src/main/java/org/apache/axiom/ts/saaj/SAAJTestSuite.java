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
package org.apache.axiom.ts.saaj;

import jakarta.xml.soap.SAAJMetaFactory;

import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.MatrixTest;
import org.apache.axiom.testutils.suite.InjectorNode;
import org.apache.axiom.testutils.suite.ParentNode;
import org.apache.axiom.testutils.suite.ParameterFanOutNode;
import org.apache.axiom.ts.saaj.body.TestAddChildElementReification;
import org.apache.axiom.ts.saaj.element.TestAddChildElementLocalName;
import org.apache.axiom.ts.saaj.element.TestAddChildElementLocalNamePrefixAndURI;
import org.apache.axiom.ts.saaj.element.TestGetOwnerDocument;
import org.apache.axiom.ts.saaj.element.TestSetParentElement;
import org.apache.axiom.ts.saaj.header.TestExamineMustUnderstandHeaderElements;
import org.apache.axiom.ts.soap.SOAPSpec;

public class SAAJTestSuite {
    public static InjectorNode create(SAAJMetaFactory metaFactory) {
        return new InjectorNode(
                binder ->
                        binder.bind(SAAJImplementation.class)
                                .toInstance(new SAAJImplementation(metaFactory)),
                new ParameterFanOutNode<>(
                        Multiton.getInstances(SOAPSpec.class),
                        (binder, value) -> binder.bind(SOAPSpec.class).toInstance(value),
                        "spec",
                        SOAPSpec::getName,
                        new ParentNode(
                                new MatrixTest(TestAddChildElementReification.class),
                                new MatrixTest(TestExamineMustUnderstandHeaderElements.class),
                                new MatrixTest(TestAddChildElementLocalName.class),
                                new MatrixTest(TestAddChildElementLocalNamePrefixAndURI.class),
                                new MatrixTest(TestSetParentElement.class),
                                new MatrixTest(TestGetOwnerDocument.class))));
    }
}
