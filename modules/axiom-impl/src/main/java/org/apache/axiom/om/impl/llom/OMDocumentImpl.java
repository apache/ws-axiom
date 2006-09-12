/*
 * Copyright 2004,2005 The Apache Software Foundation.
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
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.traverse.OMChildrenIterator;
import org.apache.axiom.om.impl.traverse.OMChildrenQNameIterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * Class OMDocumentImpl
 */
public class OMDocumentImpl implements OMDocument, OMContainerEx {
    /**
     * Field documentElement
     */
    protected OMElement documentElement;

    /**
     * Field firstChild
     */
    protected OMNode firstChild;

    /**
     * Field lastChild
     */
    protected OMNode lastChild;

    /**
     * Field done
     */
    protected boolean done = false;

    /**
     * Field parserWrapper
     */
    protected OMXMLParserWrapper parserWrapper;

    /**
     * Field charSetEncoding
     * Default : UTF-8
     */
    protected String charSetEncoding = "UTF-8";

    /**
     * Field xmlVersion
     */
    protected String xmlVersion = "1.0";

    protected String isStandalone;

    protected OMFactory factory;
    
    /**
     * Default constructor
     */
    public OMDocumentImpl() {
        this.done = true;
    }

    /**
     * @param documentElement
     * @param parserWrapper
     */
    public OMDocumentImpl(OMElement documentElement, OMXMLParserWrapper parserWrapper) {
        this.documentElement = documentElement;
        this.parserWrapper = parserWrapper;
    }

    /**
     * @param parserWrapper
     */
    public OMDocumentImpl(OMXMLParserWrapper parserWrapper) {
        this.parserWrapper = parserWrapper;
    }

    /**
     * Create a <code>OMDocument</code> given the <code>OMFactory</code>
     * @param factory The <code>OMFactory</code> that created this instace
     */
    public OMDocumentImpl(OMFactory factory) {
        this();
        this.factory = factory;
    }

    /**
     * Create the <code>OMDocument</code> with the factory
     * @param parserWrapper
     * @param factory
     */
    public OMDocumentImpl(OMXMLParserWrapper parserWrapper, OMFactory factory) {
        this(parserWrapper);
        this.factory = factory;
    }
    
    /**
     * Create the <code>OMDoucment</code> with the factory and set the given 
     * <code>OMElement</code> as the document element
     * @param documentElement
     * @param parserWrapper
     * @param factory
     */
    public OMDocumentImpl(OMElement documentElement, OMXMLParserWrapper parserWrapper, OMFactory factory) {
        this(documentElement, parserWrapper);
        this.factory = factory;
    }

    
    /**
     * Method getDocumentElement.
     *
     * @return Returns OMElement.
     */
    public OMElement getOMDocumentElement() {
        while (documentElement == null) {
            parserWrapper.next();
        }
        return documentElement;
    }

    /**
     * Method setDocumentElement.
     *
     * @param documentElement
     */
    public void setOMDocumentElement(OMElement documentElement) {
        this.documentElement = documentElement;
    }

    /**
     * Indicates whether parser has parsed this information item completely or not.
     * If some information is not available in the item, one has to check this 
     * attribute to make sure that, this item has been parsed completely or not.
     *
     * @return Returns boolean.
     */
    public boolean isComplete() {
        return done;
    }

    /**
     * Method setComplete.
     *
     * @param state
     */
    public void setComplete(boolean state) {
        this.done = state;
    }

    /**
     * Forces the parser to proceed, if parser has not yet finished with the XML input.
     */
    public void buildNext() {
        if (!parserWrapper.isCompleted())
            parserWrapper.next();
    }

    /**
     * Adds child to the element. One can decide whether to append the child or to add to the
     * front of the children list.
     *
     * @param child
     */
    public void addChild(OMNode child) {
    	if(child instanceof OMElement) {
    		if(this.documentElement == null) {
    			addChild((OMNodeImpl) child);
    			this.documentElement = (OMElement)child;
    		} else {
    			throw new OMException("Document element already exists");
    		}
    	} else {
    		addChild((OMNodeImpl) child);
    	}
    }

    /**
     * Method addChild.
     *
     * @param child
     */
    private void addChild(OMNodeImpl child) {
        if (firstChild == null) {
            firstChild = child;
            child.setPreviousOMSibling(null);
        } else {
            child.setPreviousOMSibling(lastChild);
            ((OMNodeEx)lastChild).setNextOMSibling(child);
        }
        child.setNextOMSibling(null);
        child.setParent(this);
        lastChild = child;

    }

    /**
     * Returns a collection of this element.
     * Children can be of types OMElement, OMText.
     *
     * @return Returns iterator.
     */
    public Iterator getChildren() {
        return new OMChildrenIterator(getFirstOMChild());
    }

