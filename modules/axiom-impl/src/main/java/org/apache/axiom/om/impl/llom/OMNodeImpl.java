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

import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.io.Writer;

/** Class OMNodeImpl */
public abstract class OMNodeImpl implements OMNode, OMNodeEx {
    
    private static final Log log = LogFactory.getLog(OMNodeImpl.class);
    private static boolean DEBUG_ENABLED = log.isDebugEnabled();
    
    /** Field parent */
    protected OMContainerEx parent;

    /** Field nextSibling */
    protected OMNodeImpl nextSibling;

    /** Field previousSibling */
    protected OMNodeImpl previousSibling;
    /** Field builder */
    public OMXMLParserWrapper builder;

    /** Field done */
    protected boolean done = false;

    /** Field nodeType */
    protected int nodeType;

    protected OMFactory factory;

    /**
     * Constructor OMNodeImpl
     *
     * @param factory The <code>OMFactory</code> that created this
     */
    public OMNodeImpl(OMFactory factory) {
        this.factory = factory;
    }

    /**
     * For a node to exist there must be a parent.
     *
     * @param parent  Parent <code>OMContainer</code> of this node
     * @param factory The <code>OMFactory</code> that created this
     */
    public OMNodeImpl(OMContainer parent, OMFactory factory, boolean done) {
        this.done = done;
        this.factory = factory;
        if ((parent != null)) {
            this.parent = (OMContainerEx) parent;
            parent.addChild(this);
        }

    }

    /**
     * Returns the immediate parent of the node. Parent is always an Element.
     *
     * @return Returns OMContainer.
     * @throws OMException
     */
    public OMContainer getParent() {
        return parent;
    }

    /**
     * Method setParent.
     *
     * @param element
     */
    public void setParent(OMContainer element) {

        if ((this.parent) == element) {
            return;
        }

        //If we are asked to assign a new parent in place
        //of an existing one. We should detach this node
        //from the previous parent.
        if (element != null) {
            if (this.parent != null) {
                this.detach();
            }
            this.parent = (OMContainerEx) element;
        } else {
            this.parent = null;
        }
    }

    /**
     * Returns the next sibling. This can be an OMAttribute or OMText or OMElement for others.
     *
     * @return Returns OMNode.
     * @throws org.apache.axiom.om.OMException
     *
     */
    public OMNode getNextOMSibling() throws OMException {
        if ((nextSibling == null) && (parent != null) && !parent.isComplete()) {
            parent.buildNext();
        }
        return nextSibling;
    }

    /**
     * Method setNextOMSibling.
     *
     * @param node
     */
    public void setNextOMSibling(OMNode node) {
        if (node == null || node.getOMFactory() instanceof OMLinkedListImplFactory) {
            this.nextSibling = (OMNodeImpl) node;
        } else {
            this.nextSibling = (OMNodeImpl) importNode(node);
        }
        this.nextSibling = (OMNodeImpl) node;
    }


    /**
     * Indicates whether parser has parsed this information item completely or not. If some
     * information is not available in the item, one has to check this attribute to make sure that,
     * this item has been parsed completely or not.
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
        if (parent != null) {
            if (!done) {
                parent.setComplete(false);
            } else if (parent instanceof OMElementImpl) {
                ((OMElementImpl) parent).notifyChildComplete();
            }
        }
    }

    /**
     * Removes this information item and its children, from the model completely.
     *
     * @throws OMException
     */
    public OMNode detach() throws OMException {
        if (parent == null) {
            throw new OMException(
                    "Elements that doesn't have a parent can not be detached");
        }
        OMNodeImpl nextSibling = (OMNodeImpl) getNextOMSibling();
        if (previousSibling == null) {
            parent.setFirstChild(nextSibling);
        } else {
            ((OMNodeEx) getPreviousOMSibling()).setNextOMSibling(nextSibling);
        }
        if (nextSibling != null) {
            nextSibling.setPreviousOMSibling(getPreviousOMSibling());
        }

        if ((parent instanceof OMElementImpl) && ((OMElementImpl) parent).lastChild == this) {
            ((OMElementImpl) parent).lastChild = getPreviousOMSibling();
        }

        this.previousSibling = null;
        this.nextSibling = null;
        this.parent = null;
        return this;
    }

