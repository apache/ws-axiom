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
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.serializer.Serializer;
import org.apache.axiom.core.stream.serializer.writer.UnmappableCharacterHandler;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class XmlHandlerStreamWriter implements XMLStreamWriter, DataHandlerWriter {
    private static final Log log = LogFactory.getLog(XmlHandlerStreamWriter.class);

    private final XmlHandler handler;
    private final Serializer serializer;
    private final ScopedNamespaceContext namespaceContext = new ScopedNamespaceContext();
    private boolean inStartElement;
    private boolean inEmptyElement;

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
    
    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        if (DataHandlerWriter.PROPERTY.equals(name)) {
            return this;
        } else {
            throw new IllegalArgumentException("Unsupported property " + name);
        }
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        // We currently don't support this method
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException {
        return namespaceContext.getPrefix(uri);
    }

    private void internalSetPrefix(String prefix, String uri) {
        if (inEmptyElement) {
            log.warn("The behavior of XMLStreamWriter#setPrefix and " +
                    "XMLStreamWriter#setDefaultNamespace is undefined when invoked in the " +
                    "context of an empty element");
        }
        namespaceContext.setPrefix(prefix, uri);
    }
    
    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        internalSetPrefix("", uri);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        internalSetPrefix(prefix, uri);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        try {
            handler.startDocument(null, "1.0", null, null);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        try {
            handler.startDocument(null, version, encoding, null);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException {
        try {
            handler.startDocument(null, version, null, null);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        try {
            handler.completed();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException {
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

    private void doWriteStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        finishStartElement();
        try {
            handler.startElement(normalize(namespaceURI), localName, normalize(prefix));
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        inStartElement = true;
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException {
        doWriteStartElement(prefix, localName, namespaceURI);
        namespaceContext.startScope();
        inEmptyElement = false;
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    private void doWriteEndElement() throws XMLStreamException {
        finishStartElement();
        try {
            handler.endElement();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        doWriteEndElement();
        namespaceContext.endScope();
        inEmptyElement = false;
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
    
    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException {
        doWriteStartElement(prefix, localName, namespaceURI);
        finishStartElement();
        doWriteEndElement();
        inEmptyElement = true;
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
            throws XMLStreamException {
        try {
            handler.processAttribute(normalize(namespaceURI), localName, normalize(prefix), value, OMConstants.XMLATTRTYPE_CDATA, true);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        writeAttribute(null, null, localName, value);
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        prefix = normalize(prefix);
        namespaceURI = normalize(namespaceURI);
        try {
            handler.processNamespaceDeclaration(prefix, namespaceURI);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        namespaceContext.setPrefix(prefix, namespaceURI);
    }

    @Override
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        writeNamespace("", namespaceURI);
    }

    private String internalGetPrefix(String namespaceURI) throws XMLStreamException {
        String prefix = namespaceContext.getPrefix(namespaceURI);
        if (prefix == null) {
            throw new XMLStreamException("Unbound namespace URI '" + namespaceURI + "'");
        } else {
            return prefix;
        }
    }
    
    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        writeStartElement(internalGetPrefix(namespaceURI), localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName)
            throws XMLStreamException {
        writeEmptyElement(internalGetPrefix(namespaceURI), localName, namespaceURI);
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value)
            throws XMLStreamException {
        writeAttribute(internalGetPrefix(namespaceURI), namespaceURI, localName, value);
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        writeCharacters(new String(text, start, len));
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        finishStartElement();
        try {
            handler.processCharacterData(text, false);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        inEmptyElement = false;
    }

    @Override
    public void writeCData(String data) throws XMLStreamException {
        finishStartElement();
        try {
            handler.startCDATASection();
            handler.processCharacterData(data, false);
            handler.endCDATASection();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        inEmptyElement = false;
    }

    @Override
    public void writeComment(String data) throws XMLStreamException {
        finishStartElement();
        try {
            handler.startComment();
            handler.processCharacterData(data, false);
            handler.endComment();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        inEmptyElement = false;
    }

    @Override
    public void writeEntityRef(String name) throws XMLStreamException {
        finishStartElement();
        try {
            handler.processEntityReference(name, null);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        inEmptyElement = false;
    }

    @Override
    public void writeProcessingInstruction(String target, String data)
            throws XMLStreamException {
        finishStartElement();
        try {
            handler.startProcessingInstruction(target);
            handler.processCharacterData(data, false);
            handler.endProcessingInstruction();
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
        inEmptyElement = false;
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        writeProcessingInstruction(target, "");
    }

    @Override
    public void flush() throws XMLStreamException {
        if (serializer != null) {
            try {
                serializer.flushBuffer();
            } catch (StreamException ex) {
                throw toXMLStreamException(ex);
            }
        }
    }

    @Override
    public void close() throws XMLStreamException {
        flush();
    }

    @Override
    public void writeDataHandler(DataHandler dataHandler, String contentID, boolean optimize)
            throws IOException, XMLStreamException {
        finishStartElement();
        try {
            handler.processCharacterData(new TextContent(contentID, dataHandler, optimize), false);
        } catch (StreamException ex) {
            throw toXMLStreamException(ex);
        }
    }

    @Override
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
