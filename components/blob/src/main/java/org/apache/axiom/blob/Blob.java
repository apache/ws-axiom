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

/**
 * Stores binary data.
 *
 * <p>Blobs are thread safe in the sense that methods defined by this interface may be called
 * concurrently. In addition, two different threads can safely invoke methods on two different
 * {@link InputStream} instances retrieved by {@link #getInputStream()} concurrently. However some
 * blobs (in particular {@link WritableBlob} implementations) may define additional methods and
 * invoking these methods concurrently with methods defined by this interface is generally not
 * thread safe.
 */
public interface Blob {
    /**
     * Get an input stream to read the data in the blob. A new {@link InputStream} object is
     * returned each time this method is called, and the stream is positioned at the beginning of
     * the data.
     *
     * @return the input stream to read the data from
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Write the data to a given output stream. This method can be called multiple times, i.e. it
     * doesn't consume the content.
     *
     * @param out The output stream to write the data to. This method will not close the stream.
     * @throws StreamCopyException Thrown if there is an I/O when reading the data from the blob or
     *     when writing it to the stream. {@link StreamCopyException#getOperation()} can be used to
     *     determine whether the failed operation was a read or a write.
     */
    void writeTo(OutputStream out) throws StreamCopyException;

    /**
     * Get the (approximate) size of the blob. Returns -1 if the size can't be determined without
     * reading the entire blob (in which case the caller may want to use {@link
     * #writeTo(OutputStream)} with an {@link OutputStream} that counts the number of bytes to
     * determine the size). The method may choose to return a value based on an estimation. This may
     * be the case e.g. if reading the data involves a decoding operation, and the length of the
     * resulting stream can't be determined precisely without performing the decoding operation.
     *
     * <p>When reading the actual data, the code should always read until the end of the stream is
     * reached (as indicated by the return value of the {@code read} methods of the {@link
     * InputStream} class). It must be prepared to reach the end of the stream after a number of
     * bytes that is lower or higher than the value returned by this method.
     *
     * @return the number of bytes in the blob, or -1 if the size is not known
     */
    long getSize();
}