    /**
     * Inserts a sibling just after the current information item.
     *
     * @param sibling
     * @throws OMException
     */
    public void insertSiblingAfter(OMNode sibling) throws OMException {
        if (parent == null) {
            throw new OMException("Parent can not be null");
        } else if (this == sibling) {
            throw new OMException("Inserting self as the sibling is not allowed");
        }
        ((OMNodeEx) sibling).setParent(parent);
        if (sibling instanceof OMNodeImpl) {
            OMNodeImpl siblingImpl = (OMNodeImpl) sibling;
            if (nextSibling == null) {
                getNextOMSibling();
            }
            siblingImpl.setPreviousOMSibling(this);
            if (nextSibling != null) {
                nextSibling.setPreviousOMSibling(sibling);
            }
            ((OMNodeEx) sibling).setNextOMSibling(nextSibling);
            nextSibling = siblingImpl;
        }
    }

    /**
     * Inserts a sibling just before the current information item.
     *
     * @param sibling
     * @throws OMException
     */
    public void insertSiblingBefore(OMNode sibling) throws OMException {
        if (parent == null) {
            throw new OMException("Parent can not be null");
        } else if (this == sibling) {
            throw new OMException("Inserting self as the sibling is not allowed");
        }
        if (sibling instanceof OMNodeImpl) {
            OMNodeImpl siblingImpl = (OMNodeImpl) sibling;
            siblingImpl.nextSibling = this;
            if (previousSibling == null) {
                parent.setFirstChild(siblingImpl);
                siblingImpl.previousSibling = null;
            } else {
                siblingImpl.setParent(parent);
                previousSibling.setNextOMSibling(siblingImpl);
                siblingImpl.setPreviousOMSibling(previousSibling);
            }
            previousSibling = siblingImpl;

        }
    }

    /**
     * Gets the type of node, as this is the super class of all the nodes.
     *
     * @return Returns the type of node as indicated by {@link #setType}
     * @see #setType
     */
    public int getType() {
        return nodeType;
    }

    /**
     * Method setType.
     *
     * @param nodeType
     * @throws OMException
     */
    public void setType(int nodeType) throws OMException {
        this.nodeType = nodeType;
    }

    /**
     * Gets the previous sibling.
     *
     * @return boolean
     */
    public OMNode getPreviousOMSibling() {
        return previousSibling;
    }

    /**
     * Method setPreviousOMSibling.
     *
     * @param previousSibling
     */
    public void setPreviousOMSibling(OMNode previousSibling) {
        if (previousSibling == null ||
                previousSibling.getOMFactory() instanceof OMLinkedListImplFactory) {
            this.previousSibling = (OMNodeImpl) previousSibling;
        } else {
            this.previousSibling = (OMNodeImpl) importNode(previousSibling);
        }
    }

    /**
     * Parses this node and builds the object structure in memory. However a node, created
     * programmatically, will have done set to true by default and this will cause populateyourself
     * not to work properly!
     *
     * @throws OMException
     */
    public void build() throws OMException {
        while (!done) {
            builder.next();
            if (builder.isCompleted() && !done) {
                if (DEBUG_ENABLED) {
                    log.debug("Builder is complete.  Setting OMNode to complete.");
                }
                setComplete(true);
            }
        }
    }

    /**
     * Parses this node and builds the object structure in memory. AXIOM supports two levels of
     * deffered building. First is deffered building of AXIOM using StAX. Second level is the
     * deffered building of attachments. AXIOM reads in the attachements from the stream only when
     * user asks by calling getDataHandler(). build() method builds the OM without the attachments.
     * buildAll() builds the OM together with attachement data. This becomes handy when user wants
     * to free the input stream.
     */
    public void buildWithAttachments() {
        if (!this.done) {
            this.build();
        }
    }

    
    public void close(boolean build) {
        if (build) {
            this.build();
        }
        this.done = true;
        
        // If this is a StAXBuilder, close it.
        if (builder instanceof StAXBuilder &&
            !((StAXBuilder) builder).isClosed()) {
            ((StAXBuilder) builder).releaseParserOnClose(true);
            ((StAXBuilder) builder).close();
        }
    }

