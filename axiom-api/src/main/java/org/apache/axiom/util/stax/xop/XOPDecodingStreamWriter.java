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
package org.apache.axiom.util.stax.xop;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.util.stax.wrapper.XMLStreamWriterWrapper;

/**
 * {@link XMLStreamWriter} wrapper that decodes XOP. It assumes that the underlying stream
 * implements the {@link DataHandlerWriter} extension and translates calls that write
 * <tt>xop:Include</tt> elements into calls to the appropriate methods defined by
 * {@link DataHandlerWriter}.
 */
public class XOPDecodingStreamWriter extends XMLStreamWriterWrapper {
    private final MimePartProvider mimePartProvider;
    private final DataHandlerWriter dataHandlerWriter;
    private boolean inXOPInclude;
    private String contentID;

    /**
     * Constructor.
     * 
     * @param parent
     *            the {@link XMLStreamWriter} to write the decoded stream to; the instance must
     *            implement the {@link DataHandlerWriter} extension
     * @param mimePartProvider
     *            an implementation of the {@link MimePartProvider} interface that will be used to
     *            load the {@link DataHandler} objects for MIME parts referenced by
     *            <tt>xop:Include</tt> element information items written to this wrapper
     */
    public XOPDecodingStreamWriter(XMLStreamWriter parent, MimePartProvider mimePartProvider) {
        super(parent);
        this.mimePartProvider = mimePartProvider;
        dataHandlerWriter = (DataHandlerWriter)parent.getProperty(DataHandlerWriter.PROPERTY);
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException {
        if (localName.equals(XOPConstants.INCLUDE)
                && namespaceURI.equals(XOPConstants.NAMESPACE_URI)) {
            inXOPInclude = true;
        } else {
            super.writeStartElement(prefix, localName, namespaceURI);
        }
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        if (localName.equals(XOPConstants.INCLUDE)
                && namespaceURI.equals(XOPConstants.NAMESPACE_URI)) {
            inXOPInclude = true;
        } else {
            super.writeStartElement(namespaceURI, localName);
        }
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
            throws XMLStreamException {
        if (inXOPInclude) {
            processAttribute(namespaceURI, localName, value);
        } else {
            super.writeAttribute(prefix, namespaceURI, localName, value);
        }
    }

    public void writeAttribute(String namespaceURI, String localName, String value)
            throws XMLStreamException {
        if (inXOPInclude) {
            processAttribute(namespaceURI, localName, value);
        } else {
            super.writeAttribute(namespaceURI, localName, value);
        }
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        if (inXOPInclude) {
            processAttribute(null, localName, value);
        } else {
            super.writeAttribute(localName, value);
        }
    }

    private void processAttribute(String namespaceURI, String localName, String value)
            throws XMLStreamException {
        if ((namespaceURI == null || namespaceURI.length() == 0)
                && localName.equals(XOPConstants.HREF)) {
            if (!value.startsWith("cid:")) {
                throw new XMLStreamException("Expected href attribute containing a URL in the " +
                        "cid scheme");
            }
            try {
                contentID = URLDecoder.decode(value.substring(4), "ascii");
            } catch (UnsupportedEncodingException ex) {
                // We should never get here
                throw new XMLStreamException(ex);
            }
        } else {
            throw new XMLStreamException("Expected xop:Include element information item with " +
                    "a (single) href attribute");
        }
    }
    
    public void writeEndElement() throws XMLStreamException {
        if (inXOPInclude) {
            if (contentID == null) {
                throw new XMLStreamException("Encountered an xop:Include element without " +
                		"href attribute");
            }
            // TODO: we should create a DataHandlerProvider if isLoaded returns false for the given contentID
            DataHandler dh;
            try {
                dh = mimePartProvider.getDataHandler(contentID);
            } catch (IOException ex) {
                throw new XMLStreamException("Error while fetching data handler", ex);
            }
            try {
                dataHandlerWriter.writeDataHandler(dh, contentID, true);
            } catch (IOException ex) {
                throw new XMLStreamException("Error while writing data handler", ex);
            }
            inXOPInclude = false;
            contentID = null;
        } else {
            super.writeEndElement();
        }
    }
}
