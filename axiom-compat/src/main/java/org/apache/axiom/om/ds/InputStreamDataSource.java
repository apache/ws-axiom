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

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
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

/**
 * @deprecated This class has been deprecated without replacement. Generally there is no need for
 *             {@link OMDataSource} instances that are pull based (i.e. use an
 *             {@link XMLStreamReader}) and that are destructive. For these scenarios, simply use
 *             {@link OMXMLBuilderFactory} to create an {@link OMElement} and add it to the tree.
 *             Since this operation will not expand the element (in Axiom 1.3.x), the result will be
 *             the same as using an {@link OMSourcedElement} with a destructive {@link OMDataSource}.
 */
public class InputStreamDataSource extends OMDataSourceExtBase {

    Data data = null;
    private static final int BUFFER_LEN = 4096;
    
    /**
     * Constructor
     * @param is 
     * @param encoding
     */
    public InputStreamDataSource(InputStream is, String encoding) {
        data = new Data();
        data.is = is;
        data.encoding = encoding;
    }
   
    @Override
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

    @Override
    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        if (data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        super.serialize(xmlWriter);
    }
    
    @Override
    public XMLStreamReader getReader() throws XMLStreamException {
        if (data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return StAXUtils.createXMLStreamReader(data.is,data.encoding);                                                                       
    }
    
    @Override
    public InputStream getXMLInputStream(String encoding)  throws 
        UnsupportedEncodingException{
        if (data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return data.is;
    }

    @Override
    public Object getObject() {
       return data;
    }

    @Override
    public boolean isDestructiveRead() {
        if (data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        return true;
    }

    @Override
    public boolean isDestructiveWrite() {
        if (data == null) {
            throw new OMException("The InputStreamDataSource does not have a backing object");
        }
        // Writing an input stream is destructive
        return true;
    }

    @Override
    public byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        
        // Return the byte array directly if it is the same encoding
        // Otherwise convert the bytes to the proper encoding
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OMOutputFormat format = new OMOutputFormat();
        format.setCharSetEncoding(encoding);
        try {
            serialize(baos, format);
        } catch (XMLStreamException e) {
            throw new OMException(e);
        }
        return baos.toByteArray();
    }
    
    @Override
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
    @Override
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
     * Object containing the InputStream/encoding pair
     */
    public class Data {
        public String encoding;
        public InputStream is;
    }
}
