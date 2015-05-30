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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
import org.apache.axiom.om.impl.common.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomChildNode;
import org.apache.axiom.om.impl.common.OMDataSourceUtil;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;

public abstract class Serializer {
    private static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSI_LOCAL_NAME = "type";
    
    private final OMSerializable root;
    private final OMElement contextElement;
    private final boolean namespaceRepairing;
    private final boolean preserveNamespaceContext;
    
    /**
     * Constructor.
     * 
     * @param root
     *            the root node of the object model subtree that is being serialized; this
     *            information is used by the serializer in scenarios that require access to the
     *            namespace context of the parent of the root node
     * @param namespaceRepairing
     *            indicates if the serializer should perform namespace repairing
     * @param preserveNamespaceContext
     *            indicates if the namespace context determined by the ancestors of the root node
     *            should be strictly preserved in the output
     */
    public Serializer(OMSerializable root, boolean namespaceRepairing, boolean preserveNamespaceContext) {
        this.root = root;
        if (root instanceof OMNode) {
            OMContainer parent = ((OMNode)root).getParent();
            if (parent instanceof OMElement) {
                contextElement = (OMElement)parent; 
            } else {
                contextElement = null;
            }
        } else {
            contextElement = null;
        }
        this.namespaceRepairing = namespaceRepairing;
        this.preserveNamespaceContext = preserveNamespaceContext;
    }

