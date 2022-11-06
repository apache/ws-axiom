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

import org.apache.axiom.blob.WritableBlob;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;

public class TestAvailable extends WritableBlobTestCase {
    public TestAvailable(WritableBlobFactory<?> factory) {
        super(factory, State.NEW);
    }

    @Override
    protected void runTest(WritableBlob blob) throws Throwable {
        blob.readFrom(new NullInputStream(1000));
        InputStream in = blob.getInputStream();
        try {
            IOUtils.toByteArray(in, 200);
            assertThat(in.available()).isEqualTo(800);
        } finally {
            in.close();
        }
    }
}
