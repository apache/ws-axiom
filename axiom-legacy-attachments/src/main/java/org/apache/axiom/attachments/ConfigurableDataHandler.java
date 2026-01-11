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

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.mime.ContentTransferEncoding;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.om.format.xop.ContentTransferEncodingPolicy;
import org.apache.axiom.util.activation.DataHandlerUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;

/**
 * This Axiom DataHandler inplementation allows the user to set custom values for the following MIME
 * body part headers.
 *
 * <ul>
 *   <li>content-transfer-encoding
 *   <li>content-type
 * </ul>
 *
 * <p>Data written to the MIME part gets encoded by content-transfer-encoding specified as above
 *
 * <p>Usage is Similar to the javax.activation.DataHandler except for the setting of the above
 * properties.
 *
 * <p>eg:
 *
 * <p>dataHandler = new ConfigurableDataHandler(new ByteArrayDataSource(byteArray));
 *
 * <p>dataHandler.setTransferEncoding("quoted-printable");
 *
 * <p>dataHandler.setContentType("image/jpg");
 *
 * @see jakarta.activation.DataHandler
 */
public class ConfigurableDataHandler extends DataHandler {
    private static final Log log = LogFactory.getLog(ConfigurableDataHandler.class);

    /**
     * {@link ContentTransferEncodingPolicy} implementation that recognizes blobs created from
     * {@link ConfigurableDataHandler} objects and returns the content transfer encoding set using
     * {@link ConfigurableDataHandler#setTransferEncoding(String)}.
     */
    public static final ContentTransferEncodingPolicy CONTENT_TRANSFER_ENCODING_POLICY =
            new ContentTransferEncodingPolicy() {
                @Override
                public ContentTransferEncoding getContentTransferEncoding(
                        Blob blob, ContentType contentType) {
                    DataHandler dataHandler = DataHandlerUtils.toDataHandler(blob);
                    if (!(dataHandler instanceof ConfigurableDataHandler)) {
                        return null;
                    }
                    String cte = ((ConfigurableDataHandler) dataHandler).getTransferEncoding();
                    if (cte == null) {
                        return null;
                    }
                    return switch (cte) {
                        case "8bit" -> ContentTransferEncoding.EIGHT_BIT;
                        case "binary" -> ContentTransferEncoding.BINARY;
                        case "base64" -> ContentTransferEncoding.BASE64;
                        default -> {
                            log.warn(
                                    String.format(
                                            "Unrecognized content transfer encoding: %s", cte));
                            yield null;
                        }
                    };
                }
            };

    private String transferEncoding;

    private String contentType;

    public ConfigurableDataHandler(DataSource ds) {
        super(ds);
    }

    public ConfigurableDataHandler(Object data, String type) {
        super(data, type);
    }

    public ConfigurableDataHandler(URL url) {
        super(url);
    }

    //	public String getContentID() {
    //		return contentID;
    //	}
    //
    //	public void setContentID(String contentID) {
    //		this.contentID = contentID;
    //	}

    @Override
    public String getContentType() {
        if (contentType != null) {
            return contentType;
        } else {
            return super.getContentType();
        }
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTransferEncoding() {
        return transferEncoding;
    }

    public void setTransferEncoding(String transferEncoding) {
        this.transferEncoding = transferEncoding;
    }
}
