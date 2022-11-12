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

import java.io.IOException;
import java.io.OutputStream;

import org.apache.axiom.blob.WritableBlob;
import org.apache.axiom.testutils.suite.Dimension;
import org.apache.axiom.testutils.suite.MatrixTestCase;

public abstract class State implements Dimension {
    public static final State NEW =
            new State("NEW") {
                @Override
                public CleanupCallback transition(WritableBlob blob) throws IOException {
                    return null;
                }
            };

    public static final State UNCOMMITTED =
            new State("UNCOMMITTED") {
                @Override
                public CleanupCallback transition(final WritableBlob blob) throws IOException {
                    final OutputStream out = blob.getOutputStream();
                    return new CleanupCallback() {
                        @Override
                        public void cleanup() throws IOException {
                            out.close();
                        }
                    };
                }
            };

    public static final State COMMITTED =
            new State("COMMITTED") {
                @Override
                public CleanupCallback transition(final WritableBlob blob) throws IOException {
                    blob.getOutputStream().close();
                    return null;
                }
            };

    public static final State RELEASED =
            new State("RELEASED") {
                @Override
                public CleanupCallback transition(WritableBlob blob) throws IOException {
                    blob.getOutputStream().close();
                    blob.release();
                    return null;
                }
            };

    private final String name;

    private State(String name) {
        this.name = name;
    }

    @Override
    public final void addTestParameters(MatrixTestCase testCase) {
        testCase.addTestParameter("state", name);
    }

    public abstract CleanupCallback transition(WritableBlob blob) throws IOException;
}
