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
package org.apache.axiom.jakarta.util.activation;

import java.text.ParseException;

import jakarta.activation.DataHandler;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.om.format.xop.ContentTypeProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link ContentTypeProvider} implementation that recognizes blobs created from {@link DataHandler}
 * objects and returns the content type specified by {@link DataHandler#getContentType()}.
 */
public final class DataHandlerContentTypeProvider implements ContentTypeProvider {
    private static final Log log = LogFactory.getLog(DataHandlerContentTypeProvider.class);

    public static final DataHandlerContentTypeProvider INSTANCE =
            new DataHandlerContentTypeProvider();

    private DataHandlerContentTypeProvider() {}

    @Override
    public ContentType getContentType(Blob blob) {
        DataHandler dh = DataHandlerUtils.getDataHandler(blob);
        if (dh == null) {
            return null;
        }
        String contentType = dh.getContentType();
        if (contentType == null) {
            return null;
        }
        try {
            return new ContentType(contentType);
        } catch (ParseException ex) {
            log.warn("Couldn't parse content type returned by DataHandler", ex);
            return null;
        }
    }
}
