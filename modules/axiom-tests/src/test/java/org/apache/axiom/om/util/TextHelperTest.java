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

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Validate TextHelper code
 */
public class TextHelperTest extends AbstractTestCase {

    private File file;
    private FileInputStream fis;
    private static final long SIZE = 10 * 1024;
    
    
    public TextHelperTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        file = File.createTempFile("TextHelperTest", "txt");
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        for (long i = 0; i < SIZE; i++) {
            bos.write((byte)(i % 256));
        }
        fos.close();
        fis = new FileInputStream(file);
        file.deleteOnExit();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        if (file != null) {
            file.delete();
        }
    }
    
    public void test_toString() throws Exception {
        String text = TextHelper.toString(fis);
        assertTrue(text.length() > SIZE);
    }
    
    public void test_toStringBuffer() throws Exception {
        StringBuffer buffer = new StringBuffer();
        TextHelper.toStringBuffer(fis, buffer);
        assertTrue(buffer.length() > SIZE);
    }
    
    public void test_fromOMText() throws Exception {
        
        OMFactory factory = OMAbstractFactory.getOMFactory();
        FileDataSource fds = new FileDataSource(file);
        DataHandler dh = new DataHandler(fds);
        OMText omText = factory.createOMText(dh, true);
        StringBuffer buffer = new StringBuffer();
        TextHelper.toStringBuffer(omText, buffer);
        assertTrue(buffer.length() > SIZE);
    }
    
}