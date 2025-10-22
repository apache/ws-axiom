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
package org.apache.axiom.mime;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;

final class DebugInputStream extends InputStream {
    private final InputStream parent;
    private final Log log;
    private long read;
    private int chunks;
    private boolean logged;

    DebugInputStream(InputStream parent, Log log) {
        this.parent = parent;
        this.log = log;
    }

    private void log(IOException ex) {
        if (!logged) {
            log.debug(
                    "IOException occurred after reading "
                            + read
                            + " bytes in "
                            + chunks
                            + " chunks",
                    ex);
            logged = true;
        }
    }

    private void logEOF() {
        if (!logged) {
            log.debug("EOF reached after reading " + read + " bytes in " + chunks + " chunks");
            logged = true;
        }
    }

    @Override
    public int available() throws IOException {
        try {
            return parent.available();
        } catch (IOException ex) {
            log(ex);
            throw ex;
        }
    }

    @Override
    public boolean markSupported() {
        return parent.markSupported();
    }

    @Override
    public void mark(int readlimit) {
        parent.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        try {
            parent.reset();
        } catch (IOException ex) {
            log(ex);
            throw ex;
        }
    }

    @Override
    public int read() throws IOException {
        int result;
        try {
            result = parent.read();
        } catch (IOException ex) {
            log(ex);
            throw ex;
        }
        if (result == -1) {
            logEOF();
        } else {
            read++;
            chunks++;
        }
        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int c;
        try {
            c = parent.read(b, off, len);
        } catch (IOException ex) {
            log(ex);
            throw ex;
        }
        if (c == -1) {
            logEOF();
        } else {
            read += c;
            chunks++;
        }
        return c;
    }

    @Override
    public long skip(long n) throws IOException {
        try {
            return parent.skip(n);
        } catch (IOException ex) {
            log(ex);
            throw ex;
        }
    }

    @Override
    public void close() throws IOException {
        if (!logged) {
            log.debug("Closing stream after reading " + read + " bytes in " + chunks + " chunks");
            logged = true;
        }
        parent.close();
    }
}
