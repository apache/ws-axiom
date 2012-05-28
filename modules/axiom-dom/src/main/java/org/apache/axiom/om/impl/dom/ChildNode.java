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

package org.apache.axiom.om.impl.dom;

import java.io.OutputStream;
import java.io.Writer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class ChildNode extends NodeImpl {
    /** @param ownerDocument  */
    protected ChildNode(DocumentImpl ownerDocument, OMFactory factory) {
        super(factory);
        setOwnerDocument(ownerDocument);
    }

    protected ChildNode(OMFactory factory) {
        super(factory);
    }

    /**
     * Get the parent or the owner document of the node. The meaning of the return value depends on
     * the {@link NodeImpl#HAS_PARENT} flag.
     */
    abstract ParentNode internalGetOwnerNode();
    
    abstract void internalSetOwnerNode(ParentNode ownerNode);
    
    abstract ChildNode internalGetPreviousSibling();
    
    abstract ChildNode internalGetNextSibling();
    
    abstract void internalSetPreviousSibling(ChildNode previousSibling);
    
    abstract void internalSetNextSibling(ChildNode nextSibling);
    
    /**
     * Get the owner document of this node. In contrast to {@link Node#getOwnerDocument()}, this
     * method returns a non null value when invoked on a {@link Document} instance.
     * 
     * @return the owner document
     */
    final DocumentImpl ownerDocument() {
        ParentNode ownerNode = internalGetOwnerNode();
        if (ownerNode == null) {
            // As specified by DOMMetaFactory, the OMFactory for an implicitly created owner
            // document is always the OMFactory for plain XML.
            DocumentImpl document = new DocumentImpl(factory.getMetaFactory().getOMFactory());
            internalSetOwnerNode(document);
            return document;
        } else if (ownerNode instanceof DocumentImpl) {
            // Note: the value of the HAS_PARENT flag doesn't matter here. If the ownerNode is of
            // type Document, it must be the owner document.
            return (DocumentImpl)ownerNode;
        } else {
            return ownerNode.ownerDocument();
        }
    }
    
    void checkSameOwnerDocument(Node otherNode) {
        if (ownerDocument() != (otherNode instanceof AttrImpl
                ? ((AttrImpl)otherNode).getOwnerDocument()
                : ((ChildNode)otherNode).ownerDocument())) {
            throw new DOMException(DOMException.WRONG_DOCUMENT_ERR,
                                   DOMMessageFormatter.formatMessage(
                                           DOMMessageFormatter.DOM_DOMAIN,
                                           DOMException.WRONG_DOCUMENT_ERR, null));
        }
    }
    
    /**
     * Sets the owner document.
     *
     * @param document
     */
    void setOwnerDocument(DocumentImpl document) {
        if (hasParent()) {
            throw new IllegalStateException();
        }
        internalSetOwnerNode(document);
    }

    public Document getOwnerDocument() {
        return ownerDocument();
    }

    ParentNode parentNode() {
        return hasParent() ? internalGetOwnerNode() : null;
    }

    public OMNode getNextOMSibling() throws OMException {
        ParentNode parentNode = parentNode();
        while (internalGetNextSibling() == null && parentNode != null && !parentNode.done && parentNode.builder != null) {
            parentNode.buildNext();
        }
        return (OMNode)internalGetNextSibling();
    }

    public final OMNode getNextOMSiblingIfAvailable() {
        return (OMNode)internalGetNextSibling();
    }

    public final Node getNextSibling() {
        return (Node) this.getNextOMSibling();
    }

    public final OMNode getPreviousOMSibling() {
        return (OMNode)internalGetPreviousSibling();
    }

    public final Node getPreviousSibling() {
        return internalGetPreviousSibling();
    }

    // /
    // /OMNode methods
    // /
    public final void setNextOMSibling(OMNode node) {
        if (node == null) {
            internalSetNextSibling(null);
            return;
        }
        if (node instanceof ChildNode) {
            internalSetNextSibling((ChildNode)node);
        } else {
            throw new OMException("The node is not a " + ChildNode.class);
        }
    }

    public final void setPreviousOMSibling(OMNode node) {
        if (node == null) {
            internalSetPreviousSibling(null);
            return;
        }
        if (node instanceof ChildNode) {
            internalSetPreviousSibling((ChildNode)node);
        } else {
            throw new OMException("The node is not a " + ChildNode.class);
        }
    }

    public final OMContainer getParent() throws OMException {
        return (OMContainer)parentNode();
    }

    public Node getParentNode() {
        return parentNode();
    }

    public final void setParent(OMContainer element) {
        setParent((ParentNode)element, false);
    }
    
    protected void setParent(ParentNode parent, boolean useDomSemantics) {
        if (parent == null) {
            internalSetOwnerNode(useDomSemantics ? ownerDocument() : null);
            hasParent(false);
        } else {
            internalSetOwnerNode(parent);
            hasParent(true);
        }
    }

    public OMNode detach() throws OMException {
        return detach(false);
    }
    
    OMNode detach(boolean useDomSemantics) {
        ParentNode parentNode = parentNode();
        if (parentNode == null) {
            throw new OMException("Parent level elements cannot be detached");
        } else {
            if (!done) {
                build();
            }
            getNextOMSibling(); // Make sure that nextSibling is set correctly
            ChildNode previousSibling = internalGetPreviousSibling();
            ChildNode nextSibling = internalGetNextSibling();
            if (previousSibling == null) { // This is the first child
                if (nextSibling != null) {
                    parentNode.setFirstChild((OMNode)nextSibling);
                } else {
                    parentNode.firstChild = null;
                    parentNode.lastChild = null;
                }
            } else {
                previousSibling.setNextOMSibling((OMNode)nextSibling);
                if (nextSibling == null) {
                    previousSibling.parentNode().done = true;
                }
            }
            if (nextSibling != null) {
                nextSibling.setPreviousOMSibling((OMNode)previousSibling);
                internalSetNextSibling(null);
            }
            if (parentNode != null && parentNode.lastChild == this) {
                parentNode.lastChild = previousSibling;
            }
            setParent(null, useDomSemantics);
            internalSetPreviousSibling(null);
        }
        return (OMNode)this;
    }

    public void discard() throws OMException {
        throw new UnsupportedOperationException("Cannot discard this node");
    }

    /** Inserts the given sibling next to this item. */
    public void insertSiblingAfter(OMNode sibling) throws OMException {
        ParentNode parentNode = parentNode();
        if (parentNode == null) {
            throw new OMException("Parent can not be null");
        } else if (this == sibling) {
            throw new OMException("Inserting self as the sibling is not allowed");
        }
        ((OMNodeEx) sibling).setParent((OMContainer)parentNode);
        if (sibling instanceof ChildNode) {
            ChildNode domSibling = (ChildNode) sibling;
            domSibling.internalSetPreviousSibling(this);
            ChildNode nextSibling = internalGetNextSibling();
            if (nextSibling == null) {
                parentNode.setLastChild(sibling);
            } else {
                nextSibling.internalSetPreviousSibling(domSibling);
            }
            domSibling.internalSetNextSibling(nextSibling);
            internalSetNextSibling(domSibling);

        } else {
            throw new OMException("The given child is not of type "
                    + ChildNode.class);
        }
    }

    /** Inserts the given sibling before this item. */
    public void insertSiblingBefore(OMNode sibling) throws OMException {
        ParentNode parentNode = parentNode();
        // ((OMNodeEx)sibling).setParent(this.parentNode);
        if (parentNode == null) {
            throw new OMException("Parent can not be null");
        } else if (this == sibling) {
            throw new OMException("Inserting self as the sibling is not allowed");
        }
        if (sibling instanceof ChildNode) {
            // ChildNode domSibling = (ChildNode)sibling;
            // domSibling.nextSibling = this;
            // if(this.previousSibling != null) {
            // this.previousSibling.nextSibling = domSibling;
            // }
            // domSibling.previousSibling = this.previousSibling;
            // this.previousSibling = domSibling;
            ChildNode siblingImpl = (ChildNode) sibling;
            siblingImpl.internalSetNextSibling(this);
            ChildNode previousSibling = internalGetPreviousSibling();
            if (previousSibling == null) {
                parentNode.setFirstChild((OMNode)siblingImpl);
                siblingImpl.internalSetPreviousSibling(null);
            } else {
                siblingImpl.setParent(parentNode, false);
                previousSibling.setNextOMSibling((OMNode)siblingImpl);
                siblingImpl.setPreviousOMSibling((OMNode)previousSibling);
            }
            internalSetPreviousSibling(siblingImpl);

        } else {
            throw new OMException("The given child is not of type "
                    + ChildNode.class);
        }

    }

    public Node cloneNode(boolean deep) {

        ChildNode newnode = (ChildNode) super.cloneNode(deep);

        newnode.isFirstChild(false);
        newnode.internalSetOwnerNode(ownerDocument());
        newnode.hasParent(false);

        return newnode;
    }

    public void setComplete(boolean state) {
        done = state;
        ParentNode parentNode = parentNode();
        if (parentNode != null) {
            if (!done) {
                parentNode.setComplete(false);
            } else {
                parentNode.notifyChildComplete();
            }
        }
    }

    public boolean isComplete() {
        return this.done;
    }

    /** Builds next element. */
    public void build() {
        while (!done)
            this.builder.next();
    }

    /**
     * Parses this node and builds the object structure in memory. AXIOM supports two levels of
     * deffered building. First is deffered building of AXIOM using StAX. Second level is the deffered
     * building of attachments. AXIOM reads in the attachements from the stream only when user asks by
     * calling getDataHandler(). build() method builds the OM without the attachments. buildAll()
     * builds the OM together with attachement data. This becomes handy when user wants to free the
     * input stream.
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

    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        serialize(xmlWriter, true);
    }

    public void serializeAndConsume(XMLStreamWriter xmlWriter) throws XMLStreamException {
        serialize(xmlWriter, false);
    }

    public void serialize(XMLStreamWriter xmlWriter, boolean cache) throws XMLStreamException {
        MTOMXMLStreamWriter writer = xmlWriter instanceof MTOMXMLStreamWriter ?
                (MTOMXMLStreamWriter) xmlWriter : 
                    new MTOMXMLStreamWriter(xmlWriter);
        internalSerialize(writer, cache);
        writer.flush();
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

    public void serializeAndConsume(OutputStream output)
            throws XMLStreamException {
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

    public void serialize(OutputStream output, OMOutputFormat format)
            throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format, true);
        try {
            internalSerialize(writer, true);
            // TODO: the flush is necessary because of an issue with the lifecycle of MTOMXMLStreamWriter
            writer.flush();
        } finally {
            writer.close();
        }
    }

    public void serialize(Writer writer2, OMOutputFormat format)
            throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(StAXUtils
                .createXMLStreamWriter(writer2));
        writer.setOutputFormat(format);
        try {
            internalSerialize(writer, true);
            // TODO: the flush is necessary because of an issue with the lifecycle of MTOMXMLStreamWriter
            writer.flush();
        } finally {
            writer.close();
        }
    }

    public void serializeAndConsume(OutputStream output, OMOutputFormat format)
            throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(output, format, false);
        try {
            internalSerialize(writer, false);
            // TODO: the flush is necessary because of an issue with the lifecycle of MTOMXMLStreamWriter
            writer.flush();
        } finally {
            writer.close();
        }
    }

    public void serializeAndConsume(Writer writer2, OMOutputFormat format)
            throws XMLStreamException {
        MTOMXMLStreamWriter writer = new MTOMXMLStreamWriter(StAXUtils
                .createXMLStreamWriter(writer2));
        try {
            writer.setOutputFormat(format);
            // TODO: the flush is necessary because of an issue with the lifecycle of MTOMXMLStreamWriter
            internalSerialize(writer, false);
            writer.flush();
        } finally {
            writer.close();
        }
    }

    public void internalSerialize(XMLStreamWriter writer) throws XMLStreamException {
        internalSerialize(writer, true);
    }

    public void internalSerializeAndConsume(XMLStreamWriter writer) throws XMLStreamException {
        internalSerialize(writer, false);
    }
    
    // This method is actually defined by OMNodeEx, but OMNodeEx is only implemented
    // by certain subclasses (for the reason, see AXIOM-385).
    public abstract void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException;
    
    abstract OMNode clone(OMCloneOptions options, OMContainer targetParent);
}
