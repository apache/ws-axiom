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
package org.apache.axiom.mime.impl.axiom;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.axiom.mime.Header;
import org.apache.axiom.mime.MultipartBodyWriter;
import org.apache.axiom.mime.MultipartWriter;

/**
 * @deprecated
 */
class MultipartWriterImpl implements MultipartWriter {
    private final MultipartBodyWriter writer;

    public MultipartWriterImpl(OutputStream out, String boundary) {
        writer = new MultipartBodyWriter(out, boundary);
    }

    @Override
    public OutputStream writePart(String contentType, String contentTransferEncoding,
            String contentID, List<Header> extraHeaders) throws IOException {
        return writer.writePart(contentType, contentTransferEncoding, contentID, extraHeaders);
    }
    
    @Override
    public OutputStream writePart(String contentType, String contentTransferEncoding,
            String contentID) throws IOException {    	
        return writer.writePart(contentType, contentTransferEncoding, contentID, null);
    }
    
    @Override
    public void writePart(DataHandler dataHandler, String contentTransferEncoding, String contentID, List<Header> extraHeaders)
            throws IOException {
        writer.writePart(dataHandler, contentTransferEncoding, contentID, extraHeaders);
    }
    
    @Override
    public void writePart(DataHandler dataHandler, String contentTransferEncoding,
            String contentID) throws IOException {
        writer.writePart(dataHandler, contentTransferEncoding, contentID, null);
    }

    @Override
    public void complete() throws IOException {
        writer.complete();
    }
}
