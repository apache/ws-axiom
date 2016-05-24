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

package org.apache.axiom.om.impl.stream.stax;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.attachments.lifecycle.DataHandlerExt;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.serializer.Serializer;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.OMMultipartWriter;
import org.apache.axiom.om.impl.stream.xop.CompletionListener;
import org.apache.axiom.om.impl.stream.xop.XOPEncodingFilterHandler;
import org.apache.axiom.om.util.CommonUtils;
import org.apache.axiom.om.util.XMLStreamWriterFilter;
import org.apache.axiom.util.io.IOUtils;
import org.apache.axiom.util.stax.xop.ContentIDGenerator;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MTOMXMLStreamWriterImpl extends MTOMXMLStreamWriter {
    /**
     * Stores a part that has been added without using the {@link DataHandlerWriter} API.
     */
    private static class Part {
        private final String contentID;
        private final DataHandler dataHandler;
        
        public Part(String contentID, DataHandler dataHandler) {
            this.contentID = contentID;
            this.dataHandler = dataHandler;
        }

        public String getContentID() {
            return contentID;
        }

        public DataHandler getDataHandler() {
            return dataHandler;
        }
    }
    
    private static final Log log = LogFactory.getLog(MTOMXMLStreamWriterImpl.class);
    private XMLStreamWriter xmlWriter;
    private List<Part> otherParts = new LinkedList<Part>();
    private OMOutputFormat format;
    private final OptimizationPolicy optimizationPolicy;
    
    // State variables
    private boolean isEndDocument = false; // has endElement been called
    private boolean isComplete = false;    // have the attachments been written
    private int depth = 0;                 // current element depth
    
    // Set the filter object if provided
    private XMLStreamWriterFilter xmlStreamWriterFilter  = null;

    public MTOMXMLStreamWriterImpl(XMLStreamWriter xmlWriter, OMOutputFormat format) {
        this.xmlWriter = xmlWriter;
        if (log.isTraceEnabled()) {
            log.trace("Call Stack =" + CommonUtils.callStackToString());
        }
        this.format = format;
        optimizationPolicy = new OptimizationPolicyImpl(format);
    }

    public MTOMXMLStreamWriterImpl(XMLStreamWriter xmlWriter) {
        this(xmlWriter, new OMOutputFormat());
    }
    
    public MTOMXMLStreamWriterImpl(OutputStream outStream, OMOutputFormat format)
            throws XMLStreamException, FactoryConfigurationError {
        this(outStream, format, true);
    }
    
    /**
     * Creates a new MTOMXMLStreamWriter with specified encoding.
     * 
     * @param outStream
     * @param format
     * @param preserveAttachments
     *            specifies whether attachments must be preserved or can be consumed (i.e. streamed)
     *            during serialization; if set to <code>false</code> then
     *            {@link DataHandlerExt#readOnce()} or an equivalent method may be used to get the
     *            data for an attachment
     * @throws XMLStreamException
     * @throws FactoryConfigurationError
     * @see OMOutputFormat#DEFAULT_CHAR_SET_ENCODING
     */
    public MTOMXMLStreamWriterImpl(OutputStream outStream, OMOutputFormat format, final boolean preserveAttachments)
            throws XMLStreamException, FactoryConfigurationError {
        if (log.isDebugEnabled()) {
            log.debug("Creating MTOMXMLStreamWriter");
            log.debug("OutputStream =" + outStream.getClass());
            log.debug("OMFormat = " + format.toString());
            log.debug("preserveAttachments = " + preserveAttachments);
        }
        if (log.isTraceEnabled()) {
            log.trace("Call Stack =" + CommonUtils.callStackToString());
        }
        this.format = format;

        String encoding = format.getCharSetEncoding();
        if (encoding == null) { //Default encoding is UTF-8
            format.setCharSetEncoding(encoding = OMOutputFormat.DEFAULT_CHAR_SET_ENCODING);
        }

        optimizationPolicy = new OptimizationPolicyImpl(format);
        
        final OMMultipartWriter multipartWriter;
        final OutputStream rootPartOutputStream;
        if (format.isOptimized()) {
            multipartWriter = new OMMultipartWriter(outStream, format);
            try {
                rootPartOutputStream = multipartWriter.writeRootPart();
            } catch (IOException ex) {
                throw new XMLStreamException(ex);
            }
        } else {
            multipartWriter = null;
            rootPartOutputStream = outStream;
        }
        
        Serializer serializer = new Serializer(rootPartOutputStream, encoding);
        
        XmlHandler handler;
        if (format.isOptimized()) {
            ContentIDGenerator contentIDGenerator = new ContentIDGenerator() {
                public String generateContentID(String existingContentID) {
                    return existingContentID != null ? existingContentID : getNextContentId();
                }
            };
            handler = new XOPEncodingFilterHandler(serializer, contentIDGenerator, optimizationPolicy, new CompletionListener() {
                @Override
                public void completed(XOPEncodingFilterHandler encoder) throws StreamException {
                    try {
                        rootPartOutputStream.close();
                        // First write the attachments added properly through the DataHandlerWriter extension
                        for (String contentID : encoder.getContentIDs()) {
                            DataHandler dataHandler = encoder.getDataHandler(contentID);
                            if (preserveAttachments || !(dataHandler instanceof DataHandlerExt)) {
                                multipartWriter.writePart(dataHandler, contentID);
                            } else {
                                OutputStream out = multipartWriter.writePart(dataHandler.getContentType(), contentID);
                                IOUtils.copy(((DataHandlerExt)dataHandler).readOnce(), out, -1);
                                out.close();
                            }
                        }
                        // Now write parts that have been added by prepareDataHandler
                        for (Part part : otherParts) {
                            multipartWriter.writePart(part.getDataHandler(), part.getContentID());
                        }
                        multipartWriter.complete();
                    } catch (IOException ex) {
                        throw new StreamException(ex);
                    }
                }
            });
        } else {
            handler = serializer;
        }
        
        xmlWriter = new XmlHandlerStreamWriter(handler, serializer);

        xmlStreamWriterFilter = format.getXmlStreamWriterFilter();
        if (xmlStreamWriterFilter != null) {
            if (log.isDebugEnabled()) {
                log.debug("Installing XMLStreamWriterFilter " + xmlStreamWriterFilter);
            }
            xmlStreamWriterFilter.setDelegate(xmlWriter);
            xmlWriter = xmlStreamWriterFilter;
        }
    }

    /**
     * Get the {@link XmlHandler} events are serialized to.
     * 
     * @return the {@link XmlHandler} or {@code null} if the {@link XMLStreamWriter} is not
     *         connected to a {@link XmlHandler} (e.g. because the {@link XMLStreamWriter} is user
     *         supplied)
     */
    private XmlHandler getHandler() {
        XMLStreamWriter writer = xmlWriter;
        while (writer instanceof XMLStreamWriterFilter) {
            writer = ((XMLStreamWriterFilter)writer).getDelegate();
        }
        if (writer instanceof XmlHandlerStreamWriter) {
            return ((XmlHandlerStreamWriter)writer).getHandler();
        } else {
            return null;
        }
    }

    public void writeStartElement(String string) throws XMLStreamException {
        xmlWriter.writeStartElement(string);
        depth++;
    }

    public void writeStartElement(String string, String string1) throws XMLStreamException {
        xmlWriter.writeStartElement(string, string1);
        depth++;
    }

    public void writeStartElement(String string, String string1, String string2)
            throws XMLStreamException {
        xmlWriter.writeStartElement(string, string1, string2);
        depth++;
    }

    public void writeEmptyElement(String string, String string1) throws XMLStreamException {
        xmlWriter.writeEmptyElement(string, string1);
    }

    public void writeEmptyElement(String string, String string1, String string2)
            throws XMLStreamException {
        xmlWriter.writeEmptyElement(string, string1, string2);
    }

    public void writeEmptyElement(String string) throws XMLStreamException {
        xmlWriter.writeEmptyElement(string);
    }

    public void writeEndElement() throws XMLStreamException {
        xmlWriter.writeEndElement();
        depth--;
    }

    public void writeEndDocument() throws XMLStreamException {
        log.debug("writeEndDocument");
        xmlWriter.writeEndDocument();
        isEndDocument = true; 
    }

    public void close() throws XMLStreamException {
        log.debug("close");
        // Only call flush because data may have been written to the underlying output stream
        // without ever calling writeStartElement. In this case, close would trigger an
        // exception.
        flush();
    }

    /**
     * Flush is overridden to trigger the attachment serialization
     */
    public void flush() throws XMLStreamException {
        log.debug("Calling MTOMXMLStreamWriter.flush");
        xmlWriter.flush();
        // flush() triggers the optimized attachment writing.
        // If the optimized attachments are specified, and the xml
        // document is completed, then write out the attachments.
        if (format.isOptimized() && !isComplete & (isEndDocument || depth == 0)) {
            log.debug("The XML writing is completed.  Now the attachments are written");
            isComplete = true;
            XmlHandler handler = getHandler();
            if (handler != null) {
                try {
                    handler.completed();
                } catch (StreamException ex) {
                    throw new XMLStreamException(ex);
                }
            }
        }
    }
    

    public void writeAttribute(String string, String string1) throws XMLStreamException {
        xmlWriter.writeAttribute(string, string1);
    }

    public void writeAttribute(String string, String string1, String string2, String string3)
            throws XMLStreamException {
        xmlWriter.writeAttribute(string, string1, string2, string3);
    }

    public void writeAttribute(String string, String string1, String string2)
            throws XMLStreamException {
        xmlWriter.writeAttribute(string, string1, string2);
    }

    public void writeNamespace(String string, String string1) throws XMLStreamException {
        xmlWriter.writeNamespace(string, string1);
    }

    public void writeDefaultNamespace(String string) throws XMLStreamException {
        xmlWriter.writeDefaultNamespace(string);
    }

    public void writeComment(String string) throws XMLStreamException {
        xmlWriter.writeComment(string);
    }

    public void writeProcessingInstruction(String string) throws XMLStreamException {
        xmlWriter.writeProcessingInstruction(string);
    }

    public void writeProcessingInstruction(String string, String string1)
            throws XMLStreamException {
        xmlWriter.writeProcessingInstruction(string, string1);
    }

    public void writeCData(String string) throws XMLStreamException {
        xmlWriter.writeCData(string);
    }

    public void writeDTD(String string) throws XMLStreamException {
        xmlWriter.writeDTD(string);
    }

    public void writeEntityRef(String string) throws XMLStreamException {
        xmlWriter.writeEntityRef(string);
    }

    public void writeStartDocument() throws XMLStreamException {
        xmlWriter.writeStartDocument();
    }

    public void writeStartDocument(String string) throws XMLStreamException {
        xmlWriter.writeStartDocument(string);
    }

    public void writeStartDocument(String string, String string1) throws XMLStreamException {
        xmlWriter.writeStartDocument(string, string1);
    }

    public void writeCharacters(String string) throws XMLStreamException {
        xmlWriter.writeCharacters(string);
    }

    public void writeCharacters(char[] chars, int i, int i1) throws XMLStreamException {
        xmlWriter.writeCharacters(chars, i, i1);
    }

    public String getPrefix(String string) throws XMLStreamException {
        return xmlWriter.getPrefix(string);
    }

    public void setPrefix(String string, String string1) throws XMLStreamException {
        xmlWriter.setPrefix(string, string1);
    }

    public void setDefaultNamespace(String string) throws XMLStreamException {
        xmlWriter.setDefaultNamespace(string);
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        xmlWriter.setNamespaceContext(namespaceContext);
    }

    public NamespaceContext getNamespaceContext() {
        return xmlWriter.getNamespaceContext();
    }

    public Object getProperty(String string) throws IllegalArgumentException {
        return xmlWriter.getProperty(string);
    }

    public boolean isOptimized() {
        return format.isOptimized();
    }

    public String getContentType() {
        return format.getContentType();
    }

    public String prepareDataHandler(DataHandler dataHandler) {
        boolean doOptimize;
        try {
            doOptimize = optimizationPolicy.isOptimized(dataHandler, true);
        } catch (IOException ex) {
            doOptimize = true;
        }
        if (doOptimize) {
            String contentID = getNextContentId();
            otherParts.add(new Part(contentID, dataHandler));
            return contentID;
        } else {
            return null;
        }
    }
    
    public void setXmlStreamWriter(XMLStreamWriter xmlWriter) {
        this.xmlWriter = xmlWriter;
    }

    public XMLStreamWriter getXmlStreamWriter() {
        return xmlWriter;
    }

    public String getMimeBoundary() {
        return format.getMimeBoundary();
    }

    public String getRootContentId() {
        return format.getRootContentId();
    }

    public String getNextContentId() {
        return format.getNextContentId();
    }

    public String getCharSetEncoding() {
        return format.getCharSetEncoding();
    }

    public void setCharSetEncoding(String charSetEncoding) {
        format.setCharSetEncoding(charSetEncoding);
    }

    public String getXmlVersion() {
        return format.getXmlVersion();
    }

    public void setXmlVersion(String xmlVersion) {
        format.setXmlVersion(xmlVersion);
    }

    public void setSoap11(boolean b) {
        format.setSOAP11(b);
    }

    public boolean isIgnoreXMLDeclaration() {
        return format.isIgnoreXMLDeclaration();
    }

    public void setIgnoreXMLDeclaration(boolean ignoreXMLDeclaration) {
        format.setIgnoreXMLDeclaration(ignoreXMLDeclaration);
    }

    public void setDoOptimize(boolean b) {
        format.setDoOptimize(b);
    }

    public OMOutputFormat getOutputFormat() {
        return format;
    }

    public void setOutputFormat(OMOutputFormat format) {
        this.format = format;
    }
    
    public OutputStream getOutputStream() throws XMLStreamException {  
        
        if (xmlStreamWriterFilter != null) {
            if (log.isDebugEnabled()) {
                log.debug("getOutputStream returning null due to presence of XMLStreamWriterFilter " + 
                        xmlStreamWriterFilter);
            }
            return null;
        }
        
        OutputStream outputStream;
        XmlHandler handler = getHandler();
        // Remove the XOPEncodingFilterHandler wrapper if necessary
        if (handler instanceof XOPEncodingFilterHandler) {
            handler = ((XOPEncodingFilterHandler)handler).getParent();
        }
        if (handler instanceof Serializer) {
            try {
                outputStream = ((Serializer)handler).getOutputStream();
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
    
    public void setFilter(XMLStreamWriterFilter filter) {
        if (filter != null) {
            if (log.isDebugEnabled()) {
                log.debug("setting filter " + filter.getClass());
            }
            xmlStreamWriterFilter = filter;
            filter.setDelegate(xmlWriter);
            xmlWriter = filter;
        }
    }
    
    public XMLStreamWriterFilter removeFilter() {
        XMLStreamWriterFilter filter = null;
        if (xmlStreamWriterFilter != null) {
            filter = xmlStreamWriterFilter;
            if (log.isDebugEnabled()) {
                log.debug("removing filter " + filter.getClass());
            }
            xmlWriter = xmlStreamWriterFilter.getDelegate();
            filter.setDelegate(null);
            xmlStreamWriterFilter = (xmlWriter instanceof XMLStreamWriterFilter) ? 
                        (XMLStreamWriterFilter) xmlWriter : 
                                null;
        }
        return filter;
    }
}
