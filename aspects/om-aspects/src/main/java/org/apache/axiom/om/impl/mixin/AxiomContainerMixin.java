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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.core.Axis;
import org.apache.axiom.core.Builder;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.ElementMatcher;
import org.apache.axiom.core.Mappers;
import org.apache.axiom.core.impl.builder.BuilderImpl;
import org.apache.axiom.core.stream.NamespaceContextProvider;
import org.apache.axiom.core.stream.NamespaceRepairingFilterHandler;
import org.apache.axiom.core.stream.NamespaceURIInterningFilterHandler;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.sax.input.XmlHandlerContentHandler;
import org.apache.axiom.core.stream.serializer.Serializer;
import org.apache.axiom.core.stream.stax.pull.output.StAXPivot;
import org.apache.axiom.core.stream.stax.push.input.XMLStreamWriterNamespaceContextProvider;
import org.apache.axiom.mime.PartDataHandler;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.XOPEncoded;
import org.apache.axiom.om.impl.OMMultipartWriter;
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.common.SAXResultContentHandler;
import org.apache.axiom.om.impl.common.builder.OMXMLParserWrapperImpl;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.stream.NamespaceContextPreservationFilterHandler;
import org.apache.axiom.om.impl.stream.XmlDeclarationRewriterHandler;
import org.apache.axiom.om.impl.stream.XsiTypeFilterHandler;
import org.apache.axiom.om.impl.stream.sax.XMLReaderImpl;
import org.apache.axiom.om.impl.stream.stax.pull.AxiomXMLStreamReaderExtensionFactory;
import org.apache.axiom.om.impl.stream.stax.push.XMLStreamWriterHandler;
import org.apache.axiom.om.impl.stream.xop.ContentIDGenerator;
import org.apache.axiom.om.impl.stream.xop.ContentIDGeneratorImpl;
import org.apache.axiom.om.impl.stream.xop.OptimizationPolicy;
import org.apache.axiom.om.impl.stream.xop.OptimizationPolicyImpl;
import org.apache.axiom.om.impl.stream.xop.XOPEncodingFilterHandler;
import org.apache.axiom.util.io.IOUtils;
import org.xml.sax.InputSource;

@org.apache.axiom.weaver.annotation.Mixin(AxiomContainer.class)
public abstract class AxiomContainerMixin implements AxiomContainer {
    private static final OMXMLStreamReaderConfiguration defaultReaderConfiguration = new OMXMLStreamReaderConfiguration();
    
    public final OMXMLParserWrapper getBuilder() {
        BuilderImpl builder = (BuilderImpl)coreGetBuilder();
        if (builder == null) {
            return null;
        } else {
            OMXMLParserWrapper facade = (OMXMLParserWrapper)builder.getFacade();
            if (facade == null) {
                facade = new OMXMLParserWrapperImpl(builder, null);
            }
            return facade;
        }
    }

    public final XMLStreamReader getXMLStreamReader() {
        return getXMLStreamReader(true);
    }
    
    public final XMLStreamReader getXMLStreamReaderWithoutCaching() {
        return getXMLStreamReader(false);
    }

    public final XMLStreamReader getXMLStreamReader(boolean cache) {
        return getXMLStreamReader(cache, defaultReaderConfiguration);
    }
    
