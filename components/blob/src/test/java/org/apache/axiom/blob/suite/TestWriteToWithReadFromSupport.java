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

import java.io.OutputStream;
import java.util.Random;

import org.apache.axiom.blob.WritableBlob;
import org.apache.axiom.blob.WritableBlobFactory;

public class TestWriteToWithReadFromSupport extends SizeSensitiveWritableBlobTestCase {
    public TestWriteToWithReadFromSupport(WritableBlobFactory<?> factory, int size) {
        super(factory, State.NEW, size);
    }

    @Override
    protected void runTest(WritableBlob blob) throws Throwable {
        Random random = new Random();
        byte[] data = new byte[size];
        random.nextBytes(data);
        OutputStream out = blob.getOutputStream();
        out.write(data);
        out.close();
        ByteArrayOutputStreamWithReadFromSupport baos =
                new ByteArrayOutputStreamWithReadFromSupport();
        blob.writeTo(baos);
        assertThat(baos.toByteArray()).isEqualTo(data);
        assertThat(baos.isReadFromCalled()).isTrue();
    }
}
