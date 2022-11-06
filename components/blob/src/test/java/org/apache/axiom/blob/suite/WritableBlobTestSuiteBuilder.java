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
import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;

public class WritableBlobTestSuiteBuilder extends MatrixTestSuiteBuilder {
    private final WritableBlobFactory<?> factory;
    private final int[] sizes;
    private final boolean outputStreamHasReadFromSupport;
    private final boolean writeToUsesReadFromSupport;

    public WritableBlobTestSuiteBuilder(WritableBlobFactory<?> factory, int[] sizes,
            boolean outputStreamHasReadFromSupport, boolean writeToUsesReadFromSupport) {
        this.factory = factory;
        this.sizes = sizes;
        this.outputStreamHasReadFromSupport = outputStreamHasReadFromSupport;
        this.writeToUsesReadFromSupport = writeToUsesReadFromSupport;
    }

    @Override
    protected void addTests() {
        addTest(new TestAvailable(factory));
        addTest(new TestGetInputStreamIllegalState(factory, State.NEW));
        addTest(new TestGetInputStreamIllegalState(factory, State.UNCOMMITTED));
        addTest(new TestGetInputStreamIllegalState(factory, State.RELEASED));
        addTest(new TestGetOutputStreamIllegalState(factory, State.UNCOMMITTED));
        addTest(new TestGetOutputStreamIllegalState(factory, State.COMMITTED));
        addTest(new TestGetOutputStreamIllegalState(factory, State.RELEASED));
        addTest(new TestGetSizeIllegalState(factory, State.NEW));
        addTest(new TestGetSizeIllegalState(factory, State.UNCOMMITTED));
        addTest(new TestGetSizeIllegalState(factory, State.RELEASED));
        addTest(new TestReadEOF(factory));
        addTest(new TestReadFromIllegalState(factory, State.UNCOMMITTED));
        addTest(new TestReadFromIllegalState(factory, State.COMMITTED));
        addTest(new TestReadFromIllegalState(factory, State.RELEASED));
        addTest(new TestReadFromWithError(factory));
        addTest(new TestReadZeroLength(factory));
        addTest(new TestReleaseTwice(factory));
        addTest(new TestResetWithoutMark(factory));
        addTest(new TestSkip(factory));
        addTest(new TestWriteAfterCommit(factory));
        addTest(new TestWriteToIllegalState(factory, State.NEW));
        addTest(new TestWriteToIllegalState(factory, State.UNCOMMITTED));
        addTest(new TestWriteToIllegalState(factory, State.RELEASED));
        for (int size : sizes) {
            addTests(size);
        }
    };
    
    private void addTests(int size) {
        addTest(new TestMarkReset(factory, size));
        addTest(new TestReadFrom(factory, size));
        if (outputStreamHasReadFromSupport) {
            addTest(new TestReadFromSupport(factory, size));
        }
        addTest(new TestRandomReadWrite(factory, size));
        addTest(new TestWriteTo(factory, size));
        if (writeToUsesReadFromSupport) {
            addTest(new TestWriteToWithReadFromSupport(factory, size));
        }
        addTest(new TestWriteToWithError(factory, size));
    }
}
