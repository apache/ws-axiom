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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.mime.MediaType;
import org.junit.jupiter.api.Test;

public class DataHandlerContentTypeProviderTest {
    @Test
    public void testNotDataHandler() {
        assertThat(
                        DataHandlerContentTypeProvider.INSTANCE.getContentType(
                                Blobs.createBlob(new byte[10])))
                .isNull();
    }

    @Test
    public void testDataHandlerWithoutContentType() {
        DataHandler dh =
                new DataHandler(
                        new DataSource() {
                            @Override
                            public InputStream getInputStream() throws IOException {
                                throw new UnsupportedOperationException();
                            }

                            @Override
                            public OutputStream getOutputStream() throws IOException {
                                throw new UnsupportedOperationException();
                            }

                            @Override
                            public String getContentType() {
                                return null;
                            }

                            @Override
                            public String getName() {
                                throw new UnsupportedOperationException();
                            }
                        });
        assertThat(
                        DataHandlerContentTypeProvider.INSTANCE.getContentType(
                                DataHandlerUtils.toBlob(dh)))
                .isNull();
    }

    @Test
    public void testDataHandlerWithContentType() {
        DataHandler dh = new DataHandler("test", "text/plain");
        ContentType contentType =
                DataHandlerContentTypeProvider.INSTANCE.getContentType(DataHandlerUtils.toBlob(dh));
        assertThat(contentType).isNotNull();
        assertThat(contentType.getMediaType()).isEqualTo(MediaType.TEXT_PLAIN);
    }
}
