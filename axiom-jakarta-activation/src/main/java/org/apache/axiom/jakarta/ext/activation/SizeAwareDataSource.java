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

package org.apache.axiom.jakarta.ext.activation;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.jakarta.util.activation.DataHandlerUtils;

/**
 * Optional extension interface that can be implemented by data sources that support a {@code
 * getSize} method. This is used by {@link DataHandlerUtils#toBlob(DataHandler)} to implement the
 * {@link Blob#getSize()} method. Note that the latter is able to determine the size of some types
 * of {@link DataSource} instances that don't implement this extension interface.
 *
 * <p>Some implementations of this interface may be unable to guarantee 100% accuracy when
 * determining the size of the data source. Situations where this can occur include:
 *
 * <ul>
 *   <li>The data source uses a network protocol that allows to get the size of the data but that
 *       doesn't guarantee accurateness.
 *   <li>Reading the data involves a decoding operation and the length of the resulting stream can't
 *       be determined precisely without performing the decoding operation. In this case the
 *       implementation of this interface may return a value based on an estimation.
 * </ul>
 *
 * When reading the actual data, the code should always read until the end of the stream is reached
 * (as indicated by the return value of the <code>read</code> methods of the {@link
 * java.io.InputStream} class). It must be prepared to reach the end of the stream after a number of
 * bytes that is lower or higher than the value returned by {@link #getSize()}.
 */
public interface SizeAwareDataSource extends DataSource {
    /**
     * Get the size of the data source. Implementations must return the number of bytes that can be
     * read from the input stream returned by {@link #getInputStream()} before reaching the end of
     * the stream. If the implementation is unable to determine the size, it must return -1.
     *
     * @return the size of the data source or -1 if the size is not known
     */
    long getSize();
}
