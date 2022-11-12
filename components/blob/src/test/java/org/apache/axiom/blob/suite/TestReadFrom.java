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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Random;

import org.apache.axiom.blob.WritableBlob;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.axiom.testutils.io.InstrumentedInputStream;
import org.apache.commons.io.IOUtils;

public class TestReadFrom extends SizeSensitiveWritableBlobTestCase {
    public TestReadFrom(WritableBlobFactory<?> factory, int size) {
        super(factory, State.NEW, size);
    }

    @Override
    protected void runTest(WritableBlob blob) throws Throwable {
        Random random = new Random();
        byte[] data = new byte[size];
        random.nextBytes(data);
        InstrumentedInputStream in = new InstrumentedInputStream(new ByteArrayInputStream(data));
        assertThat(blob.readFrom(in)).isEqualTo(size);
        assertThat(in.isClosed()).isFalse();
        InputStream in2 = blob.getInputStream();
        try {
            assertThat(IOUtils.toByteArray(in2)).isEqualTo(data);
        } finally {
            in2.close();
        }
    }
}
