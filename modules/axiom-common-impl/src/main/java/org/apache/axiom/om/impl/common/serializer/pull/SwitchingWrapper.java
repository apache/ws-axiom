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

package org.apache.axiom.om.impl.common.serializer.pull;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.common.IContainer;
import org.apache.axiom.om.impl.common.OMDataSourceUtil;
import org.apache.axiom.util.namespace.MapBasedNamespaceContext;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class used internally by {@link OMXMLStreamReaderExAdapter}.
 */
final class SwitchingWrapper extends PullSerializerState
    implements DataHandlerReader, CharacterDataReader, DTDReader, XMLStreamConstants {
    
    private static final Log log = LogFactory.getLog(SwitchingWrapper.class);
    
    private final PullSerializer serializer;
    
    /**
     * The current node, corresponding to the current event.
     */
    private OMSerializable node;

    /**
     * If the current node is an {@link OMContainer}, then this flag indicates whether the current
     * event is the start event ({@link XMLStreamConstants#START_DOCUMENT} or
     * {@link XMLStreamConstants#START_ELEMENT}) or the end event
     * ({@link XMLStreamConstants#END_DOCUMENT} or {@link XMLStreamConstants#END_ELEMENT}) for that
     * node. In the latter case, we have already visited the node before, hence the name of the
     * attribute.
     */
    private boolean visited;

    /** Field builder */
    private OMXMLParserWrapper builder;

    /** Field parser */
    private XMLStreamReader parser;
    
    /**
     * The root node, i.e. the node from which the {@link XMLStreamReader} has been requested.
     */
    private final OMContainer rootNode;

    // Navigable means the output should be taken from the navigator.
    // As soon as the navigator returns a null navigable will be reset
    // to false and the subsequent events will be taken from the builder
    // or the parser directly.

    /** Field NAVIGABLE */
    private static final short NAVIGABLE = 0;
    
    /**
     * Indicates that the last event before the final {@link XMLStreamConstants#END_DOCUMENT} event
     * has been generated. The next event will be {@link XMLStreamConstants#END_DOCUMENT} and state
     * will transition to {@link #DOCUMENT_COMPLETE}.
     */
    private static final short COMPLETED = 2;
    
    /**
     * Indicates that the final {@link XMLStreamConstants#END_DOCUMENT} event has been generated.
     */
    private static final short DOCUMENT_COMPLETE = 4;

    /** Field state */
    private short state;

    /** Field currentEvent Default set to START_DOCUMENT */
    private int currentEvent;

    /**
     * Specifies whether the original document content is cached (i.e. whether the object model is
     * built) or can be consumed.
     */
    private final boolean cache;
    
    /**
     * Specifies whether additional namespace declarations should be generated to preserve the
     * namespace context. See {@link OMElement#getXMLStreamReader(boolean, boolean)} for more
     * information about the meaning of this attribute.
     */
    private final boolean preserveNamespaceContext;
    
    /** Track depth to ensure we stop generating events when we are done with the root node. */
    int depth = 0;

    // Cache attributes and namespaces. This avoids creating a new Iterator for every call
    // to getAttributeXXX and getNamespaceXXX. A value of -1 indicates that the
    // attributes or namespaces for the current element have not been loaded yet. The
    // two arrays are resized on demand.
    private int attributeCount = -1;
    private OMAttribute[] attributes = new OMAttribute[16];
    private int namespaceCount = -1;
    private OMNamespace[] namespaces = new OMNamespace[16];
    
    /**
     * Constructor.
     *
     * @param serializer
     * @param builder
     * @param startNode
     * @param cache
     * @param preserveNamespaceContext
     */
    SwitchingWrapper(PullSerializer serializer, OMXMLParserWrapper builder, OMContainer startNode,
                            boolean cache, boolean preserveNamespaceContext) {
        this.serializer = serializer;
        this.builder = builder;
        this.rootNode = startNode;
        this.cache = cache;
        this.preserveNamespaceContext = preserveNamespaceContext;

        // If the start node is a document it become the current node. If the start node
        // is an element, then there is no current node, because there is no node
        // corresponding to the current event (START_DOCUMENT).
        if (startNode instanceof OMDocument) {
            node = startNode;
        }
        currentEvent = START_DOCUMENT;
    }

    DTDReader getDTDReader() {
        return this;
    }

    DataHandlerReader getDataHandlerReader() {
        return this;
    }

    String getPrefix() {
        if (parser != null && currentEvent != END_DOCUMENT) {
            return parser.getPrefix();
        } else {
            if ((currentEvent == START_ELEMENT)
                    || (currentEvent == END_ELEMENT)) {
                return ((OMElement)node).getPrefix();
            } else {
                throw new IllegalStateException();
            }
        }
    }

    String getNamespaceURI() {
        if (parser != null && currentEvent != END_DOCUMENT) {
            return parser.getNamespaceURI();
        } else {
            if ((currentEvent == START_ELEMENT)
                    || (currentEvent == END_ELEMENT)) {
                return ((OMElement)node).getNamespaceURI();
            } else {
                throw new IllegalStateException();
            }
        }
    }

    String getLocalName() {
        if (parser != null && currentEvent != END_DOCUMENT) {
            return parser.getLocalName();
        } else {
            switch (currentEvent) {
                case START_ELEMENT:
                case END_ELEMENT:
                    return ((OMElement)node).getLocalName();
                case ENTITY_REFERENCE:
                    return ((OMEntityReference)node).getName();
                default:
                    throw new IllegalStateException();
            }
        }
    }

    QName getName() {
        if (parser != null && currentEvent != END_DOCUMENT) {
            return parser.getName();
        } else {
            if ((currentEvent == START_ELEMENT)
                    || (currentEvent == END_ELEMENT)) {
                // START quick & dirty hack
                if (node instanceof OMSourcedElement) {
                    // TODO: Need to force expansion to solve an issue in Axis2 where the returned QName is incorrect.
                    //       Note that in previous versions of SwitchingWrapper, the sourced element was always expanded
                    //       at this point (because SwitchingWrapper was looking 2 nodes ahead).
                    ((OMElement)node).getFirstOMChild();
                }
                // END quick & dirty hack
                return ((OMElement)node).getQName();
            } else {
                throw new IllegalStateException();
            }
        }
    }

    int getTextLength() {
        if (parser != null) {
            return parser.getTextLength();
        } else {
            return getTextFromNode().length();
        }
    }

    int getTextStart() {
        if (parser != null) {
            return parser.getTextStart();
        } else {
            switch (currentEvent) {
                case CHARACTERS:
                case CDATA:
                case COMMENT:
                case SPACE:
                    // getTextCharacters always returns a new char array and the start
                    // index is therefore always 0
                    return 0;
                default:
                    // getTextStart() is not allowed for DTD and ENTITY_REFERENCE events; see
                    // the table in the Javadoc of XMLStreamReader
                    throw new IllegalStateException();
            }
        }
    }

    int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
            throws XMLStreamException {
        if (parser != null) {
            return parser.getTextCharacters(sourceStart, target, targetStart, length);
        } else {
            String text = getTextFromNode();
            int copied = Math.min(length, text.length()-sourceStart);
            text.getChars(sourceStart, sourceStart + copied, target, targetStart);
            return copied;
        }
    }

    char[] getTextCharacters() {
        if (parser != null) {
            return parser.getTextCharacters();
        } else {
            return getTextFromNode().toCharArray();
        }
    }

    String getText() {
        if (parser != null) {
            return parser.getText();
        } else {
            // For DTD and ENTITY_REFERENCE events, only getText() is allowed, but not getTextCharacters() etc.
            // (see the table in the Javadoc of XMLStreamReader); therefore we handle these event here,
            // and the other ones in getTextFromNode().
            switch (currentEvent) {
                case DTD:
                    String internalSubset = ((OMDocType)node).getInternalSubset();
                    // Woodstox returns the empty string if there is no internal subset
                    return internalSubset != null ? internalSubset : "";
                case ENTITY_REFERENCE:
                    return ((OMEntityReference)node).getReplacementText();
                default:
                    return getTextFromNode();
            }
        }
    }
    
    /**
     * Get the text for the current node. This methods applies to events for which all
     * <code>getText</code> methods are valid. This excludes {@link XMLStreamConstants#DTD} events
     * for which only {@link #getText()} is valid.
     * 
     * @return the text for the current node
     */
    private String getTextFromNode() {
        switch (currentEvent) {
            case CHARACTERS:
            case CDATA:
            case SPACE:
                return ((OMText)node).getText();
            case COMMENT:
                return ((OMComment)node).getValue();
            default:
                throw new IllegalStateException();
        }
    }

    public void writeTextTo(Writer writer) throws XMLStreamException, IOException {
        if (parser != null) {
            XMLStreamReaderUtils.writeTextTo(parser, writer);
        } else {
            switch (currentEvent) {
                case CHARACTERS:
                case CDATA:
                case SPACE:
                    OMText text = (OMText)node;
                    if (text.isCharacters()) {
                        writer.write(text.getTextCharacters());
                    } else {
                        // TODO: we should cover the binary case in an optimized way
                        writer.write(text.getText());
                    }
                    break;
                case COMMENT:
                    writer.write(((OMComment)node).getValue());
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    // todo this should be improved
    int getEventType() {
        return currentEvent;
    }

    private void loadAttributes() {
        if (attributeCount == -1) {
            attributeCount = 0;
            for (Iterator it = ((OMElement)node).getAllAttributes(); it.hasNext(); ) {
                OMAttribute attr = (OMAttribute)it.next();
                if (attributeCount == attributes.length) {
                    OMAttribute[] newAttributes = new OMAttribute[attributes.length*2];
                    System.arraycopy(attributes, 0, newAttributes, 0, attributes.length);
                    attributes = newAttributes;
                }
                attributes[attributeCount] = attr;
                attributeCount++;
            }
        }
    }
    
    private OMAttribute getAttribute(int index) {
        loadAttributes();
        return attributes[index];
    }
    
    private void loadNamespaces() {
        if (namespaceCount == -1) {
            namespaceCount = 0;
            for (Iterator it = ((OMElement)node).getAllDeclaredNamespaces(); it.hasNext(); ) {
                addNamespace((OMNamespace)it.next());
            }
            if (preserveNamespaceContext && node == rootNode) {
                OMElement element = (OMElement)node;
                while (true) {
                    OMContainer container = element.getParent();
                    if (container instanceof OMElement) {
                        element = (OMElement)container;
                        decl: for (Iterator it = element.getAllDeclaredNamespaces(); it.hasNext(); ) {
                            OMNamespace ns = (OMNamespace)it.next();
                            String prefix = ns.getPrefix();
                            for (int i=0; i<namespaceCount; i++) {
                                if (namespaces[i].getPrefix().equals(prefix)) {
                                    continue decl;
                                }
                            }
                            addNamespace(ns);
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }
    
    private OMNamespace getNamespace(int index) {
        loadNamespaces();
        return namespaces[index];
    }
    
    private void addNamespace(OMNamespace ns) {
        // TODO: verify if this check is actually still necessary
        // Axiom internally creates an OMNamespace instance for the "xml" prefix, even
        // if it is not declared explicitly. Filter this instance out.
        if (!"xml".equals(ns.getPrefix())) {
            if (namespaceCount == namespaces.length) {
                OMNamespace[] newNamespaces = new OMNamespace[namespaces.length*2];
                System.arraycopy(namespaces, 0, newNamespaces, 0, namespaces.length);
                namespaces = newNamespaces;
            }
            namespaces[namespaceCount] = ns;
            namespaceCount++;
        }
    }
    
    String getNamespaceURI(int i) {
        if (parser != null) {
            String uri = parser.getNamespaceURI(i);

            /*
              The following line is necessary to overcome an issue where the empty
              namespace URI returning null rather than the empty string. Our resolution
              is to return "" if the return is actually null

              Note that this is not the case for  getNamespaceURI(prefix) method
              where the contract clearly specifies that the return may be null

            */
            if (uri == null) {
                uri = "";
            }
            return uri;
        } else {
            if (currentEvent == START_ELEMENT || currentEvent == END_ELEMENT) {
                return getNamespace(i).getNamespaceURI();
            } else {
                throw new IllegalStateException();
            }
        }
    }

    String getNamespacePrefix(int i) {
        if (parser != null) {
            return parser.getNamespacePrefix(i);
        } else {
            if (currentEvent == START_ELEMENT || currentEvent == END_ELEMENT) {
                String prefix = getNamespace(i).getPrefix();
                return prefix.length() == 0 ? null : prefix; 
            } else {
                throw new IllegalStateException();
            }
        }
    }

    int getNamespaceCount() {
        if (parser != null && currentEvent != END_DOCUMENT) {
            return parser.getNamespaceCount();
        } else {
            if (currentEvent == START_ELEMENT || currentEvent == END_ELEMENT) {
                loadNamespaces();
                return namespaceCount;
            } else {
                throw new IllegalStateException();
            }
        }
    }

    boolean isAttributeSpecified(int i) {
        if (parser != null) {
            return parser.isAttributeSpecified(i);
        } else {
            if (currentEvent == START_ELEMENT) {
                // The Axiom object model doesn't store this information,
                // but returning true is a reasonable default.
                return true;
            } else {
                throw new IllegalStateException(
                        "attribute type accessed in illegal event!");
            }
        }
    }

    String getAttributeValue(int i) {
        if (parser != null) {
            return parser.getAttributeValue(i);
        } else {
            if (currentEvent == START_ELEMENT) {
                return getAttribute(i).getAttributeValue();
            } else {
                throw new IllegalStateException(
                        "attribute type accessed in illegal event!");
            }
        }
    }

    String getAttributeType(int i) {
        if (parser != null) {
            return parser.getAttributeType(i);
        } else {
            if (currentEvent == START_ELEMENT) {
                return getAttribute(i).getAttributeType();
            } else {
                throw new IllegalStateException(
                        "attribute type accessed in illegal event!");
            }
        }
    }

    String getAttributePrefix(int i) {
        if (parser != null) {
            return parser.getAttributePrefix(i);
        } else {
            if (currentEvent == START_ELEMENT) {
                return getAttribute(i).getPrefix();
            } else {
                throw new IllegalStateException(
                        "attribute prefix accessed in illegal event!");
            }
        }
    }

    String getAttributeLocalName(int i) {
        if (parser != null) {
            return parser.getAttributeLocalName(i);
        } else {
            if (currentEvent == START_ELEMENT) {
                return getAttribute(i).getLocalName();
            } else {
                throw new IllegalStateException(
                        "attribute localName accessed in illegal event!");
            }
        }
    }

    String getAttributeNamespace(int i) {
        if (parser != null) {
            return parser.getAttributeNamespace(i);
        } else {
            if (currentEvent == START_ELEMENT) {
                return getAttribute(i).getNamespaceURI();
            } else {
                throw new IllegalStateException(
                        "attribute nameSpace accessed in illegal event!");
            }
        }
    }

    QName getAttributeName(int i) {
        if (parser != null) {
            return parser.getAttributeName(i);
        } else {
            if (currentEvent == START_ELEMENT) {
                return getAttribute(i).getQName();
            } else {
                throw new IllegalStateException(
                        "attribute count accessed in illegal event!");
            }
        }
    }

    int getAttributeCount() {
        int returnCount = 0;
        if (parser != null) {
            returnCount = parser.getAttributeCount();
        } else {
            if (currentEvent == START_ELEMENT) {
                loadAttributes();
                returnCount = attributeCount;
            } else {
                throw new IllegalStateException(
                        "attribute count accessed in illegal event (" +
                                currentEvent + ")!");
            }
        }
        return returnCount;
    }

    String getAttributeValue(String s, String s1) {
        String returnString = null;
        if (parser != null) {
            returnString = parser.getAttributeValue(s, s1);
        } else {
            if (currentEvent == START_ELEMENT) {
                QName qname = new QName(s, s1);
                OMAttribute attrib = ((OMElement) node).getAttribute(qname);
                if (attrib != null) {
                    returnString = attrib.getAttributeValue();
                }
            } else {
                throw new IllegalStateException(
                        "attribute type accessed in illegal event!");
            }
        }
        return returnString;
    }

    Boolean isWhiteSpace() {
        return null;
    }

    String getNamespaceURI(String prefix) {
        String returnString = null;
        if (parser != null) {
            returnString = parser.getNamespaceURI(prefix);
        } else {
            if (currentEvent == START_ELEMENT || currentEvent == END_ELEMENT) {

                if (node instanceof OMElement) {
                    OMNamespace namespaceURI =
                            ((OMElement) node).findNamespaceURI(prefix);
                    return namespaceURI != null ? namespaceURI.getNamespaceURI() : null;
                }
            }
        }
        return returnString;
    }

    void close() throws XMLStreamException {
        try {
            // If there is a builder, it controls its parser
            if (builder != null && builder instanceof StAXBuilder) {
                StAXBuilder staxBuilder = (StAXBuilder) builder;
                staxBuilder.close();
                setParser(null);
            } else {
                if (parser != null) {
                    parser.close();
                }
            }
        } finally {
            // Note that as a side effect of this instruction, the SwitchingWrapper instance
            // will become unreachable and the parser can be GC'd or reused.
            serializer.switchState(ClosedState.INSTANCE);
        }
    }

    boolean hasNext() throws XMLStreamException {
        return currentEvent != END_DOCUMENT;
    }

    String getElementText() throws XMLStreamException {
        // Let PullSerializer handle this method
        return null;
    }

    /**
     * @param n OMNode
     * @return true if this OMNode should be considered a leaf node
     */
    private boolean isLeaf(OMSerializable n) {
        if (n instanceof OMContainer) {
            return serializer.isDataSourceALeaf() && isOMSourcedElement(n) && !((OMSourcedElement)n).isExpanded() && n != rootNode;
        } else {
            return true;
        }
    }

    private boolean isOMSourcedElement(OMSerializable node) {
        if (node instanceof OMSourcedElement) {
            try {
                return ((OMSourcedElement)node).getDataSource() != null;
            } catch (UnsupportedOperationException e) {
                // Operation unsupported for some implementations
                return false;
            }
        } else {
            return false;
        }
    }
    
    /**
     * @param node
     * @return first child or null
     */
    private OMNode _getFirstChild(OMContainer node) {
        if (cache) {
            return node.getFirstOMChild();
        } else {
            return ((IContainer)node).getFirstOMChildIfAvailable();
        }
    }

    /**
     * @param node
     * @return next sibling or null
     */
    private OMNode getNextSibling(OMNode node) {
        if (cache || isOMSourcedElement(node)) {
            return node.getNextOMSibling();
        } else {
            return ((OMNodeEx) node).getNextOMSiblingIfAvailable();
        }
    }

    int next() throws XMLStreamException {
        switch (state) {
            case DOCUMENT_COMPLETE:
                throw new NoSuchElementException("End of the document reached");
            case COMPLETED:
                state = DOCUMENT_COMPLETE;
                currentEvent = END_DOCUMENT;
                break;
            case NAVIGABLE:
                OMSerializable nextNode;
                if (node == null) {
                    // We get here if rootNode is an element and the current event is START_DOCUMENT
                    nextNode = rootNode;
                } else if (!isLeaf(node) && !visited) {
                    OMNode firstChild = _getFirstChild((OMContainer) node);
                    if (firstChild != null) {
                        nextNode = firstChild;
                        visited = false;
                    } else if (node.isComplete()) {
                        nextNode = node;
                        visited = true;
                    } else {
                        nextNode = null;
                    }
                } else {
                    OMNode current = (OMNode)node;
                    OMContainer parent = current.getParent();
                    OMNode nextSibling = getNextSibling(current);
                    if (nextSibling != null) {
                        nextNode = nextSibling;
                        visited = false;
                    } else if (parent.isComplete() || parent.getBuilder() == null) { // TODO: review this condition
                        nextNode = parent;
                        visited = true;
                    } else {
                        nextNode = null;
                    }
                }
                if (nextNode instanceof OMSourcedElement) {
                    OMSourcedElement element = (OMSourcedElement)nextNode;
                    if (!element.isExpanded()) {
                        OMDataSource ds = element.getDataSource();
                        if (ds != null && !(OMDataSourceUtil.isPushDataSource(ds)
                                || (cache && OMDataSourceUtil.isDestructiveRead(ds)))) {
                            XMLStreamReader reader = ds.getReader();
                            while (reader.next() != START_ELEMENT) {
                                // Just loop
                            }
                            serializer.switchState(new IncludeWrapper(serializer, this, reader));
                            node = nextNode;
                            visited = true;
                            return START_ELEMENT;
                        }
                    }
                }
                if (nextNode != null) {
                    node = nextNode;
                    currentEvent = generateEvents(node);
                    attributeCount = -1;
                    namespaceCount = -1;
                } else {
                    OMContainer container;
                    if (!(node instanceof OMContainer) || visited) {
                        container = ((OMNode)node).getParent();
                    } else {
                        container = (OMContainer)node;
                    }
                    StAXOMBuilder builder = (StAXOMBuilder)container.getBuilder();
                    int depth = 1;
                    // Find the root node for the builder (i.e. the topmost node having the same
                    // builder as the current node)
                    while (container != rootNode && container instanceof OMElement) {
                        OMContainer parent = ((OMElement)container).getParent();
                        if (parent.getBuilder() != builder) {
                            break;
                        }
                        container = parent;
                        depth++;
                    }
                    PullThroughWrapper wrapper = new PullThroughWrapper(serializer, this, builder, container, builder.disableCaching(), depth);
                    serializer.switchState(wrapper);
                    node = container;
                    visited = true;
                    currentEvent = wrapper.next();
                }
                if (currentEvent == START_ELEMENT) {
                    depth++;
                } else if (currentEvent == END_ELEMENT) {
                    depth--;
                }
                if (rootNode == node && visited) {
                    if (currentEvent == END_DOCUMENT) {
                        state = DOCUMENT_COMPLETE;
                    } else {
                        state = COMPLETED;
                    }
                }
                break;
            default:
                throw new IllegalStateException("unsuppported state!");
        }
        return currentEvent;
    }

    int nextTag() throws XMLStreamException {
        // Let PullSerializer handle this method
        return -1;
    }

    Object getProperty(String s) throws IllegalArgumentException {
        // TODO: CharacterDataReader extension needs to be handled in PullSerializer
        if (CharacterDataReader.PROPERTY.equals(s)) {
            return this;
        }
        if (parser != null) {
            return parser.getProperty(s);
        }
        // Delegate to the builder's parser.
        if (builder != null && builder instanceof StAXBuilder) {
            StAXBuilder staxBuilder = (StAXBuilder) builder;
            if (!staxBuilder.isClosed()) {
                // If the parser was closed by something other
                // than the builder, an IllegalStateException is
                // thrown.  For now, return null as this is unexpected
                // by the caller.
                try {
                    return ((StAXBuilder) builder).getReaderProperty(s);
                } catch (IllegalStateException ise) {
                    return null;
                }
            }
        }
        return null;
    }

    NamespaceContext getNamespaceContext() {
        if (parser != null) {
            return currentEvent == END_DOCUMENT ? new MapBasedNamespaceContext(Collections.EMPTY_MAP) : parser.getNamespaceContext();
        } else {
            return new MapBasedNamespaceContext(
                    currentEvent == END_DOCUMENT ? Collections.EMPTY_MAP : getAllNamespaces(node));
        }
    }

    String getEncoding() {
        if (parser != null) {
            return parser.getEncoding();
        } else {
            if (currentEvent == START_DOCUMENT) {
                if (node instanceof OMDocument) {
                    return ((OMDocument)node).getCharsetEncoding();
                } else {
                    return null;
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    String getVersion() {
        return "1.0"; // todo put the constant
    }

    boolean isStandalone() {
        return true;
    }

    boolean standaloneSet() {
        return false;
    }

    String getCharacterEncodingScheme() {
        if (parser != null) {
            return parser.getCharacterEncodingScheme();
        } else {
            if (currentEvent == START_DOCUMENT) {
                if (node instanceof OMDocument) {
                    return ((OMDocument)node).getXMLEncoding();
                } else {
                    return null;
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    String getPITarget() {
        if (parser != null) {
            return parser.getPITarget();
        } else {
            if (currentEvent == PROCESSING_INSTRUCTION) {
                return ((OMProcessingInstruction)node).getTarget();
            } else {
                throw new IllegalStateException();
            }
        }
    }

    String getPIData() {
        if (parser != null) {
            return parser.getPIData();
        } else {
            if (currentEvent == PROCESSING_INSTRUCTION) {
                return ((OMProcessingInstruction)node).getValue();
            } else {
                throw new IllegalStateException();
            }
        }
    }

    /*
     *
     * ################################################################
     * DataHandlerReader extension methods
     * ################################################################
     *
     */

    public boolean isBinary() {
        if (node instanceof OMText) {
            return ((OMText)node).isBinary();
        } else {
            return false;
        }
    }

    public boolean isOptimized() {
        if (node instanceof OMText) {
            return ((OMText)node).isOptimized();
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean isDeferred() {
        if (node instanceof OMText) {
            // TODO: we should support deferred building of the DataHandler
            return false;
        } else {
            throw new IllegalStateException();
        }
    }

    public String getContentID() {
        if (node instanceof OMText) {
            return ((OMText)node).getContentID();
        } else {
            throw new IllegalStateException();
        }
    }

    public DataHandler getDataHandler() throws XMLStreamException {
        if (node instanceof OMText) {
            return (DataHandler)((OMText)node).getDataHandler();
        } else {
            throw new IllegalStateException();
        }
    }

    public DataHandlerProvider getDataHandlerProvider() {
        throw new IllegalStateException();
    }

    /*
     *
     * ################################################################
     * DTDReader extension methods
     * ################################################################
     *
     */

    public String getRootName() {
        if (currentEvent == DTD) {
            return ((OMDocType)node).getRootName();
        } else {
            throw new IllegalStateException();
        }
    }

    public String getPublicId() {
        if (currentEvent == DTD) {
            return ((OMDocType)node).getPublicId();
        } else {
            throw new IllegalStateException();
        }
    }

    public String getSystemId() {
        if (currentEvent == DTD) {
            return ((OMDocType)node).getSystemId();
        } else {
            throw new IllegalStateException();
        }
    }

    /*
     *
     * ################################################################
     * Generator methods for the OMNodes returned by the navigator
     * ################################################################
     *
     */

    private int generateEvents(OMSerializable node) {
        if (node instanceof OMContainer) {
            OMContainer container = (OMContainer)node;
            if (visited) {
                return container instanceof OMDocument ? END_DOCUMENT : END_ELEMENT;
            } else {
                return container instanceof OMDocument ? START_DOCUMENT : START_ELEMENT;
            }
        } else {
            return ((OMNode)node).getType();
        }
    }

    /*
     * ####################################################################
     * Other helper methods
     * ####################################################################
     */

    private void setParser(XMLStreamReader parser) {
        this.parser = parser;
    }

    private Map getAllNamespaces(OMSerializable contextNode) {
        if (contextNode == null) {
            return Collections.EMPTY_MAP;
        }
        OMContainer context;
        if (contextNode instanceof OMContainer) {
            context = (OMContainer)contextNode;
        } else {
            context = ((OMNode)contextNode).getParent();
        }
        Map nsMap = new LinkedHashMap();
        while (context != null && !(context instanceof OMDocument)) {
            OMElement element = (OMElement) context;
            for (Iterator it = element.getAllDeclaredNamespaces(); it.hasNext(); ) {
                addNamespaceToMap((OMNamespace) it.next(), nsMap);
            }
            if (element.getNamespace() != null) {
                addNamespaceToMap(element.getNamespace(), nsMap);
            }
            for (Iterator it = element.getAllAttributes(); it.hasNext(); ) {
                OMAttribute attr = (OMAttribute) it.next();
                if (attr.getNamespace() != null) {
                    addNamespaceToMap(attr.getNamespace(), nsMap);
                }
            }
            context = element.getParent();
        }
        return nsMap;
    }

    private void addNamespaceToMap(OMNamespace ns, Map map) {
        if (map.get(ns.getPrefix()) == null) {
            map.put(ns.getPrefix(), ns.getNamespaceURI());
        }
    }

    /**
     * @return OMDataSource associated with the current node or Null
     */
    OMDataSource getDataSource() {
        if (getEventType() != XMLStreamReader.START_ELEMENT ||
                state != NAVIGABLE) {
            return null;
        }
        OMDataSource ds = null;
        if (node != null &&
            node instanceof OMSourcedElement) {
            OMSourcedElement element = (OMSourcedElement)node;
            if (element.isExpanded()) {
                // If the element is expanded, then we can't return the OMDataSource because the
                // expanded element may already have been modified
                ds = null;
            } else {
                try {
                    ds = element.getDataSource();
                } catch (UnsupportedOperationException e) {
                    // Some implementations throw an UnsupportedOperationException.
                    ds =null;
                }
                if (log.isDebugEnabled()) {
                    if (ds != null) {
                        log.debug("OMSourcedElement exposed an OMDataSource." + ds);
                    } else {
                        log.debug("OMSourcedElement does not have a OMDataSource.");
                    }
                }
            }
        }
        return ds;
    }
}
