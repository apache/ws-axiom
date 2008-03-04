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

package org.apache.axiom.attachments.lifecycle.impl;

import org.apache.axiom.attachments.CachedFileDataSource;
import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * FileAccessor wraps the attachment temp file. It is created from PartOnFile.
 * The idea behind wrapping the file is to give rumtime an ability to track
 * when the file is accessed with streams or data handler  and accordingly trigger
 * events to handle the the files lifecycle.
 *
 */
public class FileAccessor implements LifecycleEventHandler{
    private static final Log log = LogFactory.getLog(FileAccessor.class);
    File file = null;
    String id = null;
    LifecycleManager manager;
    
    //TODO remove hard coded time interval, 30 mins/1800 secs
    private final static int DELETE_INTERVAL = 1800;
    public FileAccessor(LifecycleManager manager, File file, String id) {
        super();
        this.manager = manager;
        this.file = file;
        this.id = id;
    }

    public DataHandler getDataHandler(String contentType) throws MessagingException {
        if(log.isDebugEnabled()){
            log.debug("getDataHandler()");
        }
        CachedFileDataSource dataSource = new CachedFileDataSource(file);
        dataSource.setContentType(contentType);
        return new DataHandler(dataSource);
    }

    public String getFileName() throws MessagingException {
        if(log.isDebugEnabled()){
            log.debug("getFileName()");
        }
        return file.getAbsolutePath();
    }

    public InputStream getInputStream() throws IOException, MessagingException {
        if(log.isDebugEnabled()){
            log.debug("getInputStream()");
        }
        return new FileInputStream(file);
    }

    public OutputStream getOutputStream() throws FileNotFoundException{
        if(log.isDebugEnabled()){
            log.debug("getOutputStream()");
        }
        return new FileOutputStream(file);
    }

    public int getSize() {
        return (int) file.length();
    }

    public void handleEvent(int eventId) throws IOException {
        switch (eventId) {
        case LifecycleEventDefinitions.DELETE_ON_EXIT:
            manager.deleteOnExit(id);
            break;
        case LifecycleEventDefinitions.DELETE_ON_TIME_INTERVAL:
            manager.deleteOnTimeInterval(DELETE_INTERVAL, id);
            break;
        case LifecycleEventDefinitions.READ_ONCE_AND_DELETE:
            manager.delete(id);
            break;
        default:
            manager.delete(id);
            break;
        }
    }

    public File getFile() {
        return file;
    }

    public String getId() {
        return id;
    }
}
