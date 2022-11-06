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
package org.apache.axiom.mime.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.mime.Part;
import org.apache.axiom.mime.PartBlob;

/**
 * {@link PartBlob} implementation that wraps a {@link PartDataHandler}.
 */
public final class PartDataHandlerBlob implements PartBlob {
    private final PartDataHandler dataHandler;

    PartDataHandlerBlob(PartDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public PartDataHandler getDataHandler() {
        return dataHandler;
    }

    @Override
    public Part getPart() {
        return dataHandler.getPart();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return dataHandler.getPart().getBlob().getInputStream();
    }

    @Override
    public void writeTo(OutputStream out) throws StreamCopyException {
        dataHandler.getPart().getBlob().writeTo(out);
    }

    @Override
    public long getSize() {
        return dataHandler.getPart().getBlob().getSize();
    }
}
