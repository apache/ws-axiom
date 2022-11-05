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

import org.apache.axiom.blob.Blob;
import org.apache.axiom.util.activation.DataHandlerUtils;

/**
 * Factory for the {@link Blob} instances returned by {@link Part#getBlob()}. This may be used to
 * create {@link Blob} instances that wrap some other type of objects representing the content of
 * MIME parts.
 */
public interface BlobFactory {
    /**
     * Default factory that creates {@link PartDataHandler} instances.
     */
    BlobFactory DEFAULT = new BlobFactory() {
        @Override
        public Blob createBlob(Part part, Supplier<Blob> contentSupplier) {
            return DataHandlerUtils.toBlob(new PartDataHandler(part, contentSupplier));
        }
    };

    /**
     * Create a {@link Blob} for the given MIME part.
     * 
     * @param part
     *            the MIME part
     * @param contentSupplier
     *            a supplier for the content of the part
     * @return the blob
     */
    Blob createBlob(Part part, Supplier<Blob> contentSupplier);
}
