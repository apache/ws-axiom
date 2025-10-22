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
package org.apache.axiom.om.ds.custombuilder;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.blob.WritableBlob;
import org.apache.axiom.blob.WritableBlobFactory;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.ds.BlobOMDataSource;

/** {@link CustomBuilder} implementation that creates a {@link BlobOMDataSource}. */
public final class BlobOMDataSourceCustomBuilder implements CustomBuilder {
    private final WritableBlobFactory<?> blobFactory;
    private final String encoding;

    /**
     * Constructor.
     *
     * @param blobFactory determines the type of blobs to create
     * @param encoding the encoding used to store data in the blobs
     */
    public BlobOMDataSourceCustomBuilder(WritableBlobFactory<?> blobFactory, String encoding) {
        this.blobFactory = blobFactory;
        this.encoding = encoding;
    }

    @Override
    public OMDataSource create(OMElement element) throws OMException {
        try {
            WritableBlob blob = blobFactory.createBlob();
            OutputStream out = blob.getOutputStream();
            try {
                element.serializeAndConsume(out);
            } finally {
                out.close();
            }
            return new BlobOMDataSource(blob, encoding);
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        } catch (IOException ex) {
            throw new OMException(ex);
        }
    }
}
