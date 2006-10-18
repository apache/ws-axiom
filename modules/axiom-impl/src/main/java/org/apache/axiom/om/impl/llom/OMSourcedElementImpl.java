/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.*;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

/**
 * <p>Element backed by an arbitrary data source. When necessary, this element
 * will be expanded by creating a parser from the data source.</p>
 * 
 * <p>Whenever methods are added to the base {@link
 * org.apache.axiom.om.impl.llom.OMElementImpl} class the corresponding methods
 * must be added to this class (there's a unit test to verify that this has been
 * done, just to make sure nothing gets accidentally broken). If the method
 * only requires the element name and/or namespace information, the base class
 * method can be called directly. Otherwise, the element must be expanded into a
 * full OM tree (by calling the {@link #forceExpand()} method) before the base
 * class method is called. This will typically involve a heavy overhead penalty,
 * so should be avoided if possible.</p>
 */
public class OMSourcedElementImpl extends OMElementImpl
{
    /** Data source for element data. */
    private final OMDataSource dataSource;
    
    /** Namespace for element, needed in order to bypass base class handling. */
    private OMNamespace definedNamespace;
    
    /** Flag for parser provided to base element class. */
    private boolean isParserSet;
    
    private static Log log = LogFactory.getLog(OMSourcedElementImpl.class);
    
    /**
     * Constructor.
     * 
     * @param localName
     * @param ns
     * @param factory
     * @param source
     */
    public OMSourcedElementImpl(String localName, OMNamespace ns, OMFactory factory, OMDataSource source) {
        super(localName, null, factory);
        dataSource = source;
        definedNamespace = ns;
    }

    /**
     * Constructor that takes a QName instead of the
     * local name and the namespace seperately
     *
     * @param QName
     * @param factory
     * @param source
     */
    public OMSourcedElementImpl(QName qName, OMFactory factory, OMDataSource source) {
        //create a namespace
        this(qName.getLocalPart(),
            factory.createOMNamespace(qName.getNamespaceURI(),
                                      qName.getPrefix()),
            factory,
            source);

    }
    
    /**
     * Generate element name for output.
     * @return name
     */
    private String getPrintableName() {
        String uri = getNamespace().getNamespaceURI();
        if (uri == null || uri.length() == 0) {
            return getLocalName();
        } else {
            return "{" + uri + '}' + getLocalName();
        }
    }

    /**
     * Get parser from data source.
     * @return parser
     */
    private XMLStreamReader getDirectReader() {
        try {
        	// If expansion has occurred, then the reader from the datasource is consumed or stale.
        	// In such cases use the stream reader from the OMElementImpl
        	if (isDataSourceConsumed()) {
        		return super.getXMLStreamReader();
        	} else {
        		return dataSource.getReader();
        	}
        } catch (XMLStreamException e) {
            log.error("Could not get parser from data source for element " +
                getPrintableName(), e);
            throw new RuntimeException("Error obtaining parser from data source:" +
                e.getMessage());
        }
    }

    /**
     * Set parser for OM, if not previously set. Since the builder is what
     * actually constructs the tree on demand, this first creates a builder
     */
    private void forceExpand() {
        if (!isParserSet) {
            
            if (log.isDebugEnabled()) {
                log.debug("forceExpand: expanding element " +
                    getPrintableName());
            }
            
            // position reader to start tag
            XMLStreamReader reader = getDirectReader();
            try {
            	if (reader.getEventType()!= XMLStreamConstants.START_ELEMENT) {
                   while (reader.next() != XMLStreamConstants.START_ELEMENT);
            	}
            } catch (XMLStreamException e) {
                log.error("forceExpand: error parsing data soruce document for element " +
                    getLocalName(), e);
                throw new RuntimeException("Error parsing data source document:" +
                    e.getMessage());
            }
            
            // make sure element name matches what was expected
            if (!reader.getLocalName().equals(getLocalName())) {
                log.error("forceExpand: expected element name " +
                    getLocalName() + ", found " + reader.getLocalName());
                throw new RuntimeException("Element name from data source is " +
                    reader.getLocalName() + ", not the expected " + getLocalName());
            }
            String readerURI = reader.getNamespaceURI();
            readerURI = (readerURI == null) ? "" : readerURI;
            String uri = getNamespace().getNamespaceURI();
            if (!readerURI.equals(uri)) {
                log.error("forceExpand: expected element namespace " +
                    getLocalName() + ", found " + uri);
                throw new RuntimeException("Element namespace from data source is " +
                    readerURI + ", not the expected " + uri);
            }
            
            // set the builder for this element
            isParserSet = true;
            super.setBuilder(new StAXOMBuilder(getOMFactory(), reader, this));
            setComplete(false);
        }
    }
    