    public XMLStreamReader getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration) {
        return defaultGetXMLStreamReader(cache, configuration);
    }
    
    public final XMLStreamReader defaultGetXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration) {
        StAXPivot pivot = new StAXPivot(AxiomXMLStreamReaderExtensionFactory.INSTANCE);
        XmlHandler handler = pivot;
        if (configuration.isNamespaceURIInterning()) {
            handler = new NamespaceURIInterningFilterHandler(handler);
        }
        CoreElement contextElement = getContextElement();
        if (contextElement != null) {
            if (configuration.isPreserveNamespaceContext()) {
                handler = new NamespaceContextPreservationFilterHandler(handler, contextElement);
            } else {
                for (Iterator<OMNamespace> it = ((OMElement)contextElement).getNamespacesInScope(); it.hasNext(); ) {
                    OMNamespace ns = it.next();
                    pivot.setPrefix(ns.getPrefix(), ns.getNamespaceURI());
                }
                handler = new XsiTypeFilterHandler(handler, contextElement);
            }
        }
        try {
            pivot.setReader(coreGetReader(handler, cache, true));
        } catch (StreamException ex) {
            throw new OMException(ex);
        }
        return pivot;
    }
    
    public final XOPEncoded<XMLStreamReader> getXOPEncodedStreamReader(boolean cache) {
        StAXPivot pivot = new StAXPivot(AxiomXMLStreamReaderExtensionFactory.INSTANCE);
        XOPEncodingFilterHandler encoder = new XOPEncodingFilterHandler(pivot, ContentIDGenerator.DEFAULT, OptimizationPolicy.ALL);
        try {
            pivot.setReader(coreGetReader(encoder, cache, true));
        } catch (StreamException ex) {
            throw new OMException(ex);
        }
        return new XOPEncoded<XMLStreamReader>(pivot, encoder);
    }
    
    public final AxiomChildNode prepareNewChild(OMNode omNode) {
        AxiomChildNode child;
        // Careful here: if the child was created by another Axiom implementation, it doesn't
        // necessarily implement AxiomChildNode
        if (omNode.getOMFactory().getMetaFactory().equals(getOMFactory().getMetaFactory())) {
            child = (AxiomChildNode)omNode;
        } else {
            child = (AxiomChildNode)getOMFactory().importInformationItem(omNode);
        }
        checkChild(omNode);
        return child;
    }

    public final void addChild(OMNode omNode) {
        try {
            coreAppendChild(prepareNewChild(omNode));
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }
    
    public OMNode getFirstOMChild() {
        try {
            return (OMNode)coreGetFirstChild();
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }
    
    public void removeChildren() {
        try {
            coreRemoveChildren(AxiomSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }
    
    public Iterator<OMNode> getChildren() {
        return coreGetNodes(Axis.CHILDREN, AxiomChildNode.class, Mappers.<OMNode>identity(), AxiomSemantics.INSTANCE);
    }

    public Iterator<OMElement> getChildrenWithLocalName(String localName) {
        return coreGetElements(Axis.CHILDREN, AxiomElement.class,
                ElementMatcher.BY_LOCAL_NAME, null, localName,
                Mappers.<OMElement>identity(), AxiomSemantics.INSTANCE);
    }

    public Iterator<OMElement> getChildrenWithNamespaceURI(String uri) {
        return coreGetElements(Axis.CHILDREN, AxiomElement.class,
                ElementMatcher.BY_NAMESPACE_URI, uri, null,
                Mappers.<OMElement>identity(), AxiomSemantics.INSTANCE);
    }

    public Iterator<OMElement> getChildrenWithName(QName name) {
        return coreGetElements(Axis.CHILDREN, AxiomElement.class,
                ElementMatcher.BY_QNAME, name.getNamespaceURI(), name.getLocalPart(),
                Mappers.<OMElement>identity(), AxiomSemantics.INSTANCE);
    }
    
    public final OMElement getFirstChildWithName(QName name) throws OMException {
        try {
            CoreChildNode child = coreGetFirstChild();
            while (child != null) {
                if (child instanceof AxiomElement) {
                    AxiomElement element = (AxiomElement)child;
                    if (name.getLocalPart().equals(element.coreGetLocalName()) && name.getNamespaceURI().equals(element.coreGetNamespaceURI())) {
                        return element;
                    }
                }
                child = child.coreGetNextSibling();
            }
            return null;
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    public final SAXSource getSAXSource(boolean cache) {
        return new SAXSource(new XMLReaderImpl(this, cache), new InputSource());
    }

    public final SAXResult getSAXResult() {
        XmlHandlerContentHandler handler = new XmlHandlerContentHandler(new SAXResultContentHandler(this), true);
        SAXResult result = new SAXResult();
        result.setHandler(handler);
        result.setLexicalHandler(handler);
        return result;
    }

    private void serialize(XmlHandler handler, NamespaceContextProvider namespaceContextProvider, OMOutputFormat format, boolean cache) throws StreamException {
        handler = new XmlDeclarationRewriterHandler(handler, format);
        CoreElement contextElement = getContextElement();
        if (contextElement != null) {
            handler = new XsiTypeFilterHandler(handler, contextElement);
        }
        handler = new NamespaceRepairingFilterHandler(handler, namespaceContextProvider, true);
        try {
            internalSerialize(handler, cache);
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    private void serializeAndSurfaceIOException(XmlHandler handler, NamespaceContextProvider namespaceContextProvider, OMOutputFormat format, boolean cache) throws IOException {
        try {
            serialize(handler, namespaceContextProvider, format, cache);
        } catch (StreamException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            } else {
                throw new OMException(ex);
            }
        }
    }

    public final void serialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        try {
            serialize(new XMLStreamWriterHandler(writer), new XMLStreamWriterNamespaceContextProvider(writer), new OMOutputFormat(), cache);
        } catch (StreamException ex) {
            throw AxiomExceptionTranslator.toXMLStreamException(ex);
        }
    }

    public final void serialize(Writer writer, boolean cache) throws IOException {
        serialize(writer, new OMOutputFormat(), cache);
    }

    public final void serialize(OutputStream out, boolean cache) throws IOException {
        serialize(out, new OMOutputFormat(), cache);
    }

    public final void serialize(OutputStream out, OMOutputFormat format, boolean cache) throws IOException {
        String encoding = format.getCharSetEncoding();
        if (encoding == null) { //Default encoding is UTF-8
            format.setCharSetEncoding(encoding = OMOutputFormat.DEFAULT_CHAR_SET_ENCODING);
        }

        OMMultipartWriter multipartWriter;
        OutputStream rootPartOutputStream;
        if (format.isOptimized()) {
            multipartWriter = new OMMultipartWriter(out, format);
            rootPartOutputStream = multipartWriter.writeRootPart();
        } else {
            multipartWriter = null;
            rootPartOutputStream = out;
        }
        
        Serializer serializer = new Serializer(rootPartOutputStream, encoding);
        
        XmlHandler handler;
        XOPEncodingFilterHandler encoder;
        if (format.isOptimized()) {
            handler = encoder = new XOPEncodingFilterHandler(
                    serializer, 
                    new ContentIDGeneratorImpl(format),
                    new OptimizationPolicyImpl(format));
        } else {
            handler = serializer;
            encoder = null;
        }
        
        serializeAndSurfaceIOException(handler, null, format, cache);

        if (encoder != null) {
            rootPartOutputStream.close();
            for (String contentID : encoder.getContentIDs()) {
                DataHandler dataHandler = encoder.getDataHandler(contentID);
                if (cache || !(dataHandler instanceof PartDataHandler)) {
                    multipartWriter.writePart(dataHandler, contentID);
                } else {
                    OutputStream part = multipartWriter.writePart(dataHandler.getContentType(), contentID);
                    IOUtils.copy(((PartDataHandler)dataHandler).getPart().getInputStream(false), part, -1);
                    part.close();
                }
            }
            multipartWriter.complete();
        };
    }

    public final void serialize(Writer writer, OMOutputFormat format, boolean cache) throws IOException {
        serializeAndSurfaceIOException(new Serializer(writer), null, format, cache);
    }

    public final void serialize(OutputStream output) throws XMLStreamException {
        serialize(output, new OMOutputFormat());
    }

    public final void serializeAndConsume(OutputStream output) throws XMLStreamException {
        serializeAndConsume(output, new OMOutputFormat());
    }

    public final void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        try {
            serialize(output, format, true);
        } catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    public final void serializeAndConsume(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        try {
            serialize(output, format, false);
        } catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    public final void serialize(Writer writer) throws XMLStreamException {
        try {
            serialize(writer, true);
        } catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    public final void serializeAndConsume(Writer writer) throws XMLStreamException {
        try {
            serialize(writer, false);
        } catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    public final void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        try {
            serialize(writer, format, true);
        } catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    public final void serializeAndConsume(Writer writer, OMOutputFormat format) throws XMLStreamException {
        try {
            serialize(writer, format, false);
        } catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    public final void close(boolean build) {
        Builder builder = coreGetBuilder();
        if (build) {
            this.build();
        }
        
        if (builder != null) {
            builder.close();
        }
    }
}
