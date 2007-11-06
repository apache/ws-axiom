/*
 * Copyright 2004,20057 The Apache Software Foundation.
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
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.StAXUtils;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;

/**
 * InputStream is an example OMDataSourceExt.
 * Use it to insert a (InputStream, encoding) into an OM Tree.
 * This data source is useful for placing an InputStream into an OM
 * tree, instead of having a deeply nested tree.
 */
public class InputStreamDataSource implements OMDataSourceExt {

    Data data = null;
    private static final int BUFFER_LEN = 4096;
    
    HashMap map = null;  // Map of properties
    
    /**
     * Constructor
     * @param bytes 
     * @param encoding
     */
    public InputStreamDataSource(InputStream is, String encoding) {
        data = new Data();
        data.is = is;
        data.encoding = encoding;
    }
   
    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        if (data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        String encoding = format.getCharSetEncoding();
        try {
            if (!data.encoding.equalsIgnoreCase(encoding)) {
                byte[] bytes = getXMLBytes(encoding);
                output.write(bytes);
            } else {
                // Write the input stream to the output stream
                inputStream2OutputStream(data.is, output);
            }
        } catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        if (data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        try {
            // Convert the bytes into a String and write it to the Writer
            String text = new String(getXMLBytes(format.getCharSetEncoding()));
            writer.write(text);
        } catch (UnsupportedEncodingException e) {
            throw new XMLStreamException(e);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        if (data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        
        // Some XMLStreamWriters (e.g. MTOMXMLStreamWriter) 
        // provide direct access to the OutputStream.  
        // This allows faster writing.
        OutputStream os = getOutputStream(xmlWriter);
        if (os != null) {
            String encoding = getCharacterEncoding(xmlWriter);
            OMOutputFormat format = new OMOutputFormat();
            format.setCharSetEncoding(encoding);
            serialize(os, format);
        } else {
            // Read the bytes into a reader and 
            // write to the writer.
            XMLStreamReader xmlReader = getReader();
            reader2writer(xmlReader, xmlWriter);
        }
    }
    
    public XMLStreamReader getReader() throws XMLStreamException {
        if (data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return StAXUtils.createXMLStreamReader(data.is,data.encoding);                                                                       
    }
    
    public InputStream getXMLInputStream(String encoding)  throws 
        UnsupportedEncodingException{
        if (data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return data.is;
    }

    public Object getObject() {
       return data;
    }

    public boolean isDestructiveRead() {
        if (data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return true;
    }

    public boolean isDestructiveWrite() {
        if (data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        // Writing an input stream is destructive
        return true;
    }

    public byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        
        // Return the byte array directly if it is the same encoding
        // Otherwise convert the bytes to the proper encoding
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OMOutputFormat format = new OMOutputFormat();
        format.setCharSetEncoding(encoding);
        try {
            serialize(baos, format);
        } catch (XMLStreamException e) {
            new OMException(e);
        }
        return baos.toByteArray();
    }
    
    public void close() {
        if (data.is != null) {
            try {
                data.is.close();
            } catch (IOException e) {
                throw new OMException(e);
            }
            data.is = null;
        }
    }

    /**
     * Return a InputStreamDataSource backed by a ByteArrayInputStream
     */
    public OMDataSourceExt copy() {
        byte[] bytes;
        try {
            bytes = getXMLBytes(data.encoding);
        } catch (UnsupportedEncodingException e) {
            throw new OMException(e);
        }
        InputStream is1 = new ByteArrayInputStream(bytes);
        InputStream is2 = new ByteArrayInputStream(bytes);
        data.is = is1;
        return new InputStreamDataSource(is2, data.encoding);
    }
    
    /**
     * Some XMLStreamWriters expose an OutputStream that can be
     * accessed directly.
     * @return OutputStream or null
     */
    private OutputStream getOutputStream(XMLStreamWriter writer) 
     throws XMLStreamException {
        if (writer instanceof MTOMXMLStreamWriter) {
            return ((MTOMXMLStreamWriter) writer).getOutputStream();
        }
        return null;
    }
    
    /**
     * Get the character set encoding of the XMLStreamWriter
     * @return String or null
     */
    private String getCharacterEncoding(XMLStreamWriter writer) {
        if (writer instanceof MTOMXMLStreamWriter) {
            return ((MTOMXMLStreamWriter) writer).getCharSetEncoding();
        }
        return null;
    }
     
    /**
     * Private utility to write the InputStream contents to the OutputStream.
     * @param is
     * @param os
     * @throws IOException
     */
    private static void inputStream2OutputStream(InputStream is, 
                                                 OutputStream os)
    throws IOException {
        byte[] buffer = new byte[BUFFER_LEN];
        int bytesRead = is.read(buffer);
        while (bytesRead > 0) {
            os.write(buffer, 0, bytesRead);
            bytesRead = is.read(buffer);
        }
    }
    
    /**
     * Simple utility that takes an XMLStreamReader and writes it
     * to an XMLStreamWriter
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    private static void reader2writer(XMLStreamReader reader, 
                                     XMLStreamWriter writer)
    throws XMLStreamException {
        StAXOMBuilder builder = new StAXOMBuilder(reader);
        builder.releaseParserOnClose(true);
        try {
            OMDocument omDocument = builder.getDocument();
            Iterator it = omDocument.getChildren();
            while (it.hasNext()) {
                OMNode omNode = (OMNode) it.next();
                omNode.serializeAndConsume(writer);
            }
        } finally {
            builder.close();
        }
    }
    
    /**
     * Object containing the InputStream/encoding pair
     */
    public class Data {
        public String encoding;
        public InputStream is;
    }

    public Object getProperty(String key) {
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    public Object setProperty(String key, Object value) {
        if (map == null) {
            map = new HashMap();
        }
        return map.put(key, value);
    }
    
    public boolean hasProperty(String key) {
        if (map == null) {
            return false;
        } 
        return map.containsKey(key);
    }
}
