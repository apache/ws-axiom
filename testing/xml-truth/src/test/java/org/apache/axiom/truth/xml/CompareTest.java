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
import org.apache.axiom.testutils.suite.FanOutNode;
import org.apache.axiom.ts.xml.XMLSample;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import com.google.common.collect.ImmutableList;
import com.google.inject.name.Names;

public class CompareTest {
    @TestFactory
    public Stream<DynamicNode> tests() {
        return new FanOutNode<>(
                        Multiton.getInstances(XMLSample.class),
                        (binder, value) ->
                                binder.bind(XMLSample.class)
                                        .annotatedWith(Names.named("sample"))
                                        .toInstance(value),
                        (params, value) -> params.addTestParameter("sample", value.getName()),
                        new FanOutNode<>(
                                Multiton.getInstances(XMLObjectFactory.class),
                                (binder, value) ->
                                        binder.bind(XMLObjectFactory.class)
                                                .annotatedWith(Names.named("left"))
                                                .toInstance(value),
                                (params, value) -> params.addTestParameter("left", value.getName()),
                                new FanOutNode<>(
                                        Multiton.getInstances(XMLObjectFactory.class),
                                        (binder, value) ->
                                                binder.bind(XMLObjectFactory.class)
                                                        .annotatedWith(Names.named("right"))
                                                        .toInstance(value),
                                        (params, value) ->
                                                params.addTestParameter("right", value.getName()),
                                        new FanOutNode<>(
                                                ImmutableList.of(true, false),
                                                (binder, value) ->
                                                        binder.bind(Boolean.class)
                                                                .annotatedWith(
                                                                        Names.named(
                                                                                "expandEntityReferences"))
                                                                .toInstance(value),
                                                (params, value) ->
                                                        params.addTestParameter(
                                                                "expandEntityReferences",
                                                                String.valueOf(value)),
                                                new MatrixTest(CompareTestCase.class)))))
                .toDynamicNodes();
    }
}
