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

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.common.IContainer;
import org.apache.axiom.om.impl.common.OMChildrenLocalNameIterator;
import org.apache.axiom.om.impl.common.OMChildrenNamespaceIterator;
import org.apache.axiom.om.impl.common.OMChildrenQNameIterator;
import org.apache.axiom.om.impl.common.OMContainerHelper;
import org.apache.axiom.om.impl.common.OMDescendantsIterator;
import org.apache.axiom.om.impl.common.OMDocumentImplUtil;
import org.apache.axiom.om.impl.jaxp.OMSource;
import org.apache.axiom.om.impl.traverse.OMChildrenIterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXSource;

import java.util.Iterator;

/** Class OMDocumentImpl */
public class OMDocumentImpl extends OMSerializableImpl implements OMDocument, IContainer {
    protected OMXMLParserWrapper builder;

    protected boolean done;

    /** Field firstChild */
    protected OMNode firstChild;

    /** Field lastChild */
    protected OMNode lastChild;

    /** Field charSetEncoding Default : UTF-8 */
    protected String charSetEncoding = "UTF-8";

    /** Field xmlVersion */
    protected String xmlVersion = "1.0";
    
    protected String xmlEncoding;

    protected String isStandalone;

    /**
     * Create a <code>OMDocument</code> given the <code>OMFactory</code>
     *
     * @param factory The <code>OMFactory</code> that created this instace
     */
    public OMDocumentImpl(OMFactory factory) {
        super(factory);
        this.done = true;
    }

    /**
     * Create the <code>OMDocument</code> with the factory
     *
     * @param parserWrapper
     * @param factory
     */
    public OMDocumentImpl(OMXMLParserWrapper parserWrapper, OMFactory factory) {
        super(factory);
        this.builder = parserWrapper;
    }

    /**
     * Create the <code>OMDoucment</code> with the factory and set the given <code>OMElement</code>
     * as the document element
     *
     * @param documentElement
     * @param parserWrapper
     * @param factory
     */
    public OMDocumentImpl(OMElement documentElement, OMXMLParserWrapper parserWrapper,
                          OMFactory factory) {
        super(factory);
        this.builder = parserWrapper;
        setOMDocumentElement(documentElement);
    }

    public OMXMLParserWrapper getBuilder() {
        return builder;
    }

    public OMElement getOMDocumentElement() {
        OMNode child = getFirstOMChild();
        while (child != null) {
            if (child instanceof OMElement) {
                return (OMElement)child;
            }
            child = child.getNextOMSibling();
        }
        return null;
    }

    public void setOMDocumentElement(OMElement documentElement) {
        if (documentElement == null) {
            throw new IllegalArgumentException("documentElement must not be null");
        }
        OMElement existingDocumentElement = getOMDocumentElement();
        if (existingDocumentElement == null) {
            addChild(documentElement);
        } else {
            OMNode nextSibling = existingDocumentElement.getNextOMSibling();
            existingDocumentElement.detach();
            if (nextSibling == null) {
                addChild(documentElement);
            } else {
                nextSibling.insertSiblingBefore(documentElement);
            }
        }
    }

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

    public void addChild(OMNode child) {
        addChild(child, false);
    }

    public void addChild(OMNode omNode, boolean fromBuilder) {
        if (!fromBuilder && omNode instanceof OMElement && getOMDocumentElement() != null) {
            throw new OMException("Document element already exists");
        }
        OMContainerHelper.addChild(this, omNode, fromBuilder);
    }

    /**
     * Returns a collection of this element. Children can be of types OMElement, OMText.
     *
     * @return Returns iterator.
     */
    public Iterator getChildren() {
        return new OMChildrenIterator(getFirstOMChild());
    }

    public Iterator getDescendants(boolean includeSelf) {
        return new OMDescendantsIterator(this, includeSelf);
    }

    /**
     * Searches for children with a given QName and returns an iterator to traverse through the
     * OMNodes. The QName can contain any combination of prefix, localname and URI.
     *
     * @param elementQName
     * @return Returns Iterator.
     * @throws org.apache.axiom.om.OMException
     *
     */
    public Iterator getChildrenWithName(QName elementQName) {
        return new OMChildrenQNameIterator(getFirstOMChild(),
                                           elementQName);
    }

    public Iterator getChildrenWithLocalName(String localName) {
        return new OMChildrenLocalNameIterator(getFirstOMChild(),
                                               localName);
    }


    public Iterator getChildrenWithNamespaceURI(String uri) {
        return new OMChildrenNamespaceIterator(getFirstOMChild(),
                                               uri);
    }
    /**
     * Method getFirstOMChild.
     *
     * @return Returns first om child.
     */
    public OMNode getFirstOMChild() {
        return OMContainerHelper.getFirstOMChild(this);
    }

    public OMNode getFirstOMChildIfAvailable() {
        return firstChild;
    }

    public OMNode getLastKnownOMChild() {
        return lastChild;
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
     * Forcefully set the last child
     * @param omNode
     */
    public void setLastChild(OMNode omNode) {
        this.lastChild = omNode;
    }

    public String getCharsetEncoding() {
        return charSetEncoding;
    }

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

    public String getXMLEncoding() {
        return xmlEncoding;
    }

    public void setXMLEncoding(String encoding) {
        this.xmlEncoding = encoding;
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        internalSerialize(writer, cache, !((MTOMXMLStreamWriter) writer).isIgnoreXMLDeclaration());
    }

    protected void internalSerialize(XMLStreamWriter writer, boolean cache,
                                     boolean includeXMLDeclaration) throws XMLStreamException {
        OMDocumentImplUtil.internalSerialize(this, writer, cache, includeXMLDeclaration);
    }

    /** Serializes the document with the XML declaration. */
    public void internalSerializeAndConsume(XMLStreamWriter writer)
            throws XMLStreamException {
        internalSerialize(writer, false);
    }


    /** Serializes the document with cache. */
    public void internalSerialize(XMLStreamWriter writer) throws XMLStreamException {
        internalSerialize(writer, true);
    }
    
    public XMLStreamReader getXMLStreamReader() {
        return getXMLStreamReader(true);
    }

    public XMLStreamReader getXMLStreamReaderWithoutCaching() {
        return getXMLStreamReader(false);
    }

    public XMLStreamReader getXMLStreamReader(boolean cache) {
        return OMContainerHelper.getXMLStreamReader(this, cache);
    }

    public XMLStreamReader getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration) {
        return OMContainerHelper.getXMLStreamReader(this, cache, configuration);
    }

    void notifyChildComplete() {
        if (!this.done && builder == null) {
            Iterator iterator = getChildren();
            while (iterator.hasNext()) {
                OMNode node = (OMNode) iterator.next();
                if (!node.isComplete()) {
                    return;
                }
            }
            this.setComplete(true);
        }
    }
    
    public SAXSource getSAXSource(boolean cache) {
        return new OMSource(this);
    }

    public void build() {
        OMContainerHelper.build(this);
    }

    public void buildNext() {
        OMContainerHelper.buildNext(this);
    }
}
