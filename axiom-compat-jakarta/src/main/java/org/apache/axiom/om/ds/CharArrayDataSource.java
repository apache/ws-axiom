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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * @deprecated Use {@link StringOMDataSource} instead.
 */
public class CharArrayDataSource extends OMDataSourceExtBase {

    char[] chars = null;
   
    /**
     * Constructor
     * @param chars
     */
    public CharArrayDataSource(char[] chars) {
        this.chars = chars;
    }

    @Override
    public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        try {
            writer.write(chars);
        } catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    @Override
    public XMLStreamReader getReader() throws XMLStreamException {
        CharArrayReader reader = new CharArrayReader(chars);
        
        return StAXUtils.createXMLStreamReader(reader);                                                                   
    }
    
    
    @Override
    public Object getObject() {
       return chars;
    }

    @Override
    public boolean isDestructiveRead() {
        // Reading chars is not destructive
        return false;
    }

    @Override
    public boolean isDestructiveWrite() {
        // Writing chars is not destructive
        return false;
    }

    @Override
    public byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        
        String text = new String(chars);
        return text.getBytes(encoding);
    }
    
    @Override
    public void close() {
        chars = null;
    }

    @Override
    public OMDataSourceExt copy() {
        // Return shallow copy
        return new CharArrayDataSource(chars);
    }
}
