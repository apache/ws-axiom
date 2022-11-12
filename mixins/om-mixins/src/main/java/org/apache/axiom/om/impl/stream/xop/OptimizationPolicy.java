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

package org.apache.axiom.om.impl.stream.xop;

import java.io.IOException;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.ext.stax.BlobProvider;

/**
 * Encapsulates an algorithm that decides whether base64 encoded binary data should be optimized
 * using XOP. The implementation takes the decision based on the submitted binary content and the
 * "eligible for optimization" flag. Depending on the context of use, this flag is provided by the
 * return value of {@link org.apache.axiom.ext.stax.BlobReader#isOptimized()} or the <code>optimize
 * </code> argument of {@link org.apache.axiom.ext.stax.BlobWriter#writeBlob(Blob, String, boolean)}
 * or {@link org.apache.axiom.ext.stax.BlobWriter#writeBlob(BlobProvider, String, boolean)}.
 */
public interface OptimizationPolicy {
    /**
     * Policy implementation that optimizes all binary content marked as eligible for optimization.
     */
    OptimizationPolicy DEFAULT =
            new OptimizationPolicy() {
                @Override
                public boolean isOptimized(Blob blob, boolean optimize) {
                    return optimize;
                }

                @Override
                public boolean isOptimized(BlobProvider blobProvider, boolean optimize) {
                    return optimize;
                }
            };

    /**
     * Policy implementation that optimizes all binary content, regardless of whether is has been
     * marked as eligible for optimization.
     */
    OptimizationPolicy ALL =
            new OptimizationPolicy() {
                @Override
                public boolean isOptimized(Blob blob, boolean optimize) {
                    return true;
                }

                @Override
                public boolean isOptimized(BlobProvider blobProvider, boolean optimize) {
                    return true;
                }
            };

    /**
     * Determine whether the binary content supplied by a given {@link Blob} should be optimized.
     *
     * @param blob the binary content
     * @param optimize indicates whether the binary content was initially marked as eligible for
     *     optimization (see above)
     * @return <code>true</code> if the binary content should be optimized using XOP, i.e. encoded
     *     using {@code xop:Include}
     * @throws IOException if an error occurs while reading the blob
     */
    boolean isOptimized(Blob blob, boolean optimize) throws IOException;

    /**
     * Determine whether the binary content supplied by a given {@link BlobProvider} should be
     * optimized.
     *
     * @param blobProvider the binary content
     * @param optimize indicates whether the binary content was initially marked as eligible for
     *     optimization (see above)
     * @return <code>true</code> if the binary content should be optimized using XOP, i.e. encoded
     *     using {@code xop:Include}
     * @throws IOException if an error occurs while reading the blob
     */
    boolean isOptimized(BlobProvider blobProvider, boolean optimize) throws IOException;
}
