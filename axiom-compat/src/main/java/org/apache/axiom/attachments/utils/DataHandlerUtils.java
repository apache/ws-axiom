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

package org.apache.axiom.attachments.utils;


import jakarta.activation.DataHandler;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.util.base64.Base64Utils;

/**
 * @deprecated
 */
public class DataHandlerUtils {

    public static Object getDataHandlerFromText(String value, String mimeType) {
        ByteArrayDataSource dataSource;
        byte[] data = Base64Utils.decode(value);
        if (mimeType != null) {
            dataSource = new ByteArrayDataSource(data, mimeType);
        } else {
            // Assumes type as application/octet-stream
            dataSource = new ByteArrayDataSource(data);
        }
        return new DataHandler(dataSource);
    }
}
