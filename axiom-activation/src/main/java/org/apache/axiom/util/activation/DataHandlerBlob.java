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
package org.apache.axiom.util.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.ext.io.StreamCopyException;

final class DataHandlerBlob implements Blob {
    private final DataHandler dataHandler;
    
    DataHandlerBlob(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    DataHandler getDataHandler() {
        return dataHandler;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return dataHandler.getInputStream();
    }

    @Override
    public void writeTo(OutputStream out) throws StreamCopyException {
        try {
            dataHandler.writeTo(out);
        } catch (IOException ex) {
            // TODO(AXIOM-506): maybe we can do some wrapping to determine the operation that failed
            throw new StreamCopyException(StreamCopyException.WRITE, ex);
        }
    }

    @Override
    public long getSize() {
        return DataSourceUtils.getSize(dataHandler.getDataSource());
    }
}
