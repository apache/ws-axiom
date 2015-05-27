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
package org.apache.axiom.om.ds.jaxb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;

import org.apache.axiom.util.UIDGenerator;
import org.apache.axiom.util.stax.xop.MimePartProvider;

final class DataHandlerWriterAttachmentMarshaller extends AttachmentMarshallerBase implements MimePartProvider {
    private final Map<String,DataHandler> dataHandlers = new HashMap<String,DataHandler>();

    @Override
    public String addMtomAttachment(DataHandler data, String elementNamespace,
            String elementLocalName) {
        String contentID = UIDGenerator.generateContentId();
        dataHandlers.put(contentID, data);
        return "cid:" + contentID;
    }

    public boolean isLoaded(String contentID) {
        // DataHandlers are always loaded, in the sense that getDataHandler will always
        // return a DataHandler immediately (or throw an exception in the unlikely case that
        // a wrong content ID is provided)
        return true;
    }

    public DataHandler getDataHandler(String contentID) throws IOException {
        DataHandler dataHandler = dataHandlers.get(contentID);
        if (dataHandler == null) {
            throw new IOException("No DataHandler found for content ID " + contentID);
        } else {
            return dataHandler;
        }
    }
}
