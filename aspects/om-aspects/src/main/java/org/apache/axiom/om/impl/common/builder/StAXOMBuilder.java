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

import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder;
import org.apache.axiom.om.ds.custombuilder.CustomBuilderSupport;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder.Selector;
import org.apache.axiom.om.impl.builder.Detachable;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import java.io.Closeable;

public class StAXOMBuilder extends AbstractBuilder implements CustomBuilderSupport {
    private static final Log log = LogFactory.getLog(StAXOMBuilder.class);
    
    private final StAXHelper helper;

    private final Detachable detachable;
    
    // keeps the state of the parser access. if the parser is
    // accessed atleast once,this flag will be set

    /** Field parserAccessed */
    private boolean parserAccessed = false;
    private String charEncoding = null;
    
    private CustomBuilderManager customBuilderManager;
    
    protected StAXOMBuilder(NodeFactory nodeFactory, XMLStreamReader parser,
            boolean autoClose, Detachable detachable, Closeable closeable, Model model,
            AxiomSourcedElement root) {
        // TODO: disable namespace repairing for XMLStreamReader created from a parser
        super(nodeFactory, model, root, true);
        if (parser.getEventType() != XMLStreamReader.START_DOCUMENT) {
            throw new IllegalStateException("The XMLStreamReader must be positioned on a START_DOCUMENT event");
        }
        helper = new StAXHelper(parser, handler, closeable, autoClose);
        this.detachable = detachable;
        charEncoding = parser.getEncoding();
    }
    
    public StAXOMBuilder(NodeFactory nodeFactory, XMLStreamReader parser, boolean autoClose,
            Detachable detachable, Closeable closeable) {
        this(nodeFactory, parser, autoClose, detachable, closeable, PlainXMLModel.INSTANCE, null);
    }
    
    public StAXOMBuilder(NodeFactory nodeFactory,
                         XMLStreamReader parser, 
                         AxiomSourcedElement element) {
        this(nodeFactory, parser, true, null, null, PlainXMLModel.INSTANCE, element);
    }
    
    private void discarded(CoreParentNode container) {
        ((AxiomContainer)container).discarded();
        if (builderHandler.discardTracker != null) {
            builderHandler.discardTracker.put(container, new Throwable());
        }
    }
    
    public final void debugDiscarded(CoreParentNode container) {
        if (log.isDebugEnabled() && builderHandler.discardTracker != null) {
            Throwable t = builderHandler.discardTracker.get(container);
            if (t != null) {
                log.debug("About to throw NodeUnavailableException. Location of the code that caused the node to be discarded/consumed:", t);
            }
        }
    }
    
    // For compatibility only
    public final void discard(OMElement element) throws OMException {
        discard((CoreParentNode)element);
        element.discard();
    }
    
    public final void discard(CoreParentNode container) {
        int targetElementLevel = builderHandler.depth;
        Context current = builderHandler.context;
        while (current.target != container) {
            targetElementLevel--;
            current = current.parentContext;
        }
        if (targetElementLevel == 0 || targetElementLevel == 1 && builderHandler.document == null) {
            close();
            current = builderHandler.context;
            while (true) {
                discarded(current.target);
                if (current.target == container) {
                    break;
                }
                current = current.parentContext;
            }
            return;
        }
        int skipDepth = 0;
        loop: while (true) {
            switch (helper.parserNext()) {
                case XMLStreamReader.START_ELEMENT:
                    skipDepth++;
                    break;
                case XMLStreamReader.END_ELEMENT:
                    if (skipDepth > 0) {
                        skipDepth--;
                    } else {
                        discarded(builderHandler.context.target);
                        boolean found = container == builderHandler.context.target;
                        builderHandler.context = builderHandler.context.parentContext;
                        builderHandler.depth--;
                        if (found) {
                            break loop;
                        }
                    }
                    break;
                case XMLStreamReader.END_DOCUMENT:
                    if (skipDepth != 0 || builderHandler.depth != 0) {
                        throw new OMException("Unexpected END_DOCUMENT");
                    }
                    if (builderHandler.context.target != builderHandler.document) {
                        throw new OMException("Called discard for an element that is not being built by this builder");
                    }
                    discarded(builderHandler.context.target);
                    builderHandler.context = null;
                    builderHandler.done = true;
                    break loop;
            }
        }
    }

    public final String getNamespaceURI() {
        return helper.parser.getNamespaceURI();
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
        builderHandler.cache = b;
    }
    
