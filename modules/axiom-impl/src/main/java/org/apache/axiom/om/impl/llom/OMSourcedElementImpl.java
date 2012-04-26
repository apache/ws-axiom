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

package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.QNameAwareOMDataSource;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXSource;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

/**
 * <p>Element backed by an arbitrary data source. When necessary, this element will be expanded by
 * creating a parser from the data source.</p>
 * <p/>
 * <p>Whenever methods are added to the base {@link OMElementImpl}
 * class the corresponding methods must be added to this class (there's a unit test to verify that
 * this has been done, just to make sure nothing gets accidentally broken). If the method only
 * requires the element name and/or namespace information, the base class method can be called
 * directly. Otherwise, the element must be expanded into a full OM tree (by calling the {@link
 * #forceExpand()} method) before the base class method is called. This will typically involve a
 * heavy overhead penalty, so should be avoided if possible.</p>
 */
public class OMSourcedElementImpl extends OMElementImpl implements OMSourcedElement {
    
    /** Data source for element data. */
    private OMDataSource dataSource;

    /** Namespace for element, needed in order to bypass base class handling. */
    private OMNamespace definedNamespace = null;
    
    /**
     * Flag indicating whether the {@link #definedNamespace} attribute has been set. If this flag is
     * <code>true</code> and {@link #definedNamespace} is <code>null</code> then the element has no
     * namespace. If this flag is set to <code>false</code> (in which case {@link #definedNamespace}
     * is always <code>null</code>) then the namespace is not known and needs to be determined
     * lazily. The flag is used only if {@link #isExpanded} is <code>false</code>.
     */
    private boolean definedNamespaceSet;

    /** Flag for parser provided to base element class. */
    private boolean isExpanded;

    private static final Log log = LogFactory.getLog(OMSourcedElementImpl.class);
    
    private static final Log forceExpandLog = LogFactory.getLog(OMSourcedElementImpl.class.getName() + ".forceExpand");
    
    private XMLStreamReader readerFromDS = null;  // Reader from DataSource

    private static OMNamespace normalize(OMNamespace ns) {
        // TODO: the ns.getPrefix() == null case actually doesn't make sense for a sourced element!
        return ns == null || (ns.getPrefix() == null || ns.getPrefix().length() == 0) && ns.getNamespaceURI().length() == 0 ? null : ns;
    }
    
    private static OMNamespace getOMNamespace(QName qName) {
        return qName.getNamespaceURI().length() == 0 ? null
                : new OMNamespaceImpl(qName.getNamespaceURI(), qName.getPrefix());
    }
    
    public OMSourcedElementImpl(OMFactory factory, OMDataSource source) {
        super(factory);
        dataSource = source;
        isExpanded = false;
    }
    
    /**
     * Constructor.
     *
     * @param localName
     * @param ns
     * @param factory
     * @param source
     */
    public OMSourcedElementImpl(String localName, OMNamespace ns, OMFactory factory,
                                OMDataSource source) {
        super(localName, null, factory);
        if (source == null) {
            throw new IllegalArgumentException("OMDataSource can't be null");
        }
        dataSource = source;
        isExpanded = false;
        if (!isLossyPrefix(dataSource)) {
            // Believe the prefix and create a normal OMNamespace
            definedNamespace = normalize(ns);
        } else {
            // Create a deferred namespace that forces an expand to get the prefix
            definedNamespace = new DeferredNamespace(ns.getNamespaceURI());
        }
        definedNamespaceSet = true;
    }

    /**
     * Constructor that takes a QName instead of the local name and the namespace seperately
     *
     * @param qName
     * @param factory
     * @param source
     */
    public OMSourcedElementImpl(QName qName, OMFactory factory, OMDataSource source) {
        //create a namespace
        super(qName.getLocalPart(), null, factory);
        if (source == null) {
            throw new IllegalArgumentException("OMDataSource can't be null");
        }
        dataSource = source;
        isExpanded = false;
        if (!isLossyPrefix(dataSource)) {
            // Believe the prefix and create a normal OMNamespace
            definedNamespace = getOMNamespace(qName);
        } else {
            // Create a deferred namespace that forces an expand to get the prefix
            definedNamespace = new DeferredNamespace(qName.getNamespaceURI());
        }
        definedNamespaceSet = true;
    }

