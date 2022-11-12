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

import java.io.File;

/** Contains factory methods to create various types of blobs. */
public final class Blobs {
    private Blobs() {}

    /**
     * Create a blob from a byte array.
     *
     * @param data the byte array
     * @return the blob
     */
    public static Blob createBlob(byte[] data) {
        return new ByteArrayBlob(data);
    }

    /**
     * Create a {@link MemoryBlob} instance.
     *
     * @return the blob
     */
    public static MemoryBlob createMemoryBlob() {
        return new MemoryBlobImpl();
    }

    /**
     * Create an {@link OverflowableBlob}.
     *
     * @param threshold the threshold above which data is transferred to the overflow blob
     * @param overflowBlobFactory the factory that will be used to create the overflow blob
     * @return the blob
     */
    public static OverflowableBlob createOverflowableBlob(
            int threshold, WritableBlobFactory<?> overflowBlobFactory) {
        int numberOfChunks = Math.max(16, Math.min(1, threshold / 4096));
        int chunkSize = threshold / numberOfChunks;
        return new OverflowableBlobImpl(numberOfChunks, chunkSize, overflowBlobFactory);
    }

    /**
     * Create an {@link OverflowableBlob} that overflows to a temporary file. Temporary files are
     * created using {@link File#createTempFile(String, String, File)}.
     *
     * @param threshold the overflow threshold
     * @param tempPrefix the prefix to be used in generating the name of the temporary file
     * @param tempSuffix the suffix to be used in generating the name of the temporary file
     * @param tempDirectory the directory in which the temporary file is to be created, or <code>
     *     null</code> if the default temporary directory is to be used
     * @return the blob
     */
    public static OverflowableBlob createOverflowableBlob(
            int threshold, String tempPrefix, String tempSuffix, File tempDirectory) {
        return createOverflowableBlob(
                threshold, new TempFileBlobFactory(tempPrefix, tempSuffix, tempDirectory));
    }
}
