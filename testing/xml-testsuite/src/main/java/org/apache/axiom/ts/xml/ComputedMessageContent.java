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
package org.apache.axiom.ts.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.activation.DataSource;

import org.apache.axiom.testutils.net.protocol.mem.DataSourceRegistry;

public abstract class ComputedMessageContent extends MessageContent {
    private byte[] content;
    private URL url;

    @Override
    public final synchronized InputStream getInputStream() {
        if (content == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                buildContent(baos);
            } catch (Exception ex) {
                throw new MessageContentLoadingException(ex);
            }
            content = baos.toByteArray();
        }
        return new ByteArrayInputStream(content);
    }

    @Override
    public final synchronized URL getURL() {
        if (url == null) {
            url = DataSourceRegistry.registerDataSource(new DataSource() {
                @Override
                public OutputStream getOutputStream() throws IOException {
                    throw new UnsupportedOperationException();
                }
                
                @Override
                public String getName() {
                    return null;
                }
                
                @Override
                public InputStream getInputStream() throws IOException {
                    return ComputedMessageContent.this.getInputStream();
                }
                
                @Override
                public String getContentType() {
                    return "application/octet-stream";
                }
            }).getURL();
        }
        return url;
    }

    protected abstract void buildContent(OutputStream out) throws Exception;
}
