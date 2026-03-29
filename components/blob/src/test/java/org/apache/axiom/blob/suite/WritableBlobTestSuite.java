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
package org.apache.axiom.blob.suite;

import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.axiom.testutils.suite.Binding;
import org.apache.axiom.testutils.suite.ConditionalNode;
import org.apache.axiom.testutils.suite.FanOutNode;
import org.apache.axiom.testutils.suite.InjectorNode;
import org.apache.axiom.testutils.suite.MatrixTestNode;
import org.apache.axiom.testutils.suite.MatrixTest;
import org.apache.axiom.testutils.suite.LabelBinding;
import org.apache.axiom.testutils.suite.ParentNode;

import com.google.common.collect.ImmutableList;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class WritableBlobTestSuite {
    public static MatrixTestNode create(
            WritableBlobFactory<?> factory,
            ImmutableList<Integer> sizes,
            boolean outputStreamHasReadFromSupport,
            boolean writeToUsesReadFromSupport) {

        return new InjectorNode(
                binder ->
                        binder.bind(new TypeLiteral<WritableBlobFactory<?>>() {})
                                .toInstance(factory),
                new ParentNode(
                        new MatrixTest(TestAvailable.class),
                        new MatrixTest(TestReadEOF.class),
                        new MatrixTest(TestReadFromWithError.class),
                        new MatrixTest(TestReadZeroLength.class),
                        new MatrixTest(TestReleaseTwice.class),
                        new MatrixTest(TestResetWithoutMark.class),
                        new MatrixTest(TestSkip.class),
                        new MatrixTest(TestWriteAfterCommit.class),
                        new FanOutNode<>(
                                ImmutableList.of(State.NEW, State.UNCOMMITTED, State.RELEASED),
                                Binding.singleton(Key.get(State.class)),
                                LabelBinding.DIMENSION,
                                new ParentNode(
                                        new MatrixTest(TestGetInputStreamIllegalState.class),
                                        new MatrixTest(TestGetSizeIllegalState.class),
                                        new MatrixTest(TestWriteToIllegalState.class))),
                        new FanOutNode<>(
                                ImmutableList.of(
                                        State.UNCOMMITTED, State.COMMITTED, State.RELEASED),
                                Binding.singleton(Key.get(State.class)),
                                LabelBinding.DIMENSION,
                                new ParentNode(
                                        new MatrixTest(TestGetOutputStreamIllegalState.class),
                                        new MatrixTest(TestReadFromIllegalState.class))),
                        new FanOutNode<>(
                                sizes,
                                Binding.singleton(Key.get(Integer.class, Names.named("size"))),
                                LabelBinding.simpleInt("size"),
                                new ParentNode(
                                        new MatrixTest(TestMarkReset.class),
                                        new MatrixTest(TestReadFrom.class),
                                        new MatrixTest(TestRandomReadWrite.class),
                                        new MatrixTest(TestWriteTo.class),
                                        new MatrixTest(TestWriteToWithError.class),
                                        new ConditionalNode(
                                                injector -> outputStreamHasReadFromSupport,
                                                new MatrixTest(TestReadFromSupport.class)),
                                        new ConditionalNode(
                                                injector -> writeToUsesReadFromSupport,
                                                new MatrixTest(
                                                        TestWriteToWithReadFromSupport.class))))));
    }
}
