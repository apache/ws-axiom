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
package org.apache.axiom.om.ds.jaxb;

import javax.activation.DataHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.util.activation.BlobDataSource;
import org.apache.axiom.util.activation.DataHandlerUtils;

final class AttachmentMarshallerImpl extends AttachmentMarshaller {
    private final MTOMXMLStreamWriter out;

    public AttachmentMarshallerImpl(MTOMXMLStreamWriter out) {
        this.out = out;
    }

    @Override
    public boolean isXOPPackage() {
        return true;
    }

    @Override
    public String addMtomAttachment(
            DataHandler data, String elementNamespace, String elementLocalName) {
        return "cid:" + out.prepareBlob(DataHandlerUtils.toBlob(data));
    }

    @Override
    public String addMtomAttachment(
            byte[] data,
            int offset,
            int length,
            String mimeType,
            String elementNamespace,
            String elementLocalName) {
        // TODO: instead of copying the array, we could use a specialized DataHandler/DataSource
        if (offset != 0 || length != data.length) {
            int len = length - offset;
            byte[] newData = new byte[len];
            System.arraycopy(data, offset, newData, 0, len);
            data = newData;
        }
        return addMtomAttachment(
                new DataHandler(
                        new BlobDataSource(Blobs.createBlob(data), "application/octet-stream")),
                elementNamespace,
                elementLocalName);
    }

    @Override
    public String addSwaRefAttachment(DataHandler data) {
        return null;
    }
}
