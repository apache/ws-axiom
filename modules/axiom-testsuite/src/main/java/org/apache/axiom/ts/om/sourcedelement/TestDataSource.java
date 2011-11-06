/*
 * Copyright 2009-2011 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.axiom.ts.om.sourcedelement;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import org.apache.axiom.om.util.StAXUtils;

class TestDataSource implements OMDataSource {
    // The data source is a ByteArrayInputStream so that we can verify that the datasource 
    // is only accessed once.  Currently there is no way to identify a destructive vs. non-destructive OMDataSource.
    private final ByteArrayInputStream data;

    TestDataSource(String data) {
        this.data = new ByteArrayInputStream(data.getBytes());
        this.data.mark(0);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMDataSource#serialize(java.io.OutputStream, org.apache.axiom.om.OMOutputFormat)
     */
    public void serialize(OutputStream output, OMOutputFormat format)
            throws XMLStreamException {
        try {
            output.write(getBytes());
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMDataSource#serialize(java.io.Writer, org.apache.axiom.om.OMOutputFormat)
     */
    public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        try {
            writer.write(getString());
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMDataSource#serialize(javax.xml.stream.XMLStreamWriter)
     */
    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        serializer.serialize(getReader(), xmlWriter);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMDataSource#getReader()
     */
    public XMLStreamReader getReader() throws XMLStreamException {
        return StAXUtils.createXMLStreamReader(new StringReader(getString()));
    }

    private byte[] getBytes() throws XMLStreamException {
        try {
            // The data from the data source should only be accessed once
            //data.reset();
            byte[] rc = new byte[data.available()];
            data.read(rc);
            return rc;
        } catch (IOException io) {
            throw new XMLStreamException(io);
        }
    }

    private String getString() throws XMLStreamException {
        String text = new String(getBytes());
        return text;
    }
}