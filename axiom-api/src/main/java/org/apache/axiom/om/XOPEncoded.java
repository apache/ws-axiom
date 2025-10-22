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

/**
 * Represent XOP encoded data. Since an XOP message is a MIME package with a root part in XML and a
 * set of additional (binary) parts referenced from the main part, this class encapsulates an API
 * dependent object representing the main part and an {@link OMAttachmentAccessor} giving access to
 * the attachments. Instances of this class can be obtained from {@link
 * OMContainer#getXOPEncodedStreamReader(boolean)}.
 */
public final class XOPEncoded<T> {
    private final T rootPart;
    private final OMAttachmentAccessor attachmentAccessor;

    public XOPEncoded(T rootPart, OMAttachmentAccessor attachmentAccessor) {
        this.rootPart = rootPart;
        this.attachmentAccessor = attachmentAccessor;
    }

    /**
     * Get the root part of the XOP message.
     *
     * @return the root part
     */
    public T getRootPart() {
        return rootPart;
    }

    /**
     * Get the accessor for the additional MIME parts referenced by the root part.
     *
     * @return the attachment accessor
     */
    public OMAttachmentAccessor getAttachmentAccessor() {
        return attachmentAccessor;
    }
}
