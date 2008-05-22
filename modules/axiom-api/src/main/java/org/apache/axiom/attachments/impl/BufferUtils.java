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

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import org.apache.axiom.attachments.utils.BAAOutputStream;
import org.apache.axiom.om.OMException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Attachment processing uses a lot of buffers.
 * The BufferUtils class attempts to reuse buffers to prevent 
 * excessive GarbageCollection
 */
public class BufferUtils {
    private static Log log = LogFactory.getLog(BufferUtils.class);
    // Performance testing indicates that 4K is the best size for medium
    // and small payloads.  And there is a neglible effect on large payloads.
    public final static int BUFFER_LEN = 4 * 1024;   // Copy Buffer size
    static boolean ENABLE_FILE_CHANNEL = true;       // Enable file channel optimization
    static boolean ENABLE_BAAOS_OPT = true;          // Enable BAAOutputStream opt
    
    private static byte[] _cacheBuffer = new byte[BUFFER_LEN];
    private static boolean _cacheBufferInUse = false;
    
    private static ByteBuffer _cacheByteBuffer = ByteBuffer.allocate(BUFFER_LEN);
    private static boolean _cacheByteBufferInUse = false;
    
    /**
     * Private utility to write the InputStream contents to the OutputStream.
     * @param is
     * @param os
     * @throws IOException
     */
    public static void inputStream2OutputStream(InputStream is, 
                                                OutputStream os)
        throws IOException {
            
        
        // If this is a FileOutputStream, use the optimized method
        if (ENABLE_FILE_CHANNEL && os instanceof FileOutputStream) {
            if (inputStream2FileOutputStream(is, (FileOutputStream) os)) {
                return;
            }
        }
        
        // If this is a BAAOutputStream, use the optimized method
        if (ENABLE_BAAOS_OPT && os instanceof BAAOutputStream) {
            inputStream2BAAOutputStream(is, (BAAOutputStream) os, Long.MAX_VALUE);
            return;
        }
        
        byte[] buffer = getTempBuffer();
        
        try {
        int bytesRead = is.read(buffer);
        
        
        // Continue reading until no bytes are read and no
        // bytes are now available.
        while (bytesRead > 0 || is.available() > 0) {
            if (bytesRead > 0) {
                os.write(buffer, 0, bytesRead);
            }
            bytesRead = is.read(buffer);
        }
        } finally {
            releaseTempBuffer(buffer);
        }
        
    }
    
