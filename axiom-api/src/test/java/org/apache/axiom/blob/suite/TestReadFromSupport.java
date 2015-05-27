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
import java.io.OutputStream;

import org.apache.axiom.blob.WritableBlob;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.axiom.ext.io.ReadFromSupport;
import org.apache.commons.io.IOUtils;

public class TestReadFromSupport extends WritableBlobTestCase {
    public TestReadFromSupport(WritableBlobFactory factory) {
        super(factory, State.NEW);
    }

    @Override
    protected void runTest(WritableBlob blob) throws Throwable {
        OutputStream out = blob.getOutputStream();
        try {
            out.write(new byte[] { 50, 60 });
            ((ReadFromSupport)out).readFrom(new ByteArrayInputStream(new byte[] { 1, 2, 3, 4 }), -1);
            ((ReadFromSupport)out).readFrom(new ByteArrayInputStream(new byte[] { 5, 6, 7, 8 }), 2);
            out.write(new byte[] { 70, 80 });
        } finally {
            out.close();
        }
        InputStream in = blob.getInputStream();
        try {
            assertThat(IOUtils.toByteArray(in)).isEqualTo(
                    new byte[] { 50, 60, 1, 2, 3, 4, 5, 6, 70, 80 });
        } finally {
            in.close();
        }
    }
}
