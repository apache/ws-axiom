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

/**
 * Blob that accumulates data in memory and transfers it to a different type of blob if the size
 * exceeds a certain threshold. Typically the overflow blob (i.e. the blob that is created if the
 * memory buffer overflows) stores data in a temporary file. It is therefore mandatory to call
 * {@link WritableBlob#release()} to discard the blob.
 * <p>
 * Instances are created with {@link Blobs#createOverflowableBlob(int, WritableBlobFactory)} or any
 * of the other methods returning {@link OverflowableBlob}.
 */
public interface OverflowableBlob extends WritableBlob {
    /**
     * Get a reference to the overflow blob. The overflow blob is created only if the size of the
     * blob exceeds the threshold. The method returns <code>null</code> if that's not the case.
     * 
     * @return the overflow blob, or <code>null</code> if this blob hasn't overflown
     */
    WritableBlob getOverflowBlob();
}
