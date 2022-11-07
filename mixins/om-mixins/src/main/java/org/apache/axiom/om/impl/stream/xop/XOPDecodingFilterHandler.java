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
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.xop.AbstractXOPDecodingFilterHandler;
import org.apache.axiom.ext.stax.BlobProvider;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.intf.TextContent;

final class XOPDecodingFilterHandler extends AbstractXOPDecodingFilterHandler {
    private static class BlobProviderImpl implements BlobProvider {
        private final OMAttachmentAccessor attachmentAccessor;
        private final String contentID;

        public BlobProviderImpl(OMAttachmentAccessor attachmentAccessor, String contentID) {
            this.attachmentAccessor = attachmentAccessor;
            this.contentID = contentID;
        }

        @Override
        public Blob getBlob() throws IOException {
            Blob blob = attachmentAccessor.getBlob(contentID);
            if (blob == null) {
                throw new OMException("No MIME part found for content ID '" + contentID + "'");
            } else {
                return blob;
            }
        }
    }

    private final OMAttachmentAccessor attachmentAccessor;

    XOPDecodingFilterHandler(XmlHandler parent, OMAttachmentAccessor attachmentAccessor) {
        super(parent);
        this.attachmentAccessor = attachmentAccessor;
    }

    @Override
    protected Object buildCharacterData(String contentID) {
        return new TextContent(
                contentID, new BlobProviderImpl(attachmentAccessor, contentID), true);
    }
}
