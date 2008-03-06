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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Attachment processing uses a lot of buffers.
 * The BufferUtils class attempts to reuse buffers to prevent 
 * excessive GarbageCollection
 */
public class BufferUtils {
    
    static int BUFFER_LEN = 100 * 1024;         // Copy Buffer size
    static boolean ENABLE_FILE_CHANNEL = true;  // Enable file channel optimization 
    
    /**
     * Private utility to write the InputStream contents to the OutputStream.
     * @param is
     * @param os
     * @throws IOException
     */
    public static void inputStream2OutputStream(InputStream is, 
                                                OutputStream os)
        throws IOException {
            
        
        // If this is a FileOutputStream, use th
        if (ENABLE_FILE_CHANNEL && os instanceof FileOutputStream) {
            if (inputStream2FileOutputStream(is, (FileOutputStream) os)) {
                return;
            }
        }
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
    
    /**
     * Opimized writing to FileOutputStream using a channel
     * @param is
     * @param fos
     * @return false if lock was not aquired
     * @throws IOException
     */
    public static boolean inputStream2FileOutputStream(InputStream is, 
                                                FileOutputStream fos)
        throws IOException {
        
        // See if a file channel and lock can be obtained on the FileOutputStream
        FileChannel channel = null;
        FileLock lock = null;
        ByteBuffer bb = null;
        try {
            channel = fos.getChannel();
            if (channel != null) {
                lock = channel.tryLock();
            }
            bb = ByteBuffer.allocate(BUFFER_LEN);
        } catch (Throwable t) {
        }
        if (lock == null || bb == null || !bb.hasArray()) {
            return false;  // lock could not be set or bb does not have direct array access
        }
        
        try {

            // Read directly into the ByteBuffer array
            int bytesRead = is.read(bb.array());
            // Continue reading until no bytes are read and no
            // bytes are now available.
            while (bytesRead > 0 || is.available() > 0) {
                if (bytesRead > 0) {
                    int written = 0;
                    
                    
                    if (bytesRead < BUFFER_LEN) {
                        // If the ByteBuffer is not full, allocate a new one
                        ByteBuffer temp = ByteBuffer.allocate(bytesRead);
                        temp.put(bb.array(), 0, bytesRead);
                        temp.position(0);
                        written = channel.write(temp);
                    } else {
                        // Write to channel
                        bb.position(0);
                        written = channel.write(bb);
                        bb.clear();
                    }
                  
                }
                
                // REVIEW: Do we need to ensure that bytesWritten is 
                // the same as the number of bytes sent ?
                
                bytesRead = is.read(bb.array());
            }
        } finally {
            // Release the lock
           lock.release();
        }
        return true;
    }
}
