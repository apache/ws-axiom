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

package org.apache.axiom.om.impl.common.builder;

import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.DeferredParsingException;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.builder.Builder;
import org.apache.axiom.om.impl.builder.CustomBuilder;
import org.apache.axiom.om.impl.builder.CustomBuilderSupport;
import org.apache.axiom.om.impl.builder.Detachable;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.util.stax.XMLEventUtils;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.axiom.util.xml.QNameMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.namespace.QName;

import java.io.Closeable;

/**
 * Internal implementation class.
 */
/* Implementation note about error handling
 * ----------------------------------------
 * 
 * Usually, code that uses StAX directly just stops processing of an XML document
 * once the first parsing error has been reported. However, since Axiom
 * uses deferred parsing, and client code accesses the XML infoset using
 * an object model, things are more complicated. Indeed, if the XML
 * document is not well formed, the corresponding error might be reported
 * as a runtime exception by any call to a method of an OM node.
 * 
 * Typically the client code will have some error handling that will intercept
 * runtime exceptions and take appropriate action. Very often this error handling
 * code might want to access the object model again, for example to log the request that caused the
 * failure. This causes no problem except if the runtime exception was caused by a
 * parsing error, in which case Axiom would again try to pull events from the parser.
 * 
 * This would lead to a situation where Axiom accesses a parser that has reported a parsing
 * error before. While one would expect that after a first error reported by the parser, all
 * subsequent invocations of the parser will fail, this is not the case for all parsers
 * (at least not in all situations). Instead, the parser might be left in an inconsistent
 * state after the error. E.g. AXIOM-34 describes a case where Woodstox
 * encounters an error in XMLStreamReader#getText() but continues to return
 * (incorrect) events afterwards. The explanation for this behaviour might be that
 * the situation described here is quite uncommon when StAX is used directly (i.e. not through
 * Axiom).
 * 
 * To avoid this, the builder remembers exceptions thrown by the parser and rethrows
 * them during a call to next().
 */
public class StAXOMBuilder extends AbstractBuilder implements Builder, CustomBuilderSupport {
    private static final Log log = LogFactory.getLog(StAXOMBuilder.class);
    
    /** Field parser */
    private XMLStreamReader parser;

    private final Detachable detachable;
    private final Closeable closeable;
    
    // keeps the state of the parser access. if the parser is
    // accessed atleast once,this flag will be set

    /** Field parserAccessed */
    private boolean parserAccessed = false;
    private String charEncoding = null;
    
    /**
     * Specifies whether the builder/parser should be automatically closed when the
     * {@link XMLStreamConstants#END_DOCUMENT} event is reached.
     */
    private final boolean autoClose;
    
    private boolean _isClosed = false;              // Indicate if parser is closed

    // Fields for Custom Builder implementation
    private final PayloadSelector payloadSelector;
    private CustomBuilder customBuilderForPayload;
    private QNameMap<CustomBuilder> customBuilders;
    private int maxDepthForCustomBuilders = -1;
    
    /**
     * Reference to the {@link DataHandlerReader} extension of the parser, or <code>null</code> if
     * the parser doesn't support this extension.
     */
    private DataHandlerReader dataHandlerReader;
    
    /**
     * Stores exceptions thrown by the parser. Used to avoid accessing the parser
     * again after is has thrown a parse exception.
     */
    private Exception parserException;
    
    private int lookAheadToken = XMLStreamReader.START_DOCUMENT;

    protected StAXOMBuilder(NodeFactory nodeFactory, XMLStreamReader parser,
            boolean autoClose, Detachable detachable, Closeable closeable, Model model, PayloadSelector payloadSelector,
            AxiomSourcedElement root) {
        // TODO: disable namespace repairing for XMLStreamReader created from a parser
        super(nodeFactory, model, root, true);
        if (parser.getEventType() != XMLStreamReader.START_DOCUMENT) {
            throw new IllegalStateException("The XMLStreamReader must be positioned on a START_DOCUMENT event");
        }
        this.parser = parser;
        this.autoClose = autoClose;
        this.detachable = detachable;
        this.closeable = closeable;
        this.payloadSelector = payloadSelector;
        charEncoding = parser.getEncoding();
        dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(parser);
    }
    
