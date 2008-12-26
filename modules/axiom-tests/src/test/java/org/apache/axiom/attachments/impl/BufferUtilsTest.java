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

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apache.axiom.om.AbstractTestCase;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Simple test for the BufferUtils copying code
 */
public class BufferUtilsTest extends AbstractTestCase {

    byte[] bytes;
    static final int MAX = 1024 * 1024;
   
    protected void setUp() throws Exception {
        bytes = new byte[MAX];
        for (int i = 0; i < MAX /20; i++) {
            for (int j = 0; j < 20; j++) {
                bytes[i*20 + j] = (byte) j;
            }
        }
    }

    /**
     * Create a temp file, and write to it using buffer utils
     * @throws Exception
     */
    public void test() throws Exception {
        // Create temp file
        File file =  File.createTempFile("bufferUtils", "tst");
        file.deleteOnExit();
        try {
            OutputStream fos = new FileOutputStream(file, true);
            for (int i = 0; i < 20; i++) {
                long start = System.currentTimeMillis();
                InputStream bais = new ByteArrayInputStream(bytes);
                BufferUtils.inputStream2OutputStream(bais, fos);
                fos.flush();
                long end = System.currentTimeMillis();

            }
            fos.close();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[20];
            fis.read(buffer);
            for (int i = 0; i < buffer.length; i++) {
                assertTrue(buffer[i] == (byte) i);
            }
        } finally {
            file.delete();
        }    
    }
    
    public void testDataSourceBackedDataHandlerExceedLimit(){
        File imgFile = getTestResourceFile("mtom/img/test2.jpg");
        FileDataSource fds = new FileDataSource(imgFile);
        DataHandler dh = new DataHandler(fds);
        int unsupported= BufferUtils.doesDataHandlerExceedLimit(dh, 0);
        assertEquals(unsupported, -1);
        int doesExceed = BufferUtils.doesDataHandlerExceedLimit(dh, 1000);
        assertEquals(doesExceed, 1);
        int doesNotExceed = BufferUtils.doesDataHandlerExceedLimit(dh, 100000);
        assertEquals(doesNotExceed, 0);
        
    }
    
    public void testObjectBackedDataHandlerExceedLimit(){
        String str = "This is a test String";
        try{
            DataHandler dh = new DataHandler(str, "text/plain");          
            int unsupported= BufferUtils.doesDataHandlerExceedLimit(dh, 0);
            assertEquals(unsupported, -1);
            int doesExceed = BufferUtils.doesDataHandlerExceedLimit(dh, 10);
            //Expecting Mark NotSupported
            assertEquals(doesExceed, -1);
        }catch(Exception e){
            e.printStackTrace();
            fail();
        }
        
    }
    public void testByteArrayDataSourceBackedDataHandlerExceedLimit(){
        String str = "This is a test String";
        byte[] b = str.getBytes();
        ByteArrayDataSource bads = new ByteArrayDataSource(b, "text/plain");
        try{
            DataHandler dh = new DataHandler(bads);          
            int unsupported= BufferUtils.doesDataHandlerExceedLimit(dh, 0);
            assertEquals(unsupported, -1);
            int doesExceed = BufferUtils.doesDataHandlerExceedLimit(dh, 10);
            assertEquals(doesExceed, 1);
            int doesNotExceed = BufferUtils.doesDataHandlerExceedLimit(dh, 100);
            assertEquals(doesNotExceed, 0);
        }catch(Exception e){
            e.printStackTrace();
            fail();
        }
        
    }
}
