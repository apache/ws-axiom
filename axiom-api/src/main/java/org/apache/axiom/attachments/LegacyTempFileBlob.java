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

import javax.activation.DataSource;

import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.attachments.lifecycle.impl.FileAccessor;
import org.apache.axiom.blob.AbstractWritableBlob;

final class LegacyTempFileBlob extends AbstractWritableBlob {
    private final LifecycleManager lifecycleManager;
    private final String attachmentDir;
    private FileAccessor fileAccessor;

    LegacyTempFileBlob(LifecycleManager lifecycleManager, String attachmentDir) {
        this.lifecycleManager = lifecycleManager;
        this.attachmentDir = attachmentDir;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        fileAccessor = lifecycleManager.create(attachmentDir);
        return fileAccessor.getOutputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return fileAccessor.getInputStream();
    }

    DataSource getDataSource(String contentType) {
        CachedFileDataSource ds = new CachedFileDataSource(fileAccessor.getFile());
        ds.setContentType(contentType);
        return ds;
    }
    
    @Override
    public long getSize() {
        return fileAccessor.getSize();
    }

    @Override
    public void release() throws IOException {
        lifecycleManager.delete(fileAccessor.getFile());
    }
}