    public final void serializeStartpart(OMElement element) throws OutputException {
        OMNamespace ns = element.getNamespace();
        if (ns == null) {
            internalBeginStartElement("", "", element.getLocalName());
        } else {
            internalBeginStartElement(ns.getPrefix(), ns.getNamespaceURI(), element.getLocalName());
        }
        if (preserveNamespaceContext && element == root) {
            // Maintain a set of the prefixes we have already seen. This is required to take into
            // account that a namespace mapping declared on an element can hide another one declared
            // for the same prefix on an ancestor of the element.
            Set/*<String>*/ seenPrefixes = new HashSet();
            OMElement current = element;
            while (true) {
                for (Iterator it = current.getAllDeclaredNamespaces(); it.hasNext(); ) {
                    ns = (OMNamespace)it.next();
                    if (seenPrefixes.add(ns.getPrefix())) {
                        mapNamespace(ns.getPrefix(), ns.getNamespaceURI(), true, false);
                    }
                }
                OMContainer parent = current.getParent();
                if (!(parent instanceof OMElement)) {
                    break;
                }
                current = (OMElement)parent;
            }
        } else {
            for (Iterator it = element.getAllDeclaredNamespaces(); it.hasNext(); ) {
                ns = (OMNamespace)it.next();
                mapNamespace(ns.getPrefix(), ns.getNamespaceURI(), true, false);
            }
        }
        for (Iterator it = element.getAllAttributes(); it.hasNext(); ) {
            OMAttribute attr = (OMAttribute)it.next();
            ns = attr.getNamespace();
            if (ns == null) {
                processAttribute("", "", attr.getLocalName(), attr.getAttributeType(), attr.getAttributeValue());
            } else {
                processAttribute(ns.getPrefix(), ns.getNamespaceURI(), attr.getLocalName(), attr.getAttributeType(), attr.getAttributeValue());
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
                    internalBeginStartElement(normalize(reader.getPrefix()), normalize(reader.getNamespaceURI()), reader.getLocalName());
                    for (int i=0, count=reader.getNamespaceCount(); i<count; i++) {
                        mapNamespace(normalize(reader.getNamespacePrefix(i)), normalize(reader.getNamespaceURI(i)), true, false);
                    }
                    for (int i=0, count=reader.getAttributeCount(); i<count; i++) {
                        processAttribute(
                                normalize(reader.getAttributePrefix(i)),
                                normalize(reader.getAttributeNamespace(i)),
                                reader.getAttributeLocalName(i),
                                reader.getAttributeType(i),
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
    
    private void internalBeginStartElement(String prefix, String namespaceURI, String localName) throws OutputException {
        beginStartElement(prefix, namespaceURI, localName);
        mapNamespace(prefix, namespaceURI, false, false);
    }
    
    private void processAttribute(String prefix, String namespaceURI, String localName, String type, String value) throws OutputException {
        mapNamespace(prefix, namespaceURI, false, true);
        if (namespaceRepairing && contextElement != null && namespaceURI.equals(XSI_URI) && localName.equals(XSI_LOCAL_NAME)) {
            String trimmedValue = value.trim();
            if (trimmedValue.indexOf(":") > 0) {
                String refPrefix = trimmedValue.substring(0, trimmedValue.indexOf(":"));
                OMNamespace ns = contextElement.findNamespaceURI(refPrefix);
                if (ns != null) {
                    mapNamespace(refPrefix, ns.getNamespaceURI(), false, true);
                }
            }
        }
        addAttribute(prefix, namespaceURI, localName, type, value);
    }
    
    /**
     * Method used internally to report usage of a given namespace binding. This method will
     * generate a namespace declaration if required by the configured namespace repairing policy.
     * 
     * @param prefix
     *            the namespace prefix; must not be <code>null</code>
     * @param namespaceURI
     *            the namespace URI; must not be <code>null</code>
     * @param fromDecl
     *            <code>true</code> if the namespace binding was defined by an explicit namespace
     *            declaration, <code>false</code> if the namespace binding was used implicitly in
     *            the name of an element or attribute
     * @param attr
     */
    private void mapNamespace(String prefix, String namespaceURI, boolean fromDecl, boolean attr) throws OutputException {
        if (namespaceRepairing) {
            // If the prefix and namespace are already associated, no generation is needed
            if (isAssociated(prefix, namespaceURI)) {
                return;
            }
            
            // Attributes without a prefix always are associated with the unqualified namespace
            // according to the schema specification.  No generation is needed.
            if (prefix.length() == 0 && namespaceURI.length() == 0 && attr) {
                return;
            }
            
            // Add the namespace if the prefix is not associated.
            addNamespace(prefix, namespaceURI);
        } else {
            // If namespace repairing is disabled, only output namespace declarations that appear
            // explicitly in the input
            if (fromDecl) {
                addNamespace(prefix, namespaceURI);
            }
        }
    }
    
    public final void serializeChildren(AxiomContainer container, OMOutputFormat format, boolean cache) throws OutputException {
        if (container.getState() == AxiomContainer.DISCARDED) {
            StAXBuilder builder = (StAXBuilder)container.getBuilder();
            if (builder != null) {
                builder.debugDiscarded(container);
            }
            throw new NodeUnavailableException();
        }
        if (cache) {
            AxiomChildNode child = (AxiomChildNode)container.getFirstOMChild();
            while (child != null) {
                child.internalSerialize(this, format, true);
                child = (AxiomChildNode)child.getNextOMSibling();
            }
        } else {
            // First, recursively serialize all child nodes that have already been created
            AxiomChildNode child = (AxiomChildNode)container.getFirstOMChildIfAvailable();
            while (child != null) {
                child.internalSerialize(this, format, cache);
                child = (AxiomChildNode)child.getNextOMSiblingIfAvailable();
            }
            // Next, if the container is incomplete, disable caching (temporarily)
            // and serialize the nodes that have not been built yet by copying the
            // events from the underlying XMLStreamReader.
            if (!container.isComplete() && container.getBuilder() != null) {
                StAXOMBuilder builder = (StAXOMBuilder)container.getBuilder();
                XMLStreamReader reader = builder.disableCaching();
                DataHandlerReader dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(reader);
                boolean first = true;
                int depth = 0;
                loop: while (true) {
                    int event;
                    if (first) {
                        event = reader.getEventType();
                        first = false;
                    } else {
                        try {
                            event = reader.next();
                        } catch (XMLStreamException ex) {
                            throw new DeferredParsingException(ex);
                        }
                    }
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
    
    public abstract void writeStartDocument(String version) throws OutputException;
    
    public abstract void writeStartDocument(String encoding, String version) throws OutputException;
    
    public abstract void writeDTD(String rootName, String publicId, String systemId, String internalSubset) throws OutputException;
    
    /**
     * Prepare to write an element start tag. A call to this method will be followed by zero or more
     * calls to {@link #addNamespace(String, String)} and
     * {@link #addAttribute(String, String, String, String, String)} and a single call to
     * {@link #finishStartElement()}.
     * 
     * @param prefix
     *            the prefix of the element; never <code>null</code>
     * @param namespaceURI
     *            the namespace URI of the element; never <code>null</code>
     * @param localName
     *            the local name of the element; never <code>null</code>
     * @throws OutputException
     */
    protected abstract void beginStartElement(String prefix, String namespaceURI, String localName) throws OutputException;
    
    /**
     * Add the given namespace to the element. The implementation of this method must take the
     * appropriate actions such that the following two conditions are satisfied:
     * <ul>
     * <li>A namespace declaration is written to the output.
     * <li>The namespace binding defined by the parameters is visible in the namespace context of
     * the {@link XMLStreamWriter} that {@link #serializePushOMDataSource(OMDataSource)} passes to
     * {@link OMDataSource#serialize(XMLStreamWriter)} if an {@link OMDataSource} is serialized in
     * the scope of the current element (and of course unless the namespace binding is hidden by a
     * namespace defined on a nested element).
     * </ul>
     * 
     * @param prefix
     *            the namespace prefix; never <code>null</code>
     * @param namespaceURI
     *            the namespace URI; never <code>null</code>
     * @throws OutputException
     */
    protected abstract void addNamespace(String prefix, String namespaceURI) throws OutputException;
    
    /**
     * Add the given attribute to the element.
     * 
     * @param prefix
     *            the namespace prefix of the attribute; never <code>null</code>
     * @param namespaceURI
     *            the namespace URI or the attribute; never <code>null</code>
     * @param localName
     *            the local name of the attribute; never <code>null</code>
     * @param type
     *            the attribute type (e.g. <tt>CDATA</tt>); never <code>null</code>
     * @param value
     *            the value of the attribute; never <code>null</code>
     * @throws OutputException
     */
    protected abstract void addAttribute(String prefix, String namespaceURI, String localName, String type, String value) throws OutputException;
    
    protected abstract void finishStartElement() throws OutputException;
    
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
    
    public abstract void writeEndDocument() throws OutputException;
}
