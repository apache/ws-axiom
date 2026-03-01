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
import org.apache.axiom.testutils.suite.MatrixTestSuite;
import org.apache.axiom.testutils.suite.ParameterFanOutNode;
import org.apache.axiom.ts.saaj.body.TestAddChildElementReification;
import org.apache.axiom.ts.saaj.element.TestAddChildElementLocalName;
import org.apache.axiom.ts.saaj.element.TestAddChildElementLocalNamePrefixAndURI;
import org.apache.axiom.ts.saaj.element.TestGetOwnerDocument;
import org.apache.axiom.ts.saaj.element.TestSetParentElement;
import org.apache.axiom.ts.saaj.header.TestExamineMustUnderstandHeaderElements;
import org.apache.axiom.ts.soap.SOAPSpec;

import com.google.inject.AbstractModule;

public class SAAJTestSuite {
    public static MatrixTestSuite create(SAAJMetaFactory metaFactory) {
        SAAJImplementation impl = new SAAJImplementation(metaFactory);
        MatrixTestSuite suite =
                new MatrixTestSuite(
                        new AbstractModule() {
                            @Override
                            protected void configure() {
                                bind(SAAJImplementation.class).toInstance(impl);
                            }
                        });

        ParameterFanOutNode<SOAPSpec> specs =
                new ParameterFanOutNode<>(
                        SOAPSpec.class,
                        Multiton.getInstances(SOAPSpec.class),
                        "spec",
                        SOAPSpec::getName);
        specs.addChild(new MatrixTest(TestAddChildElementReification.class));
        specs.addChild(new MatrixTest(TestExamineMustUnderstandHeaderElements.class));
        specs.addChild(new MatrixTest(TestAddChildElementLocalName.class));
        specs.addChild(new MatrixTest(TestAddChildElementLocalNamePrefixAndURI.class));
        specs.addChild(new MatrixTest(TestSetParentElement.class));
        specs.addChild(new MatrixTest(TestGetOwnerDocument.class));
        suite.addChild(specs);

        return suite;
    }
}
