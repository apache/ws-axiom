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
package org.apache.axiom.om.impl.jaxp;

import java.util.stream.Stream;

import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.Binding;
import org.apache.axiom.testutils.suite.FanOutNode;
import org.apache.axiom.testutils.suite.LabelBinding;
import org.apache.axiom.testutils.suite.MatrixTest;
import org.apache.axiom.testutils.suite.MatrixTestFilters;
import org.apache.axiom.ts.xml.XMLSample;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

import com.google.common.collect.ImmutableList;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class StreamSourceToOMResultTest {
    @TestFactory
    public Stream<DynamicNode> tests() {
        MatrixTestFilters excludes =
                MatrixTestFilters.builder()
                        .add("(|(file=sax-attribute-namespace-bug.xml)(file=large.xml))")
                        .build();
        return new FanOutNode<>(
                        ImmutableList.of("default", "dom"),
                        Binding.singleton(
                                Key.get(String.class, Names.named("axiomImplementation"))),
                        LabelBinding.simpleString("axiomImplementation"),
                        new FanOutNode<>(
                                Multiton.getInstances(XMLSample.class),
                                Binding.singleton(Key.get(XMLSample.class)),
                                LabelBinding.simpleString("file", XMLSample::getName),
                                new MatrixTest(StreamSourceToOMResultTestCase.class)))
                .toDynamicNodes(excludes);
    }
}
