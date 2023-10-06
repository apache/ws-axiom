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

package org.apache.axiom.util.blob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axiom.ext.io.StreamCopyException;

/**
 * Stores binary data.
 * <p>
 * Not that blobs are not thread safe. While they support requesting multiple concurrent input
 * streams, these streams must be used in the same thread, unless appropriate synchronization or
 * locking is done.
 * 
 * @deprecated Use {@link org.apache.axiom.blob.Blob} instead.
 */
public interface Blob {
    /**
     * Get an input stream to read the data in the blob.
     * 
     * @return the input stream to read the data from
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Write the data to a given output stream.
     * 
     * @param out
     *            The output stream to write the data to. This method will not close the stream.
     * @throws StreamCopyException
     *             Thrown if there is an I/O when reading the data from the blob or when writing it
     *             to the stream. {@link StreamCopyException#getOperation()} can be used to
     *             determine whether the failed operation was a read or a write.
     */
    void writeTo(OutputStream out) throws StreamCopyException;

    /**
     * Get the length of the data in the blob, i.e. the number of bytes.
     * 
     * @return the length of the data in the blob
     */
    long getLength();
}