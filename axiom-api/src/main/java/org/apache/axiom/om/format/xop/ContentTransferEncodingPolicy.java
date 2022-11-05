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
package org.apache.axiom.om.format.xop;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.mime.ContentTransferEncoding;
import org.apache.axiom.mime.ContentType;

/**
 * Determines the content transfer encoding to use for (non-root) MIME parts in an XOP package.
 * Note that in general, XOP encoded messages are sent over transport protocols that can handle
 * arbitrary sequences of bytes (such as HTTP), and the default {@code binary} or {@code 8bit}
 * encoding will work just fine. Therefore changing the content transfer encoding is only needed
 * in very specific use cases where the transport may not be able to handle arbitrary sequences
 * of bytes (such as SMTP).
 */
public interface ContentTransferEncodingPolicy {
    /**
     * Selects the {@code base64} content transfer encoding for parts that are not textual
     * (as determined by {@link ContentType#isTextual()}.
     */
    ContentTransferEncodingPolicy USE_BASE64_FOR_NON_TEXTUAL_PARTS = new ContentTransferEncodingPolicy() {
        @Override
        public ContentTransferEncoding getContentTransferEncoding(Blob blob, ContentType contentType) {
            if (contentType == null) {
                return null;
            }
            if (!contentType.isTextual()) {
                return ContentTransferEncoding.BASE64;
            }
            return null;
        }
    };

    /**
     * Determine the content transfer encoding to use for a MIME part.
     * 
     * @param blob the content of the MIME part; may be {@code null}
     * @param contentType the content type of the MIME part (as determined by {@link ContentTypeProvider}; may be {@code null}
     * @return the content transfer encoding, or {@code null} if no content transfer encoding is specified (in which case another {@link ContentTransferEncodingPolicy} may be consulted or a default is used)
     */
    ContentTransferEncoding getContentTransferEncoding(Blob blob, ContentType contentType);
}
