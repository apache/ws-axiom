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
package org.apache.axiom.ts.soapdom;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.Binding;
import org.apache.axiom.testutils.suite.FanOutNode;
import org.apache.axiom.testutils.suite.InjectorNode;
import org.apache.axiom.testutils.suite.MatrixTestNode;
import org.apache.axiom.testutils.suite.MatrixTest;
import org.apache.axiom.testutils.suite.ParentNode;
import org.apache.axiom.ts.soap.SOAPSpec;

import com.google.inject.Key;

public class SOAPDOMTestSuite {
    public static MatrixTestNode create(DOMMetaFactory metaFactory) {
        return new InjectorNode(
                binder -> binder.bind(OMMetaFactory.class).toInstance(metaFactory),
                new FanOutNode<>(
                        Multiton.getInstances(SOAPSpec.class),
                        Binding.singleton(Key.get(SOAPSpec.class)),
                        (injector, value, labels) -> labels.addLabel("spec", value.getName()),
                        new ParentNode(
                                new MatrixTest(
                                        org.apache.axiom.ts.soapdom.header
                                                .TestExamineAllHeaderBlocks.class),
                                new MatrixTest(
                                        org.apache.axiom.ts.soapdom.header
                                                .TestExamineMustUnderstandHeaderBlocks.class),
                                new MatrixTest(
                                        org.apache.axiom.ts.soapdom.message
                                                .TestLazySOAPFactorySelection.class))));
    }
}
