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

package org.apache.axiom.attachments.part;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.activation.DataHandler;
import javax.mail.MessagingException;

import org.apache.axiom.attachments.CachedFileDataSource;
import org.apache.axiom.attachments.Part;
import org.apache.axiom.attachments.PushbackFilePartInputStream;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.util.UUIDGenerator;
/*
 * PartOnFile stores Attachment part data in to a File
 * and provides a DataHandler to access it. 
 */
public class PartOnFile extends DynamicPart {
    File cacheFile;

    Part bodyPart;

    String contentType;

    String contentID;

    public PartOnFile(PushbackFilePartInputStream inStream, String repoDir) {
        super();

        headers = new Hashtable();
        if (repoDir == null) {
            repoDir = ".";
        }
        try {
            File repoDirFile = null;
            if (repoDir != null) {
                repoDirFile = new File(repoDir);
                if (!repoDirFile.exists()) {
                    repoDirFile.mkdirs();
                }
            }
            if (!repoDirFile.isDirectory()) {
                throw new IllegalArgumentException("Given Axis2 Attachment File Cache Location "
                    + repoDir + "  should be a directory.");
            }
            //Generate uniqu UUID
            String id = UUIDGenerator.getUUID();
            //repleace all colon with underscore
            id = id.replaceAll(":", "_");
            cacheFile = File.createTempFile("Axis2"+id, ".att", repoDirFile);

            FileOutputStream fileOutStream = new FileOutputStream(cacheFile);
            int value;
            value = parseTheHeaders(inStream);
            fileOutStream.write(value);
            do {
                byte[] buffer = new byte[4000];
                int len;
                while ((len = inStream.read(buffer)) > 0) {
                    fileOutStream.write(buffer, 0, len);
                }
            } while (inStream.available() > 0);
            fileOutStream.flush();
            fileOutStream.close();
        } catch (IOException e) {
            throw new OMException("Error creating temporary File.", e);
        }
    }

    public DataHandler getDataHandler() throws MessagingException {
        CachedFileDataSource dataSource = new CachedFileDataSource(cacheFile);
        dataSource.setContentType(getContentType());
        return new DataHandler(dataSource);
    }

    public String getFileName() throws MessagingException {
        return cacheFile.getAbsolutePath();
    }

    public InputStream getInputStream() throws IOException, MessagingException {
        return new FileInputStream(cacheFile);
    }

    public int getSize() throws MessagingException {
        return (int) cacheFile.length();
    }
}