    public OMSourcedElementImpl(String localName, OMNamespace ns, OMContainer parent, OMFactory factory) {
        super(localName, null, parent, factory);
        dataSource = null;
        definedNamespace = ns;
        isExpanded = true;
        if (ns != null) {
            this.setNamespace(ns);
        }
    }

    public OMSourcedElementImpl(String localName, OMNamespace ns, OMContainer parent, OMXMLParserWrapper builder, OMFactory factory) {
        super(localName, null, parent, builder, factory);
        dataSource = null;
        definedNamespace = ns;
        isExpanded = true;
        if (ns != null) {
            this.setNamespace(ns);
        }
    }

    public OMSourcedElementImpl(String localName, OMNamespace ns, OMFactory factory) {
        super(localName, null, factory);
        dataSource = null;
        definedNamespace = ns;
        isExpanded = true;
        if (ns != null) {
            this.setNamespace(ns);
        }
    }
    
    
    /**
     * The namespace uri is immutable, but the OMDataSource may change
     * the value of the prefix.  This method queries the OMDataSource to 
     * see if the prefix is known.
     * @param source
     * @return true or false
     */
    private boolean isLossyPrefix(OMDataSource source) {
        Object lossyPrefix = null;
        if (source instanceof OMDataSourceExt) {
            lossyPrefix = 
                ((OMDataSourceExt) source).getProperty(OMDataSourceExt.LOSSY_PREFIX);
                        
        }
        return lossyPrefix == Boolean.TRUE;
    }
    private void setDeferredNamespace(OMDataSource source, String uri, String prefix) {
        Object lossyPrefix = null;
        if (source instanceof OMDataSourceExt) {
            lossyPrefix = 
                ((OMDataSourceExt) source).getProperty(OMDataSourceExt.LOSSY_PREFIX);
                        
        }
        if (lossyPrefix != Boolean.TRUE) {
            // Believe the prefix and create a normal OMNamespace
            definedNamespace = new OMNamespaceImpl(uri, prefix);
        } else {
            // Create a deferred namespace that forces an expand to get the prefix
            definedNamespace = new DeferredNamespace(uri);
        }
    }

    /**
     * Generate element name for output.
     *
     * @return name
     */
    private String getPrintableName() {
        String uri = null;
        if (getNamespace() != null) {
            uri = getNamespace().getNamespaceURI();
        }
        if (uri == null || uri.length() == 0) {
            return getLocalName();
        } else {
            return "{" + uri + '}' + getLocalName();
        }
    }

    /**
     * Get parser from data source. Note that getDataReader may consume the underlying data source.
     *
     * @return parser
     */
    private XMLStreamReader getDirectReader() {
        try {
            // If expansion has occurred, then the reader from the datasource is consumed or stale.
            // In such cases use the stream reader from the OMElementImpl
            if (isExpanded()) {
                return super.getXMLStreamReader();
            } else {
                return dataSource.getReader();  
            }
        } catch (XMLStreamException e) {
            log.error("Could not get parser from data source for element " +
                    getPrintableName(), e);
            throw new RuntimeException("Error obtaining parser from data source:" +
                    e.getMessage(), e);
        }
    }

