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
package org.apache.axiom.util.sax;

import org.apache.axiom.testutils.suite.Binding;
import org.apache.axiom.testutils.suite.InjectorNode;
import org.apache.axiom.testutils.suite.MatrixTestNode;
import org.apache.axiom.testutils.suite.MatrixTest;
import org.apache.axiom.testutils.suite.FanOutNode;
import org.xml.sax.XMLReader;

import com.google.common.collect.ImmutableList;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class XMLReaderTestSuite {
    public static MatrixTestNode create(XMLReader xmlReader) {
        return new InjectorNode(
                binder -> binder.bind(XMLReader.class).toInstance(xmlReader),
                new FanOutNode<>(
                        ImmutableList.of(
                                "http://xml.org/sax/features/namespaces",
                                "http://xml.org/sax/features/namespace-prefixes",
                                "http://xml.org/sax/features/external-general-entities"),
                        Binding.singleton(Key.get(String.class, Names.named("feature"))),
                        (injector, value, params) -> params.addTestParameter("feature", value),
                        new MatrixTest(TestGetSetFeature.class)));
    }
}
