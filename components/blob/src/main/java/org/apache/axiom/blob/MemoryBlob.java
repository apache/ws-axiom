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

/**
 * Blob that stores data in memory.
 * <p>
 * This interface redefines several methods from {@link Blob} and {@link WritableBlob} to not throw
 * {@link IOException}. Also note that since data is stored in memory, calling
 * {@link WritableBlob#release()} is not required.
 * <p>
 * Instances are created with {@link Blobs#createMemoryBlob()} or using {@link #FACTORY}.
 */
public interface MemoryBlob extends WritableBlob {
    WritableBlobFactory<MemoryBlob> FACTORY = MemoryBlobImpl::new;

    @Override
    MemoryBlobInputStream getInputStream();

    @Override
    MemoryBlobOutputStream getOutputStream();

    @Override
    long getSize();

    @Override
    void release();

    /**
     * Get an input stream that consumes the content of this blob. The memory held by this blob will
     * be gradually released as data is read from the stream.
     * 
     * @return the input stream to read the data from
     */
    MemoryBlobInputStream readOnce();
}
