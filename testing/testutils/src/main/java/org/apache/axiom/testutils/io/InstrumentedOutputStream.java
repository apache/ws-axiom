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
package org.apache.axiom.testutils.io;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.ProxyOutputStream;

/** {@link OutputStream} wrapper that implements {@link InstrumentedStream}. */
public final class InstrumentedOutputStream extends ProxyOutputStream
        implements InstrumentedStream {
    private long count;
    private boolean closed;

    public InstrumentedOutputStream(OutputStream proxy) {
        super(proxy);
    }

    @Override
    protected void beforeWrite(int n) {
        count += n;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        super.close();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