    /**
     * Set parser for OM, if not previously set. Since the builder is what actually constructs the
     * tree on demand, this first creates a builder
     */
    private void forceExpand() {
        // The dataSource != null is required because this method may be called indirectly
        // by the constructor before the data source is set. After the constructor has completed,
        // isExpanded is always true if dataSource is null.
        if (!isExpanded && dataSource != null) {

            if (log.isDebugEnabled()) {
                log.debug("forceExpand: expanding element " +
                        getPrintableName());
                if(forceExpandLog.isDebugEnabled()){
                	// When using an OMSourcedElement, it can be particularly difficult to
                	// determine why an expand occurs... a stack trace should help debugging this
                	Exception e = new Exception("Debug Stack Trace");
                	forceExpandLog.debug("forceExpand stack", e);
                }
            }

            // Get the XMLStreamReader
            readerFromDS = getDirectReader();
            
            // Advance past the START_DOCUMENT to the start tag.
            // Remember the character encoding.
            String characterEncoding = readerFromDS.getCharacterEncodingScheme();
            if (characterEncoding != null) {
                characterEncoding = readerFromDS.getEncoding();
            }
            try {
                if (readerFromDS.getEventType() != XMLStreamConstants.START_ELEMENT) {
                    while (readerFromDS.next() != XMLStreamConstants.START_ELEMENT) ;
                }
            } catch (XMLStreamException e) {
                log.error("forceExpand: error parsing data soruce document for element " +
                        getLocalName(), e);
                throw new RuntimeException("Error parsing data source document:" +
                        e.getMessage(), e);
            }

            String readerLocalName = readerFromDS.getLocalName();
            if (localName == null) {
                // The local name was not known in advance; initialize it from the reader
                localName = readerLocalName;
            } else {
                // Make sure element local name and namespace matches what was expected
                if (!readerLocalName.equals(getLocalName())) {
                    log.error("forceExpand: expected element name " +
                            getLocalName() + ", found " + readerLocalName);
                    throw new RuntimeException("Element name from data source is " +
                            readerLocalName + ", not the expected " + getLocalName());
                }
            }
            if (definedNamespaceSet) {
                String readerURI = readerFromDS.getNamespaceURI();
                readerURI = (readerURI == null) ? "" : readerURI;
                String uri = (getNamespace() == null) ? "" : getNamespace().getNamespaceURI();
                if (!readerURI.equals(uri)) {
                    log.error("forceExpand: expected element namespace " +
                            getLocalName() + ", found " + uri);
                    throw new RuntimeException("Element namespace from data source is " +
                            readerURI + ", not the expected " + uri);
                }
            }

            // Set the builder for this element. Note that the StAXOMBuilder constructor will also
            // update the namespace of the element, so we don't need to do that here.
            isExpanded = true;
            super.setBuilder(new StAXOMBuilder(getOMFactory(), 
                                               readerFromDS, 
                                               this, 
                                               characterEncoding));
            setComplete(false);
        }
    }

    /**
     * Check if element has been expanded into tree.
     *
     * @return <code>true</code> if expanded, <code>false</code> if not
     */
    public boolean isExpanded() {
        return isExpanded;
    }

    public Iterator getChildElements() {
        forceExpand();
        return super.getChildElements();
    }

    public OMNamespace declareNamespace(String uri, String prefix) {
        forceExpand();
        return super.declareNamespace(uri, prefix);
    }

    public OMNamespace declareDefaultNamespace(String uri) {
        forceExpand();
        return super.declareDefaultNamespace(uri);
    }

    public OMNamespace getDefaultNamespace() {
        forceExpand();
        return super.getDefaultNamespace();
    }

    public OMNamespace declareNamespace(OMNamespace namespace) {
        forceExpand();
        return super.declareNamespace(namespace);
    }

    public OMNamespace addNamespaceDeclaration(String uri, String prefix) {
        return super.addNamespaceDeclaration(uri, prefix);
    }

    public void undeclarePrefix(String prefix) {
        forceExpand();
        super.undeclarePrefix(prefix);
    }

    public OMNamespace findNamespace(String uri, String prefix) {
        forceExpand();
        return super.findNamespace(uri, prefix);
    }

    public OMNamespace findNamespaceURI(String prefix) {
        forceExpand();
        return super.findNamespaceURI(prefix);
    }

    public Iterator getAllDeclaredNamespaces() throws OMException {
        forceExpand();
        return super.getAllDeclaredNamespaces();
    }

    public Iterator getNamespacesInScope() throws OMException {
        forceExpand();
        return super.getNamespacesInScope();
    }

    public NamespaceContext getNamespaceContext(boolean detached) {
        forceExpand();
        return super.getNamespaceContext(detached);
    }

