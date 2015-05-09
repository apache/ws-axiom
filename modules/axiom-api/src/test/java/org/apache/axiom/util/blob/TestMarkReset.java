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
package org.apache.axiom.util.blob;

import static com.google.common.truth.Truth.assertThat;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.util.Random;

public class TestMarkReset extends WritableBlobTestCase {
    public TestMarkReset(WritableBlobFactory factory) {
        super(factory);
    }

    @Override
    protected void runTest(WritableBlob blob) throws Throwable {
        Random random = new Random();
        byte[] sourceData1 = new byte[2000];
        byte[] sourceData2 = new byte[2000];
        random.nextBytes(sourceData1);
        random.nextBytes(sourceData2);
        OutputStream out = blob.getOutputStream();
        out.write(sourceData1);
        out.write(sourceData2);
        out.close();
        DataInputStream in = new DataInputStream(blob.getInputStream());
        byte[] data1 = new byte[sourceData1.length];
        byte[] data2 = new byte[sourceData2.length];
        in.readFully(data1);
        in.mark(sourceData2.length);
        in.readFully(data2);
        in.reset();
        in.readFully(data2);
        assertThat(data1).isEqualTo(sourceData1);
        assertThat(data2).isEqualTo(sourceData2);
    }
}
