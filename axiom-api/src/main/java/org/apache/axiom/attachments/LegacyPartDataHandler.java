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

import javax.activation.DataSource;

import org.apache.axiom.attachments.lifecycle.DataHandlerExt;
import org.apache.axiom.blob.Blob;

final class LegacyPartDataHandler extends PartDataHandler implements DataHandlerExt {
    public LegacyPartDataHandler(PartImpl part) {
        super(part);
    }

    @Override
    protected DataSource createDataSource(Part part, String contentType) {
        Blob blob = part.getBlob();
        if (blob instanceof LegacyTempFileBlob) {
            return ((LegacyTempFileBlob)blob).getDataSource(contentType);
        } else {
            return null;
        }
    }

    public InputStream readOnce() throws IOException {
        return getPart().getInputStream(false);
    }

    public void purgeDataSource() throws IOException {
        getPart().releaseContent();
    }

    public void deleteWhenReadOnce() throws IOException {
        // As shown in AXIOM-381, in all released versions of Axiom, deleteWhenReadOnce
        // always has the same effect as purgeDataSource
        purgeDataSource();
    }
}
