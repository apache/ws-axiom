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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * OMDataSourceExtBase is a convenient base class that can be extended
 * by other OMDataSourceExt implementations.
 * 
 * @deprecated As described in <a
 *             href="https://issues.apache.org/jira/browse/AXIOM-419">AXIOM-419</a>, this class has
 *             multiple issues and should no longer be used as a base class for {@link OMDataSource}
 *             implementations. Instead, use {@link AbstractOMDataSource},
 *             {@link AbstractPullOMDataSource} or {@link AbstractPushOMDataSource}.
 */
public abstract class OMDataSourceExtBase implements OMDataSourceExt {

    private static final Log log = LogFactory.getLog(OMDataSourceExtBase.class);
	
    private Map<String,Object> map;  // Map of properties

    @Override
    public Object getProperty(String key) {
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    @Override
    public Object setProperty(String key, Object value) {
        if (map == null) {
            map = new HashMap<String,Object>();
        }
        return map.put(key, value);
    }
    
    @Override
    public boolean hasProperty(String key) {
        if (map == null) {
            return false;
        } 
        return map.containsKey(key);
    }
   
    @Override
    public InputStream getXMLInputStream(String encoding)  throws 
        UnsupportedEncodingException{
        if (log.isDebugEnabled()) {
            log.debug("getXMLInputStream encoding="+encoding);
        }
        return new ByteArrayInputStream(getXMLBytes(encoding));
    }

    @Override
    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("serialize output="+output+" format="+format);
        }
        try {
            // Write bytes to the output stream
            output.write(getXMLBytes(format.getCharSetEncoding()));
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("serialize writer="+writer+" format="+format);
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

    @Override
    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("serialize xmlWriter="+xmlWriter);
        }
        // Some XMLStreamWriters (e.g. MTOMXMLStreamWriter) 
        // provide direct access to the OutputStream.  
        // This allows faster writing.
        OutputStream os = getOutputStream(xmlWriter);
        if (os != null) {
        	if (log.isDebugEnabled()) {
                log.debug("serialize OutputStream optimisation: true");
            }
            String encoding = getCharacterEncoding(xmlWriter);
            OMOutputFormat format = new OMOutputFormat();
            format.setCharSetEncoding(encoding);
            serialize(os, format);
        } else {
        	if (log.isDebugEnabled()) {
                log.debug("serialize OutputStream optimisation: false");
            }
            // Read the bytes into a reader and 
            // write to the writer.
            XMLStreamReader xmlReader = getReader();
            reader2writer(xmlReader, xmlWriter);
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
                                     XMLStreamWriter writer) throws XMLStreamException {
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXOMBuilder(reader);
        try {
            OMDocument omDocument = builder.getDocument();
            Iterator<OMNode> it = omDocument.getChildren();
            while (it.hasNext()) {
                // TODO: this is extremely inefficient since next() will actually build the node!
                OMNode omNode = it.next();
                // TODO: quick fix required because OMChildrenIterator#next() no longer builds the node
                omNode.getNextOMSibling();
                omNode.serializeAndConsume(writer);
            }
        } finally {
            builder.close();
        }
    }
    
    /**
     * Some XMLStreamWriters expose an OutputStream that can be
     * accessed directly.
     * @return OutputStream or null
     */
    private static OutputStream getOutputStream(XMLStreamWriter writer) 
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
    private static String getCharacterEncoding(XMLStreamWriter writer) {
        if (writer instanceof MTOMXMLStreamWriter) {
            return ((MTOMXMLStreamWriter) writer).getCharSetEncoding();
        }
        return null;
    }
}
