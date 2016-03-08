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
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder;
import org.apache.axiom.om.ds.custombuilder.CustomBuilderSupport;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder.Selector;
import org.apache.axiom.om.impl.builder.Detachable;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import java.io.Closeable;

public class StAXOMBuilder extends AbstractBuilder implements CustomBuilderSupport {
    private final StAXHelper helper;

    private final Detachable detachable;
    
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
    
    public final String getNamespaceURI() {
        return helper.parser.getNamespaceURI();
    }

    public final String getLocalName() {
        return helper.parser.getLocalName();
    }

    public final String getPrefix() {
        return helper.parser.getPrefix();
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
}
