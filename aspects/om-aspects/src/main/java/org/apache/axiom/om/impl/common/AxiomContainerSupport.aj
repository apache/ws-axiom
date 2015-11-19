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
package org.apache.axiom.om.impl.common;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.core.Axis;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.DeferredParsingException;
import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReader;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.common.serializer.pull.OMXMLStreamReaderExAdapter;
import org.apache.axiom.om.impl.common.serializer.pull.PullSerializer;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.impl.common.serializer.push.sax.XMLReaderImpl;
import org.apache.axiom.om.impl.common.serializer.push.stax.StAXSerializer;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.util.OMXMLStreamReaderValidator;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

public aspect AxiomContainerSupport {
    private static final Log log = LogFactory.getLog(AxiomContainerSupport.class);
    
    private static final OMXMLStreamReaderConfiguration defaultReaderConfiguration = new OMXMLStreamReaderConfiguration();
    
    public final void AxiomContainer.discarded() {
        coreSetState(DISCARDED);
    }

    public final XMLStreamReader AxiomContainer.getXMLStreamReader() {
        return getXMLStreamReader(true);
    }
    
    public final XMLStreamReader AxiomContainer.getXMLStreamReaderWithoutCaching() {
        return getXMLStreamReader(false);
    }

    public final XMLStreamReader AxiomContainer.getXMLStreamReader(boolean cache) {
        return getXMLStreamReader(cache, defaultReaderConfiguration);
    }
    
    public XMLStreamReader AxiomContainer.getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration) {
        return defaultGetXMLStreamReader(cache, configuration);
    }
    
    public final XMLStreamReader AxiomContainer.defaultGetXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration) {
        OMXMLParserWrapper builder = getBuilder();
        if (builder != null && builder.isCompleted() && !cache && !isComplete()) {
            throw new UnsupportedOperationException("The parser is already consumed!");
        }
        OMXMLStreamReader reader = new OMXMLStreamReaderExAdapter(new PullSerializer(this, cache, configuration.isPreserveNamespaceContext()));
        
        if (configuration.isNamespaceURIInterning()) {
            reader = new NamespaceURIInterningXMLStreamReaderWrapper(reader);
        }
        
        // If debug is enabled, wrap the OMXMLStreamReader in a validator.
        // The validator will check for mismatched events to help determine if the OMStAXWrapper
        // is functioning correctly.  All problems are reported as debug.log messages
        
        if (log.isDebugEnabled()) {
            reader = 
                new OMXMLStreamReaderValidator(reader, // delegate to actual reader
                     false); // log problems (true will cause exceptions to be thrown)
        }
        
        return reader;
    }
    
    public void AxiomContainer.addChild(OMNode omNode) {
        addChild(omNode, false);
    }

    public final AxiomChildNode AxiomContainer.prepareNewChild(OMNode omNode) {
        AxiomChildNode child;
        // Careful here: if the child was created by another Axiom implementation, it doesn't
        // necessarily implement AxiomChildNode
        if (omNode.getOMFactory().getMetaFactory().equals(getOMFactory().getMetaFactory())) {
            child = (AxiomChildNode)omNode;
        } else {
            child = (AxiomChildNode)((OMFactoryEx)getOMFactory()).importNode(omNode);
        }
        checkChild(omNode);
        return child;
    }

    public void AxiomContainer.addChild(OMNode omNode, boolean fromBuilder) {
        AxiomChildNode child;
        if (fromBuilder) {
            // If the new child was provided by the builder, we know that it was created by
            // the same factory
            child = (AxiomChildNode)omNode;
        } else {
            child = prepareNewChild(omNode);
        }
        
        coreAppendChild(child, fromBuilder);

        // For a normal OMNode, the incomplete status is
        // propogated up the tree.  
        // However, a OMSourcedElement is self-contained 
        // (it has an independent parser source).
        // So only propogate the incomplete setting if this
        // is a normal OMNode
        // TODO: this is crap and needs to be reviewed
        if (!fromBuilder && !child.isComplete() && 
            !(child instanceof OMSourcedElement)) {
            setComplete(false);
        }
    }
    
    public final void AxiomContainer.build() {
        OMXMLParserWrapper builder = getBuilder();
        // builder is null. Meaning this is a programatical created element but it has children which are not completed
        // Build them all.
        if (builder == null && getState() == INCOMPLETE) {
            for (Iterator childrenIterator = this.getChildren(); childrenIterator.hasNext();) {
                OMNode omNode = (OMNode) childrenIterator.next();
                omNode.build();
            }
        } else {
            if (getState() == AxiomContainer.DISCARDED) {
                if (builder != null) {
                    ((StAXBuilder)builder).debugDiscarded(this);
                }
                throw new NodeUnavailableException();
            }
            if (builder != null && builder.isCompleted()) {
                log.debug("Builder is already complete.");
            }
            while (!isComplete()) {
    
                builder.next();    
                if (builder.isCompleted() && !isComplete()) {
                    log.debug("Builder is complete.  Setting OMObject to complete.");
                    setComplete(true);
                }
            }
        }
    }
    
    public OMNode AxiomContainer.getFirstOMChild() {
        return (OMNode)coreGetFirstChild();
    }
    
    public void AxiomContainer.removeChildren() {
        coreRemoveChildren(AxiomSemantics.INSTANCE);
    }
    
    public Iterator AxiomContainer.getChildren() {
        return coreGetNodes(Axis.CHILDREN, OMNode.class, AxiomExceptionTranslator.INSTANCE, AxiomSemantics.INSTANCE);
    }

    public Iterator AxiomContainer.getChildrenWithLocalName(String localName) {
        return new OMChildrenLocalNameIterator(getFirstOMChild(), localName);
    }

    public Iterator AxiomContainer.getChildrenWithNamespaceURI(String uri) {
        return new OMChildrenNamespaceIterator(getFirstOMChild(), uri);
    }

    public Iterator AxiomContainer.getChildrenWithName(QName elementQName) {
        OMNode firstChild = getFirstOMChild();
        Iterator it =  new OMChildrenQNameIterator(firstChild, elementQName);
        
        // The getChidrenWithName method used to tolerate an empty namespace
        // and interpret that as getting any element that matched the local
        // name.  There are custmers of axiom that have hard-coded dependencies
        // on this semantic.
        // The following code falls back to this legacy behavior only if
        // (a) elementQName has no namespace, (b) the new iterator finds no elements
        // and (c) there are children.
        // TODO: DOOM actually supported elementQName == null; need to test and document this
        if (elementQName != null && elementQName.getNamespaceURI().length() == 0 &&
            firstChild != null &&
            !it.hasNext()) {
            if (log.isTraceEnabled()) {
                log.trace("There are no child elements that match the unqualifed name: " +
                          elementQName);
                log.trace("Now looking for child elements that have the same local name.");
            }
            it = new OMChildrenLegacyQNameIterator(getFirstOMChild(), elementQName);
        }
        
        return it;
    }
    
    public Iterator AxiomContainer.getDescendants(boolean includeSelf) {
        return coreGetNodes(includeSelf ? Axis.DESCENDANTS_OR_SELF : Axis.DESCENDANTS, OMSerializable.class, AxiomExceptionTranslator.INSTANCE, AxiomSemantics.INSTANCE);
    }

    public OMElement AxiomContainer.getFirstChildWithName(QName elementQName) throws OMException {
        OMChildrenQNameIterator omChildrenQNameIterator =
                new OMChildrenQNameIterator(getFirstOMChild(),
                                            elementQName);
        OMNode omNode = null;
        if (omChildrenQNameIterator.hasNext()) {
            omNode = (OMNode) omChildrenQNameIterator.next();
        }

        return ((omNode != null) && (OMNode.ELEMENT_NODE == omNode.getType())) ?
                (OMElement) omNode : null;
    }

    public final SAXSource AxiomContainer.getSAXSource(boolean cache) {
        return new SAXSource(new XMLReaderImpl(this, cache), new InputSource());
    }

    public final SAXResult AxiomContainer.getSAXResult() {
        SAXResultContentHandler handler = new SAXResultContentHandler(this);
        SAXResult result = new SAXResult();
        result.setHandler(handler);
        result.setLexicalHandler(handler);
        return result;
    }

    public final void AxiomContainer.serialize(OutputStream output) throws XMLStreamException {
        serialize(output, new OMOutputFormat());
    }

    public final void AxiomContainer.serialize(Writer writer) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter = StAXUtils.createXMLStreamWriter(writer);
        try {
            serialize(xmlStreamWriter);
        } finally {
            xmlStreamWriter.close();
        }
    }

    public final void AxiomContainer.serializeAndConsume(OutputStream output) throws XMLStreamException {
        serializeAndConsume(output, new OMOutputFormat());
    }

    public final void AxiomContainer.serializeAndConsume(Writer writer) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter = StAXUtils.createXMLStreamWriter(writer);
        try {
            serializeAndConsume(xmlStreamWriter);
        } finally {
            xmlStreamWriter.close();
        }
    }

    public final void AxiomContainer.serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format, true);
        try {
            try {
                internalSerialize(new StAXSerializer(this, writer), format, true);
            } catch (OutputException ex) {
                throw (XMLStreamException)ex.getCause();
            }
            // TODO: the flush is necessary because of an issue with the lifecycle of MTOMXMLStreamWriter
            writer.flush();
        } finally {
            writer.close();
        }
    }

    public final void AxiomContainer.serialize(Writer writer2, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer =
                new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer2));
        writer.setOutputFormat(format);
        try {
            try {
                internalSerialize(new StAXSerializer(this, writer), format, true);
            } catch (OutputException ex) {
                throw (XMLStreamException)ex.getCause();
            }
            // TODO: the flush is necessary because of an issue with the lifecycle of MTOMXMLStreamWriter
            writer.flush();
        } finally {
            writer.close();
        }
    }

    public final void AxiomContainer.serializeAndConsume(OutputStream output, OMOutputFormat format)
            throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format, false);
        try {
            try {
                internalSerialize(new StAXSerializer(this, writer), format, false);
            } catch (OutputException ex) {
                throw (XMLStreamException)ex.getCause();
            }
            // TODO: the flush is necessary because of an issue with the lifecycle of MTOMXMLStreamWriter
            writer.flush();
        } finally {
            writer.close();
        }
    }

    public final void AxiomContainer.serializeAndConsume(Writer writer2, OMOutputFormat format)
            throws XMLStreamException {
        MTOMXMLStreamWriter writer =
                new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer2));
        writer.setOutputFormat(format);
        try {
            try {
                internalSerialize(new StAXSerializer(this, writer), format, false);
            } catch (OutputException ex) {
                throw (XMLStreamException)ex.getCause();
            }
            // TODO: the flush is necessary because of an issue with the lifecycle of MTOMXMLStreamWriter
            writer.flush();
        } finally {
            writer.close();
        }
    }

    final void AxiomContainer.serializeChildren(Serializer serializer, OMOutputFormat format, boolean cache) throws OutputException {
        if (getState() == AxiomContainer.DISCARDED) {
            StAXBuilder builder = (StAXBuilder)getBuilder();
            if (builder != null) {
                builder.debugDiscarded(this);
            }
            throw new NodeUnavailableException();
        }
        if (cache) {
            AxiomChildNode child = (AxiomChildNode)getFirstOMChild();
            while (child != null) {
                child.internalSerialize(serializer, format, true);
                child = (AxiomChildNode)child.getNextOMSibling();
            }
        } else {
            // First, recursively serialize all child nodes that have already been created
            AxiomChildNode child = (AxiomChildNode)coreGetFirstChildIfAvailable();
            while (child != null) {
                child.internalSerialize(serializer, format, cache);
                child = (AxiomChildNode)child.coreGetNextSiblingIfAvailable();
            }
            // Next, if the container is incomplete, disable caching (temporarily)
            // and serialize the nodes that have not been built yet by copying the
            // events from the underlying XMLStreamReader.
            if (!isComplete() && getBuilder() != null) {
                StAXOMBuilder builder = (StAXOMBuilder)getBuilder();
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
                    serializer.copyEvent(reader, dataHandlerReader);
                }
                builder.reenableCaching(this);
            }
        }
    }

    public final void AxiomContainer.notifyChildComplete() {
        if (getState() == INCOMPLETE && getBuilder() == null) {
            Iterator iterator = getChildren();
            while (iterator.hasNext()) {
                OMNode node = (OMNode) iterator.next();
                if (!node.isComplete()) {
                    return;
                }
            }
            this.setComplete(true);
        }
    }
}
