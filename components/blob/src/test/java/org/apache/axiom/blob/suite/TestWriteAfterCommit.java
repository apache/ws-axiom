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
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.axiom.ext.io.ReadFromSupport;
import org.apache.commons.io.input.NullInputStream;

public class TestWriteAfterCommit extends WritableBlobTestCase {
    public TestWriteAfterCommit(WritableBlobFactory<?> factory) {
        super(factory, State.NEW);
    }

    @Override
    protected void runTest(WritableBlob blob) throws Throwable {
        OutputStream out = blob.getOutputStream();
        out.close();
        try {
            out.write(new byte[10]);
            fail("Expected exception");
        } catch (IllegalStateException ex) {
            // OK
        } catch (IOException ex) {
            // OK
        }
        try {
            out.write(new byte[10], 3, 5);
            fail("Expected exception");
        } catch (IllegalStateException ex) {
            // OK
        } catch (IOException ex) {
            // OK
        }
        try {
            out.write(0);
            fail("Expected exception");
        } catch (IllegalStateException ex) {
            // OK
        } catch (IOException ex) {
            // OK
        }
        if (out instanceof ReadFromSupport) {
            try {
                ((ReadFromSupport)out).readFrom(new NullInputStream(10), -1);
                fail("Expected exception");
            } catch (IllegalStateException ex) {
                // OK
            } catch (IOException ex) {
                // OK
            }
        }
    }
}
