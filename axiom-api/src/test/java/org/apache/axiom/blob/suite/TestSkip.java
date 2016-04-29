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

import static com.google.common.truth.Truth.assertThat;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axiom.blob.WritableBlob;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.commons.io.IOUtils;

public class TestSkip extends WritableBlobTestCase {
    public TestSkip(WritableBlobFactory<?> factory) {
        super(factory, State.NEW);
    }

    @Override
    protected void runTest(WritableBlob blob) throws Throwable {
        OutputStream out = blob.getOutputStream();
        out.write(new byte[] { 2, 4, 6, 8, 9, 7, 5, 3, 1 });
        out.close();
        InputStream in = blob.getInputStream();
        try {
            assertThat(in.skip(4)).isEqualTo(4);
            assertThat(IOUtils.toByteArray(in, 3)).isEqualTo(new byte[] { 9, 7, 5 });
            // The skip method in FileInputStream returns 10 instead of 2
            // (see http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6294974).
            assertThat(in.skip(10)).isAnyOf(2L, 10L);
            assertThat(in.read()).isEqualTo(-1);
        } finally {
            in.close();
        }
    }
}
