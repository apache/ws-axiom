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
package org.apache.axiom.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Supplier;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.axiom.blob.Blob;

/**
 * {@link DataHandler} implementation for MIME parts read from a stream.
 */
public class PartDataHandler extends DataHandler {
    private final Part part;
    private final Supplier<Blob> contentSupplier;
    private DataSource dataSource;

    protected PartDataHandler(Part part, Supplier<Blob> contentSupplier) {
        // We can't call PartImpl#getDataSource() here because it would fetch the content of the
        // part and therefore disable streaming. We can't pass null here either because Geronimo's
        // DataHandler implementation would throw a NullPointerException. Therefore we create the
        // default PartDataSource. When the DataSource is requested, we check if for there is an
        // implementation specific to the buffering strategy and return that instead of the default
        // implementation.
        super(new PartDataSource(part, contentSupplier));
        this.part = part;
        this.contentSupplier = contentSupplier;
    }

    /**
     * Get the MIME part linked to this data handler.
     * 
     * @return the MIME part
     */
    public final Part getPart() {
        return part;
    }

    @Override
    public final DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = createDataSource(contentSupplier.get(), Util.getDataSourceContentType(part));
            if (dataSource == null) {
                // We get here if there is no DataSource implementation specific to the buffering
                // strategy being used. In this case we use super.getDataSource() to get the
                // default PartDataSource that we created in the constructor.
                dataSource = super.getDataSource();
            }
        }
        return dataSource;
    }

    /**
     * Create the {@link DataSource} to be returned by {@link #getDataSource()}. This method may be
     * overridden by subclasses to support custom {@link DataSource} implementations.
     * 
     * @param content
     *            the content of the part
     * @param contentType
     *            the content type expected to be returned by {@link DataSource#getContentType()};
     *            defaults to {@code application/octet-stream} if the part doesn't specify a content
     *            type
     * @return the {@link DataSource} instance, or {@code null} to use the default implementation
     */
    protected DataSource createDataSource(Blob content, String contentType) {
        return null;
    }

    @Override
    public final void writeTo(OutputStream os) throws IOException {
        // The PartContent may have an implementation of writeTo that is more efficient than the default
        // DataHandler#writeTo method (which requests an input stream and then copies it to the output
        // stream).
        contentSupplier.get().writeTo(os);
    }
}
