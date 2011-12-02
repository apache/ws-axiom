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
package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * Stores the content of a MIME part using a particular buffering strategy.
 */
abstract class PartContent {
    /**
     * Get an {@link InputStream} representing the buffered MIME part content. Note that a new
     * {@link InputStream} object must be returned each time this method is called, and the stream
     * must be positioned at the beginning of the data.
     * 
     * @return the stream representing the content of this MIME part
     * @throws IOException
     *             if an error occurs while accessing the buffered content
     */
    abstract InputStream getInputStream() throws IOException;

    /**
     * Get a {@link DataSource} implementation specific for this buffering strategy, if supported.
     * 
     * @param contentType
     *            the content type for the {@link DataSource}, which must be returned by
     *            {@link DataSource#getContentType()}
     * @return the {@link DataSource} implementation or <code>null</code> if a default
     *         {@link DataSource} implementation should be used
     */
    abstract DataSource getDataSource(String contentType);
    
    /**
     * Write the buffered MIME part content to the given output stream. Note that the implementation
     * must not consume the content, i.e. the content must still be available after this method
     * completes.
     * 
     * @param out
     *            the output stream to write the content to
     * @throws IOException
     *             if an I/O error occurs (either while reading the buffered content or while
     *             writing to the output stream)
     */
    abstract void writeTo(OutputStream out) throws IOException;

    // TODO: currently not used; all our DataSources should implement SizeAwareDataSource!
    abstract long getSize();

    abstract void destroy() throws IOException;
}
