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
package org.apache.axiom.truth.xml;

import java.util.stream.Stream;

import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.MatrixTest;
import org.apache.axiom.testutils.suite.ParameterFanOutNode;
import org.apache.axiom.ts.xml.XMLSample;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import com.google.common.collect.ImmutableList;

public class CompareTest {
    @TestFactory
    public Stream<DynamicNode> tests() {
        return new ParameterFanOutNode<>(
                        XMLSample.class,
                        Multiton.getInstances(XMLSample.class),
                        "sample",
                        XMLSample::getName,
                        new ParameterFanOutNode<>(
                                XMLObjectFactory.class,
                                Multiton.getInstances(XMLObjectFactory.class),
                                "left",
                                XMLObjectFactory::getName,
                                new ParameterFanOutNode<>(
                                        XMLObjectFactory.class,
                                        Multiton.getInstances(XMLObjectFactory.class),
                                        "right",
                                        XMLObjectFactory::getName,
                                        new ParameterFanOutNode<>(
                                                Boolean.class,
                                                ImmutableList.of(true, false),
                                                "expandEntityReferences",
                                                String::valueOf,
                                                new MatrixTest(CompareTestCase.class)))))
                .toDynamicNodes();
    }
}