    /**
     * @return true if caching
     */
    public final boolean isCache() {
        return builderHandler.cache;
    }

    public final String getLocalName() {
        return helper.parser.getLocalName();
    }

    public final String getPrefix() {
        return helper.parser.getPrefix();
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
        if (!builderHandler.cache) {
            parserAccessed = true;
            // Mark all containers in the hierarchy as discarded because they can no longer be built
            Context current = builderHandler.context;
            while (builderHandler.depth > 0) {
                discarded(current.target);
                current = current.parentContext;
                builderHandler.depth--;
            }
            if (current != null && current.target == builderHandler.document) {
                discarded(current.target);
            }
            builderHandler.context = null;
            return helper.parser;
        } else {
            throw new IllegalStateException(
                    "cache must be switched off to access the parser");
        }
    }

    public final XMLStreamReader disableCaching() {
        builderHandler.cache = false;
        // Always advance to the event right after the current node; this also takes
        // care of lookahead
        helper.parserNext();
        if (log.isDebugEnabled()) {
            log.debug("Caching disabled; current element level is " + builderHandler.depth);
        }
        return helper.parser;
    }
    
    // This method expects that the parser is currently positioned on the
    // end event corresponding to the container passed as parameter
    public final void reenableCaching(CoreParentNode container) {
        Context current = builderHandler.context;
        while (true) {
            discarded(current.target);
            if (builderHandler.depth == 0) {
                if (current.target != container || current.target != builderHandler.document) {
                    throw new IllegalStateException();
                }
                break;
            }
            builderHandler.depth--;
            if (current.target == container) {
                break;
            }
            current = current.parentContext;
        }
        // Note that at this point current == container
        if (container == builderHandler.document) {
            builderHandler.context = null;
            builderHandler.done = true;
        } else if (builderHandler.depth == 0 && builderHandler.document == null) {
            // Consume the remaining event; for the rationale, see StAXOMBuilder#next()
            while (helper.parserNext() != XMLStreamConstants.END_DOCUMENT) {
                // Just loop
            }
            builderHandler.context = null;
            builderHandler.done = true;
        } else {
            builderHandler.context = builderHandler.context.parentContext;
        }
        if (log.isDebugEnabled()) {
            log.debug("Caching re-enabled; new element level: " + builderHandler.depth + "; done=" + builderHandler.done);
        }
        if (builderHandler.done && helper.autoClose) {
            close();
        }
        builderHandler.cache = true;
    }

    @Override
    public void registerCustomBuilder(Selector selector, CustomBuilder customBuilder) {
        if (customBuilderManager == null) {
            customBuilderManager = new CustomBuilderManager();
            builderHandler.addListener(customBuilderManager);
        }
        customBuilderManager.register(selector, customBuilder);
    }
    
    public final String getCharsetEncoding() {
        return builderHandler.document.getCharsetEncoding();
    }

    public final void close() {
        helper.close();
    }

    public final Object getReaderProperty(String name) throws IllegalArgumentException {
        return helper.getReaderProperty(name);
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
        return helper.isClosed();
    }
    
    public final void detach() throws OMException {
        if (detachable != null) {
            detachable.detach();
        } else {
            while (!builderHandler.done) {
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
        if (!builderHandler.cache) {
            throw new IllegalStateException("Can't process next node because caching is disabled");
        }
        if (builderHandler.done) {
            throw new OMException();
        }
        int event = helper.next();
        builderHandler.executeDeferredListenerActions();
        
        // TODO: this will fail if there is whitespace before the document element
        if (event != XMLStreamConstants.START_DOCUMENT && builderHandler.depth == 0 && builderHandler.document == null && !builderHandler.done) {
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
            while (helper.parserNext() != XMLStreamConstants.END_DOCUMENT) {
                // Just loop
            }
            builderHandler.done = true;
        }
        
        return event;
    }
    
    public final OMElement getDocumentElement() {
        return getDocumentElement(false);
    }

    public final OMElement getDocumentElement(boolean discardDocument) {
        OMElement element = getDocument().getOMDocumentElement();
        if (discardDocument) {
            ((AxiomElement)element).detachAndDiscardParent();
            builderHandler.document = null;
        }
        return element;
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
        return helper.lookahead();
    }

    public final AxiomContainer getTarget() {
        return builderHandler.context.target;
    }
}
