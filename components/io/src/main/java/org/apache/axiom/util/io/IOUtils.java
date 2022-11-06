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
package org.apache.axiom.util.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axiom.ext.io.ReadFromSupport;
import org.apache.axiom.ext.io.StreamCopyException;

public final class IOUtils {
    private IOUtils() {}
    
    /**
     * Copy bytes between streams. This method supports the {@link ReadFromSupport} interface. It
     * will not call {@link Closeable#close()} on either of the two streams.
     * 
     * @param in
     *            the stream to read bytes from
     * @param out
     *            the stream to write bytes to
     * @param length
     *            the maximum number of bytes to copy, or -1 to copy an unlimited number of bytes
     * @return the number of bytes copied
     * @throws StreamCopyException
     *             if a read/write operation on one of the streams triggered an {@link IOException}
     */
    public static long copy(InputStream in, OutputStream out, long length) throws StreamCopyException {
        if (out instanceof ReadFromSupport) {
            return ((ReadFromSupport)out).readFrom(in, length);
        } else {
            byte[] buffer = new byte[4096];
            long read = 0;
            long toRead = length == -1 ? Long.MAX_VALUE : length;
            while (toRead > 0) {
                int c;
                try {
                    c = in.read(buffer, 0, (int)Math.min(toRead, buffer.length));
                } catch (IOException ex) {
                    throw new StreamCopyException(StreamCopyException.READ, ex);
                }
                if (c == -1) {
                    break;
                }
                try {
                    out.write(buffer, 0, c);
                } catch (IOException ex) {
                    throw new StreamCopyException(StreamCopyException.WRITE, ex);
                }
                read += c;
                toRead -= c;
            }
            return read;
        }
    }
}
