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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import org.apache.axiom.om.util.StAXUtils;

/**
 * Base class for {@link OMDataSourceExt} implementations that can easily produce the content as an
 * {@link XMLStreamReader} and that don't implement any kind of optimization for serializing the
 * content.
 */
public abstract class AbstractPullOMDataSource implements OMDataSourceExt {
    private HashMap properties = null;

    public final Object getProperty(String key) {
        return properties == null ? null : properties.get(key);
    }

    public final boolean hasProperty(String key) {
        return properties != null && properties.containsKey(key);
    }

    public final Object setProperty(String key, Object value) {
        if (properties == null) {
            properties = new HashMap();
        }
        return properties.put(key, value);
    }
    
    public final boolean isDestructiveWrite() {
        // Since we serialize by copying the events from the XMLStreamReader returned by getReader(),
        // obviously write is destructive if and only if read is destructiveñ
        return isDestructiveRead();
    }

    public final void serialize(OutputStream out, OMOutputFormat format) throws XMLStreamException {
        XMLStreamWriter writer = new MTOMXMLStreamWriter(out, format);
        serialize(writer);
        writer.flush();
    }

    public final void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter xmlWriter =
            new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer));
        xmlWriter.setOutputFormat(format);
        serialize(xmlWriter);
        xmlWriter.flush();
    }

    public final void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        serializer.serialize(getReader(), xmlWriter);
    }

    public final byte[] getXMLBytes(String encoding) throws UnsupportedEncodingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OMOutputFormat format = new OMOutputFormat();
        format.setCharSetEncoding(encoding);
        try {
            serialize(baos, format);
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
        return baos.toByteArray();
    }
    
    public final InputStream getXMLInputStream(String encoding) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(getXMLBytes(encoding));
    }

    // 
    // Default implementations that may be overridden by subclasses
    //
    
    public Object getObject() {
        return null;
    }

    public void close() {
    }

    public OMDataSourceExt copy() {
        throw new UnsupportedOperationException();
    }
}
