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
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMOutputFormat;

/**
 * Base class for {@link OMDataSourceExt} implementations. This class should only be used by data
 * sources that can equally well produce an {@link XMLStreamReader} and have a meaningful
 * implementation of {@link OMDataSource#serialize(XMLStreamWriter)}. Most implementations should
 * actually use {@link AbstractPullOMDataSource} or {@link AbstractPushOMDataSource}.
 */
public abstract class AbstractOMDataSource implements OMDataSourceExt {
    private Map<String, Object> properties;

    @Override
    public final Object getProperty(String key) {
        return properties == null ? null : properties.get(key);
    }

    @Override
    public final boolean hasProperty(String key) {
        return properties != null && properties.containsKey(key);
    }

    @Override
    public final Object setProperty(String key, Object value) {
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        return properties.put(key, value);
    }

    // Note: this method is never executed by Axiom itself
    @Override
    public final void serialize(OutputStream out, OMOutputFormat format) throws XMLStreamException {
        OMAbstractFactory.getOMFactory().createOMElement(this).serializeAndConsume(out, format);
    }

    // Note: this method is never executed by Axiom itself
    @Override
    public final void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        OMAbstractFactory.getOMFactory().createOMElement(this).serializeAndConsume(writer, format);
    }

    @Override
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

    @Override
    public final InputStream getXMLInputStream(String encoding)
            throws UnsupportedEncodingException {
        return new ByteArrayInputStream(getXMLBytes(encoding));
    }

    //
    // Default implementations that may be overridden by subclasses
    //

    @Override
    public Object getObject() {
        return null;
    }

    @Override
    public void close() {}

    @Override
    public OMDataSourceExt copy() {
        return null;
    }
}
