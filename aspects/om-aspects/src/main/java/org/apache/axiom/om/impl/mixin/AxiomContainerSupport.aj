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
package org.apache.axiom.om.impl.mixin;

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
import org.apache.axiom.core.Builder;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNSAwareElement;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.ElementMatcher;
import org.apache.axiom.core.Mapper;
import org.apache.axiom.core.stream.NamespaceRepairingFilterHandler;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.sax.XmlHandlerContentHandler;
import org.apache.axiom.core.stream.stax.XMLStreamWriterNamespaceContextProvider;
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
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.common.NamespaceURIInterningXMLStreamReaderWrapper;
import org.apache.axiom.om.impl.common.OMChildrenQNameIterator;
import org.apache.axiom.om.impl.common.SAXResultContentHandler;
import org.apache.axiom.om.impl.common.builder.StAXHelper;
import org.apache.axiom.om.impl.common.serializer.pull.OMXMLStreamReaderExAdapter;
import org.apache.axiom.om.impl.common.serializer.pull.PullSerializer;
import org.apache.axiom.om.impl.common.serializer.push.XmlDeclarationRewriterHandler;
import org.apache.axiom.om.impl.common.serializer.push.XsiTypeFilterHandler;
import org.apache.axiom.om.impl.common.serializer.push.sax.XMLReaderImpl;
import org.apache.axiom.om.impl.common.serializer.push.stax.StAXSerializer;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.OMFactoryEx;
import org.apache.axiom.om.util.OMXMLStreamReaderValidator;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

