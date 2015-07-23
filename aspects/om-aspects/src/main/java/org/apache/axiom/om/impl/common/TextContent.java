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
package org.apache.axiom.om.impl.common;

import javax.activation.DataHandler;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;

final class TextContent {
    final String value;
    
    String mimeType;
    
    /** Field contentID for the mime part used when serializing Binary stuff as MTOM optimized. */
    String contentID;
    
    /**
     * Contains a {@link DataHandler} or {@link DataHandlerProvider} object if the text node
     * represents base64 encoded binary data.
     */
    Object dataHandlerObject;

    boolean optimize;
    boolean binary;
    
    TextContent(String value) {
        this.value = value;
    }
}
