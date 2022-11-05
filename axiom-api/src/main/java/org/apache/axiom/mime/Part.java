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

import java.io.InputStream;
import java.util.List;

import org.apache.axiom.blob.Blob;

/**
 * A MIME part.
 */
public interface Part {
    /**
     * Get the headers of this part.
     * 
     * @return the headers
     */
    List<Header> getHeaders();

    /**
     * Get the value of a specific header. If there are multiple headers with the same name, only
     * the first value is returned.
     * 
     * @param name
     *            the header name
     * @return the value of the header, or {@code null} if the part doesn't have a header with the
     *         given name
     */
    String getHeader(String name);

    /**
     * Get the content type of this part.
     * 
     * @return the parsed value of the {@code Content-Type} header
     */
    ContentType getContentType();

    /**
     * Get the content ID of this part, i.e. the value of the {@code Content-ID} header with the
     * enclosing brackets removed.
     * 
     * @return the content ID of the part or {@code null} if the part doesn't have a content ID
     */
    String getContentID();

    /**
     * Get the content of this part as a {@link Blob}.
     * 
     * @return the content of this part
     */
    Blob getBlob();

    /**
     * Get the content of this part as an {@link InputStream}.
     * 
     * @param preserve
     *            {@code true} if the content should be preserved so that it can be read multiple
     *            times, {@code false} to discard the content when it is read
     * @return the content of the part
     */
    InputStream getInputStream(boolean preserve);

    /**
     * Make sure that this part has been fully read from the underlying stream.
     */
    void fetch();

    /**
     * Discard the content of this part.
     */
    void discard();
}
