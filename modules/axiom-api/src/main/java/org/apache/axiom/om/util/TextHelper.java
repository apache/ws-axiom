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

package org.apache.axiom.om.util;

import org.apache.axiom.om.OMText;

import javax.activation.DataHandler;

import java.io.IOException;
import java.io.InputStream;

public class TextHelper {
    
    /**
     * @param inStream InputStream
     * @return Base64 encoded string representint the data in inStream
     * @throws IOException
     */
    public static String toString(InputStream inStream) throws IOException {
        StringBuffer buffer = new StringBuffer();
        toStringBuffer(inStream, buffer);
        return buffer.toString();
    }
    
    /**
     * Append Base64 encoding of the data in the inStream to the specified buffer
     * @param inStream InputStream
     * @param buffer Buffer
     * @throws IOException
     */
    public static void toStringBuffer(InputStream inStream, StringBuffer buffer) throws IOException {
        byte[] data;
        
        int avail = inStream.available();
        
        // The Base64 will increase the size by 1.33 + some additional 
        // space at the data byte[] boundaries.  So a factor of 1.35 is used
        // to ensure capacity.
        if (avail > 0) {
            buffer.ensureCapacity((int) (avail* 1.35) + buffer.length());
        }
        
        
        do {
            data = new byte[1023];
            int len;
            while ((len = inStream.read(data)) > 0) {
                Base64.encode(data, 0, len, buffer);
            }
        } while (inStream.available() > 0);
        return;
    }
    
    /**
     * Append data in the omText to the specified buffer
     * @param inStream InputStream
     * @param buffer Buffer
     * @throws IOException
     */
    public static void toStringBuffer(OMText omText, StringBuffer buffer) throws IOException {
        // If an InputStream is present, stream the BASE64 text to the StreamBuffer
        if (omText.isOptimized()) {
           Object dh = omText.getDataHandler();
           if (dh instanceof DataHandler) {
               InputStream is = ((DataHandler) dh).getInputStream();
               if (is != null) {
                   toStringBuffer(is, buffer);
                   return;
               }
           }
        }
        
        // Otherwise append the text
        buffer.append(omText.getText());
        return;
    }
}
