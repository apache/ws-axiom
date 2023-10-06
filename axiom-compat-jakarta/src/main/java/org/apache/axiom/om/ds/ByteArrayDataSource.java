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
package org.apache.axiom.om.ds;

import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 * @deprecated Use {@link BlobOMDataSource} instead.
 */
public class ByteArrayDataSource extends OMDataSourceExtBase {

    private static final Log log = LogFactory.getLog(ByteArrayDataSource.class);
	
    ByteArray byteArray = null;
    
    /**
     * Constructor
     * @param bytes 
     * @param encoding
     */
    public ByteArrayDataSource(byte[] bytes, String encoding) {
        byteArray = new ByteArray();
        byteArray.bytes = bytes;
        byteArray.encoding = encoding;
    }
   
 
    @Override
    public XMLStreamReader getReader() throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("getReader");
        }
        return StAXUtils.createXMLStreamReader(new ByteArrayInputStream(byteArray.bytes),
                                               byteArray.encoding);                                                                       
    }

    @Override
    public Object getObject() {
       return byteArray;
    }

    @Override
    public boolean isDestructiveRead() {
        // Reading bytes is not destructive
        return false;
    }

    @Override
    public boolean isDestructiveWrite() {
        // Writing bytes is not destructive
        return false;
    }

    @Override
    public byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        if (encoding == null)
        {
          encoding = OMOutputFormat.DEFAULT_CHAR_SET_ENCODING;
        }

        if (log.isDebugEnabled()) {
            log.debug("getXMLBytes encoding="+encoding);
        }

        // Return the byte array directly if it is the same encoding
        // Otherwise convert the bytes to the proper encoding
        if (!byteArray.encoding.equalsIgnoreCase(encoding)) {
            String text = new String(byteArray.bytes, byteArray.encoding);
            
            // Convert the internal data structure to the new bytes/encoding
            byteArray.bytes = text.getBytes(encoding);
            byteArray.encoding = encoding;
        }
        return byteArray.bytes;
    }
    
    @Override
    public void close() {
        byteArray = null;
    }

    @Override
    public OMDataSourceExt copy() {
        // Return shallow copy
        return new ByteArrayDataSource(byteArray.bytes, byteArray.encoding);
    }
    
    /**
     * Object containing the byte[]/encoding pair
     */
    public class ByteArray {
        public byte[] bytes;
        public String encoding;
    }   
}
