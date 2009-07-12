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

package org.apache.axiom.util.base64;

import java.io.IOException;

import javax.activation.DataHandler;

import org.apache.axiom.util.activation.DataSourceUtils;

/**
 * Contains utility methods to work with base64 encoded data.
 */
public class Base64Utils {
    private static int getEncodedSize(int unencodedSize) {
        return (unencodedSize+2) / 3 * 4;
    }
    
    /**
     * Get a base64 representation of the content of a given {@link DataHandler}.
     * This method will try to carry out the encoding operation in the most efficient way.
     * 
     * @param dh the data handler with the content to encode
     * @return the base64 encoded content
     * @throws IOException if an I/O error occurs when reading the content of the data handler
     */
    public static String encode(DataHandler dh) throws IOException {
        long size = DataSourceUtils.getSize(dh.getDataSource());
        StringBuffer buffer;
        if (size == -1) {
            // Use a reasonable default capacity (better than the default of 16).
            buffer = new StringBuffer(4096);
        } else if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("DataHandler is too large to encode to string");
        } else {
            buffer = new StringBuffer(getEncodedSize((int)size));
        }
        Base64StringBufferOutputStream out = new Base64StringBufferOutputStream(buffer);
        // Always prefer writeTo, because getInputStream will create a thread and a pipe if
        // the DataHandler was constructed using an object instead of a DataSource
        dh.writeTo(out);
        out.complete();
        return buffer.toString();
    }
}
