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

package org.apache.axiom.om.impl.stream.ds;

import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.core.stream.DocumentElementExtractingFilterHandler;
import org.apache.axiom.core.stream.NamespaceRepairingFilterHandler;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlHandlerWrapper;
import org.apache.axiom.core.stream.serializer.Serializer;
import org.apache.axiom.core.stream.stax.push.input.XmlHandlerStreamWriter;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.stream.XmlDeclarationRewriterHandler;
import org.apache.axiom.om.impl.stream.XsiTypeFilterHandler;
import org.apache.axiom.om.impl.stream.xop.XOPEncodingFilterHandler;
import org.apache.axiom.om.impl.stream.xop.XOPHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class MTOMXMLStreamWriterImpl extends MTOMXMLStreamWriter {
    private static final Log log = LogFactory.getLog(MTOMXMLStreamWriterImpl.class);
    private final XMLStreamWriter xmlWriter;
    private final OMOutputFormat format;

    MTOMXMLStreamWriterImpl(XMLStreamWriter xmlWriter, OMOutputFormat format) {
        this.xmlWriter = xmlWriter;
        if (log.isDebugEnabled()) {
            log.debug("Creating MTOMXMLStreamWriter");
            log.debug("OMFormat = " + format.toString());
        }
        this.format = format;
    }

    /**
     * Get the {@link XmlHandler} events are serialized to.
     *
     * @return the {@link XmlHandler} or {@code null} if the {@link XMLStreamWriter} is not
     *     connected to a {@link XmlHandler} (e.g. because the {@link XMLStreamWriter} is user
     *     supplied)
     */
    private XmlHandler getHandler() {
        XMLStreamWriter xmlWriter = this.xmlWriter;
        if (xmlWriter instanceof PushOMDataSourceStreamWriter) {
            xmlWriter = ((PushOMDataSourceStreamWriter) xmlWriter).getParent();
        }
        if (xmlWriter instanceof XmlHandlerStreamWriter) {
            return ((XmlHandlerStreamWriter) xmlWriter).getHandler();
        } else {
            return null;
        }
    }

    @Override
    public void writeStartElement(String string) throws XMLStreamException {
        xmlWriter.writeStartElement(string);
    }

    @Override
    public void writeStartElement(String string, String string1) throws XMLStreamException {
        xmlWriter.writeStartElement(string, string1);
    }

    @Override
    public void writeStartElement(String string, String string1, String string2)
            throws XMLStreamException {
        xmlWriter.writeStartElement(string, string1, string2);
    }

    @Override
    public void writeEmptyElement(String string, String string1) throws XMLStreamException {
        xmlWriter.writeEmptyElement(string, string1);
    }

    @Override
    public void writeEmptyElement(String string, String string1, String string2)
            throws XMLStreamException {
        xmlWriter.writeEmptyElement(string, string1, string2);
    }

    @Override
    public void writeEmptyElement(String string) throws XMLStreamException {
        xmlWriter.writeEmptyElement(string);
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        xmlWriter.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        log.debug("writeEndDocument");
        xmlWriter.writeEndDocument();
    }

    @Override
    public void close() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    /** Flush is overridden to trigger the attachment serialization */
    @Override
    public void flush() throws XMLStreamException {
        log.debug("Calling MTOMXMLStreamWriter.flush");
        xmlWriter.flush();
    }

    @Override
    public void writeAttribute(String string, String string1) throws XMLStreamException {
        xmlWriter.writeAttribute(string, string1);
    }

    @Override
    public void writeAttribute(String string, String string1, String string2, String string3)
            throws XMLStreamException {
        xmlWriter.writeAttribute(string, string1, string2, string3);
    }

    @Override
    public void writeAttribute(String string, String string1, String string2)
            throws XMLStreamException {
        xmlWriter.writeAttribute(string, string1, string2);
    }

    @Override
    public void writeNamespace(String string, String string1) throws XMLStreamException {
        xmlWriter.writeNamespace(string, string1);
    }

    @Override
    public void writeDefaultNamespace(String string) throws XMLStreamException {
        xmlWriter.writeDefaultNamespace(string);
    }

    @Override
    public void writeComment(String string) throws XMLStreamException {
        xmlWriter.writeComment(string);
    }

    @Override
    public void writeProcessingInstruction(String string) throws XMLStreamException {
        xmlWriter.writeProcessingInstruction(string);
    }

    @Override
    public void writeProcessingInstruction(String string, String string1)
            throws XMLStreamException {
        xmlWriter.writeProcessingInstruction(string, string1);
    }

    @Override
    public void writeCData(String string) throws XMLStreamException {
        xmlWriter.writeCData(string);
    }

    @Override
    public void writeDTD(String string) throws XMLStreamException {
        xmlWriter.writeDTD(string);
    }

    @Override
    public void writeEntityRef(String string) throws XMLStreamException {
        xmlWriter.writeEntityRef(string);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        xmlWriter.writeStartDocument();
    }

    @Override
    public void writeStartDocument(String string) throws XMLStreamException {
        xmlWriter.writeStartDocument(string);
    }

    @Override
    public void writeStartDocument(String string, String string1) throws XMLStreamException {
        xmlWriter.writeStartDocument(string, string1);
    }

    @Override
    public void writeCharacters(String string) throws XMLStreamException {
        xmlWriter.writeCharacters(string);
    }

    @Override
    public void writeCharacters(char[] chars, int i, int i1) throws XMLStreamException {
        xmlWriter.writeCharacters(chars, i, i1);
    }

    @Override
    public String getPrefix(String string) throws XMLStreamException {
        return xmlWriter.getPrefix(string);
    }

    @Override
    public void setPrefix(String string, String string1) throws XMLStreamException {
        xmlWriter.setPrefix(string, string1);
    }

    @Override
    public void setDefaultNamespace(String string) throws XMLStreamException {
        xmlWriter.setDefaultNamespace(string);
    }

    @Override
    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        xmlWriter.setNamespaceContext(namespaceContext);
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return xmlWriter.getNamespaceContext();
    }

    @Override
    public Object getProperty(String string) throws IllegalArgumentException {
        return xmlWriter.getProperty(string);
    }

    @Override
    public boolean isOptimized() {
        return format.isOptimized();
    }

    @Override
    public String prepareDataHandler(DataHandler dataHandler) {
        XmlHandler handler = getHandler();
        while (handler instanceof XmlHandlerWrapper) {
            if (handler instanceof XOPHandler) {
                return ((XOPHandler) handler).prepareDataHandler(dataHandler);
            }
            handler = ((XmlHandlerWrapper) handler).getParent();
        }
        // TODO: hack for compatibility with Axis2
        // If we don't serialize to a MIME package, we should return null here because the
        // DataHandler will never be serialized and the only meaningful thing to do is to
        // base64 encode it. However, there is code in Axis2 that expects an XOP encoded
        // message without bothering about the MIME parts referenced by the xop:Include
        // elements...
        return format.getNextContentId();
    }

    @Override
    public String getCharSetEncoding() {
        return format.getCharSetEncoding();
    }

    @Override
    public OMOutputFormat getOutputFormat() {
        return format;
    }

    @Override
    public OutputStream getOutputStream() throws XMLStreamException {
        OutputStream outputStream;
        XmlHandler handler = getHandler();
        // Remove wrappers that can be safely removed
        while (handler instanceof DocumentElementExtractingFilterHandler
                || handler instanceof NamespaceRepairingFilterHandler
                || handler instanceof XsiTypeFilterHandler
                || handler instanceof XmlDeclarationRewriterHandler
                || handler instanceof XOPEncodingFilterHandler) {
            handler = ((XmlHandlerWrapper) handler).getParent();
        }
        if (handler instanceof Serializer) {
            try {
                outputStream = ((Serializer) handler).getOutputStream();
            } catch (StreamException ex) {
                throw new XMLStreamException(ex);
            }
        } else {
            outputStream = null;
        }

        if (log.isDebugEnabled()) {
            if (outputStream == null) {
                log.debug("Direct access to the output stream is not available.");
            } else {
                log.debug("Returning access to the output stream: " + outputStream);
            }
        }
        return outputStream;
    }
}
