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
import java.util.Random;

import org.apache.axiom.blob.WritableBlob;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.commons.io.IOUtils;

public class TestMarkReset extends SizeSensitiveWritableBlobTestCase {
    public TestMarkReset(WritableBlobFactory<?> factory, int size) {
        super(factory, State.NEW, size);
    }

    @Override
    protected void runTest(WritableBlob blob) throws Throwable {
        Random random = new Random();
        byte[] sourceData1 = new byte[size / 2];
        byte[] sourceData2 = new byte[size / 2];
        random.nextBytes(sourceData1);
        random.nextBytes(sourceData2);
        OutputStream out = blob.getOutputStream();
        out.write(sourceData1);
        out.write(sourceData2);
        out.close();
        InputStream in = blob.getInputStream();
        try {
            assertThat(in.markSupported()).isTrue();
            byte[] data1 = new byte[sourceData1.length];
            byte[] data2 = new byte[sourceData2.length];
            IOUtils.readFully(in, data1);
            in.mark(sourceData2.length);
            IOUtils.readFully(in, data2);
            in.reset();
            IOUtils.readFully(in, data2);
            assertThat(data1).isEqualTo(sourceData1);
            assertThat(data2).isEqualTo(sourceData2);
        } finally {
            in.close();
        }
    }
}
