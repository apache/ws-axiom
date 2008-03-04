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

import org.apache.axiom.attachments.lifecycle.LifecycleManager;
import org.apache.axiom.om.util.UUIDGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class LifecycleManagerImpl implements LifecycleManager {
    private static final Log log = LogFactory.getLog(LifecycleManagerImpl.class);
    private Map table = new Hashtable();
    public static LifecycleManager manager = new LifecycleManagerImpl();

    public LifecycleManagerImpl() {
        super(); 
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.lifecycle.LifecycleManager#create(java.lang.String)
     */
    public FileAccessor create(String attachmentDir) throws IOException {
        if(log.isDebugEnabled()){
            log.debug("Start Create()");
        }
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
        //add the file to table
        table.put(id, file);
        FileAccessor fa = new FileAccessor(manager, file, id);
        //TODO: change deleteOnExit call such that it's sent as an event to 
        //LifecycleEventHandler. example fa.handleEvent(LifeCycleDefinition.DELETE_ON_EXIT)
        //This is the default behaviour. Delete file on VM Exit.
        deleteOnExit(id);
        if(log.isDebugEnabled()){
            log.debug("End Create()");
        }
        return fa;
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.lifecycle.LifecycleManager#delete(java.io.File)
     */
    public void delete(String id) throws IOException {
        if(log.isDebugEnabled()){
            log.debug("Start delete()");
        }
        File file = getFile(id); 

        if(file!=null && file.exists()){
            file.delete();
            table.remove(id);
        }
        if(log.isDebugEnabled()){
            log.debug("End delete()");
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.lifecycle.LifecycleManager#deleteOnExit(java.io.File)
     */
    public void deleteOnExit(String id) throws IOException {
        if(log.isDebugEnabled()){
            log.debug("Start deleteOnExit()");
        }
        File file = getFile(id); 
        if(file!=null){
            file.deleteOnExit();
            table.remove(id);
        }
        if(log.isDebugEnabled()){
            log.debug("End deleteOnExit()");
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.lifecycle.LifecycleManager#deleteOnTimeInterval(int)
     */
    public void deleteOnTimeInterval(int interval, String id) throws IOException {
        if(log.isDebugEnabled()){
            log.debug("Start deleteOnTimeInterval()");
        }
        File file = getFile(id);
        Thread t = new Thread(new LifecycleManagerImpl.FileDeletor(interval, file, id));
        t.start();
        if(log.isDebugEnabled()){
            log.debug("End deleteOnTimeInterval()");
        }
    }

    private File getFile(String id){
        return (File)table.get(id);
    }

    public class FileDeletor implements Runnable{
        int interval;
        File _file;
        String _id;
        public FileDeletor(int interval, File file, String id) {
            super();
            this.interval = interval;
            this._file = file;
            this._id = id;
        }

        public void run() {
            try{
                Thread.sleep(interval*1000);
                if(_file.exists()){
                    _file.delete();
                    table.remove(_id);
                }
            }catch(InterruptedException e){
                //Log Exception
                if(log.isDebugEnabled()){
                    log.warn("InterruptedException occured "+e.getMessage());
                }
            }
        }        
    }

}


