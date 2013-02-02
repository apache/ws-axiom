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
package org.apache.axiom.om.impl.common.serializer.push;

import java.util.ArrayList;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.DeferredParsingException;
import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.common.IChildNode;
import org.apache.axiom.om.impl.common.IContainer;
import org.apache.axiom.om.impl.common.OMDataSourceUtil;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;

public abstract class Serializer {
    private static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSI_LOCAL_NAME = "type";
    
    private final OMElement contextElement;
    private final ArrayList writePrefixList = new ArrayList();
    private final ArrayList writeNSList = new ArrayList();
    
    public Serializer(OMSerializable contextNode) {
        if (contextNode instanceof OMNode) {
            OMContainer parent = ((OMNode)contextNode).getParent();
            if (parent instanceof OMElement) {
                contextElement = (OMElement)parent; 
            } else {
                contextElement = null;
            }
        } else {
            contextElement = null;
        }
    }

    public final void serializeStartpart(OMElement element) throws OutputException {
        OMNamespace ns = element.getNamespace();
        if (ns == null) {
            beginStartElement("", "", element.getLocalName());
        } else {
            beginStartElement(ns.getPrefix(), ns.getNamespaceURI(), element.getLocalName());
        }
        for (Iterator it = element.getAllDeclaredNamespaces(); it.hasNext(); ) {
            ns = (OMNamespace)it.next();
            generateSetPrefix(ns.getPrefix(), ns.getNamespaceURI(), false);
        }
        for (Iterator it = element.getAllAttributes(); it.hasNext(); ) {
            OMAttribute attr = (OMAttribute)it.next();
            ns = attr.getNamespace();
            if (ns == null) {
                processAttribute("", "", attr.getLocalName(), attr.getAttributeValue());
            } else {
                processAttribute(ns.getPrefix(), ns.getNamespaceURI(), attr.getLocalName(), attr.getAttributeValue());
            }
        }
        finishStartElement();
    }
    
    private void copyEvent(XMLStreamReader reader, DataHandlerReader dataHandlerReader) throws OutputException {
        try {
            int eventType = reader.getEventType();
            switch (eventType) {
                case XMLStreamReader.DTD:
                    DTDReader dtdReader;
                    try {
                        dtdReader = (DTDReader)reader.getProperty(DTDReader.PROPERTY);
                    } catch (IllegalArgumentException ex) {
                        dtdReader = null;
                    }
                    if (dtdReader == null) {
                        throw new XMLStreamException("Cannot serialize the DTD because the XMLStreamReader doesn't support the DTDReader extension");
                    }
                    writeDTD(dtdReader.getRootName(), dtdReader.getPublicId(), dtdReader.getSystemId(), reader.getText());
                    break;
                case XMLStreamReader.START_ELEMENT:
                    beginStartElement(normalize(reader.getPrefix()), normalize(reader.getNamespaceURI()), reader.getLocalName());
                    for (int i=0, count=reader.getNamespaceCount(); i<count; i++) {
                        generateSetPrefix(normalize(reader.getNamespacePrefix(i)), normalize(reader.getNamespaceURI(i)), false);
                    }
                    for (int i=0, count=reader.getAttributeCount(); i<count; i++) {
                        processAttribute(
                                normalize(reader.getAttributePrefix(i)),
                                normalize(reader.getAttributeNamespace(i)),
                                reader.getAttributeLocalName(i),
                                reader.getAttributeValue(i));
                    }
                    finishStartElement();
                    break;
                case XMLStreamReader.END_ELEMENT:
                    writeEndElement();
                    break;
                case XMLStreamReader.CHARACTERS:
                    if (dataHandlerReader != null && dataHandlerReader.isBinary()) {
                        if (dataHandlerReader.isDeferred()) {
                            writeDataHandler(dataHandlerReader.getDataHandlerProvider(),
                                    dataHandlerReader.getContentID(), dataHandlerReader.isOptimized());
                        } else {
                            writeDataHandler(dataHandlerReader.getDataHandler(),
                                    dataHandlerReader.getContentID(), dataHandlerReader.isOptimized());
                        }
                        break;
                    }
                    // Fall through
                case XMLStreamReader.SPACE:
                case XMLStreamReader.CDATA:
                    writeText(eventType, reader.getText());
                    break;
                case XMLStreamReader.PROCESSING_INSTRUCTION:
                    writeProcessingInstruction(reader.getPITarget(), reader.getPIData());
                    break;
                case XMLStreamReader.COMMENT:
                    writeComment(reader.getText());
                    break;
                case XMLStreamReader.ENTITY_REFERENCE:
                    writeEntityRef(reader.getLocalName());
                    break;
                default:
                    throw new IllegalStateException();
            }
        } catch (XMLStreamException ex) {
            throw new DeferredParsingException(ex);
        }
    }
    