    /**
     * Serializes the node with caching.
     *
     * @param xmlWriter
     * @throws javax.xml.stream.XMLStreamException
     *
     */
    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(xmlWriter);
        internalSerialize(writer);
        writer.flush();
    }

    /**
     * Serializes the node without caching.
     *
     * @param xmlWriter
     * @throws javax.xml.stream.XMLStreamException
     *
     */
    public void serializeAndConsume(XMLStreamWriter xmlWriter) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(xmlWriter);
        internalSerializeAndConsume(writer);
        writer.flush();
    }

    /**
     * Serializes the node with caching.
     *
     * @param writer
     * @throws XMLStreamException
     */
    public void internalSerialize(XMLStreamWriter writer) throws XMLStreamException {
        throw new RuntimeException("Not implemented yet!");
    }

    /**
     * Serializes the node without caching.
     *
     * @param writer
     * @throws XMLStreamException
     */
    public void internalSerializeAndConsume(XMLStreamWriter writer) throws XMLStreamException {
        throw new RuntimeException("Not implemented yet!");
    }

    public void serialize(OutputStream output) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter = StAXUtils.createXMLStreamWriter(output);
        try {
            serialize(xmlStreamWriter);
        } finally {
            xmlStreamWriter.close();
        }
    }

    public void serialize(Writer writer) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter = StAXUtils.createXMLStreamWriter(writer);
        try {
            serialize(xmlStreamWriter);
        } finally {
            xmlStreamWriter.close();
        }
    }

    public void serializeAndConsume(OutputStream output) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter = StAXUtils.createXMLStreamWriter(output);
        try {
            serializeAndConsume(xmlStreamWriter);
        } finally {
            xmlStreamWriter.close();
        }
    }

    public void serializeAndConsume(Writer writer) throws XMLStreamException {
        XMLStreamWriter xmlStreamWriter = StAXUtils.createXMLStreamWriter(writer);
        try {
            serializeAndConsume(xmlStreamWriter);
        } finally {
            xmlStreamWriter.close();
        }
    }

    public void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format);
        internalSerialize(writer);
        writer.flush();
        if (format.isAutoCloseWriter()) {
            writer.close();
        }
    }

    public void serialize(Writer writer2, OMOutputFormat format) throws XMLStreamException {
        MTOMXMLStreamWriter writer =
                new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer2));
        writer.setOutputFormat(format);
        internalSerialize(writer);
        writer.flush();
        if (format.isAutoCloseWriter()) {
            writer.close();
        }
    }

    public void serializeAndConsume(OutputStream output, OMOutputFormat format)
            throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format);
        internalSerializeAndConsume(writer);
        writer.flush();
        if (format.isAutoCloseWriter()) {
            writer.close();
        }
    }

    public void serializeAndConsume(Writer writer2, OMOutputFormat format)
            throws XMLStreamException {
        MTOMXMLStreamWriter writer =
                new MTOMXMLStreamWriter(StAXUtils.createXMLStreamWriter(writer2));
        writer.setOutputFormat(format);
        internalSerializeAndConsume(writer);
        writer.flush();
        if (format.isAutoCloseWriter()) {
            writer.close();
        }
    }

    public OMFactory getOMFactory() {
        return this.factory;
    }

    /**
     * This method is intended only to be used by Axiom intenals when merging Objects from different
     * Axiom implementations to the LLOM implementation.
     *
     * @param child
     */
    protected OMNode importNode(OMNode child) {
        int type = child.getType();
        switch (type) {
            case (OMNode.ELEMENT_NODE): {
                OMElement childElement = (OMElement) child;
                OMElement newElement = (new StAXOMBuilder(this.factory, childElement
                        .getXMLStreamReader())).getDocumentElement();
                newElement.buildWithAttachments();
                return newElement;
            }
            case (OMNode.TEXT_NODE): {
                OMText importedText = (OMText) child;
                OMText newText;
                if (importedText.isBinary()) {
                    boolean isOptimize = importedText.isOptimized();
                    newText = this.factory.createOMText(importedText
                            .getDataHandler(), isOptimize);
                } else if (importedText.isCharacters()) {
                    newText = this.factory.createOMText(null, importedText
                            .getTextCharacters(), importedText.getType());
                } else {
                    newText = this.factory.createOMText(null, importedText
                            .getText()/*, importedText.getOMNodeType()*/);
                }
                return newText;
            }

            case (OMNode.PI_NODE): {
                OMProcessingInstruction importedPI = (OMProcessingInstruction) child;
                return factory.createOMProcessingInstruction(null,
                                                                  importedPI.getTarget(),
                                                                  importedPI.getValue());
            }
            case (OMNode.COMMENT_NODE): {
                OMComment importedComment = (OMComment) child;
                return factory.createOMComment(null, importedComment.getValue());
            }
            case (OMNode.DTD_NODE) : {
                OMDocType importedDocType = (OMDocType) child;
                return factory.createOMDocType(null, importedDocType.getValue());
            }
            default: {
                throw new UnsupportedOperationException(
                        "Not Implemented Yet for the given node type");
            }
        }
    }
}
