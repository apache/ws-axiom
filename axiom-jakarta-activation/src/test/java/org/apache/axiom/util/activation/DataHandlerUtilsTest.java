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
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.testutils.io.ExceptionOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.junit.jupiter.api.Test;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;

public class DataHandlerUtilsTest {
    @Test
    public void testToBlobWriteError() {
        ExceptionOutputStream out = new ExceptionOutputStream(0);
        StreamCopyException ex =
                assertThrows(
                        StreamCopyException.class,
                        () -> {
                            DataHandlerUtils.toBlob(
                                            new DataHandler(
                                                    new ByteArrayDataSource(
                                                            new byte[10],
                                                            "application/octet-stream")))
                                    .writeTo(out);
                        });
        assertThat(ex.getOperation()).isEqualTo(StreamCopyException.WRITE);
        assertThat(ex.getCause()).isSameAs(out.getException());
    }

    @Test
    public void testToBlobReadError() {
        DataSource ds =
                new DataSource() {
                    @Override
                    public String getContentType() {
                        return "application/octet-stream";
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        throw new IOException("Read error");
                    }

                    @Override
                    public String getName() {
                        return null;
                    }

                    @Override
                    public OutputStream getOutputStream() throws IOException {
                        throw new UnsupportedOperationException();
                    }
                };
        StreamCopyException ex =
                assertThrows(
                        StreamCopyException.class,
                        () -> {
                            DataHandlerUtils.toBlob(new DataHandler(ds))
                                    .writeTo(NullOutputStream.INSTANCE);
                        });
        assertThat(ex.getOperation()).isEqualTo(StreamCopyException.READ);
        assertThat(ex.getCause().getMessage()).isEqualTo("Read error");
    }
}