    public Iterator getAllAttributes() {
        forceExpand();
        return super.getAllAttributes();
    }

    public OMAttribute getAttribute(QName qname) {
        forceExpand();
        return super.getAttribute(qname);
    }

    public String getAttributeValue(QName qname) {
        forceExpand();
        return super.getAttributeValue(qname);
    }

    public OMAttribute addAttribute(OMAttribute attr) {
        forceExpand();
        return super.addAttribute(attr);
    }

    public OMAttribute addAttribute(String attributeName, String value, OMNamespace namespace) {
        forceExpand();
        return super.addAttribute(attributeName, value, namespace);
    }

    public void removeAttribute(OMAttribute attr) {
        forceExpand();
        super.removeAttribute(attr);
    }

    public void setBuilder(OMXMLParserWrapper wrapper) {
        throw new UnsupportedOperationException(
                "Builder cannot be set for element backed by data source");
    }

    public OMXMLParserWrapper getBuilder() {
        forceExpand();
        return super.getBuilder();
    }

    public void setFirstChild(OMNode node) {
        forceExpand();
        super.setFirstChild(node);
    }

    public void setLastChild(OMNode omNode) {
        forceExpand();
        super.setLastChild(omNode);
    }

    public OMElement getFirstElement() {
        forceExpand();
        return super.getFirstElement();
    }

    public XMLStreamReader getXMLStreamReader(boolean cache) {
        return getXMLStreamReader(cache, new OMXMLStreamReaderConfiguration());
    }
    