    /**
     * Searches for children with a given QName and returns an iterator to traverse through
     * the OMNodes.
     * The QName can contain any combination of prefix, localname and URI.
     *
     * @param elementQName
     * @return Returns Iterator.
     * @throws org.apache.axiom.om.OMException
     */
    public Iterator getChildrenWithName(QName elementQName) {
        return new OMChildrenQNameIterator(getFirstOMChild(),
                elementQName);
    }

    /**
     * Method getFirstOMChild.
     *
     * @return Returns first om child.
     */
    public OMNode getFirstOMChild() {
        while ((firstChild == null) && !done) {
            buildNext();
        }
        return firstChild;
    }

    /**
     * Method getFirstChildWithName.
     *
     * @param elementQName
     * @return Returns OMElement.
     * @throws OMException
     */
    public OMElement getFirstChildWithName(QName elementQName) throws OMException {
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

    /**
     * Method setFirstChild.
     *
     * @param firstChild
     */
    public void setFirstChild(OMNode firstChild) {
        this.firstChild = firstChild;
    }


    /**
     * Returns the character set encoding scheme to be used.
     *
     * @return Returns charset.
     */
    public String getCharsetEncoding() {
        return charSetEncoding;
    }

    /**
     * Sets the character set encoding scheme.
     *
     * @param charEncoding
     */
    public void setCharsetEncoding(String charEncoding) {
        this.charSetEncoding = charEncoding;
    }

    public String isStandalone() {
        return isStandalone;
    }

    public void setStandalone(String isStandalone) {
        this.isStandalone = isStandalone;
    }

    public String getXMLVersion() {
        return xmlVersion;
    }

    public void setXMLVersion(String xmlVersion) {
        this.xmlVersion = xmlVersion;
    }

    /**
     * Serialize the docuement with/without the XML declaration
     */
    public void internalSerializeAndConsume(XMLStreamWriter writer, boolean includeXMLDeclaration) throws XMLStreamException {
        internalSerialize(writer, false, includeXMLDeclaration);
    }

    /**
     * Serializes the document with the XML declaration.
     */
    public void internalSerializeAndConsume(XMLStreamWriter writer)
            throws XMLStreamException {
        internalSerialize(writer, false, !((MTOMXMLStreamWriter)writer).isIgnoreXMLDeclaration());
    }


    /**
     * Serializes the document with cache.
     */
    public void internalSerialize(XMLStreamWriter writer) throws XMLStreamException {
        internalSerialize(writer, true, !((MTOMXMLStreamWriter)writer).isIgnoreXMLDeclaration());

    }

    /**
     * Serializes the document directly to the output stream with caching disabled.
     * 
     * @param output
     * @throws XMLStreamException
     */
    public void serializeAndConsume(OutputStream output) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, new OMOutputFormat());
        internalSerializeAndConsume(writer);
        writer.flush();
    }

    /**
     * Serializes the document directly to the output stream with caching enabled.
     * 
     * @param output
     * @throws XMLStreamException
     */
    public void serialize(OutputStream output) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, new OMOutputFormat());
        internalSerialize(writer);
        writer.flush();
    }

    /**
     * Serializes the document directly to the output stream with caching disabled.
     * 
     * @param output
     * @param format
     * @throws XMLStreamException
     */
    public void serializeAndConsume(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format);
        internalSerializeAndConsume(writer);
        writer.flush();
    }

    /**
     * Serializes the document directly to the output stream with caching enabled.
     * 
     * @param output
     * @param format
     * @throws XMLStreamException
     */
    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format);
        internalSerialize(writer);
        writer.flush();
    }

    /**
     * Serializes the document with cache.
     */
    public void internalSerialize(XMLStreamWriter writer, boolean includeXMLDeclaration) throws XMLStreamException {
        internalSerialize(writer, true, includeXMLDeclaration);

    }

    protected void internalSerialize(XMLStreamWriter writer2, boolean cache, boolean includeXMLDeclaration) throws XMLStreamException {
        MTOMXMLStreamWriter writer = (MTOMXMLStreamWriter) writer2;
        if (includeXMLDeclaration) {
            //Check whether the OMOutput char encoding and OMDocument char
            //encoding matches, if not use char encoding of OMOutput
            String outputCharEncoding = writer.getCharSetEncoding();
            if (outputCharEncoding == null || "".equals(outputCharEncoding)) {
                writer.getXmlStreamWriter().writeStartDocument(charSetEncoding,
                        xmlVersion);
            } else {
                writer.getXmlStreamWriter().writeStartDocument(outputCharEncoding,
                        xmlVersion);
            }
        }

        Iterator children = this.getChildren();

        if (cache) {
            while (children.hasNext()) {
                OMNodeEx omNode = (OMNodeEx) children.next();
                omNode.internalSerialize(writer);
            }
        } else {
            while (children.hasNext()) {
                OMNodeEx omNode = (OMNodeEx) children.next();
                omNode.internalSerializeAndConsume(writer);
            }
        }
    }

    public OMFactory getOMFactory() {
        return this.getOMDocumentElement().getOMFactory();
    }


}
