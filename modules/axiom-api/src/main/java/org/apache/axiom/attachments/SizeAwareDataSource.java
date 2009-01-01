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

import javax.activation.DataSource;

/**
 * Optional extension interface that can be implemented by data sources that support a
 * getSize method.
 * Code working with data sources can use this interface to optimize certain operations.
 * An example is
 * {@link org.apache.axiom.attachments.impl.BufferUtils#doesDataHandlerExceedLimit(javax.activation.DataHandler, int)}.
 */
public interface SizeAwareDataSource extends DataSource {
    /**
     * Get the size of the data source.
     * Implementations must return the number of bytes that can be read from
     * the input stream returned by {@link #getInputStream()} before reaching
     * the end of the stream.
     * 
     * @return the size of the data source
     */
    long getSize();
}
