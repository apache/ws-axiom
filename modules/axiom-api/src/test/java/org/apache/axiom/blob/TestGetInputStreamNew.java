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
package org.apache.axiom.blob;

import static com.google.common.truth.Truth.assertThat;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.axiom.blob.WritableBlob;
import org.apache.commons.io.IOUtils;

public class TestGetInputStreamNew extends WritableBlobTestCase {
    public TestGetInputStreamNew(WritableBlobFactory factory) {
        super(factory);
    }

    @Override
    protected void runTest(WritableBlob blob) throws Throwable {
        if (blob.isSupportingReadUncommitted()) {
            Random random = new Random();
            // The order of instructions is important here: we first get
            // the input stream (when the blob is still in state NEW) and
            // only then we request the output stream (which will put the
            // stream in state UNCOMMITTED).
            InputStream in = blob.getInputStream();
            OutputStream out = blob.getOutputStream();
            assertThat(in.read()).isEqualTo(-1);
            // Check that any data written to the output stream immediately becomes available
            // on the input stream.
            byte[] data = new byte[1000];
            random.nextBytes(data);
            out.write(data);
            assertThat(IOUtils.toByteArray(in)).isEqualTo(data);
            random.nextBytes(data);
            out.write(data);
            assertThat(IOUtils.toByteArray(in)).isEqualTo(data);
        } else {
            try {
                blob.getInputStream();
                fail("Expected IllegalStateException");
            } catch (IllegalStateException ex) {
                // Expected
            }
        }
    }
}
