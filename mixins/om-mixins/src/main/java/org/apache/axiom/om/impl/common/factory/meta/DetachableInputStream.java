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
package org.apache.axiom.om.impl.common.factory.meta;

import java.io.IOException;
import java.io.InputStream;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.common.builder.Detachable;

final class DetachableInputStream extends InputStream implements Detachable {
    private InputStream target;
    private final boolean closeOnDetach;

    DetachableInputStream(InputStream target, boolean closeOnDetach) {
        this.target = target;
        this.closeOnDetach = closeOnDetach;
    }

    @Override
    public int read() throws IOException {
        return target.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return target.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return target.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return target.skip(n);
    }

    @Override
    public int available() throws IOException {
        return target.available();
    }

    @Override
    public void close() throws IOException {
        target.close();
    }

    @Override
    public void detach() throws OMException {
        MemoryBlob blob = Blobs.createMemoryBlob();
        try {
            blob.readFrom(target);
        } catch (StreamCopyException ex) {
            throw new OMException(ex.getCause());
        }
        if (closeOnDetach) {
            try {
                target.close();
            } catch (IOException ex) {
                throw new OMException(ex);
            }
        }
        target = blob.readOnce();
    }
}
