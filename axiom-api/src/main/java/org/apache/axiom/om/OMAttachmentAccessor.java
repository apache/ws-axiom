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
package org.apache.axiom.om;

import org.apache.axiom.blob.Blob;

/** Interface to look up MIME parts. */
public interface OMAttachmentAccessor {
    /**
     * Get the content of the MIME part identified by a given content ID.
     *
     * @param contentID the raw content ID (without the surrounding angle brackets and {@code cid:}
     *     prefix) of the MIME part
     * @return the content of the MIME part referred by the content ID or <code>null</code> if the
     *     MIME part referred by the content ID does not exist
     */
    public Blob getBlob(String contentID);
}
