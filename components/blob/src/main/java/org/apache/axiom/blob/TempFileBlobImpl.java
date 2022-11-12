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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class TempFileBlobImpl extends AbstractWritableBlob {
    private static final Log log = LogFactory.getLog(TempFileBlobImpl.class);

    private final TempFileBlobFactory factory;
    private final Throwable trace;
    private File file;
    private State state = State.NEW;

    TempFileBlobImpl(TempFileBlobFactory factory) {
        this.factory = factory;
        trace = log.isDebugEnabled() ? new Throwable() : null;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (state != State.NEW) {
            throw new IllegalStateException();
        }
        file = factory.createTempFile();
        if (log.isDebugEnabled()) {
            log.debug("Using temporary file " + file);
        }
        file.deleteOnExit();
        OutputStream out =
                new FileOutputStream(file) {
                    @Override
                    public void close() throws IOException {
                        super.close();
                        state = State.COMMITTED;
                    }
                };
        state = State.UNCOMMITTED;
        return out;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (state != State.COMMITTED) {
            throw new IllegalStateException();
        }
        // We know that we are accessing a regular file and since we don't leak the
        // location of the file, nobody else should be modifying it. Therefore we can
        // return an input stream that supports mark/reset.
        return new TempFileInputStream(file);
    }

    @Override
    public long getSize() {
        if (state != State.COMMITTED) {
            throw new IllegalStateException();
        }
        return file.length();
    }

    @Override
    public void release() throws IOException {
        if (file != null) {
            if (log.isDebugEnabled()) {
                log.debug("Deleting temporary file " + file);
            }
            if (!file.delete()) {
                throw new IOException("Failed to delete " + file);
            }
            file = null;
            state = State.RELEASED;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (file != null) {
            log.warn("Cleaning up unreleased temporary file " + file);
            if (log.isDebugEnabled()) {
                log.debug("Blob was created here", trace);
            }
            file.delete();
        }
    }
}
