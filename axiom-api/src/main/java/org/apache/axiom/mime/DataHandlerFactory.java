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

import java.util.function.Supplier;

import javax.activation.DataHandler;

import org.apache.axiom.blob.Blob;

/**
 * Factory for the {@link DataHandler} instances returned by {@link Part#getBlob()}.
 */
public interface DataHandlerFactory {
    /**
     * Default factory that creates {@link PartDataHandler} instances.
     */
    DataHandlerFactory DEFAULT = new DataHandlerFactory() {
        @Override
        public DataHandler createDataHandler(Part part, Supplier<Blob> contentSupplier) {
            return new PartDataHandler(part, contentSupplier);
        }
    };

    /**
     * Create a data handler for the given MIME part.
     * 
     * @param part
     *            the MIME part
     * @param contentSupplier
     *            a supplier for the content of the part
     * @return the data handler
     */
    DataHandler createDataHandler(Part part, Supplier<Blob> contentSupplier);
}
