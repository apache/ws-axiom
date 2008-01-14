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

import org.apache.axiom.attachments.CachedFileDataSource;
import org.apache.axiom.om.util.UUIDGenerator;

import javax.activation.DataHandler;
import javax.mail.MessagingException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

/**
 * PartOnFile stores that attachment in a file.
 * This implementation is used for very large attachments to reduce
 * the in-memory footprint.
 * 
 * The PartOnFile object is created by the PartFactory
 * @see org.apache.axiom.attachments.impl.PartFactory.
 */
public class PartOnFile extends AbstractPart {

    File backingFile;
    
    
    /**
     * Create a PartOnFile from the specified InputStream
     * @param headers Hashtable of javax.mail.Headers
     * @param in1 InputStream containing data
     * @param in2 InputStream containing data
     * @param attachmentDir String 
     */
    PartOnFile(Hashtable headers, InputStream is1, InputStream is2, String attachmentDir) throws IOException {
        super(headers);
        backingFile = createFile(attachmentDir);
     
        
        // Now write the data to the backing file
        FileOutputStream fos = new FileOutputStream(backingFile);
        BufferUtils.inputStream2OutputStream(is1, fos);
        BufferUtils.inputStream2OutputStream(is2, fos);
        fos.flush();
        fos.close();
    }
    
    /**
     * Create a unique file in the designated directory
     * @param attachmentDir
     * @return File
     * @throws IOException
     */
    private static File createFile(String attachmentDir) throws IOException {
        File file = null;
        File dir = null;
        if (attachmentDir != null) {
            dir = new File(attachmentDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Given Axis2 Attachment File Cache Location "
                    + dir + "  should be a directory.");
        }
        // Generate unique id.  The UUID generator is used so that we can limit
        // synchronization with the java random number generator.
        String id = UUIDGenerator.getUUID();
        
        //Replace colons with underscores
        id = id.replaceAll(":", "_");
        
        String fileString = "Axis2" + id + ".att";
        file = new File(dir, fileString);
        return file;
    }
    
    /* (non-Javadoc)
     * @see org.apache.axiom.attachments.impl.AbstractPart#getDataHandler()
     */
    public DataHandler getDataHandler() throws MessagingException {
        CachedFileDataSource dataSource = new CachedFileDataSource(backingFile);
        dataSource.setContentType(getContentType());
        return new DataHandler(dataSource);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.attachments.impl.AbstractPart#getFileName()
     */
    public String getFileName() throws MessagingException {
        return backingFile.getAbsolutePath();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.attachments.impl.AbstractPart#getInputStream()
     */
    public InputStream getInputStream() throws IOException, MessagingException {
        return new FileInputStream(backingFile);
    }
    
    /* (non-Javadoc)
     * @see org.apache.axiom.attachments.impl.AbstractPart#getSize()
     */
    public int getSize() {
        return (int) backingFile.length();
    }

}