public aspect AxiomContainerSupport {
    private static final Log log = LogFactory.getLog(AxiomContainerSupport.class);
    
    private static final OMXMLStreamReaderConfiguration defaultReaderConfiguration = new OMXMLStreamReaderConfiguration();
    
    public final void AxiomContainer.discarded() {
        coreSetState(DISCARDED);
    }

    public final OMXMLParserWrapper AxiomContainer.getBuilder() {
        return (OMXMLParserWrapper)coreGetBuilder();
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
        Builder builder = coreGetBuilder();
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

    public void AxiomContainer.addChild(OMNode omNode) {
        AxiomChildNode child = prepareNewChild(omNode);
        
        coreAppendChild(child, false);

        // For a normal OMNode, the incomplete status is
        // propogated up the tree.  
        // However, a OMSourcedElement is self-contained 
        // (it has an independent parser source).
        // So only propogate the incomplete setting if this
        // is a normal OMNode
        // TODO: this is crap and needs to be reviewed
        if (!child.isComplete() && 
            !(child instanceof OMSourcedElement)) {
            setComplete(false);
        }
    }
    
    public final void AxiomContainer.build() {
        Builder builder = coreGetBuilder();
        // builder is null. Meaning this is a programatical created element but it has children which are not completed
        // Build them all.
        if (builder == null && getState() == INCOMPLETE) {
            for (Iterator<OMNode> childrenIterator = getChildren(); childrenIterator.hasNext();) {
                OMNode omNode = childrenIterator.next();
                omNode.build();
            }
        } else {
            if (getState() == AxiomContainer.DISCARDED) {
                if (builder != null) {
                    builder.debugDiscarded(this);
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
        try {
            return (OMNode)coreGetFirstChild();
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }
    
    public void AxiomContainer.removeChildren() {
        coreRemoveChildren(AxiomSemantics.INSTANCE);
    }
    
    private static final Mapper<CoreNode,OMNode> childrenMapper = new Mapper<CoreNode,OMNode>() {
        public OMNode map(CoreNode node) {
            return (OMNode)node;
        }
    };
    
    public Iterator<OMNode> AxiomContainer.getChildren() {
        return coreGetNodes(Axis.CHILDREN, childrenMapper, AxiomSemantics.INSTANCE);
    }

    private static final Mapper<CoreNSAwareElement,OMElement> childElementMapper = new Mapper<CoreNSAwareElement,OMElement>() {
        public OMElement map(CoreNSAwareElement element) {
            return (OMElement)element;
        }
    };
    
    public Iterator<OMElement> AxiomContainer.getChildrenWithLocalName(String localName) {
        return coreGetElements(Axis.CHILDREN, CoreNSAwareElement.class,
                ElementMatcher.BY_LOCAL_NAME, null, localName,
                childElementMapper, AxiomSemantics.INSTANCE);
    }

    public Iterator<OMElement> AxiomContainer.getChildrenWithNamespaceURI(String uri) {
        return coreGetElements(Axis.CHILDREN, CoreNSAwareElement.class,
                ElementMatcher.BY_NAMESPACE_URI, uri, null,
                childElementMapper, AxiomSemantics.INSTANCE);
    }

    // TODO: DOOM actually supported elementQName == null; need to test and document this
    public Iterator<OMElement> AxiomContainer.getChildrenWithName(QName elementQName) {
        return new OMChildrenQNameIterator(getFirstOMChild(), elementQName);
    }
    
    private static final Mapper<CoreNode,OMSerializable> descendantsMapper = new Mapper<CoreNode,OMSerializable>() {
        public OMSerializable map(CoreNode node) {
            return (OMSerializable)node;
        }
    };
    
    public Iterator<OMSerializable> AxiomContainer.getDescendants(boolean includeSelf) {
        return coreGetNodes(includeSelf ? Axis.DESCENDANTS_OR_SELF : Axis.DESCENDANTS, descendantsMapper, AxiomSemantics.INSTANCE);
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
        XmlHandlerContentHandler handler = new XmlHandlerContentHandler(new SAXResultContentHandler(this), true);
        SAXResult result = new SAXResult();
        result.setHandler(handler);
        result.setLexicalHandler(handler);
        return result;
    }

    private XmlHandler AxiomContainer.createSerializer(MTOMXMLStreamWriter writer, boolean useExistingNamespaceContext) {
        StAXSerializer serializer = new StAXSerializer(writer);
        XmlHandler handler = new XmlDeclarationRewriterHandler(serializer, writer.getOutputFormat());
        CoreElement contextElement = getContextElement();
        if (contextElement != null) {
            handler = new XsiTypeFilterHandler(handler, contextElement);
        }
        return new NamespaceRepairingFilterHandler(handler,
                useExistingNamespaceContext ? new XMLStreamWriterNamespaceContextProvider(writer) : null,
                true);
    }
    
    public abstract CoreElement AxiomContainer.getContextElement();
    
    public final void AxiomContainer.serialize(XMLStreamWriter xmlWriter, boolean cache) throws XMLStreamException {
        // If the input xmlWriter is not an MTOMXMLStreamWriter, then wrapper it
        MTOMXMLStreamWriter writer = xmlWriter instanceof MTOMXMLStreamWriter ?
                (MTOMXMLStreamWriter) xmlWriter : 
                    new MTOMXMLStreamWriter(xmlWriter);
        try {
            internalSerialize(createSerializer(writer, true), writer.getOutputFormat(), cache);
        } catch (StreamException ex) {
            throw AxiomExceptionTranslator.toXMLStreamException(ex);
        }
        writer.flush();
    }

    private void AxiomContainer.serialize(MTOMXMLStreamWriter writer, boolean cache) throws XMLStreamException {
        try {
            try {
                internalSerialize(createSerializer(writer, false), writer.getOutputFormat(), cache);
            } catch (StreamException ex) {
                throw AxiomExceptionTranslator.toXMLStreamException(ex);
            }
        } finally {
            writer.close();
        }
    }
    
    private void AxiomContainer.serialize(Writer writer, boolean cache) throws XMLStreamException {
        serialize(new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer)), cache);
    }

    private void AxiomContainer.serialize(OutputStream output, OMOutputFormat format, boolean cache) throws XMLStreamException {
        serialize(new MTOMXMLStreamWriter(output, format, cache), cache);
    }

    private void AxiomContainer.serialize(Writer writer, OMOutputFormat format, boolean cache) throws XMLStreamException {
        serialize(new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer), format), cache);
    }

    public final void AxiomContainer.serialize(OutputStream output) throws XMLStreamException {
        serialize(output, new OMOutputFormat());
    }

    public final void AxiomContainer.serializeAndConsume(OutputStream output) throws XMLStreamException {
        serializeAndConsume(output, new OMOutputFormat());
    }

    public final void AxiomContainer.serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        serialize(output, format, true);
    }

    public final void AxiomContainer.serializeAndConsume(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        serialize(output, format, false);
    }

    public final void AxiomContainer.serialize(Writer writer) throws XMLStreamException {
        serialize(writer, true);
    }

    public final void AxiomContainer.serializeAndConsume(Writer writer) throws XMLStreamException {
        serialize(writer, false);
    }

    public final void AxiomContainer.serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        serialize(writer, format, true);
    }

    public final void AxiomContainer.serializeAndConsume(Writer writer, OMOutputFormat format) throws XMLStreamException {
        serialize(writer, format, false);
    }

    final void AxiomContainer.serializeChildren(XmlHandler handler, OMOutputFormat format, boolean cache) throws StreamException {
        if (getState() == AxiomContainer.DISCARDED) {
            Builder builder = coreGetBuilder();
            if (builder != null) {
                builder.debugDiscarded(this);
            }
            throw new NodeUnavailableException();
        }
        if (cache) {
            AxiomChildNode child = (AxiomChildNode)getFirstOMChild();
            while (child != null) {
                child.internalSerialize(handler, format, true);
                child = (AxiomChildNode)child.getNextOMSibling();
            }
        } else {
            // First, recursively serialize all child nodes that have already been created
            AxiomChildNode child = (AxiomChildNode)coreGetFirstChildIfAvailable();
            while (child != null) {
                child.internalSerialize(handler, format, cache);
                child = (AxiomChildNode)child.coreGetNextSiblingIfAvailable();
            }
            // Next, if the container is incomplete, disable caching (temporarily)
            // and serialize the nodes that have not been built yet by copying the
            // events from the underlying XMLStreamReader.
            if (!isComplete() && coreGetBuilder() != null) {
                Builder builder = coreGetBuilder();
                StAXHelper helper = new StAXHelper(builder.disableCaching(), handler);
                int depth = 0;
                loop: while (true) {
                    switch (helper.lookahead()) {
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
                    helper.next();
                }
                builder.reenableCaching(this);
            }
        }
    }

    public final void AxiomContainer.notifyChildComplete() {
        if (getState() == INCOMPLETE && coreGetBuilder() == null) {
            for (Iterator<OMNode> iterator = getChildren(); iterator.hasNext(); ) {
                OMNode node = iterator.next();
                if (!node.isComplete()) {
                    return;
                }
            }
            this.setComplete(true);
        }
    }

    public final void AxiomContainer.close(boolean build) {
        Builder builder = coreGetBuilder();
        if (build) {
            this.build();
        }
        
        if (builder != null) {
            builder.close();
        }
    }
}