    public StAXOMBuilder(NodeFactory nodeFactory, XMLStreamReader parser, boolean autoClose,
            Detachable detachable, Closeable closeable) {
        this(nodeFactory, parser, autoClose, detachable, closeable, PlainXMLModel.INSTANCE, PayloadSelector.DEFAULT, null);
    }
    
    public StAXOMBuilder(NodeFactory nodeFactory,
                         XMLStreamReader parser, 
                         AxiomSourcedElement element) {
        this(nodeFactory, parser, true, null, null, PlainXMLModel.INSTANCE, PayloadSelector.DEFAULT, element);
    }
    
    private static String normalize(String s) {
        return s == null ? "" : s;
    }
    
    private void createOMText(int textType) {
        if (textType == XMLStreamConstants.CHARACTERS && dataHandlerReader != null && dataHandlerReader.isBinary()) {
            TextContent data;
            if (dataHandlerReader.isDeferred()) {
                data = new TextContent(dataHandlerReader.getContentID(),
                        dataHandlerReader.getDataHandlerProvider(),
                        dataHandlerReader.isOptimized());
            } else {
                try {
                    data = new TextContent(dataHandlerReader.getContentID(),
                            dataHandlerReader.getDataHandler(),
                            dataHandlerReader.isOptimized());
                } catch (XMLStreamException ex) {
                    throw new OMException(ex);
                }
            }
            handler.processCharacterData(data, false);
        } else {
            // Some parsers (like Woodstox) parse text nodes lazily and may throw a
            // RuntimeException in getText()
            String text;
            try {
                text = parser.getText();
            } catch (RuntimeException ex) {
                parserException = ex;
                throw ex;
            }
            switch (textType) {
                case XMLStreamConstants.CHARACTERS:
                    handler.processCharacterData(text, false);
                    break;
                case XMLStreamConstants.SPACE:
                    handler.processCharacterData(text, true);
                    break;
                case XMLStreamConstants.CDATA:
                    handler.processCDATASection(text);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private void discarded(AxiomContainer container) {
        container.discarded();
        if (handler.discardTracker != null) {
            handler.discardTracker.put(container, new Throwable());
        }
    }
    
    public final void debugDiscarded(Object container) {
        if (log.isDebugEnabled() && handler.discardTracker != null) {
            Throwable t = handler.discardTracker.get(container);
            if (t != null) {
                log.debug("About to throw NodeUnavailableException. Location of the code that caused the node to be discarded/consumed:", t);
            }
        }
    }
    
    // For compatibility only
    public final void discard(OMElement element) throws OMException {
        discard((OMContainer)element);
        element.discard();
    }
    
    public final void discard(OMContainer container) {
        int targetElementLevel = handler.elementLevel;
        AxiomContainer current = handler.target;
        while (current != container) {
            targetElementLevel--;
            current = (AxiomContainer)((OMElement)current).getParent();
        }
        if (targetElementLevel == 0 || targetElementLevel == 1 && handler.document == null) {
            close();
            current = handler.target;
            while (true) {
                discarded(current);
                if (current == container) {
                    break;
                }
                current = (AxiomContainer)((OMElement)current).getParent();
            }
            return;
        }
        int skipDepth = 0;
        loop: while (true) {
            switch (parserNext()) {
                case XMLStreamReader.START_ELEMENT:
                    skipDepth++;
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (skipDepth > 0) {
                        skipDepth--;
                    } else {
                        discarded(handler.target);
                        boolean found = container == handler.target;
                        handler.target = (AxiomContainer)((OMElement)handler.target).getParent();
                        handler.elementLevel--;
                        if (found) {
                            break loop;
                        }
                    }
                    break;
                case XMLStreamReader.END_DOCUMENT:
                    if (skipDepth != 0 || handler.elementLevel != 0) {
                        throw new OMException("Unexpected END_DOCUMENT");
                    }
                    if (handler.target != handler.document) {
                        throw new OMException("Called discard for an element that is not being built by this builder");
                    }
                    discarded(handler.target);
                    handler.target = null;
                    handler.done = true;
                    break loop;
            }
        }
    }

    public final String getNamespaceURI() {
        return parser.getNamespaceURI();
    }

    /**
     * Method setCache.
     *
     * @param b
     */
    public final void setCache(boolean b) {
        if (parserAccessed && b) {
            throw new UnsupportedOperationException(
                    "parser accessed. cannot set cache");
        }
        handler.cache = b;
    }
    
    /**
     * @return true if caching
     */
    public final boolean isCache() {
        return handler.cache;
    }

    public final String getLocalName() {
        return parser.getLocalName();
    }

    public final String getPrefix() {
        return parser.getPrefix();
    }

    /**
     * Get the underlying {@link XMLStreamReader} used by this builder. Note that for this type of
     * builder, accessing the underlying parser implies that can no longer be used, and any attempt
     * to call {@link #next()} will result in an exception.
     * 
     * @return The {@link XMLStreamReader} object used by this builder. Note that the constraints
     *         described in the Javadoc of the <code>reader</code> parameter of the
     *         {@link CustomBuilder#create(String, String, OMContainer, XMLStreamReader, OMFactory)}
     *         method also apply to the stream reader returned by this method, i.e.:
     *         <ul>
     *         <li>The caller should use
     *         {@link org.apache.axiom.util.stax.xop.XOPUtils#getXOPEncodedStream(XMLStreamReader)}
     *         to get an XOP encoded stream from the return value.
     *         <li>To get access to the bare StAX parser implementation, the caller should use
     *         {@link org.apache.axiom.util.stax.XMLStreamReaderUtils#getOriginalXMLStreamReader(XMLStreamReader)}.
     *         </ul>
     * @throws IllegalStateException
     *             if the parser has already been accessed
     */
    public final Object getParser() {
        if (parserAccessed) {
            throw new IllegalStateException(
                    "Parser already accessed!");
        }
        if (!handler.cache) {
            parserAccessed = true;
            // Mark all containers in the hierarchy as discarded because they can no longer be built
            AxiomContainer current = handler.target;
            while (handler.elementLevel > 0) {
                discarded(current);
                current = (AxiomContainer)((OMElement)current).getParent();
                handler.elementLevel--;
            }
            if (current != null && current == handler.document) {
                discarded(current);
            }
            handler.target = null;
            return parser;
        } else {
            throw new IllegalStateException(
                    "cache must be switched off to access the parser");
        }
    }

    public final XMLStreamReader disableCaching() {
        handler.cache = false;
        // Always advance to the event right after the current node; this also takes
        // care of lookahead
        parserNext();
        if (log.isDebugEnabled()) {
            log.debug("Caching disabled; current element level is " + handler.elementLevel);
        }
        return parser;
    }
    
    // This method expects that the parser is currently positioned on the
    // end event corresponding to the container passed as parameter
    public final void reenableCaching(OMContainer container) {
        AxiomContainer current = handler.target;
        while (true) {
            discarded(current);
            if (handler.elementLevel == 0) {
                if (current != container || current != handler.document) {
                    throw new IllegalStateException();
                }
                break;
            }
            handler.elementLevel--;
            if (current == container) {
                break;
            }
            current = (AxiomContainer)((OMElement)current).getParent();
        }
        // Note that at this point current == container
        if (container == handler.document) {
            handler.target = null;
            handler.done = true;
        } else if (handler.elementLevel == 0 && handler.document == null) {
            // Consume the remaining event; for the rationale, see StAXOMBuilder#next()
            while (parserNext() != XMLStreamConstants.END_DOCUMENT) {
                // Just loop
            }
            handler.target = null;
            handler.done = true;
        } else {
            handler.target = (AxiomContainer)((OMElement)container).getParent();
        }
        if (log.isDebugEnabled()) {
            log.debug("Caching re-enabled; new element level: " + handler.elementLevel + "; done=" + handler.done);
        }
        if (handler.done && autoClose) {
            close();
        }
        handler.cache = true;
    }

    public final CustomBuilder registerCustomBuilder(QName qName, int maxDepth, CustomBuilder customBuilder) {
        CustomBuilder old = null;
        if (customBuilders == null) {
            customBuilders = new QNameMap<CustomBuilder>();
        } else {
            old = customBuilders.get(qName);
        }
        maxDepthForCustomBuilders = 
                (maxDepthForCustomBuilders > maxDepth) ?
                        maxDepthForCustomBuilders: maxDepth;
        customBuilders.put(qName, customBuilder);
        return old;
    }
    
    
    public final CustomBuilder registerCustomBuilderForPayload(CustomBuilder customBuilder) {
        CustomBuilder old = null;
        this.customBuilderForPayload = customBuilder;
        return old;
    }
    
    public final String getCharsetEncoding() {
        return handler.document.getCharsetEncoding();
    }

    public final void close() {
        try {
            if (!isClosed()) {
                parser.close();
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (Throwable e) {
            // Can't see a reason why we would want to surface an exception
            // while closing the parser.
            if (log.isDebugEnabled()) {
                log.debug("Exception occurred during parser close.  " +
                                "Processing continues. " + e);
            }
        } finally {
            _isClosed = true;
            handler.done = true;
            // Release the parser so that it can be GC'd or reused. This is important because the
            // object model keeps a reference to the builder even after the builder is complete.
            parser = null;
        }
    }

    public final Object getReaderProperty(String name) throws IllegalArgumentException {
        if (!isClosed()) {
            return parser.getProperty(name);
        } 
        return null;
    }

    /**
     * Returns the encoding style of the XML data
     * @return the character encoding, defaults to "UTF-8"
     */
    public final String getCharacterEncoding() {
        if(this.charEncoding == null){
            return "UTF-8";
        }
        return this.charEncoding;
    }
    
    public final boolean isClosed() {
        return _isClosed;
    }
    
    public final void detach() throws OMException {
        if (detachable != null) {
            detachable.detach();
        } else {
            while (!handler.done) {
                next();
            }
        }
    }
    
    /**
     * Forwards the parser one step further, if parser is not completed yet. If this is called after
     * parser is done, then throw an OMException. If the cache is set to false, then returns the
     * event, *without* building the OM tree. If the cache is set to true, then handles all the
     * events within this, and builds the object structure appropriately and returns the event.
     *
     * @return Returns int.
     * @throws OMException
     */
    public int next() throws OMException {
        if (!handler.cache) {
            throw new IllegalStateException("Can't process next node because caching is disabled");
        }
        // We need a loop here because we may decide to skip an event
        while (true) {
            if (handler.done) {
                throw new OMException();
            }
            int token = parserNext();
            if (!handler.cache) {
                return token;
            }
           
            // Note: if autoClose is enabled, then the parser may be null at this point
            
            switch (token) {
                case XMLStreamConstants.START_DOCUMENT:
                    handler.startDocument(charEncoding, parser.getVersion(), parser.getCharacterEncodingScheme(), parser.isStandalone());
                    break;
                case XMLStreamConstants.START_ELEMENT: {
                    createNextOMElement();
                    break;
                }
                case XMLStreamConstants.CHARACTERS:
                case XMLStreamConstants.CDATA:
                case XMLStreamConstants.SPACE:
                    createOMText(token);
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    handler.endElement();
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    handler.endDocument();
                    break;
                case XMLStreamConstants.COMMENT:
                    handler.processComment(parser.getText());
                    break;
                case XMLStreamConstants.DTD:
                    createDTD();
                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    handler.processProcessingInstruction(parser.getPITarget(), parser.getPIData());
                    break;
                case XMLStreamConstants.ENTITY_REFERENCE:
                    handler.processEntityReference(parser.getLocalName(), parser.getText());
                    break;
                default :
                    throw new OMException();
            }
            
            // TODO: this will fail if there is whitespace before the document element
            if (token != XMLStreamConstants.START_DOCUMENT && handler.target == null && !handler.done) {
                // We get here if the document has been discarded (by getDocumentElement(true)
                // or because the builder is linked to an OMSourcedElement) and
                // we just processed the END_ELEMENT event for the root element. In this case, we consume
                // the remaining events until we reach the end of the document. This serves several purposes:
                //  * It allows us to detect documents that have an epilog that is not well formed.
                //  * Many parsers will perform some cleanup when the end of the document is reached.
                //    For example, Woodstox will recycle the symbol table if the parser gets past the
                //    last END_ELEMENT. This improves performance because Woodstox by default interns
                //    all symbols; if the symbol table can be recycled, then this reduces the number of
                //    calls to String#intern().
                //  * If autoClose is set, the parser will be closed so that even more resources
                //    can be released.
                while (parserNext() != XMLStreamConstants.END_DOCUMENT) {
                    // Just loop
                }
                handler.done = true;
            }
            
            return token;
        }
    }
    
    /**
     * Creates a new OMElement using either a CustomBuilder or 
     * the default Builder mechanism.
     */
    private void createNextOMElement() {
        String namespaceURI = normalize(parser.getNamespaceURI());
        String localName = parser.getLocalName();
        String prefix = normalize(parser.getPrefix());
        OMElement newElement = null;
        if (customBuilderForPayload != null && payloadSelector.isPayload(handler.elementLevel+1, handler.target)) {
            newElement = createWithCustomBuilder(customBuilderForPayload);
        }
        if (newElement == null && customBuilders != null && handler.elementLevel < this.maxDepthForCustomBuilders) {
            CustomBuilder customBuilder = customBuilders.get(namespaceURI, localName);
            if (customBuilder != null) {
                newElement = createWithCustomBuilder(customBuilder);
            }
        }
        if (newElement == null) {
            handler.startElement(namespaceURI, localName, prefix);
            for (int i = 0, count = parser.getNamespaceCount(); i < count; i++) {
                handler.processNamespaceDeclaration(
                        normalize(parser.getNamespacePrefix(i)),
                        normalize(parser.getNamespaceURI(i)));
            }
            for (int i = 0, count = parser.getAttributeCount(); i < count; i++) {
                handler.processAttribute(
                        normalize(parser.getAttributeNamespace(i)),
                        parser.getAttributeLocalName(i),
                        normalize(parser.getAttributePrefix(i)),
                        parser.getAttributeValue(i),
                        parser.getAttributeType(i),
                        parser.isAttributeSpecified(i));
            }
            handler.attributesCompleted();
        }
    }
    
    private OMElement createWithCustomBuilder(CustomBuilder customBuilder) {
        
        String namespace = parser.getNamespaceURI();
        if (namespace == null) {
            namespace = "";
        }
        String localPart = parser.getLocalName();
        
        if (log.isDebugEnabled()) {
            log.debug("Invoking CustomBuilder, " + customBuilder.toString() + 
                      ", to the OMNode for {" + namespace + "}" + localPart);
        }
        
        // TODO: dirty hack part 1
        // The custom builder will use addNode to insert the new node into the tree. However,
        // addNode is expected to always add the new child at the end and will attempt to
        // build the parent node. We temporarily set complete to true to avoid this.
        // There is really an incompatibility between the contract of addNode and the
        // custom builder API. This should be fixed in Axiom 1.3.
        handler.target.setComplete(true);
        
        // Use target.getOMFactory() because the factory may actually be a SOAPFactory
        OMElement node = customBuilder.create(namespace, localPart, handler.target, parser, handler.target.getOMFactory());
        
        // TODO: dirty hack part 2
        handler.target.setComplete(false);
        
        if (log.isDebugEnabled()) {
            if (node != null) {
                log.debug("The CustomBuilder, " + customBuilder.toString() + 
                          "successfully constructed the OMNode for {" + namespace + "}" + localPart);
            } else {
                log.debug("The CustomBuilder, " + customBuilder.toString() + 
                          " did not construct an OMNode for {" + namespace + "}" + localPart +
                          ". The OMNode will be constructed using the installed stax om builder");
            }
        }
        return node;
    }
    
    private void createDTD() throws OMException {
        DTDReader dtdReader;
        try {
            dtdReader = (DTDReader)parser.getProperty(DTDReader.PROPERTY);
        } catch (IllegalArgumentException ex) {
            dtdReader = null;
        }
        if (dtdReader == null) {
            throw new OMException("Cannot create OMDocType because the XMLStreamReader doesn't support the DTDReader extension");
        }
        String internalSubset = getDTDText();
        // Woodstox returns an empty string if there is no internal subset
        if (internalSubset != null && internalSubset.length() == 0) {
            internalSubset = null;
        }
        handler.processDocumentTypeDeclaration(dtdReader.getRootName(), dtdReader.getPublicId(),
                dtdReader.getSystemId(), internalSubset);
    }
    
    /**
     * The getText() method for a DOCTYPE returns the 
     * subset of the DOCTYPE (not the direct infoset).
     * This may force the parser to get information from 
     * the network.
     * @return doctype subset
     * @throws OMException
     */
    private String getDTDText() throws OMException { 
        String text = null;
        try {
            text = parser.getText();
        } catch (RuntimeException e) {
            // Woodstox (and perhaps other parsers)
            // attempts to load the external subset even if
            // external enties is false.  So ignore this error
            // if external entity support is explicitly disabled.
            Boolean b = (Boolean) parser.getProperty(
                   XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES);
            if (b == null || b == Boolean.TRUE) {
                throw e;
            }
            if (log.isDebugEnabled()) {
                log.debug("An exception occurred while calling getText() for a DOCTYPE.  " +
                                "The exception is ignored because external " +
                                "entites support is disabled.  " +
                                "The ignored exception is " + e);
            }
        }
        return text;
    }

    public final OMElement getDocumentElement() {
        return getDocumentElement(false);
    }

    public final OMElement getDocumentElement(boolean discardDocument) {
        OMElement element = getDocument().getOMDocumentElement();
        if (discardDocument) {
            ((AxiomElement)element).detachAndDiscardParent();
            handler.document = null;
        }
        return element;
    }

    /**
     * Pushes the virtual parser ahead one token.
     * If a look ahead token was calculated it is returned.
     * @return next token
     * @throws DeferredParsingException
     */
    private int parserNext() {
        if (lookAheadToken >= 0) {
            if (log.isDebugEnabled()) {
                log.debug("Consuming look-ahead token " + XMLEventUtils.getEventTypeString(lookAheadToken));
            }
            int token = lookAheadToken;
            lookAheadToken = -1; // Reset
            return token;
        } else {
            try {
                if (parserException != null) {
                    log.warn("Attempt to access a parser that has thrown a parse exception before; " +
                    		"rethrowing the original exception.");
                    if (parserException instanceof XMLStreamException) {
                        throw (XMLStreamException)parserException;
                    } else {
                        throw (RuntimeException)parserException;
                    }
                }
                int event;
                try {
                    event = parser.next();
                } catch (XMLStreamException ex) {
                    parserException = ex;
                    throw ex;
                }
                if (event == XMLStreamConstants.END_DOCUMENT) {
                    if (handler.cache && handler.elementLevel != 0) {
                        throw new OMException("Unexpected END_DOCUMENT event");
                    }
                    if (autoClose) {
                        close();
                    }
                }
                return event;
            } catch (XMLStreamException ex) {
                throw new DeferredParsingException(ex);
            }
        }
    }
    
    /**
     * Look ahead to the next event. This method advanced the parser to the next event, but defers
     * creation of the corresponding node to the next call of {@link #next()}.
     * 
     * @return The type of the next event. If the return value is
     *         {@link XMLStreamConstants#START_ELEMENT START_ELEMENT}, then the information related
     *         to that element can be obtained by calls to {@link #getLocalName()},
     *         {@link #getNamespaceURI()} and {@link #getPrefix()}.
     */
    public final int lookahead() {
        if (lookAheadToken < 0) {
            lookAheadToken = parserNext();
        }
        return lookAheadToken;
    }

    public final AxiomContainer getTarget() {
        return handler.target;
    }
}
