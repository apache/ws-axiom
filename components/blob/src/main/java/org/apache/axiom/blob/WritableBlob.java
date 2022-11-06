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

import org.apache.axiom.ext.io.ReadFromSupport;
import org.apache.axiom.ext.io.StreamCopyException;

/**
 * A writable blob.
 * <p>
 * The behavior of the methods defined by this interface is described in terms of four logical
 * states the blob can be in:
 * <dl>
 *   <dt>NEW
 *   <dd>The blob has just been created and no data has been written to it yet.
 *   <dt>UNCOMMITTED
 *   <dd>Data is being written to the blob.
 *   <dt>COMMITTED
 *   <dd>All data has been written to the blob and the blob will no longer accept any new data.
 *   <dt>RELEASED
 *   <dd>The blob has been released, i.e. its data has been discarded.
 * </dl>
 * Reading data from a blob in state NEW or UNCOMMITTED is not supported: methods defined by the
 * {@link Blob} interface will throw {@link IllegalStateException} if the blob is not in state
 * COMMITTED.
 * <p>
 * {@link WritableBlob} instances are generally not thread safe. However, for a blob in state
 * COMMITTED, all methods defined by the {@link Blob} interface are thread safe.
 */
public interface WritableBlob extends Blob {
    /**
     * Create an output stream to write data to the blob. The blob must be in state NEW when this
     * method is called. It will be in state UNCOMMITTED after this method completes successfully.
     * Note that this implies that this method may be called at most once for a given blob instance.
     * <p>
     * Calls to methods of the returned output stream will modify the state of the blob
     * according to the following rules:
     * <ul>
     *   <li>A call to {@link OutputStream#close()} will change the state to COMMITTED.
     *   <li>Calls to other methods will not modify the state of the blob. They will result in
     *       an {@link IOException} if the state is COMMITTED, i.e. if the stream has already been
     *       closed.
     * </ul>
     * <p>
     * The returned stream may implement {@link ReadFromSupport}, especially if the blob stores its
     * data in memory (in which case {@link ReadFromSupport#readFrom(InputStream, long)} would read
     * data directly into the buffers managed by the blob).
     * 
     * @return an output stream that can be used to write data to the blob
     * 
     * @throws IllegalStateException if the blob is not in state NEW
     * @throws IOException if an I/O error occurred
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Read data from the given input stream and write it to the blob.
     * <p>
     * A call to this method has the same effect as requesting an output stream using
     * {@link #getOutputStream()} and copying the data from the input stream to that
     * output stream, but the implementation may achieve this result in a more efficient way.
     * <p>
     * The blob must be in state NEW when this method is called. It will be in state COMMITTED
     * after this method completes successfully.
     * <p>
     * The method transfers data from the input stream to the blob until the end of the input
     * stream is reached.
     * 
     * @param in An input stream to read data from. This method will not
     *           close the stream.
     * @return the number of bytes transferred
     * @throws StreamCopyException
     * @throws IllegalStateException if the blob is not in state NEW
     */
    long readFrom(InputStream in) throws StreamCopyException;
    
    /**
     * Release all resources held by this blob. This method will put the blob into the RELEASED
     * state and the content will no longer be accessible.
     *
     * @throws IOException if the cleanup could not be completed because an I/O error occurred
     */
    void release() throws IOException;
}
