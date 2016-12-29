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
package org.apache.axiom.om.impl.common.factory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.common.builder.Detachable;

final class DetachableReader extends Reader implements Detachable {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    
    private Reader target;

    DetachableReader(Reader target) {
        this.target = target;
    }

    @Override
    public int read(CharBuffer target) throws IOException {
        return target.read(target);
    }

    @Override
    public int read() throws IOException {
        return target.read();
    }

    @Override
    public int read(char[] cbuf) throws IOException {
        return target.read(cbuf);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return target.read(cbuf, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return target.skip(n);
    }

    @Override
    public boolean ready() throws IOException {
        return target.ready();
    }

    @Override
    public void close() throws IOException {
        target.close();
    }
    
    @Override
    public void detach() {
        MemoryBlob blob = Blobs.createMemoryBlob();
        Writer out = new OutputStreamWriter(blob.getOutputStream(), UTF8);
        char[] buffer = new char[2048];
        int c;
        try {
            while ((c = target.read(buffer)) != -1) {
                out.write(buffer, 0, c);
            }
            out.close();
        } catch (IOException ex) {
            throw new OMException(ex);
        }
        target = new InputStreamReader(blob.readOnce(), UTF8);
    }
}
