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
package org.apache.axiom.ts.om.text;

import javax.activation.DataHandler;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.util.activation.DataHandlerUtils;

final class DummyAttachmentAccessor implements OMAttachmentAccessor {
    private final String contentID;
    private final DataHandler dataHandler;
    private boolean loaded;

    DummyAttachmentAccessor(String contentID, DataHandler dataHandler) {
        this.contentID = contentID;
        this.dataHandler = dataHandler;
    }

    boolean isLoaded() {
        return loaded;
    }

    @Override
    public Blob getBlob(String contentID) {
        if (!contentID.equals(this.contentID)) {
            return null;
        } else {
            loaded = true;
            return DataHandlerUtils.toBlob(dataHandler);
        }
    }
}
