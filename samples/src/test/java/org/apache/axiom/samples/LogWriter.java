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
package org.apache.axiom.samples;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.ext.stax.BlobProvider;
import org.apache.axiom.ext.stax.BlobWriter;
import org.apache.axiom.util.stax.wrapper.XMLStreamWriterWrapper;

// START SNIPPET: main
public class LogWriter extends XMLStreamWriterWrapper implements BlobWriter {
    public LogWriter(XMLStreamWriter parent) {
        super(parent);
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        if (name.equals(BlobWriter.PROPERTY)) {
            return this;
        } else {
            return super.getProperty(name);
        }
    }

    @Override
    public void writeBlob(Blob blob, String contentID, boolean optimize)
            throws IOException, XMLStreamException {
        super.writeCharacters("[base64 encoded data]");
    }

    @Override
    public void writeBlob(BlobProvider blobProvider, String contentID, boolean optimize)
            throws IOException, XMLStreamException {
        super.writeCharacters("[base64 encoded data]");
    }
}
// END SNIPPET: main
