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
package org.apache.axiom.attachments;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.axiom.attachments.lifecycle.DataHandlerExt;

/**
 * {@link DataHandler} implementation for MIME parts read from a stream.
 */
class PartDataHandler extends DataHandler implements DataHandlerExt {
    private final PartImpl part;
    private DataSource dataSource;

    public PartDataHandler(PartImpl part) {
        // We can't call PartImpl#getDataSource() here because it would fetch the content of the
        // part and therefore disable streaming. We can't pass null here either because Geronimo's
        // DataHandler implementation would throw a NullPointerException. Therefore we create the
        // default PartDataSource. When the DataSource is requested, we check if for there is an
        // implementation specific to the buffering strategy and return that instead of the default
        // implementation.
        super(new PartDataSource(part));
        this.part = part;
    }

    public DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = part.getDataSource();
            if (dataSource == null) {
                // We get here if there is no DataSource implementation specific to the buffering
                // strategy being used. In this case we use super.getDataSource() to get the
                // default PartDataSource that we created in the constructor.
                dataSource = super.getDataSource();
            }
        }
        return dataSource;
    }

    public void writeTo(OutputStream os) throws IOException {
        // The PartContent may have an implementation of writeTo that is more efficient than the default
        // DataHandler#writeTo method (which requests an input stream and then copies it to the output
        // stream).
        part.writeTo(os);
    }

    public InputStream readOnce() throws IOException {
        return part.getInputStream(false);
    }

    public void purgeDataSource() throws IOException {
        part.releaseContent();
    }

    public void deleteWhenReadOnce() throws IOException {
        // As shown in AXIOM-381, in all released versions of Axiom, deleteWhenReadOnce
        // always has the same effect as purgeDataSource
        purgeDataSource();
    }
}
