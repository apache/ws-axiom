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
package org.apache.axiom.om.impl.common.serializer.push.sax;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.util.base64.Base64EncodingWriterOutputStream;
import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

final class ContentHandlerXMLStreamWriter implements XMLStreamWriter, DataHandlerWriter {
    private final SAXHelper helper;
    private final ContentHandler contentHandler;
    private final LexicalHandler lexicalHandler;
    
    /**
     * The namespace context of the {@link XMLStreamWriter}. This namespace context is inherited
     * from the {@link SAXSerializer}.
     */
    private final ScopedNamespaceContext writerNsContext;

    /**
     * Tracks the namespace declarations actually written using
     * {@link XMLStreamWriter#writeNamespace(String, String)} and
     * {@link XMLStreamWriter#writeDefaultNamespace(String)}. Note that the
     * {@link ScopedNamespaceContext} is actually not used as a {@link NamespaceContext}, but merely
     * to remember the namespace declarations. This is necessary to generate the necessary
     * {@link ContentHandler#endPrefixMapping(String)} events.
     */
    private final ScopedNamespaceContext outputNsContext = new ScopedNamespaceContext();

    ContentHandlerXMLStreamWriter(SAXHelper helper, ContentHandler contentHandler, LexicalHandler lexicalHandler,
            ScopedNamespaceContext nsContext) {
        this.helper = helper;
        this.contentHandler = contentHandler;
        this.lexicalHandler = lexicalHandler;
        writerNsContext = nsContext;
    }

    private static String normalize(String s) {
        return s == null ? "" : s;
    }
    
    private String internalGetPrefix(String namespaceURI) throws XMLStreamException {
        String prefix = writerNsContext.getPrefix(namespaceURI);
        if (prefix == null) {
            throw new XMLStreamException("Unbound namespace URI '" + namespaceURI + "'");
        } else {
            return prefix;
        }
    }
    
    public Object getProperty(String name) throws IllegalArgumentException {
        if (name.equals(DataHandlerWriter.PROPERTY)) {
            return this;
        } else {
            return null;
        }
    }

    public NamespaceContext getNamespaceContext() {
        return writerNsContext;
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        writerNsContext.setPrefix(normalize(prefix), normalize(uri));
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        writerNsContext.setPrefix("", normalize(uri));
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return writerNsContext.getPrefix(uri);
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        finishStartElementIfNecessary();
        helper.beginStartElement(normalize(prefix), normalize(namespaceURI), localName);
        writerNsContext.startScope();
        outputNsContext.startScope();
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        writeStartElement(internalGetPrefix(namespaceURI), localName, namespaceURI);
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        finishStartElementIfNecessary();
        helper.beginStartElement(normalize(prefix), normalize(namespaceURI), localName);
        try {
            helper.finishStartElement(contentHandler);
            helper.writeEndElement(contentHandler, null);
        } catch (SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        writeEmptyElement(internalGetPrefix(namespaceURI), localName, namespaceURI);
    }

    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        prefix = normalize(prefix);
        namespaceURI = normalize(namespaceURI);
        outputNsContext.setPrefix(prefix, namespaceURI);
        try {
            contentHandler.startPrefixMapping(prefix, namespaceURI);
        } catch (SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }

    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        namespaceURI = normalize(namespaceURI);
        outputNsContext.setPrefix("", namespaceURI);
        try {
            contentHandler.startPrefixMapping("", namespaceURI);
        } catch (SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        helper.addAttribute(normalize(prefix), normalize(namespaceURI), localName, "CDATA", value);
    }

    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        helper.addAttribute(internalGetPrefix(namespaceURI), normalize(namespaceURI), localName, "CDATA", value);
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        helper.addAttribute("", "", localName, "CDATA", value);
    }

    private void finishStartElementIfNecessary() throws XMLStreamException {
        if (helper.isInStartElement()) {
            try {
                helper.finishStartElement(contentHandler);
            } catch (SAXException ex) {
                throw new SAXExceptionWrapper(ex);
            }
        }
    }
    
    public void writeEndElement() throws XMLStreamException {
        finishStartElementIfNecessary();
        try {
            helper.writeEndElement(contentHandler, outputNsContext);
            writerNsContext.endScope();
        } catch (SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }

    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        finishStartElementIfNecessary();
        try {
            contentHandler.characters(text, start, len);
        } catch (SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }

    public void writeCharacters(String text) throws XMLStreamException {
        char[] ch = text.toCharArray();
        writeCharacters(ch, 0, ch.length);
    }

    public void writeCData(String data) throws XMLStreamException {
        finishStartElementIfNecessary();
        try {
            if (lexicalHandler != null) {
                lexicalHandler.startCDATA();
            }
            char[] ch = data.toCharArray();
            contentHandler.characters(ch, 0, ch.length);
            if (lexicalHandler != null) {
                lexicalHandler.endCDATA();
            }
        } catch (SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }

    public void writeDataHandler(DataHandler dataHandler, String contentID, boolean optimize)
            throws IOException, XMLStreamException {
        finishStartElementIfNecessary();
        Base64EncodingWriterOutputStream out = new Base64EncodingWriterOutputStream(new ContentHandlerWriter(contentHandler), 4096, true);
        dataHandler.writeTo(out);
        out.complete();
    }

    public void writeDataHandler(DataHandlerProvider dataHandlerProvider, String contentID,
            boolean optimize) throws IOException, XMLStreamException {
        writeDataHandler(dataHandlerProvider.getDataHandler(), contentID, optimize);
    }

    public void writeComment(String data) throws XMLStreamException {
        finishStartElementIfNecessary();
        if (lexicalHandler != null) {
            try {
                char[] ch = data.toCharArray();
                lexicalHandler.comment(ch, 0, ch.length);
            } catch (SAXException ex) {
                throw new SAXExceptionWrapper(ex);
            }
        }
    }

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        finishStartElementIfNecessary();
        try {
            contentHandler.processingInstruction(target, "");
        } catch (SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        finishStartElementIfNecessary();
        try {
            contentHandler.processingInstruction(target, data);
        } catch (SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }

    public void writeEntityRef(String name) throws XMLStreamException {
        finishStartElementIfNecessary();
        try {
            contentHandler.skippedEntity(name);
        } catch (SAXException ex) {
            throw new SAXExceptionWrapper(ex);
        }
    }

    public void flush() throws XMLStreamException {
    }

    public void close() throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT call XMLStreamWriter#close()");
    }

    public void writeStartDocument() throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartDocument()");
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartDocument(String)");
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartDocument(String, String)");
    }

    public void writeEndDocument() throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeEndDocument()");
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartElement(String)");
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeEmptyElement(String)");
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void writeDTD(String dtd) throws XMLStreamException {
        // TODO
        throw new UnsupportedOperationException();
    }
}
