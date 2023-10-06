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

import static com.google.common.truth.Truth.assertThat;

import org.apache.axiom.mime.ContentTransferEncoding;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.mime.MediaType;
import org.apache.axiom.util.activation.DataHandlerUtils;
import org.junit.Test;

public class ConfigurableDataHandlerTest {
    @Test
    public void testContentTransferEncoding() {
        ConfigurableDataHandler dh = new ConfigurableDataHandler(new byte[10],
                "application/octet-stream");
        dh.setTransferEncoding("base64");
        assertThat(ConfigurableDataHandler.CONTENT_TRANSFER_ENCODING_POLICY
                .getContentTransferEncoding(DataHandlerUtils.toBlob(dh),
                        new ContentType(MediaType.APPLICATION_OCTET_STREAM)))
                                .isSameInstanceAs(ContentTransferEncoding.BASE64);
    }

    @Test
    public void testContentTransferEncodingNotSet() {
        ConfigurableDataHandler dh = new ConfigurableDataHandler(new byte[10],
                "application/octet-stream");
        assertThat(ConfigurableDataHandler.CONTENT_TRANSFER_ENCODING_POLICY
                .getContentTransferEncoding(DataHandlerUtils.toBlob(dh),
                        new ContentType(MediaType.APPLICATION_OCTET_STREAM))).isNull();
    }

    @Test
    public void testContentTransferEncodingNotRecognized() {
        ConfigurableDataHandler dh = new ConfigurableDataHandler(new byte[10],
                "application/octet-stream");
        dh.setTransferEncoding("foobar");
        assertThat(ConfigurableDataHandler.CONTENT_TRANSFER_ENCODING_POLICY
                .getContentTransferEncoding(DataHandlerUtils.toBlob(dh),
                        new ContentType(MediaType.APPLICATION_OCTET_STREAM))).isNull();
    }
}