    /**
     * @param is InputStream
     * @param os OutputStream
     * @param limit maximum number of bytes to read
     * @return total bytes read
     * @throws IOException
     */
    public static int inputStream2OutputStream(InputStream is, 
                                                OutputStream os,
                                                int limit) 
        throws IOException {
        
        // If this is a BAAOutputStream, use the optimized method
        if (ENABLE_BAAOS_OPT && os instanceof BAAOutputStream) {
            return (int) inputStream2BAAOutputStream(is, (BAAOutputStream) os, (long) limit);
        }
            
        byte[] buffer = getTempBuffer();
        int totalWritten = 0;
        int bytesRead = 0;
        
        try {
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
        } finally {
            releaseTempBuffer(buffer);
        }
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
            bb = getTempByteBuffer();
        } catch (Throwable t) {
        }
        if (lock == null || bb == null || !bb.hasArray()) {
            releaseTempByteBuffer(bb);
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
           releaseTempByteBuffer(bb);
        }
        return true;
    }
    
    /** 
     * inputStream2BAAOutputStream
     * @param is
     * @param baaos
     * @param limit
     * @return
     */
    public static long inputStream2BAAOutputStream(InputStream is, 
                                               BAAOutputStream baaos,
                                               long limit) throws IOException {
        return baaos.receive(is, limit);
    }
    
    
    /**
     * The method checks to see if attachment is eligble for optimization.
     * An attachment is eligible for optimization if and only if the size of 
     * the attachment is greated then the optimzation threshold size limit. 
     * if the Content represented by DataHandler has size less than the 
     * optimize threshold size, the attachment will not be eligible for 
     * optimization, instead it will be inlined.
     * returns 1 if DataHandler data is bigger than limit.
     * returns 0 if DataHandler data is smaller.
     * return -1 if an error occurs or unsupported.
     * @param in
     * @return
     * @throws IOException
     */
    public static int doesDataHandlerExceedLimit(DataHandler dh, int limit){
        if(log.isDebugEnabled()){
            log.debug("start isEligibleForOptimization");
        }
        //If Optimized Threshold not set return true.
        if(limit==0){
            if(log.isDebugEnabled()){
                log.debug("optimizedThreshold not set");
            }
            return -1;
        }
        
        InputStream in=null;
        //read bytes from input stream to check if 
        //attachment size is greater than the optimized size.        
        int totalRead = 0;
        try{
            in = getInputStream(dh);
            if(in.markSupported()){
                in.mark(limit);
            }
            if(in == null){
                if(log.isDebugEnabled()){
                    log.debug("Input Stream is null");
                }
                return -1;
            }
            do{
                byte[] buffer = getTempBuffer();
                int bytesRead = in.read(buffer, 0, BUFFER_LEN);
                totalRead = totalRead+bytesRead;
                releaseTempBuffer(buffer);
            }while((limit>totalRead) && (in.available()>0));
            
            if(in.markSupported()){                
                in.reset();
            }
            if(totalRead > limit){
                if(log.isDebugEnabled()){
                    log.debug("Attachment size greater than limit");
                }
                return 1;
            }
        }catch(Exception e){            
            log.warn(e.getMessage());
            return -1;
        }
        
        if(log.isDebugEnabled()){
            log.debug("Attachment Size smaller than limit");
        }
        
        return 0;        
    }
    
    private static java.io.InputStream getInputStream(DataHandler dataHandlerObject) throws OMException {
        InputStream inStream = null;
        javax.activation.DataHandler dataHandler =
            (javax.activation.DataHandler) dataHandlerObject;
        try {
            DataSource ds = dataHandler.getDataSource();
            if(ds instanceof FileDataSource){
                inStream = ds.getInputStream();
            }else{
                inStream = dataHandler.getDataSource().getInputStream();
                if(!inStream.markSupported()){
                    throw new OMException("Stream does not support mark, Cannot read the stream as DataSource will be consumed.");
                }
            }
        } catch (IOException e) {
            throw new OMException(
                "Cannot get InputStream from DataHandler." + e);
        }
        return inStream;

    }
    
    private static synchronized byte[] getTempBuffer() {
        // Try using cached buffer
        synchronized(_cacheBuffer) {
            if (!_cacheBufferInUse) {
                _cacheBufferInUse = true;
                return _cacheBuffer;
            }
        }
        
        // Cache buffer in use, create new buffer
        return new byte[BUFFER_LEN];
    }
    
    private static void releaseTempBuffer(byte[] buffer) {
        // Try using cached buffer
        synchronized(_cacheBuffer) {
            if (buffer == _cacheBuffer) {
                _cacheBufferInUse = false;
            }
        }
    }
    
    private static synchronized ByteBuffer getTempByteBuffer() {
        // Try using cached buffer
        synchronized(_cacheByteBuffer) {
            if (!_cacheByteBufferInUse) {
                _cacheByteBufferInUse = true;
                return _cacheByteBuffer;
            }
        }
        
        // Cache buffer in use, create new buffer
        return ByteBuffer.allocate(BUFFER_LEN);
    }
    
    private static void releaseTempByteBuffer(ByteBuffer buffer) {
        // Try using cached buffer
        synchronized(_cacheByteBuffer) {
            if (buffer == _cacheByteBuffer) {
                _cacheByteBufferInUse = false;
            }
        }
    }
}
