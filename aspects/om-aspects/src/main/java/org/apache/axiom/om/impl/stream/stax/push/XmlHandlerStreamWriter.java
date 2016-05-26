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
package org.apache.axiom.om.impl.stream.stax.push;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.serializer.Serializer;
import org.apache.axiom.core.stream.serializer.writer.UnmappableCharacterHandler;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.util.stax.AbstractXMLStreamWriter;

public class XmlHandlerStreamWriter extends AbstractXMLStreamWriter implements DataHandlerWriter {
    private final XmlHandler handler;
    private final Serializer serializer;
    private boolean inStartElement;

    public XmlHandlerStreamWriter(XmlHandler handler, Serializer serializer) {
        this.handler = handler;
        this.serializer = serializer;
    }
    
    public XmlHandler getHandler() {
        return handler;
    }

    private static String normalize(String s) {
        return s == null ? "" : s;
    }
    
    private static XMLStreamException toXMLStreamException(StreamException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof XMLStreamException) {
            return (XMLStreamException)cause;
        } else {
            return new XMLStreamException(ex);
        }
    }
    
    public Object getProperty(String name) throws IllegalArgumentException {
        if (DataHandlerWriter.PROPERTY.equals(name)) {
            return this;
        } else {
            throw new IllegalArgumentException("Unsupported property " + name);
        }
    }

    protected void doWriteStartDocument() throws XMLStreamException {
        try {
            handler.startDocument(null, "1.0", null, null);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    protected void doWriteStartDocument(String encoding, String version) throws XMLStreamException {
        try {
            handler.startDocument(null, version, encoding, null);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    protected void doWriteStartDocument(String version) throws XMLStreamException {
        try {
            handler.startDocument(null, version, null, null);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    protected void doWriteEndDocument() throws XMLStreamException {
        try {
            handler.completed();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    protected void doWriteDTD(String dtd) throws XMLStreamException {
        if (serializer != null) {
            try {
                serializer.writeRaw(dtd, UnmappableCharacterHandler.CONVERT_TO_CHARACTER_REFERENCE);
            } catch (StreamException ex) {
                throw toXMLStreamException(ex);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    protected void doWriteStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        finishStartElement();
        try {
            handler.startElement(normalize(namespaceURI), localName, normalize(prefix));
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        inStartElement = true;
    }

    protected void doWriteStartElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    protected void doWriteEndElement() throws XMLStreamException {
        finishStartElement();
        try {
            handler.endElement();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    private void finishStartElement() throws XMLStreamException {
        if (inStartElement) {
            try {
                handler.attributesCompleted();
            } catch (StreamException ex) {
                throw toXMLStreamException(ex);
            }
            inStartElement = false;
        }
    }
    
    protected void doWriteEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        doWriteStartElement(prefix, localName, namespaceURI);
        finishStartElement();
        doWriteEndElement();
    }

    protected void doWriteEmptyElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    protected void doWriteAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        try {
            handler.processAttribute(normalize(namespaceURI), localName, normalize(prefix), value, OMConstants.XMLATTRTYPE_CDATA, true);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    protected void doWriteAttribute(String localName, String value) throws XMLStreamException {
        doWriteAttribute(null, null, localName, value);
    }

    protected void doWriteNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        try {
            handler.processNamespaceDeclaration(normalize(prefix), normalize(namespaceURI));
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    protected void doWriteDefaultNamespace(String namespaceURI) throws XMLStreamException {
        doWriteNamespace(null, namespaceURI);
    }

    protected void doWriteCharacters(char[] text, int start, int len) throws XMLStreamException {
        doWriteCharacters(new String(text, start, len));
    }

    protected void doWriteCharacters(String text) throws XMLStreamException {
        finishStartElement();
        try {
            handler.processCharacterData(text, false);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    protected void doWriteCData(String data) throws XMLStreamException {
        finishStartElement();
        try {
            handler.startCDATASection();
            handler.processCharacterData(data, false);
            handler.endCDATASection();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    protected void doWriteComment(String data) throws XMLStreamException {
        finishStartElement();
        try {
            handler.startComment();
            handler.processCharacterData(data, false);
            handler.endComment();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    protected void doWriteEntityRef(String name) throws XMLStreamException {
        finishStartElement();
        try {
            handler.processEntityReference(name, null);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    protected void doWriteProcessingInstruction(String piTarget, String data) throws XMLStreamException {
        finishStartElement();
        try {
            handler.startProcessingInstruction(piTarget);
            handler.processCharacterData(data, false);
            handler.endProcessingInstruction();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    protected void doWriteProcessingInstruction(String target) throws XMLStreamException {
        doWriteProcessingInstruction(target, "");
    }

    public void flush() throws XMLStreamException {
        if (serializer != null) {
            try {
                serializer.flushBuffer();
            } catch (StreamException ex) {
                throw toXMLStreamException(ex);
            }
        }
    }

    public void close() throws XMLStreamException {
        flush();
    }

    public void writeDataHandler(DataHandler dataHandler, String contentID, boolean optimize)
            throws IOException, XMLStreamException {
        finishStartElement();
        try {
            handler.processCharacterData(new TextContent(contentID, dataHandler, optimize), false);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    public void writeDataHandler(DataHandlerProvider dataHandlerProvider, String contentID,
            boolean optimize) throws IOException, XMLStreamException {
        finishStartElement();
        try {
            handler.processCharacterData(new TextContent(contentID, dataHandlerProvider, optimize), false);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }
}
