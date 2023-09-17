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
package org.apache.axiom.om.util.jaxb;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.util.activation.DataHandlerUtils;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.attachment.AttachmentUnmarshaller;

final class AttachmentUnmarshallerImpl extends AttachmentUnmarshaller {
    private final OMAttachmentAccessor attachmentAccessor;

    AttachmentUnmarshallerImpl(OMAttachmentAccessor attachmentAccessor) {
        this.attachmentAccessor = attachmentAccessor;
    }

    /**
     * Extract the content ID from a URL following the cid scheme defined by RFC2392.
     *
     * @param url the URL
     * @return the corresponding content ID
     * @throws IllegalArgumentException if the URL doesn't use the cid scheme
     */
    private static String getContentIDFromURL(String url) {
        if (url.startsWith("cid:")) {
            try {
                // URIs should always be decoded using UTF-8 (see AXIOM-129). On the
                // other hand, since non ASCII characters are not allowed in content IDs,
                // we can simply decode using ASCII (which is a subset of UTF-8)
                return URLDecoder.decode(url.substring(4), "ascii");
            } catch (UnsupportedEncodingException ex) {
                // We should never get here
                throw new Error(ex);
            }
        } else {
            throw new IllegalArgumentException("The URL doesn't use the cid scheme");
        }
    }

    @Override
    public byte[] getAttachmentAsByteArray(String cid) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public DataHandler getAttachmentAsDataHandler(String cid) {
        Blob blob = attachmentAccessor.getBlob(getContentIDFromURL(cid));
        if (blob == null) {
            throw new IllegalArgumentException("No MIME part found for content ID '" + cid + "'");
        } else {
            return DataHandlerUtils.toDataHandler(blob);
        }
    }

    @Override
    public boolean isXOPPackage() {
        return true;
    }
}
