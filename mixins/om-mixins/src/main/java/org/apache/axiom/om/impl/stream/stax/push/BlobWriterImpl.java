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
package org.apache.axiom.om.impl.stream.stax.push;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.core.stream.stax.push.input.InternalXMLStreamWriter;
import org.apache.axiom.ext.stax.BlobProvider;
import org.apache.axiom.ext.stax.BlobWriter;
import org.apache.axiom.om.impl.intf.TextContent;

public final class BlobWriterImpl implements BlobWriter {
    private final InternalXMLStreamWriter writer;

    public BlobWriterImpl(InternalXMLStreamWriter writer) {
        this.writer = writer;
    }

    @Override
    public void writeBlob(Blob blob, String contentID, boolean optimize)
            throws IOException, XMLStreamException {
        writer.writeCharacterData(new TextContent(contentID, blob, optimize));
    }

    @Override
    public void writeBlob(BlobProvider blobProvider, String contentID, boolean optimize)
            throws IOException, XMLStreamException {
        writer.writeCharacterData(new TextContent(contentID, blobProvider, optimize));
    }
}
