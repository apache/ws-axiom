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

package org.apache.axiom.om.impl.builder;

import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMHierarchyException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMElementEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.stax.XMLEventUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.Location;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * StAX based builder that produces a pure XML infoset compliant object model.
 * <p>
 * This class supports the {@link XMLStreamReader} extension defined by
 * {@link org.apache.axiom.ext.stax.datahandler.DataHandlerReader} as well as the legacy extension mechanism
 * defined in the documentation of {@link org.apache.axiom.util.stax.XMLStreamReaderUtils}.
 * <h3>Error handling</h3>
 * Usually, code that uses StAX directly just stops processing of an XML document
 * once the first parsing error has been reported. However, since Axiom
 * uses deferred parsing, and client code accesses the XML infoset using
 * an object model, things are more complicated. Indeed, if the XML
 * document is not well formed, the corresponding error might be reported
 * as a runtime exception by any call to a method of an OM node.
 * <p>
 * Typically the client code will have some error handling that will intercept
 * runtime exceptions and take appropriate action. Very often this error handling
 * code might want to access the object model again, for example to log the request that caused the
 * failure. This causes no problem except if the runtime exception was caused by a
 * parsing error, in which case Axiom would again try to pull events from the parser.
 * <p>
 * This would lead to a situation where Axiom accesses a parser that has reported a parsing
 * error before. While one would expect that after a first error reported by the parser, all
 * subsequent invocations of the parser will fail, this is not the case for all parsers
 * (at least not in all situations). Instead, the parser might be left in an inconsistent
 * state after the error. E.g. AXIOM-34 describes a case where Woodstox
 * encounters an error in {@link XMLStreamReader#getText()} but continues to return
 * (incorrect) events afterwards. The explanation for this behaviour might be that
 * the situation described here is quite uncommon when StAX is used directly (i.e. not through
 * Axiom).
 * <p>
 * To avoid this, the builder remembers exceptions thrown by the parser and rethrows
 * them during a call to {@link #next()}.
 */
public class StAXOMBuilder extends StAXBuilder {
    private static final Log log = LogFactory.getLog(StAXOMBuilder.class);
    
    private boolean doTrace = log.isDebugEnabled();
    
    /**
     * @deprecated
     */
    private static int nsCount = 0;

    // namespaceURI interning
    // default is false because most XMLStreamReader implementations don't do interning
    // due to performance impacts.  Thus a customer should not assume that a namespace
    // on an OMElement is interned.
    private boolean namespaceURIInterning = false;
    
    /**
     * Specifies whether the builder/parser should be automatically closed when the
     * {@link XMLStreamConstants#END_DOCUMENT} event is reached.
     */
    private boolean autoClose;
    
    private int lookAheadToken = -1;
    
    /**
     * Constructor StAXOMBuilder.
     *
     * @param ombuilderFactory
     * @param parser
     */
    public StAXOMBuilder(OMFactory ombuilderFactory, XMLStreamReader parser) {
        super(ombuilderFactory, parser);
    }

    /**
     * Constructor linked to existing element.
     *
     * @param factory
     * @param parser
     * @param element
     * @param characterEncoding of existing element
     */
    public StAXOMBuilder(OMFactory factory, 
                         XMLStreamReader parser, 
                         OMElement element, 
                         String characterEncoding) {
        // Use this constructor because the parser is passed the START_DOCUMENT state.
        super(factory, parser, characterEncoding);  
        elementLevel = 1;
        target = (OMContainerEx)element;
        populateOMElement(element);
    }
    
    /**
     * Constructor linked to existing element.
     *
     * @param factory
     * @param parser
     * @param element
     */
    public StAXOMBuilder(OMFactory factory, XMLStreamReader parser, OMElement element) {
        this(factory, parser, element, null);
    }

    /**
     * @param filePath - Path to the XML file
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public StAXOMBuilder(String filePath) throws XMLStreamException, FileNotFoundException {
        this(StAXUtils.createXMLStreamReader(new FileInputStream(filePath)));
    }

    /**
     * Constructor StAXOMBuilder.
     *
     * @param parser
     */
    public StAXOMBuilder(XMLStreamReader parser) {
        this(OMAbstractFactory.getOMFactory(), parser);
    }

    /**
     * @param inStream - instream which contains the XML
     * @throws XMLStreamException
     */
    public StAXOMBuilder(InputStream inStream) throws XMLStreamException {
        this(StAXUtils.createXMLStreamReader(inStream));
    }

    /**
     * @deprecated
     */
    public StAXOMBuilder() {
        super();
    }

    protected OMDocument createDocument() {
        return omfactory.createOMDocument(this);
    }

    /**
     * Method next.
     *
     * @return Returns int.
     * @throws OMException
     */
    public int next() throws OMException {
        try {
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
               
                // The current token should be the same as the 
                // one just obtained.  This bit of code is used to 
                // detect invalid parser state.
                if (doTrace) {
                    int currentParserToken = parser.getEventType();
                    if (currentParserToken != token) {
    
    
                        log.debug("WARNING: The current state of the parser is not equal to the " +
                                  "state just received from the parser. The current state in the paser is " +
                                  XMLEventUtils.getEventTypeString(currentParserToken) + " the state just received is " +
                                  XMLEventUtils.getEventTypeString(token));
    
                        /*
                          throw new OMException("The current token " + token + 
                                         " does not match the current event " +
                                         "reported by the parser token.  The parser did not update its state correctly.  " +
                                         "The parser is " + parser);
                         */
                    }
                }
                
                // Now log the current state of the parser
                if (doTrace) {
                    logParserState();
                }
               
                switch (token) {
                    case XMLStreamConstants.START_ELEMENT: {
                        OMNode node = createNextOMElement();
                        // If the node was created by a custom builder, then it will be complete;
                        // in this case, the target doesn't change
                        if (!node.isComplete()) {
                            target = (OMContainerEx)node;
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
                        endElement();
                        break;
                    case XMLStreamConstants.END_DOCUMENT:
                        done = true;
                        ((OMContainerEx) this.document).setComplete(true);
                        target = null;
                        break;
                    case XMLStreamConstants.SPACE:
                        try {
                            OMNode node = createOMText(XMLStreamConstants.SPACE);
                            if (node == null) {
                                continue;
                            }
                        } catch (OMHierarchyException ex) {
                            // The OM implementation doesn't allow text nodes at the current
                            // position in the tree. Since it is only whitespace, we can safely
                            // skip this event.
                            continue;
                        }
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
        } catch (XMLStreamException e) {
            throw new OMException(e);
        }
    }
    
    /**
     * Creates a new OMElement using either a CustomBuilder or 
     * the default Builder mechanism.
     * @return TODO
     */
    protected OMNode createNextOMElement() {
        OMNode newElement = null;
        if (elementLevel == 1 && this.customBuilderForPayload != null) {
            newElement = createWithCustomBuilder(customBuilderForPayload,  omfactory);
        } else if (customBuilders != null && elementLevel <= this.maxDepthForCustomBuilders) {
            String namespace = parser.getNamespaceURI();
            String localPart = parser.getLocalName();
            CustomBuilder customBuilder = getCustomBuilder(namespace, localPart);
            if (customBuilder != null) {
                newElement = createWithCustomBuilder(customBuilder, omfactory);
            }
        }
        if (newElement == null) {
            newElement = createOMElement();
        } else {
            elementLevel--; // Decrease level since custom builder read the end element event
        }
        return newElement;
    }
    
    protected OMNode createWithCustomBuilder(CustomBuilder customBuilder, OMFactory factory) {
        
        String namespace = parser.getNamespaceURI();
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
        
        OMNode node = customBuilder.create(namespace, localPart, target, parser, factory);
        
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
            log.debug("The current state of the parser is: ");
            logParserState();
        }
        return node;
    }
    
    /**
     * Dump the current event of the parser.
     */
    protected void logParserState() {
        if (doTrace) {
            int currentEvent = parser.getEventType();
            
            switch (currentEvent) {
            case XMLStreamConstants.START_ELEMENT:
                log.trace("START_ELEMENT: ");
                log.trace("  QName: " + parser.getName());
                break;
            case XMLStreamConstants.START_DOCUMENT:
                log.trace("START_DOCUMENT: ");
                break;
            case XMLStreamConstants.CHARACTERS:
                log.trace("CHARACTERS: ");
                // This can bust up a datahandler
                //log.trace(   "[" + parser.getText() + "]");
                break;
            case XMLStreamConstants.CDATA:
                log.trace("CDATA: ");
                // This can but
                //log.trace(   "[" + parser.getText() + "]");
                break;
            case XMLStreamConstants.END_ELEMENT:
                log.trace("END_ELEMENT: ");
                log.trace("  QName: " + parser.getName());
                break;
            case XMLStreamConstants.END_DOCUMENT:
                log.trace("END_DOCUMENT: ");
                break;
            case XMLStreamConstants.SPACE:
                log.trace("SPACE: ");
                //log.trace(   "[" + parser.getText() + "]");
                break;
            case XMLStreamConstants.COMMENT:
                log.trace("COMMENT: ");
                //log.trace(   "[" + parser.getText() + "]");
                break;
            case XMLStreamConstants.DTD:
                log.trace("DTD: ");
                log.trace(   "[" + parser.getText() + "]");
                break;
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                log.trace("PROCESSING_INSTRUCTION: ");
                log.trace("   [" + parser.getPITarget() + "][" +
                            parser.getPIData() + "]");
                break;
            case XMLStreamConstants.ENTITY_REFERENCE:
                log.trace("ENTITY_REFERENCE: ");
                log.trace("    " + parser.getLocalName() + "[" +
                            parser.getText() + "]");
                break;
            default :
                log.trace("UNKNOWN_STATE: " + currentEvent);
            
            }
        }
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
    // This method is not meant to be overridden. Override constructNode to create model specific OMElement instances.
    protected final OMNode createOMElement() throws OMException {
        OMElement node = constructNode(target, parser.getLocalName());
        populateOMElement(node);
        return node;
    }

    /**
     * Instantiate the appropriate {@link OMElement} implementation for the current element. This
     * method may be overridden by subclasses to support model specific {@link OMElement} types. The
     * implementation of this method is expected to initialize the {@link OMElement} with the
     * specified local name and to add it to the specified parent. However, the implementation
     * should not set the namespace of the element or process the attributes of the element. This is
     * taken care of by the caller of this method.
     * 
     * @param parent
     *            the parent for the element
     * @param elementName
     *            the local name for the element
     * @return the newly created {@link OMElement}; must not be <code>null</code>
     */
    protected OMElement constructNode(OMContainer parent, String elementName) {
        return omfactory.createOMElement(parser.getLocalName(), target, this);
    }
    
    /**
     * Method createOMText.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    protected OMNode createComment() throws OMException {
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
            target = (OMContainerEx)document;
        } else {
            target = (OMContainerEx)((OMElement)target).getParent();
        }
    }

    public OMElement getDocumentElement() {
        return getDocumentElement(false);
    }

    public OMElement getDocumentElement(boolean discardDocument) {
        OMElement element = getDocument().getOMDocumentElement();
        if (discardDocument) {
            OMNodeEx nodeEx = (OMNodeEx)element;
            nodeEx.setParent(null);
            nodeEx.setPreviousOMSibling(null);
            nodeEx.setNextOMSibling(null);
            document = null;
        }
        return element;
    }

    /**
     * Method processNamespaceData.
     *
     * @param node
     */
    protected void processNamespaceData(OMElement node) {
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
            
            ((OMElementEx)node).addNamespaceDeclaration(namespaceURI, prefix);
        }

        // set the own namespace
        String namespaceURI = parser.getNamespaceURI();
        String prefix = parser.getPrefix();

        // See NOTE_A above
        BuilderUtil.setNamespace(node, namespaceURI, prefix, isNamespaceURIInterning());
    }

    /**
     * @param doDebug
     * @deprecated
     */
    public void setDoDebug(boolean doDebug) {
        this.doTrace = doDebug;
    }

    /**
     * @deprecated A builder doesn't need to generate prefixes.
     */
    protected String createPrefix() {
        return "ns" + nsCount++;
    }

    /**
     * Set namespace uri interning
     * @param b
     */
    public void setNamespaceURIInterning(boolean b) {
        this.namespaceURIInterning = b;
    }
    
    /**
     * @return if namespace uri interning 
     */
    public boolean isNamespaceURIInterning() {
        return this.namespaceURIInterning;
    }
    
    /**
     * For internal use only.
     * 
     * @param autoClose
     */
    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    /**
     * Pushes the virtual parser ahead one token.
     * If a look ahead token was calculated it is returned.
     * @return next token
     * @throws XMLStreamException
     */
    int parserNext() throws XMLStreamException {
        if (lookAheadToken >= 0) {
            int token = lookAheadToken;
            lookAheadToken = -1; // Reset
            return token;
        } else {
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
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    elementLevel++;
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    elementLevel--;
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    if (elementLevel != 0) {
                        throw new OMException("Unexpected END_DOCUMENT event");
                    }
                    if (autoClose) {
                        close();
                    }
                    break;
            }
            return event;
        }
    }
    
    /**
     * This method looks ahead to the next start element.
     * @return true if successful
     */
    public boolean lookahead()  {
        try {
            while (true) {
                if (lookAheadToken < 0) {
                    lookAheadToken = parserNext();
                }
                if (lookAheadToken == XMLStreamConstants.START_ELEMENT) {
                    return true;
                } else if (lookAheadToken == XMLStreamConstants.END_ELEMENT ||
                        lookAheadToken == XMLStreamConstants.START_DOCUMENT ||
                        lookAheadToken == XMLStreamConstants.END_DOCUMENT) {
                    next();
                    return false;  // leaving scope...start element not found
                } else {
                    next();  // continue looking past whitespace etc.
                }
            }
        } catch (XMLStreamException e) {
            throw new OMException(e);
        }
    }
    
    /**
     * Check if the node for the current token has already been created or if the parser is ahead
     * of the builder.
     * 
     * @return A return value of <code>true</code> indicates that the parser is one token ahead
     *         of the builder, i.e. that the node for the current token has not been created yet.
     *         This state can only be reached by a call to {@link #lookahead()}, and the
     *         current token is always a {@link XMLStreamConstants#START_ELEMENT START_ELEMENT}.
     *         The information related to that element can be obtained by calls to
     *         {@link #getName()}, {@link #getNamespace()}, {@link #getPrefix()},
     *         {@link #getAttributeCount()}, {@link #getAttributeName(int)},
     *         {@link #getAttributeNamespace(int)}, {@link #getAttributePrefix(int)},
     *         {@link #getNamespaceCount()}, {@link #getNamespacePrefix(int)} and
     *         {@link #getNamespaceUri(int)}.
     *         <p>
     *         A return value of <code>false</code> indicates that the node corresponding to the
     *         current token hold by the parser has already been created.
     */
    public boolean isLookahead() {
        return lookAheadToken >= 0;
    }
}
