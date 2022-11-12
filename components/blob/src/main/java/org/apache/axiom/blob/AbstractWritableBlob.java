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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.util.io.IOUtils;

/**
 * Partial implementation of the {@link WritableBlob} interface that implements {@link
 * WritableBlob#readFrom(InputStream)} using {@link WritableBlob#getOutputStream()} and {@link
 * WritableBlob#writeTo(OutputStream)} using {@link WritableBlob#getInputStream()}.
 */
public abstract class AbstractWritableBlob implements WritableBlob {
    @Override
    public long readFrom(InputStream in) throws StreamCopyException {
        OutputStream out;
        try {
            out = getOutputStream();
        } catch (IOException ex) {
            throw new StreamCopyException(StreamCopyException.WRITE, ex);
        }
        try {
            return IOUtils.copy(in, out, -1);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                throw new StreamCopyException(StreamCopyException.WRITE, ex);
            }
        }
    }

    @Override
    public void writeTo(OutputStream out) throws StreamCopyException {
        InputStream in;
        try {
            in = getInputStream();
        } catch (IOException ex) {
            throw new StreamCopyException(StreamCopyException.READ, ex);
        }
        try {
            IOUtils.copy(in, out, -1);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                throw new StreamCopyException(StreamCopyException.READ, ex);
            }
        }
    }
}