    /**
     * Check if element has been expanded into tree.
     * 
     * @return <code>true</code> if expanded, <code>false</code> if not
     */
    public boolean isExpanded() {
        return isParserSet;
    }
    
    /**
     * Returns whether the datasource is consumed
     */
    private boolean isDataSourceConsumed() {
    	// The datasource might be consumed when it is touched/read.  
    	// For now I am going to assume that it is since the OMDataSource currently has
    	// no way to convey this information.
    	return isExpanded();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getChildElements()
     */
    public Iterator getChildElements() {
        forceExpand();
        return super.getChildElements();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#declareNamespace(java.lang.String, java.lang.String)
     */
    public OMNamespace declareNamespace(String uri, String prefix) {
        forceExpand();
        return super.declareNamespace(uri, prefix);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#declareDefaultNamespace(java.lang.String)
     */
    public OMNamespace declareDefaultNamespace(String uri) {
        forceExpand();
        return super.declareDefaultNamespace(uri);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getDefaultNamespace()
     */
    public OMNamespace getDefaultNamespace() {
        forceExpand();
        return super.getDefaultNamespace();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#declareNamespace(org.apache.axiom.om.OMNamespace)
     */
    public OMNamespace declareNamespace(OMNamespace namespace) {
        forceExpand();
        return super.declareNamespace(namespace);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#findNamespace(java.lang.String, java.lang.String)
     */
    public OMNamespace findNamespace(String uri, String prefix) {
        forceExpand();
        return super.findNamespace(uri, prefix);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#findNamespaceURI(java.lang.String)
     */
    public OMNamespace findNamespaceURI(String prefix) {
        forceExpand();
        return super.findNamespaceURI(prefix);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getAllDeclaredNamespaces()
     */
    public Iterator getAllDeclaredNamespaces() throws OMException {
        forceExpand();
        return super.getAllDeclaredNamespaces();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getAllAttributes()
     */
    public Iterator getAllAttributes() {
        forceExpand();
        return super.getAllAttributes();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getAttribute(javax.xml.namespace.QName)
     */
    public OMAttribute getAttribute(QName qname) {
        forceExpand();
        return super.getAttribute(qname);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getAttributeValue(javax.xml.namespace.QName)
     */
    public String getAttributeValue(QName qname) {
        forceExpand();
        return super.getAttributeValue(qname);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#addAttribute(org.apache.axiom.om.OMAttribute)
     */
    public OMAttribute addAttribute(OMAttribute attr) {
        forceExpand();
        return super.addAttribute(attr);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#addAttribute(java.lang.String, java.lang.String, org.apache.axiom.om.OMNamespace)
     */
    public OMAttribute addAttribute(String attributeName, String value, OMNamespace namespace) {
        forceExpand();
        return super.addAttribute(attributeName, value, namespace);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#removeAttribute(org.apache.axiom.om.OMAttribute)
     */
    public void removeAttribute(OMAttribute attr) {
        forceExpand();
        super.removeAttribute(attr);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#setBuilder(org.apache.axiom.om.OMXMLParserWrapper)
     */
    public void setBuilder(OMXMLParserWrapper wrapper) {
        throw new UnsupportedOperationException("Builder cannot be set for element backed by data source");
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getBuilder()
     */
    public OMXMLParserWrapper getBuilder() {
        forceExpand();
        return super.getBuilder();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#setFirstChild(org.apache.axiom.om.OMNode)
     */
    public void setFirstChild(OMNode node) {
        forceExpand();
        super.setFirstChild(node);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getFirstElement()
     */
    public OMElement getFirstElement() {
        forceExpand();
        return super.getFirstElement();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getXMLStreamReader()
     */
    public XMLStreamReader getXMLStreamReader() {
        if (log.isDebugEnabled()) {
            log.debug("getting XMLStreamReader for " + getPrintableName());
        }
        if (isParserSet) {
            return super.getXMLStreamReader();
        } else {
            return getDirectReader();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getXMLStreamReaderWithoutCaching()
     */
    public XMLStreamReader getXMLStreamReaderWithoutCaching() {
        if (log.isDebugEnabled()) {
            log.debug("getting XMLStreamReader without caching for " +
                getPrintableName());
        }
        if (isParserSet) {
            return super.getXMLStreamReaderWithoutCaching();
        } else {
            return getDirectReader();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#setText(java.lang.String)
     */
    public void setText(String text) {
        forceExpand();
        super.setText(text);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#setText(javax.xml.namespace.QName)
     */
    public void setText(QName text) {
        forceExpand();
        super.setText(text);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getText()
     */
    public String getText() {
        forceExpand();
        return super.getText();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getTextAsQName()
     */
    public QName getTextAsQName() {
        forceExpand();
        return super.getTextAsQName();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getLocalName()
     */
    public String getLocalName() {
        // no need to set the parser, just call base method directly
        return super.getLocalName();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#setLocalName(java.lang.String)
     */
    public void setLocalName(String localName) {
        // no need to expand the tree, just call base method directly
        super.setLocalName(localName);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getNamespace()
     */
    public OMNamespace getNamespace() throws OMException {
        return definedNamespace;
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#setNamespace(org.apache.axiom.om.OMNamespace)
     */
    public void setNamespace(OMNamespace namespace) {
        forceExpand();
        super.setNamespace(namespace);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#setNamespaceWithNoFindInCurrentScope(org.apache.axiom.om.OMNamespace)
     */
    public void setNamespaceWithNoFindInCurrentScope(OMNamespace namespace) {
        forceExpand();
        super.setNamespaceWithNoFindInCurrentScope(namespace);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getQName()
     */
    public QName getQName() {
        if (isExpanded()) {
            return super.getQName();
        } else if (definedNamespace != null) {
            // always ignore prefix on name from sourced element
            return new QName(definedNamespace.getNamespaceURI(), getLocalName());

        } else {
            return new QName(getLocalName());
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#toStringWithConsume()
     */
    public String toStringWithConsume() throws XMLStreamException {
    	if (isDataSourceConsumed()) {
        	return super.toStringWithConsume();
        } else {
        	StringWriter writer = new StringWriter();
        	dataSource.serialize(writer, null);
        	return writer.toString();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#resolveQName(java.lang.String)
     */
    public QName resolveQName(String qname) {
        forceExpand();
        return super.resolveQName(qname);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#cloneOMElement()
     */
    public OMElement cloneOMElement() {
        forceExpand();
        return super.cloneOMElement();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#setLineNumber(int)
     */
    public void setLineNumber(int lineNumber) {
        // no need to expand the tree, just call base method directly
        super.setLineNumber(lineNumber);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMElement#getLineNumber()
     */
    public int getLineNumber() {
        // no need to expand the tree, just call base method directly
        return super.getLineNumber();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#discard()
     */
    public void discard() throws OMException {
        // discard without expanding the tree
        setComplete(true);
        super.detach();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#getType()
     */
    public int getType() {
        // no need to expand the tree, just call base method directly
        return super.getType();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#internalSerialize(javax.xml.stream.XMLStreamWriter)
     */
    public void internalSerialize(javax.xml.stream.XMLStreamWriter writer) throws XMLStreamException {
    	if (isDataSourceConsumed()) {
    		super.internalSerialize(writer);
    	} else {
    		internalSerializeAndConsume(writer);
    	}
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.impl.llom.OMElementImpl#internalSerialize(javax.xml.stream.XMLStreamWriter, boolean)
     */
    protected void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
    	if (isDataSourceConsumed()) {
    		super.internalSerialize(writer, cache);
    	} else {
    		internalSerializeAndConsume(writer);
    	}
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#internalSerializeAndConsume(javax.xml.stream.XMLStreamWriter)
     */
    public void internalSerializeAndConsume(XMLStreamWriter writer) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("serialize " + getPrintableName() + " to XMLStreamWriter");
        }
        if (isDataSourceConsumed()) {
        	super.internalSerializeAndConsume(writer);
        } else {
        	dataSource.serialize(writer);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#serialize(javax.xml.stream.XMLStreamWriter)
     */
    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        internalSerializeAndConsume(xmlWriter);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#serialize(java.io.OutputStream)
     */
    public void serialize(OutputStream output) throws XMLStreamException {
        serializeAndConsume(output);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#serialize(java.io.Writer)
     */
    public void serialize(Writer writer) throws XMLStreamException {
        serializeAndConsume(writer);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#serialize(java.io.OutputStream, org.apache.axiom.om.OMOutputFormat)
     */
    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        serializeAndConsume(output, format);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#serialize(java.io.Writer, org.apache.axiom.om.OMOutputFormat)
     */
    public void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException {
        serializeAndConsume(writer, format);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#serializeAndConsume(javax.xml.stream.XMLStreamWriter)
     */
    public void serializeAndConsume(javax.xml.stream.XMLStreamWriter xmlWriter) throws XMLStreamException {
        internalSerializeAndConsume(xmlWriter);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#serializeAndConsume(java.io.OutputStream)
     */
    public void serializeAndConsume(OutputStream output) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("serialize " + getPrintableName() + " to output stream");
        }
        if (isDataSourceConsumed()) {
        	super.serializeAndConsume(output, null);
        } else {
        	dataSource.serialize(output, null);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#serializeAndConsume(java.io.Writer)
     */
    public void serializeAndConsume(Writer writer) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("serialize " + getPrintableName() + " to writer");
        }
        if (isDataSourceConsumed()) {
        	super.serializeAndConsume(writer);
        } else {
        	dataSource.serialize(writer, null);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#serializeAndConsume(java.io.OutputStream, org.apache.axiom.om.OMOutputFormat)
     */
    public void serializeAndConsume(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("serialize formatted " + getPrintableName() +
                " to output stream");
        }
        if (isDataSourceConsumed()) {
        	super.serializeAndConsume(output, format);
        } else {
        	dataSource.serialize(output, format);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMNode#serializeAndConsume(java.io.Writer, org.apache.axiom.om.OMOutputFormat)
     */
    public void serializeAndConsume(Writer writer, OMOutputFormat format) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("serialize formatted " + getPrintableName() +
                " to writer");
        }
        if (isDataSourceConsumed()) {
        	super.serializeAndConsume(writer, format);
    	} else {
    		dataSource.serialize(writer, format);
    	}
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMContainer#addChild(org.apache.axiom.om.OMNode)
     */
    public void addChild(OMNode omNode) {
        forceExpand();
        super.addChild(omNode);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMContainer#getChildrenWithName(javax.xml.namespace.QName)
     */
    public Iterator getChildrenWithName(QName elementQName) {
        forceExpand();
        return super.getChildrenWithName(elementQName);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMContainer#getFirstChildWithName(javax.xml.namespace.QName)
     */
    public OMElement getFirstChildWithName(QName elementQName) throws OMException {
        forceExpand();
        return super.getFirstChildWithName(elementQName);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMContainer#getChildren()
     */
    public Iterator getChildren() {
        forceExpand();
        return super.getChildren();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMContainer#getFirstOMChild()
     */
    public OMNode getFirstOMChild() {
        forceExpand();
        return super.getFirstOMChild();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.OMContainer#buildNext()
     */
    public void buildNext() {
        forceExpand();
        super.buildNext();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.impl.llom.OMElementImpl#detach()
     */
    public OMNode detach() throws OMException {
        // detach without expanding the tree
        boolean complete = isComplete();
        setComplete(true);
        OMNode result = super.detach();
        setComplete(complete);
        return result;
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.impl.llom.OMElementImpl#getNextOMSibling()
     */
    public OMNode getNextOMSibling() throws OMException {
        // no need to expand the tree, just call base method directly
        return super.getNextOMSibling();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.impl.llom.OMElementImpl#getTrimmedText()
     */
    public String getTrimmedText() {
        forceExpand();
        return super.getTrimmedText();
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.impl.llom.OMElementImpl#handleNamespace(javax.xml.namespace.QName)
     */
    OMNamespace handleNamespace(QName qname) {
        forceExpand();
        return super.handleNamespace(qname);
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.impl.llom.OMElementImpl#isComplete()
     */
    public boolean isComplete() {
        if (isParserSet) {
            return super.isComplete();
        } else {
            return true;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.axiom.om.impl.llom.OMElementImpl#toString()
     */
    public String toString() {
        forceExpand();
        return super.toString();
    }
    
	/* (non-Javadoc)
	 * @see org.apache.axiom.om.OMNode#buildAll()
	 */
	public void buildWithAttachments() {
		if (!done)
		{
			this.build();
		}
		Iterator iterator = getChildren();
		while(iterator.hasNext())
		{
			OMNode node = (OMNode)iterator.next();
			node.buildWithAttachments();
		}
	}

    public void build() throws OMException {
        super.build();
    }

    protected void notifyChildComplete() {
        super.notifyChildComplete();    
    }
}