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
package org.apache.axiom.om.ds;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.blob.Blobs;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.util.StAXUtils;

/** {@link OMDataSource} implementation backed by a {@link Blob}. */
public final class BlobOMDataSource extends AbstractPullOMDataSource {
    public static final class Data {
        private final Blob blob;
        private final String encoding;

        Data(Blob blob, String encoding) {
            this.blob = blob;
            this.encoding = encoding;
        }

        public Blob getBlob() {
            return blob;
        }

        public String getEncoding() {
            return encoding;
        }
    }

    private Data data;

    /**
     * Constructor.
     *
     * @param blob the blob to read from
     * @param encoding the encoding of the data in the blob
     * @see Blobs
     */
    public BlobOMDataSource(Blob blob, String encoding) {
        data = new Data(blob, encoding);
    }

    @Override
    public Data getObject() {
        return data;
    }

    @Override
    public BlobOMDataSource copy() {
        return new BlobOMDataSource(data.getBlob(), data.getEncoding());
    }

    @Override
    public boolean isDestructiveRead() {
        return false;
    }

    @Override
    public XMLStreamReader getReader() throws XMLStreamException {
        try {
            return StAXUtils.createXMLStreamReader(
                    data.getBlob().getInputStream(), data.getEncoding());
        } catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    @Override
    public void close() {
        data = null;
    }
}