    public XMLStreamReader getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration) {
        if (log.isDebugEnabled()) {
            log.debug("getting XMLStreamReader for " + getPrintableName()
                    + " with cache=" + cache);
        }
        if (isExpanded) {
            return super.getXMLStreamReader(cache, configuration);
        } else {
            if (cache && isDestructiveRead()) {
                forceExpand();
                return super.getXMLStreamReader(true, configuration);
            }
            return getDirectReader();
        }
    }

    public XMLStreamReader getXMLStreamReader() {
        return getXMLStreamReader(true);
    }

    public XMLStreamReader getXMLStreamReaderWithoutCaching() {
        return getXMLStreamReader(false);
    }

    public void setText(String text) {
        forceExpand();
        super.setText(text);
    }

    public void setText(QName text) {
        forceExpand();
        super.setText(text);
    }

    public String getText() {
        forceExpand();
        return super.getText();
    }

    public Reader getTextAsStream(boolean cache) {
        return super.getTextAsStream(cache);
    }

    public QName getTextAsQName() {
        forceExpand();
        return super.getTextAsQName();
    }

    public void writeTextTo(Writer out, boolean cache) throws IOException {
        super.writeTextTo(out, cache);
    }

    private void ensureLocalNameSet() {
        if (localName == null) {
            if (dataSource instanceof QNameAwareOMDataSource) {
                localName = ((QNameAwareOMDataSource)dataSource).getLocalName();
            }
            if (localName == null) {
                forceExpand();
            }
        }
    }
    
    public String getLocalName() {
        ensureLocalNameSet();
        return super.getLocalName();
    }

    public void setLocalName(String localName) {
        // Need to expand the element so that the method actually overrides the the local name
        forceExpand();
        super.setLocalName(localName);
    }

    public OMNamespace getNamespace() throws OMException {
        if (isExpanded()) {
            return super.getNamespace();
        } else if (definedNamespaceSet) {
            return definedNamespace;
        } else {
            if (dataSource instanceof QNameAwareOMDataSource) {
                String namespaceURI = ((QNameAwareOMDataSource)dataSource).getNamespaceURI();
                if (namespaceURI != null) {
                    if (namespaceURI.length() == 0) {
                        // No namespace case. definedNamespace is already null, so we only need
                        // to set definedNamespaceSet to true. Note that we don't need to retrieve
                        // the namespace prefix because a prefix can't be bound to the empty
                        // namespace URI.
                        definedNamespaceSet = true;
                    } else {
                        String prefix = ((QNameAwareOMDataSource)dataSource).getPrefix();
                        if (prefix == null) {
                            // Prefix is unknown
                            definedNamespace = new DeferredNamespace(namespaceURI);
                        } else {
                            definedNamespace = new OMNamespaceImpl(namespaceURI, prefix);
                        }
                        definedNamespaceSet = true;
                    }
                }
            }
            if (definedNamespaceSet) {
                return definedNamespace;
            } else {
                // We have no information about the namespace of the element. Need to expand
                // the element to get it.
                forceExpand();
                return super.getNamespace();
            }
        }
    }

    public String getPrefix() {
        return super.getPrefix();
    }

    public String getNamespaceURI() {
        return super.getNamespaceURI();
    }

    public void setNamespace(OMNamespace namespace) {
        forceExpand();
        super.setNamespace(namespace);
    }

    public void setNamespaceWithNoFindInCurrentScope(OMNamespace namespace) {
        forceExpand();
        super.setNamespaceWithNoFindInCurrentScope(namespace);
    }

    public QName getQName() {
        if (isExpanded()) {
            return super.getQName();
        } else if (getNamespace() != null) {
            // always ignore prefix on name from sourced element
            return new QName(getNamespace().getNamespaceURI(), getLocalName());
        } else {
            return new QName(getLocalName());
        }
    }

    public String toStringWithConsume() throws XMLStreamException {
        if (isExpanded()) {
            return super.toStringWithConsume();
        } else {
            StringWriter writer = new StringWriter();
            XMLStreamWriter writer2 = StAXUtils.createXMLStreamWriter(writer);
            dataSource.serialize(writer2);  // dataSource.serialize consumes the data
            writer2.flush();
            return writer.toString();
        }
    }
    
    private boolean isDestructiveWrite() {
        if (dataSource instanceof OMDataSourceExt) {
            return ((OMDataSourceExt) dataSource).isDestructiveWrite();
        } else {
            return true;
        }
    }
    
    private boolean isDestructiveRead() {
        if (dataSource instanceof OMDataSourceExt) {
            return ((OMDataSourceExt) dataSource).isDestructiveRead();
        } else {
            return false;
        }
    }

    public QName resolveQName(String qname) {
        forceExpand();
        return super.resolveQName(qname);
    }

    public OMElement cloneOMElement() {
        forceExpand();
        return super.cloneOMElement();
    }

    public void setLineNumber(int lineNumber) {
        // no need to expand the tree, just call base method directly
        super.setLineNumber(lineNumber);
    }

    public int getLineNumber() {
        // no need to expand the tree, just call base method directly
        return super.getLineNumber();
    }

    public void discard() throws OMException {
        // discard without expanding the tree
        setComplete(true);
        super.detach();
    }

    public int getType() {
        // no need to expand the tree, just call base method directly
        return super.getType();
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache)
            throws XMLStreamException {
        if (isExpanded()) {
            super.internalSerialize(writer, cache);
        } else if (cache) {
            if (isDestructiveWrite()) {
                forceExpand();
                super.internalSerialize(writer, true);
            } else {
                dataSource.serialize(writer);
            }
        } else {
            dataSource.serialize(writer); 
        }
    }

    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        // The contract is to serialize with caching
        internalSerialize(xmlWriter, true);
    }

    public void serialize(OutputStream output) throws XMLStreamException {
        OMOutputFormat format = new OMOutputFormat();
        serialize(output, format);
    }

    public void serialize(Writer writer) throws XMLStreamException {
        OMOutputFormat format = new OMOutputFormat();
        serialize(writer, format);
    }

    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        if (isExpanded) {
            super.serialize(output, format);
        } else if (isDestructiveWrite()) {
            forceExpand();
            super.serialize(output, format);
        } else {
            dataSource.serialize(output, format);
        }
    }

    public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        if (isExpanded) {
            super.serialize(writer, format);
        } else if (isDestructiveWrite()) {
            forceExpand();
            super.serialize(writer, format);
        } else {
            dataSource.serialize(writer, format);
        }
    }

    public void serializeAndConsume(javax.xml.stream.XMLStreamWriter xmlWriter)
            throws XMLStreamException {
        internalSerialize(xmlWriter, false);
    }

    public void serializeAndConsume(OutputStream output) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("serialize " + getPrintableName() + " to output stream");
        }
        OMOutputFormat format = new OMOutputFormat();
        if (isExpanded()) {
            super.serializeAndConsume(output, format);
        } else {
            dataSource.serialize(output, format);
        }
    }

    public void serializeAndConsume(Writer writer) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("serialize " + getPrintableName() + " to writer");
        }
        if (isExpanded()) {
            super.serializeAndConsume(writer);
        } else {
            OMOutputFormat format = new OMOutputFormat();
            dataSource.serialize(writer, format); 
        }
    }

    public void serializeAndConsume(OutputStream output, OMOutputFormat format)
            throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("serialize formatted " + getPrintableName() +
                    " to output stream");
        }
        if (isExpanded()) {
            super.serializeAndConsume(output, format);
        } else {
            dataSource.serialize(output, format); 
        }
    }

    public void serializeAndConsume(Writer writer, OMOutputFormat format)
            throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("serialize formatted " + getPrintableName() +
                    " to writer");
        }
        if (isExpanded()) {
            super.serializeAndConsume(writer, format);
        } else {
            dataSource.serialize(writer, format); 
        }
    }

    public void addChild(OMNode omNode) {
        forceExpand();
        super.addChild(omNode);
    }

    public Iterator getChildrenWithName(QName elementQName) {
        forceExpand();
        return super.getChildrenWithName(elementQName);
    }
    
    public Iterator getChildrenWithLocalName(String localName) {
        forceExpand();
        return super.getChildrenWithLocalName(localName);
    }

    public Iterator getChildrenWithNamespaceURI(String uri) {
        forceExpand();
        return super.getChildrenWithNamespaceURI(uri);
    }

    public OMElement getFirstChildWithName(QName elementQName) throws OMException {
        forceExpand();
        return super.getFirstChildWithName(elementQName);
    }

    public Iterator getChildren() {
        forceExpand();
        return super.getChildren();
    }

    public Iterator getDescendants(boolean includeSelf) {
        forceExpand();
        return super.getDescendants(includeSelf);
    }

    public OMNode getFirstOMChild() {
        forceExpand();
        return super.getFirstOMChild();
    }

    public OMNode getFirstOMChildIfAvailable() {
        return super.getFirstOMChildIfAvailable();
    }

    public void buildNext() {
        forceExpand();
        super.buildNext();
    }

    public OMNode detach() throws OMException {
        // detach without expanding the tree
        boolean complete = isComplete();
        setComplete(true);
        OMNode result = super.detach();
        setComplete(complete);
        return result;
    }

    public OMNode getNextOMSibling() throws OMException {
        // no need to expand the tree, just call base method directly
        return super.getNextOMSibling();
    }

    public OMNode getNextOMSiblingIfAvailable() {
        return super.getNextOMSiblingIfAvailable();
    }

    OMNamespace handleNamespace(QName qname) {
        forceExpand();
        return super.handleNamespace(qname);
    }

    public boolean isComplete() {
        if (isExpanded) {
            return super.isComplete();
        } else {
            return true;
        }
    }

    public String toString() {
        if (isExpanded) {
            return super.toString();
        } else if (isDestructiveWrite()) {
            forceExpand();
            return super.toString();
        } else {
            try {
                StringWriter writer = new StringWriter();
                OMOutputFormat format = new OMOutputFormat();
                dataSource.serialize(writer, format);
                String text = writer.toString();
                writer.close();
                return text;
            } catch (XMLStreamException e) {
                throw new RuntimeException("Cannot serialize OM Element " + this.getLocalName(), e);
            } catch (IOException e) {
                throw new RuntimeException("Cannot serialize OM Element " + this.getLocalName(), e);
            }
        }
    }

    public void buildWithAttachments() {
        
        // If not done, force the parser to build the elements
        if (!done) {
            this.build();
        }
        
        // If the OMSourcedElement is in in expanded form, then
        // walk the descendents to make sure they are built. 
        // If the OMSourcedElement is backed by a OMDataSource,
        // we don't want to walk the children (because this will result
        // in an unnecessary translation from OMDataSource to a full OM tree).
        if (isExpanded()) {
            Iterator iterator = getChildren();
            while (iterator.hasNext()) {
                OMNode node = (OMNode) iterator.next();
                node.buildWithAttachments();
            }
        }
    }

    public void build() throws OMException {
        super.build();
    }

    void notifyChildComplete() {
        super.notifyChildComplete();
    }


    OMNamespace handleNamespace(String namespaceURI, String prefix) {
        return super.handleNamespace(namespaceURI,
                                     prefix);  
    }

    /**
     * Provide access to the data source encapsulated in OMSourcedElement. 
     * This is usesful when we want to access the raw data in the data source.
     *
     * @return the internal datasource
     */
    public OMDataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * setOMDataSource
     */
    public OMDataSource setDataSource(OMDataSource dataSource) {
        if (!isExpanded()) {
            OMDataSource oldDS = this.dataSource;
            this.dataSource = dataSource;
            return oldDS;  // Caller is responsible for closing the data source
        } else {
            // TODO
            // Remove the entire subtree and replace with 
            // new datasource.  There maybe a more performant way to do this.
            OMDataSource oldDS = this.dataSource;
            Iterator it = getChildren();
            while(it.hasNext()) {
                it.next();
                it.remove();
            }
            this.dataSource = dataSource;
            setComplete(false);
            isExpanded = false;
            super.setBuilder(null);
            if (isLossyPrefix(dataSource)) {
                // Create a deferred namespace that forces an expand to get the prefix
                definedNamespace = new DeferredNamespace(definedNamespace.getNamespaceURI());
            }
            return oldDS;
        }
    }

    /**
     * setComplete override The OMSourcedElement has its own isolated builder/reader during the
     * expansion process. Thus calls to setCompete should stop here and not propogate up to the
     * parent (which may have a different builder or no builder).
     */
    public void setComplete(boolean value) {
        done = value;
        if (done == true) {
            if (readerFromDS != null) {
                try {
                    readerFromDS.close();
                } catch (XMLStreamException e) {
                }
                readerFromDS = null;
            }
            if (dataSource != null) {
                if (dataSource instanceof OMDataSourceExt) {
                    ((OMDataSourceExt)dataSource).close();
                }
                dataSource = null;
            }
        }
        if (done == true && readerFromDS != null) {
            try {
                readerFromDS.close();
            } catch (XMLStreamException e) {
            }
            readerFromDS = null;
        }
    }
    
    public SAXSource getSAXSource(boolean cache) {
        return super.getSAXSource(cache);
    }

    class DeferredNamespace implements OMNamespace {
        
        final String uri;
        
        DeferredNamespace(String ns) {
            this.uri = ns;
        }

        public boolean equals(String uri, String prefix) {
            String thisPrefix = getPrefix();
            return (this.uri.equals(uri) &&
                    (thisPrefix == null ? prefix == null :
                            thisPrefix.equals(prefix)));
        }

        public String getName() {
            return uri;
        }

        public String getNamespaceURI() {
            return uri;
        }

        public String getPrefix() {
            if (!isExpanded()) {
                forceExpand();
            }
            return getNamespace().getPrefix();
        }
        
        public int hashCode() {
            String thisPrefix = getPrefix();
            return uri.hashCode() ^ (thisPrefix != null ? thisPrefix.hashCode() : 0);
        }
        
        public boolean equals(Object obj) {
            if (!(obj instanceof OMNamespace)) {
                return false;
            }
            OMNamespace other = (OMNamespace)obj;
            String otherPrefix = other.getPrefix();
            String thisPrefix = getPrefix();
            return (uri.equals(other.getNamespaceURI()) &&
                    (thisPrefix == null ? otherPrefix == null :
                            thisPrefix.equals(otherPrefix)));
        }
        
    }
}
