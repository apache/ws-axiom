/*
 * Copyright 2004,2007 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * CharArrayDataSource is an example implementation of OMDataSourceExt.
 * Use it to insert a char[] into an OM Tree.
 * This data source is useful for placing characters into an OM
 * tree, instead of having a deeply nested tree.
 */
public class CharArrayDataSource extends OMDataSourceExtBase {

    char[] chars = null;
   
    /**
     * Constructor
     * @param bytes 
     * @param encoding
     */
    public CharArrayDataSource(char[] chars) {
        this.chars = chars;
    }

    public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        try {
            writer.write(chars);
        } catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
    
    public XMLStreamReader getReader() throws XMLStreamException {
        CharArrayReader reader = new CharArrayReader(chars);
        
        return StAXUtils.createXMLStreamReader(reader);                                                                   
    }
    
    
    public Object getObject() {
       return chars;
    }

    public boolean isDestructiveRead() {
        // Reading chars is not destructive
        return false;
    }

    public boolean isDestructiveWrite() {
        // Writing chars is not destructive
        return false;
    }

    public byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        
        String text = new String(chars);
        return text.getBytes(encoding);
    }
    
    public void close() {
        chars = null;
    }

    public OMDataSourceExt copy() {
        // Return shallow copy
        return new CharArrayDataSource(chars);
    }
}
