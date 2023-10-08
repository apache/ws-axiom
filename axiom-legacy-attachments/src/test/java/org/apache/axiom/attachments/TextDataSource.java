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
import java.io.StringReader;

import jakarta.activation.DataSource;

import org.apache.commons.io.input.ReaderInputStream;

public class TextDataSource implements DataSource {
    private final String content;
    private final String charset;
    private final String subtype;

    public TextDataSource(String content, String charset, String subtype) {
        this.content = content;
        this.charset = charset;
        this.subtype = subtype;
    }

    @Override
    public String getContentType() {
        return "text/" + subtype + "; charset='" + charset + "'";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ReaderInputStream(new StringReader(content), charset);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
}