    private static String normalize(String s) {
        return s == null ? "" : s;
    }
    
    private void beginStartElement(String prefix, String namespaceURI, String localName) throws OutputException {
        writeStartElement(prefix, namespaceURI, localName);
        generateSetPrefix(prefix, namespaceURI, false);
    }
    
    private void processAttribute(String prefix, String namespaceURI, String localName, String value) throws OutputException {
        generateSetPrefix(prefix, namespaceURI, true);
        if (contextElement != null && namespaceURI.equals(XSI_URI) && localName.equals(XSI_LOCAL_NAME)) {
            String trimmedValue = value.trim();
            if (trimmedValue.indexOf(":") > 0) {
                String refPrefix = trimmedValue.substring(0, trimmedValue.indexOf(":"));
                OMNamespace ns = contextElement.findNamespaceURI(refPrefix);
                if (ns != null) {
                    generateSetPrefix(refPrefix, ns.getNamespaceURI(), true);
                }
            }
        }
        writeAttribute(prefix, namespaceURI, localName, value);
    }
    
    private void finishStartElement() throws OutputException {
        for (int i = 0; i < writePrefixList.size(); i++) {
            writeNamespace((String)writePrefixList.get(i), (String)writeNSList.get(i));
        }
        writePrefixList.clear();
        writeNSList.clear();
    }
    
    /**
     * Generate setPrefix/setDefaultNamespace if the prefix is not associated
     *
     * @param prefix the namespace prefix; must not be <code>null</code>
     * @param namespaceURI the namespace URI; must not be <code>null</code>
     * @param attr
     * @return prefix name if a setPrefix/setDefaultNamespace is performed
     */
    private void generateSetPrefix(String prefix, String namespaceURI, boolean attr) throws OutputException {
        // If the prefix and namespace are already associated, no generation is needed
        if (isAssociated(prefix, namespaceURI)) {
            return;
        }
        
        // Attributes without a prefix always are associated with the unqualified namespace
        // according to the schema specification.  No generation is needed.
        if (prefix.length() == 0 && namespaceURI.length() == 0 && attr) {
            return;
        }
        
        // Generate setPrefix/setDefaultNamespace if the prefix is not associated.
        setPrefix(prefix, namespaceURI);
        // If this is a new association, remember it so that it can written out later
        if (!writePrefixList.contains(prefix)) {
            writePrefixList.add(prefix);
            writeNSList.add(namespaceURI);
        }
    }
    
    public final void serializeChildren(IContainer container, OMOutputFormat format, boolean cache) throws OutputException {
        if (container.getState() == IContainer.DISCARDED) {
            StAXBuilder builder = (StAXBuilder)container.getBuilder();
            if (builder != null) {
                builder.debugDiscarded(container);
            }
            throw new NodeUnavailableException();
        }
        if (cache) {
            IChildNode child = (IChildNode)container.getFirstOMChild();
            while (child != null) {
                child.internalSerialize(this, format, true);
                child = (IChildNode)child.getNextOMSibling();
            }
        } else {
            // First, recursively serialize all child nodes that have already been created
            IChildNode child = (IChildNode)container.getFirstOMChildIfAvailable();
            while (child != null) {
                child.internalSerialize(this, format, cache);
                child = (IChildNode)child.getNextOMSiblingIfAvailable();
            }
            // Next, if the container is incomplete, disable caching (temporarily)
            // and serialize the nodes that have not been built yet by copying the
            // events from the underlying XMLStreamReader.
            if (!container.isComplete() && container.getBuilder() != null) {
                StAXOMBuilder builder = (StAXOMBuilder)container.getBuilder();
                builder.setCache(false);
                XMLStreamReader reader = (XMLStreamReader)builder.disableCaching();
                DataHandlerReader dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(reader);
                int depth = 0;
                loop: while (true) {
                    // We use the next() method on the builder instead of the XMLStreamReader
                    // because this takes care of lookahead and autoClose.
                    int event = builder.next();
                    switch (event) {
                        case XMLStreamReader.START_ELEMENT:
                            depth++;
                            break;
                        case XMLStreamReader.END_ELEMENT:
                            if (depth == 0) {
                                break loop;
                            } else {
                                depth--;
                            }
                            break;
                        case XMLStreamReader.END_DOCUMENT:
                            if (depth != 0) {
                                // If we get here, then we have seen a START_ELEMENT event without
                                // a matching END_ELEMENT
                                throw new IllegalStateException();
                            }
                            break loop;
                    }
                    // Note that we don't copy the final END_ELEMENT/END_DOCUMENT event for
                    // the container. This is the responsibility of the caller.
                    copyEvent(reader, dataHandlerReader);
                }
                builder.reenableCaching(container);
            }
        }
    }

