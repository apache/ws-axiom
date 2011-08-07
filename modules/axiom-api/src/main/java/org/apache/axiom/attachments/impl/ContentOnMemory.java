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

import org.apache.axiom.attachments.utils.BAAInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.activation.DataSource;

/**
 * PartOnMemoryEnhanced stores the attachment in memory (in non-contigous byte arrays)
 * This implementation is used for smaller attachments to enhance 
 * performance.
 * 
 * The PartOnMemoryEnhanced object is created by the PartFactory
 * @see ContentStoreFactory
 */
public class ContentOnMemory extends ContentStore {

    ArrayList data;  // Arrays of 4K buffers
    int length;      // total length of data
    
    /**
     * Construct a PartOnMemory
     * @param data array list of 4K byte[]
     * @param length (length of data in bytes)
     */
    ContentOnMemory(ArrayList data, int length) {
        this.data =  data;
        this.length = length;
    }

    public InputStream getInputStream() {
        return new BAAInputStream(data, length);
    }
    
    public DataSource getDataSource(String contentType) {
        // Use a default implementation
        return null;
    }

    public void writeTo(OutputStream os) throws IOException {
        new BAAInputStream(data, length).writeTo(os);
    }
    
    public long getSize() {
        return length;
    }
    
    public void destroy() throws IOException {
        data.clear();
    }
}
