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
package org.apache.axiom.attachments.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Attachment processing uses a lot of buffers.
 * The BufferUtils class attempts to reuse buffers to prevent 
 * excessive GarbageCollection
 */
public class BufferUtils {
    
    public static final int BUFFER_LEN = 4 * 1024;
    
    /**
     * Private utility to write the InputStream contents to the OutputStream.
     * @param is
     * @param os
     * @throws IOException
     */
    public static void inputStream2OutputStream(InputStream is, 
                                                OutputStream os)
        throws IOException {
            
        
        byte[] buffer = new byte[BUFFER_LEN];
        int bytesRead = is.read(buffer);
        
        // Continue reading until no bytes are read and no
        // bytes are now available.
        while (bytesRead > 0 || is.available() > 0) {
            if (bytesRead > 0) {
                os.write(buffer, 0, bytesRead);
            }
            bytesRead = is.read(buffer);
        }
        
    }
    
    /**
     * @param is InputStream
     * @param os OutputStream
     * @param limit maximum number of bytes to read
     * @return total ytes read
     * @throws IOException
     */
    public static int inputStream2OutputStream(InputStream is, 
                                                OutputStream os,
                                                int limit) 
        throws IOException {
            
        byte[] buffer = new byte[BUFFER_LEN];
        int totalWritten = 0;
        int bytesRead = 0;
        
        do {
            int len = (limit-totalWritten) > BUFFER_LEN ? BUFFER_LEN : (limit-totalWritten);
            bytesRead = is.read(buffer, 0, len);
            if (bytesRead > 0) {
                os.write(buffer, 0, bytesRead);
                if (bytesRead > 0) {
                    totalWritten += bytesRead;
                }
            }
        } while (totalWritten < limit && (bytesRead > 0 || is.available() > 0));
        return totalWritten;
    }
    
  
}
