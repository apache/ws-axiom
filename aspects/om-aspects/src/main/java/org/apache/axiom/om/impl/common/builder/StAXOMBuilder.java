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

import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.DeferredParsingException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.Builder;
import org.apache.axiom.om.impl.builder.CustomBuilder;
import org.apache.axiom.om.impl.builder.CustomBuilderSupport;
import org.apache.axiom.om.impl.builder.Detachable;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.OMFactoryEx;
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
import javax.xml.stream.Location;

import java.io.Closeable;
import java.util.LinkedHashMap;
import java.util.Map;

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
public class StAXOMBuilder implements Builder, CustomBuilderSupport {
    private static final Log log = LogFactory.getLog(StAXOMBuilder.class);
    
    /** Field parser */
    private XMLStreamReader parser;

    /** Field omfactory */
    private OMFactoryEx omfactory;
    
    private final Detachable detachable;
    private final Closeable closeable;

    /** Field lastNode */
    private AxiomContainer target;

    // returns the state of completion

    /** Field done */
    private boolean done = false;

    // keeps the state of the cache

    /** Field cache */
    private boolean cache = true;

    // keeps the state of the parser access. if the parser is
    // accessed atleast once,this flag will be set

    /** Field parserAccessed */
    private boolean parserAccessed = false;
    private OMDocument document;

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
     * Tracks the depth of the node identified by {@link #target}. By definition, the level of the
     * root element is defined as 1. Note that if caching is disabled, then this depth may be
     * different from the actual depth reached by the underlying parser.
     */
    private int elementLevel = 0;
    
    /**
     * Stores exceptions thrown by the parser. Used to avoid accessing the parser
     * again after is has thrown a parse exception.
     */
    private Exception parserException;
    
    /**
     * Stores the stack trace of the code that caused a node to be discarded or consumed. This is
     * only used if debug logging was enabled when builder was created.
     */
    private final Map<OMContainer,Throwable> discardTracker = log.isDebugEnabled() ? new LinkedHashMap<OMContainer,Throwable>() : null;
    
    // namespaceURI interning
    // default is false because most XMLStreamReader implementations don't do interning
    // due to performance impacts.  Thus a customer should not assume that a namespace
    // on an OMElement is interned.
    private boolean namespaceURIInterning = false;
    
    private int lookAheadToken = -1;
    
    private StAXOMBuilder(OMFactory omFactory, XMLStreamReader parser, String encoding,
            boolean autoClose, Detachable detachable, Closeable closeable, PayloadSelector payloadSelector) {
        omfactory = (OMFactoryEx)omFactory;
        this.parser = parser;
        this.autoClose = autoClose;
        this.detachable = detachable;
        this.closeable = closeable;
        this.payloadSelector = payloadSelector;
        charEncoding = encoding;
        dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(parser);
    }
    
    protected StAXOMBuilder(OMFactory omFactory, XMLStreamReader parser, boolean autoClose,
            Detachable detachable, Closeable closeable, PayloadSelector payloadSelector) {
        // The getEncoding information is only available at the START_DOCUMENT event.
        this(omFactory, parser, parser.getEncoding(), autoClose, detachable, closeable, payloadSelector);
        
    }
    
    public StAXOMBuilder(OMFactory omFactory, XMLStreamReader parser, boolean autoClose,
            Detachable detachable, Closeable closeable) {
        this(omFactory, parser, autoClose, detachable, closeable, PayloadSelector.DEFAULT);
    }
    
    public StAXOMBuilder(OMFactory factory, 
                         XMLStreamReader parser, 
                         OMElement element, 
                         String characterEncoding) {
        // Use this constructor because the parser is passed the START_DOCUMENT state.
        this(factory, parser, characterEncoding, true, null, null, PayloadSelector.DEFAULT);  
        elementLevel = 1;
        target = (AxiomContainer)element;
        populateOMElement(element);
    }
    
    /**
     * Method processAttributes.
     *
     * @param node
     */
    private void processAttributes(OMElement node) {
        int attribCount = parser.getAttributeCount();
        for (int i = 0; i < attribCount; i++) {
            String uri = parser.getAttributeNamespace(i);
            String prefix = parser.getAttributePrefix(i);


            OMNamespace namespace = null;
            if (uri != null && uri.length() > 0) {

                // prefix being null means this elements has a default namespace or it has inherited
                // a default namespace from its parent
                namespace = node.findNamespace(uri, prefix);
                if (namespace == null) {
                    namespace = node.declareNamespace(uri, prefix);
                }
            }

            // todo if the attributes are supposed to namespace qualified all the time
            // todo then this should throw an exception here

            OMAttribute attr = node.addAttribute(parser.getAttributeLocalName(i),
                              parser.getAttributeValue(i), namespace);
            attr.setAttributeType(parser.getAttributeType(i));
            ((CoreAttribute)attr).coreSetSpecified(parser.isAttributeSpecified(i));
        }
    }

    /**
     * This method will check whether the text can be optimizable using IS_BINARY flag. If that is
     * set then we try to get the data handler.
     *
     * @param textType
     * @return omNode
     */
    private OMNode createOMText(int textType) {
        if (dataHandlerReader != null && dataHandlerReader.isBinary()) {
            Object dataHandlerObject;
            if (dataHandlerReader.isDeferred()) {
                dataHandlerObject = dataHandlerReader.getDataHandlerProvider();
            } else {
                try {
                    dataHandlerObject = dataHandlerReader.getDataHandler();
                } catch (XMLStreamException ex) {
                    throw new OMException(ex);
                }
            }
            OMText text = omfactory.createOMText(target, dataHandlerObject, dataHandlerReader.isOptimized(), true);
            String contentID = dataHandlerReader.getContentID();
            if (contentID != null) {
                text.setContentID(contentID);
            }
            return text;
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
            return omfactory.createOMText(target, text, textType, true);
        }
    }

    private void discarded(AxiomContainer container) {
        container.discarded();
        if (discardTracker != null) {
            discardTracker.put(container, new Throwable());
        }
    }
    
    public final void debugDiscarded(Object container) {
        if (log.isDebugEnabled() && discardTracker != null) {
            Throwable t = discardTracker.get(container);
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
        int targetElementLevel = elementLevel;
        AxiomContainer current = target;
        while (current != container) {
            targetElementLevel--;
            current = (AxiomContainer)((OMElement)current).getParent();
        }
        if (targetElementLevel == 0 || targetElementLevel == 1 && document == null) {
            close();
            current = target;
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
                        discarded(target);
                        boolean found = container == target;
                        target = (AxiomContainer)((OMElement)target).getParent();
                        elementLevel--;
                        if (found) {
                            break loop;
                        }
                    }
                    break;
                case XMLStreamReader.END_DOCUMENT:
                    if (skipDepth != 0 || elementLevel != 0) {
                        throw new OMException("Unexpected END_DOCUMENT");
                    }
                    if (target != document) {
                        throw new OMException("Called discard for an element that is not being built by this builder");
                    }
                    discarded(target);
                    target = null;
                    done = true;
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
        cache = b;
    }
    
    /**
     * @return true if caching
     */
    public final boolean isCache() {
        return cache;
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
        if (!cache) {
            parserAccessed = true;
            // Mark all containers in the hierarchy as discarded because they can no longer be built
            AxiomContainer current = target;
            while (elementLevel > 0) {
                discarded(current);
                current = (AxiomContainer)((OMElement)current).getParent();
                elementLevel--;
            }
            if (current != null && current == document) {
                discarded(current);
            }
            target = null;
            return parser;
        } else {
            throw new IllegalStateException(
                    "cache must be switched off to access the parser");
        }
    }

    public final XMLStreamReader disableCaching() {
        cache = false;
        // Always advance to the event right after the current node; this also takes
        // care of lookahead
        parserNext();
        if (log.isDebugEnabled()) {
            log.debug("Caching disabled; current element level is " + elementLevel);
        }
        return parser;
    }
    
    // This method expects that the parser is currently positioned on the
    // end event corresponding to the container passed as parameter
    public final void reenableCaching(OMContainer container) {
        AxiomContainer current = target;
        while (true) {
            discarded(current);
            if (elementLevel == 0) {
                if (current != container || current != document) {
                    throw new IllegalStateException();
                }
                break;
            }
            elementLevel--;
            if (current == container) {
                break;
            }
            current = (AxiomContainer)((OMElement)current).getParent();
        }
        // Note that at this point current == container
        if (container == document) {
            target = null;
            done = true;
        } else if (elementLevel == 0 && document == null) {
            // Consume the remaining event; for the rationale, see StAXOMBuilder#next()
            while (parserNext() != XMLStreamConstants.END_DOCUMENT) {
                // Just loop
            }
            target = null;
            done = true;
        } else {
            target = (AxiomContainer)((OMElement)container).getParent();
        }
        if (log.isDebugEnabled()) {
            log.debug("Caching re-enabled; new element level: " + elementLevel + "; done=" + done);
        }
        if (done && autoClose) {
            close();
        }
        cache = true;
    }

    /**
     * Method isCompleted.
     *
     * @return Returns boolean.
     */
    public final boolean isCompleted() {
        return done;
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
    
    /**
     * Return CustomBuilder associated with the namespace/localPart
     * @param namespace
     * @param localPart
     * @return CustomBuilder or null
     */ 
    protected final CustomBuilder getCustomBuilder(String namespace, String localPart) {
        if (customBuilders == null) {
            return null;
        }
        return customBuilders.get(namespace, localPart);
    }

    private void createDocumentIfNecessary() {
        if (document == null && parser.getEventType() == XMLStreamReader.START_DOCUMENT) {
            document = createDocument();
            if (charEncoding != null) {
                document.setCharsetEncoding(charEncoding);
            }
            document.setXMLVersion(parser.getVersion());
            document.setXMLEncoding(parser.getCharacterEncodingScheme());
            document.setStandalone(parser.isStandalone() ? "yes" : "no");
            target = (AxiomContainer)document;
        }
    }
    
    public final OMDocument getDocument() {
        createDocumentIfNecessary();
        if (document == null) {
            throw new UnsupportedOperationException("There is no document linked to this builder");
        }
        return document;
    }

    public final String getCharsetEncoding() {
        return document.getCharsetEncoding();
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
            done = true;
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
            while (!done) {
                next();
            }
        }
    }
    
    protected OMDocument createDocument() {
        return omfactory.createOMDocument(this);
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
        if (!cache) {
            throw new IllegalStateException("Can't process next node because caching is disabled");
        }
        // We need a loop here because we may decide to skip an event
        while (true) {
            if (done) {
                throw new OMException();
            }
            createDocumentIfNecessary();
            int token = parserNext();
            if (!cache) {
                return token;
            }
           
            // Note: if autoClose is enabled, then the parser may be null at this point
           
            switch (token) {
                case XMLStreamConstants.START_ELEMENT: {
                    elementLevel++;
                    OMNode node = createNextOMElement();
                    // If the node was created by a custom builder, then it will be complete;
                    // in this case, the target doesn't change
                    if (!node.isComplete()) {
                        target = (AxiomContainer)node;
                    }
                    break;
                }
                case XMLStreamConstants.CHARACTERS:
                    createOMText(XMLStreamConstants.CHARACTERS);
                    break;
                case XMLStreamConstants.CDATA:
                    createOMText(XMLStreamConstants.CDATA);
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    elementLevel--;
                    endElement();
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    done = true;
                    ((AxiomContainer) this.document).setComplete(true);
                    target = null;
                    break;
                case XMLStreamConstants.SPACE:
                    createOMText(XMLStreamConstants.SPACE);
                    break;
                case XMLStreamConstants.COMMENT:
                    createComment();
                    break;
                case XMLStreamConstants.DTD:
                    createDTD();
                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    createPI();
                    break;
                case XMLStreamConstants.ENTITY_REFERENCE:
                    createEntityReference();
                    break;
                default :
                    throw new OMException();
            }
            
            if (target == null && !done) {
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
                done = true;
            }
            
            return token;
        }
    }
    
    /**
     * Creates a new OMElement using either a CustomBuilder or 
     * the default Builder mechanism.
     * @return TODO
     */
    private OMNode createNextOMElement() {
        OMNode newElement = null;
        if (customBuilderForPayload != null && payloadSelector.isPayload(elementLevel, target)) {
            newElement = createWithCustomBuilder(customBuilderForPayload);
        }
        if (newElement == null && customBuilders != null && elementLevel <= this.maxDepthForCustomBuilders) {
            String namespace = parser.getNamespaceURI();
            String localPart = parser.getLocalName();
            CustomBuilder customBuilder = getCustomBuilder(namespace, localPart);
            if (customBuilder != null) {
                newElement = createWithCustomBuilder(customBuilder);
            }
        }
        if (newElement == null) {
            newElement = createOMElement();
        } else {
            elementLevel--; // Decrease level since custom builder read the end element event
        }
        return newElement;
    }
    
    private OMNode createWithCustomBuilder(CustomBuilder customBuilder) {
        
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
        target.setComplete(true);
        
        // Use target.getOMFactory() because the factory may actually be a SOAPFactory
        OMNode node = customBuilder.create(namespace, localPart, target, parser, target.getOMFactory());
        
        // TODO: dirty hack part 2
        target.setComplete(false);
        
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
    
    /**
     * Populate element with data from parser START_ELEMENT event. This is used when the source of
     * data for an element needs to be parsed on demand. The supplied element must already be set to
     * the proper name and namespace.
     *
     * @param node element to be populated
     */
    private void populateOMElement(OMElement node) {
        // create the namespaces
        processNamespaceData(node);
        // fill in the attributes
        processAttributes(node);
        Location location = parser.getLocation();
        if(location != null) {
            node.setLineNumber(location.getLineNumber());
        }
    }

    /**
     * Method createOMElement.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    private OMNode createOMElement() throws OMException {
        AxiomElement node = omfactory.createAxiomElement(
                determineElementType(target, elementLevel, parser.getNamespaceURI(), parser.getLocalName()),
                parser.getLocalName(), target, this);
        populateOMElement(node);
        return node;
    }

    /**
     * Determine the element type to use for the current element. This method may be overridden by
     * subclasses to support model specific {@link OMElement} types.
     * 
     * @param parent
     *            the parent for the element
     * @param elementName
     *            the local name for the element
     * @return the type of element to create; must not be <code>null</code>
     */
    protected Class<? extends AxiomElement> determineElementType(OMContainer parent,
            int elementLevel, String namespaceURI, String localName) {
        return AxiomElement.class;
    }
    
    /**
     * Method createOMText.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    private OMNode createComment() throws OMException {
        return omfactory.createOMComment(target, parser.getText(), true);
    }

    /**
     * Method createDTD.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    protected OMNode createDTD() throws OMException {
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
        return omfactory.createOMDocType(target, dtdReader.getRootName(), dtdReader.getPublicId(),
                dtdReader.getSystemId(), internalSubset, true);
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

    /**
     * Method createPI.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    protected OMNode createPI() throws OMException {
        return omfactory.createOMProcessingInstruction(target, parser.getPITarget(), parser.getPIData(), true);
    }

    protected OMNode createEntityReference() {
        return omfactory.createOMEntityReference(target, parser.getLocalName(), parser.getText(), true);
    }
    
    private void endElement() {
        target.setComplete(true);
        if (elementLevel == 0) {
            // This is relevant for OMSourcedElements and for the case where the document has been discarded
            // using getDocumentElement(true). In these cases, this will actually set target to null. In all
            // other cases, this will have the same effect as the instruction in the else clause.
            target = (AxiomContainer)document;
        } else {
            target = (AxiomContainer)((OMElement)target).getParent();
        }
    }

    public final OMElement getDocumentElement() {
        return getDocumentElement(false);
    }

    public final OMElement getDocumentElement(boolean discardDocument) {
        OMElement element = getDocument().getOMDocumentElement();
        if (discardDocument) {
            ((AxiomElement)element).detachAndDiscardParent();
            document = null;
        }
        return element;
    }

    /**
     * Method processNamespaceData.
     *
     * @param node
     */
    private void processNamespaceData(OMElement node) {
        int namespaceCount = parser.getNamespaceCount();
        for (int i = 0; i < namespaceCount; i++) {
            String prefix = parser.getNamespacePrefix(i);

            //if the namespace is not defined already when we write the start tag declare it
            // check whether this is the default namespace and make sure we have not declared that earlier
            String namespaceURI = parser.getNamespaceURI(i);
            
            if (namespaceURI == null) {
                // No need to care about interning here; String literals are always interned
                namespaceURI = "";
            } else {
                // NOTE_A:
                // By default most parsers don't intern the namespace.
                // Unfortunately the property to detect interning on the delegate parsers is hard to detect.
                // Woodstox has a proprietary property on the XMLInputFactory.
                // IBM has a proprietary property on the XMLStreamReader.
                // For now only force the interning if requested.
                if (isNamespaceURIInterning()) {
                    namespaceURI = namespaceURI.intern();
                }
            }
            
            if (prefix == null) {
                prefix = "";
            }
            
            ((AxiomElement)node).addNamespaceDeclaration(namespaceURI, prefix);
        }

        // set the own namespace
        String namespaceURI = parser.getNamespaceURI();
        String prefix = parser.getPrefix();

        // See NOTE_A above
        BuilderUtil.setNamespace(node, namespaceURI, prefix, isNamespaceURIInterning());
    }

    /**
     * Set namespace uri interning
     * @param b
     */
    public final void setNamespaceURIInterning(boolean b) {
        this.namespaceURIInterning = b;
    }
    
    /**
     * @return if namespace uri interning 
     */
    public final boolean isNamespaceURIInterning() {
        return this.namespaceURIInterning;
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
                    if (cache && elementLevel != 0) {
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
        return target;
    }
}
