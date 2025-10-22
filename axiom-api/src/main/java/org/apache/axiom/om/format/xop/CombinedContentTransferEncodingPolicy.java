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
 * {@link ContentTransferEncodingPolicy} implementation that combines multiple other {@link
 * ContentTransferEncodingPolicy} instances into a single policy. It returns the first non-null
 * {@link ContentTransferEncoding}.
 */
public final class CombinedContentTransferEncodingPolicy implements ContentTransferEncodingPolicy {
    private final ContentTransferEncodingPolicy[] policies;

    public CombinedContentTransferEncodingPolicy(ContentTransferEncodingPolicy... policies) {
        this.policies = policies.clone();
    }

    @Override
    public ContentTransferEncoding getContentTransferEncoding(Blob blob, ContentType contentType) {
        if (blob == null) {
            return null;
        }
        for (ContentTransferEncodingPolicy policy : policies) {
            ContentTransferEncoding cte = policy.getContentTransferEncoding(blob, contentType);
            if (cte != null) {
                return cte;
            }
        }
        return null;
    }
}