    /**
     * Serialize the given data source.
     * 
     * @param dataSource
     *            the data source to serialize
     * @throws OutputException
     *             if an error occurs while writing the data
     * @throws DeferredParsingException
     *             if an error occurs while reading from the data source
     */
    public final void serialize(OMDataSource dataSource) throws OutputException {
        // Note: if we can't determine the type (push/pull) of the OMDataSource, we
        // default to push
        if (OMDataSourceUtil.isPullDataSource(dataSource)) {
            try {
                XMLStreamReader reader = dataSource.getReader();
                DataHandlerReader dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(reader);
                int depth = 0;
                int eventType;
                // Note: the loop is constructed in such a way that we skip both START_DOCUMENT and END_DOCUMENT
                while ((eventType = reader.next()) != XMLStreamReader.END_DOCUMENT) {
                    if (eventType == XMLStreamReader.START_ELEMENT) {
                        depth++;
                    }
                    if (depth > 0) {
                        copyEvent(reader, dataHandlerReader);
                    }
                    if (eventType == XMLStreamReader.END_ELEMENT) {
                        depth--;
                    }
                }
                reader.close();
            } catch (XMLStreamException ex) {
                // XMLStreamExceptions occurring while _writing_ are wrapped in an OutputException.
                // Therefore, if we get here, there must have been a problem while _reading_.
                throw new DeferredParsingException(ex);
            }
        } else {
            serializePushOMDataSource(dataSource);
        }
    }
    
    protected abstract boolean isAssociated(String prefix, String namespace) throws OutputException;
    
    protected abstract void setPrefix(String prefix, String namespaceURI) throws OutputException;
    
    public abstract void writeStartDocument(String version) throws OutputException;
    
    public abstract void writeStartDocument(String encoding, String version) throws OutputException;
    
    public abstract void writeDTD(String rootName, String publicId, String systemId, String internalSubset) throws OutputException;
    
    protected abstract void writeStartElement(String prefix, String namespaceURI, String localName) throws OutputException;
    
    protected abstract void writeNamespace(String prefix, String namespaceURI) throws OutputException;
    
    protected abstract void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws OutputException;
    
    public abstract void writeEndElement() throws OutputException;
    
    public abstract void writeText(int type, String data) throws OutputException;
    
    public abstract void writeComment(String data) throws OutputException;

    public abstract void writeProcessingInstruction(String target, String data) throws OutputException;
    
    public abstract void writeEntityRef(String name) throws OutputException;
    
    public abstract void writeDataHandler(DataHandler dataHandler, String contentID, boolean optimize) throws OutputException;

    public abstract void writeDataHandler(DataHandlerProvider dataHandlerProvider, String contentID, boolean optimize) throws OutputException;

    /**
     * Serialize the given data source using {@link OMDataSource#serialize(XMLStreamWriter)}. The
     * implementation must construct an appropriate {@link XMLStreamWriter} instance to pass to that
     * method and wrap any {@link XMLStreamException} that may be thrown in an
     * {@link OutputException} or {@link DeferredParsingException}.
     * 
     * @param dataSource
     *            the data source to serialize
     * @throws OutputException
     *             if an error occurs while writing the data
     * @throws DeferredParsingException
     *             if an error occurs while reading from the data source
     */
    protected abstract void serializePushOMDataSource(OMDataSource dataSource) throws OutputException;
}
