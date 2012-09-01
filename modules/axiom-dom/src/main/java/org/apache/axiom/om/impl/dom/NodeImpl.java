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

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.Attr;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Hashtable;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class NodeImpl implements Node {

    /** Holds the user data objects */
    private Hashtable userData; // Will be initialized in setUserData()

    /** Factory that created this node */
    protected OMFactory factory;

    // data

    protected short flags;

    /**
     * Used by {@link ChildNode} to determine the meaning of the <code>ownerNode</code> attribute.
     * If the flag is set, then the attribute contains the reference to the parent node. If the flag
     * is not set, then the node has no parent and the attribute stores a reference to the owner
     * document (which may be <code>null</code> if the owner document has not been created yet).
     */
    protected final static short HAS_PARENT = 0x1 << 1;
    
    protected final static short FIRSTCHILD = 0x1 << 2;

    protected final static short SPECIFIED = 0x1 << 4;

    //
    // Constructors
    //

    protected NodeImpl(OMFactory factory) {
        this.factory = factory;
    }

    void normalize(DOMConfigurationImpl config) {
        // Default: do nothing
    }
    
    public void normalize() {
        //Parent node should override this 
    }

    public boolean hasAttributes() {
        return false; // overridden in ElementImpl
    }

    public boolean hasChildNodes() {
        return false; // Override in ParentNode
    }

    public String getLocalName() {
        return null; // Override in AttrImpl and ElementImpl
    }

    public String getNamespaceURI() {
        return null; // Override in AttrImpl and ElementImpl
    }

    public String getNodeValue() throws DOMException {
        return null;
    }

    /*
     * Overidden in ElementImpl and AttrImpl.
     */
    public String getPrefix() {
        return null;
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        // Don't do anything, to be overridden in SOME Child classes
    }

    public void setPrefix(String prefix) throws DOMException {
        throw new DOMException(DOMException.NAMESPACE_ERR, DOMMessageFormatter
                .formatMessage(DOMMessageFormatter.DOM_DOMAIN, DOMException.NAMESPACE_ERR,
                               null));
    }

    /**
     * Returns the collection of attributes associated with this node, or null if none. At this
     * writing, Element is the only type of node which will ever have attributes.
     *
     * @see ElementImpl
     */
    public NamedNodeMap getAttributes() {
        return null; // overridden in ElementImpl
    }

    /**
     * Gets the first child of this Node, or null if none.
     * <p/>
     * By default we do not have any children, ParentNode overrides this.
     *
     * @see ParentNode
     */
    public Node getFirstChild() {
        return null;
    }

    /**
     * Gets the last child of this Node, or null if none.
     * <p/>
     * By default we do not have any children, ParentNode overrides this.
     *
     * @see ParentNode
     */
    public Node getLastChild() {
        return null;
    }

    public final Node cloneNode(boolean deep) {
        OMCloneOptions options = new OMCloneOptions();
        // This is not specified by the API, but it's compatible with versions before 1.2.14
        options.setPreserveModel(true);
        NodeImpl clone = clone(options, null, getNodeType() == Node.ATTRIBUTE_NODE ? true : deep, false);
        if (!(clone instanceof DocumentImpl)) {
            clone.setOwnerDocument(ownerDocument());
        }
        return clone;
    }

    public boolean isSupported(String feature, String version) {
        throw new UnsupportedOperationException();
        // TODO
    }

    /*
     * Flags setters and getters
     */

    final boolean hasParent() {
        return (flags & HAS_PARENT) != 0;
    }

    final void hasParent(boolean value) {
        flags = (short) (value ? flags | HAS_PARENT : flags & ~HAS_PARENT);
    }

    final boolean isFirstChild() {
        return (flags & FIRSTCHILD) != 0;
    }

    final void isFirstChild(boolean value) {
        flags = (short) (value ? flags | FIRSTCHILD : flags & ~FIRSTCHILD);
    }

    final boolean isSpecified() {
        return (flags & SPECIFIED) != 0;
    }

    final void isSpecified(boolean value) {
        flags = (short) (value ? flags | SPECIFIED : flags & ~SPECIFIED);
    }

    /*
     * DOM-Level 3 methods
     */

    public String getBaseURI() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public short compareDocumentPosition(Node other) throws DOMException {
        // This is not yet implemented. In the meantime, we throw a DOMException
        // and not an UnsupportedOperationException, since this works better with
        // some other libraries (such as Saxon 8.9).
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, DOMMessageFormatter
                .formatMessage(DOMMessageFormatter.DOM_DOMAIN, DOMException.NOT_SUPPORTED_ERR, null));
    }

    public String getTextContent() throws DOMException {
        return getNodeValue();  // overriden in some subclasses
    }

    // internal method taking a StringBuffer in parameter
    void getTextContent(StringBuffer buf) throws DOMException {
        String content = getNodeValue();
        if (content != null) {
            buf.append(content);
        }
    }

    public void setTextContent(String textContent) throws DOMException {
        setNodeValue(textContent);  // overriden in some subclasses
    }

    public boolean isSameNode(Node node) {
        // TODO : check
        return this == node;
    }

    public String lookupPrefix(String namespaceURI) {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isDefaultNamespace(String namespaceURI) {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Returns the namespace for a given prefix <br/>
     * The prefix can be of an Attribute's or an Element's.
     */
    public String lookupNamespaceURI(String specifiedPrefix) {
        short type = this.getNodeType();
        switch (type) {
        case Node.ELEMENT_NODE: {

            String namespace = this.getNamespaceURI();
            String prefix = this.getPrefix();
            // looking in the element
            if (namespace != null) {
                if (prefix == null && specifiedPrefix == null) {
                    // looking for default namespace
                    return namespace;
                } else if (prefix != null && prefix.equals(specifiedPrefix)) {
                    // non default namespace
                    return namespace;
                }
            }
            // looking in attributes
            if (this.hasAttributes()) {
                NamedNodeMap map = this.getAttributes();
                int length = map.getLength();
                for (int i = 0; i < length; i++) {
                    Node attr = map.item(i);
                    String attrPrefix = attr.getPrefix();
                    String value = attr.getNodeValue();
                    namespace = attr.getNamespaceURI();
                    if (namespace != null && namespace.equals("http://www.w3.org/2000/xmlns/")) {
                        if (specifiedPrefix == null && attr.getNodeName().equals("xmlns")) {
                            return value.length() > 0 ? value : null;
                        } else if (attrPrefix != null && attrPrefix.equals("xmlns")
                                && attr.getLocalName().equals(specifiedPrefix)) {
                            return value.length() > 0 ? value : null;
                        }
                    }
                }
            }
            // looking in ancestor
            NodeImpl ancestor = (NodeImpl) getElementAncestor(this);
            if (ancestor != null) {
                return ancestor.lookupNamespaceURI(specifiedPrefix);
            }

            return null;

        }
        case Node.DOCUMENT_NODE: {
            Element documentElement = ((Document) this).getDocumentElement();
            return documentElement == null ? null
                    : documentElement.lookupNamespaceURI(specifiedPrefix);
        }
        case Node.ENTITY_NODE:
        case Node.NOTATION_NODE:
        case Node.DOCUMENT_FRAGMENT_NODE:
        case Node.DOCUMENT_TYPE_NODE:
            // type is unknown
            return null;
        case Node.ATTRIBUTE_NODE: {
            NodeImpl ownerNode = (NodeImpl) ((Attr) this).getOwnerElement();
            if (ownerNode.getNodeType() == Node.ELEMENT_NODE) {
                return ownerNode.lookupNamespaceURI(specifiedPrefix);

            }
            return null;
        }
        default: {
            NodeImpl ancestor = (NodeImpl) getElementAncestor(this);
            if (ancestor != null) {
                return ancestor.lookupNamespaceURI(specifiedPrefix);
            }
            return null;
        }

        }
    }

    /**
     * Tests whether two nodes are equal. <br>This method tests for equality of nodes, not sameness
     * (i.e., whether the two nodes are references to the same object) which can be tested with
     * <code>Node.isSameNode()</code>. All nodes that are the same will also be equal, though the
     * reverse may not be true. <br>Two nodes are equal if and only if the following conditions are
     * satisfied: <ul> <li>The two nodes are of the same type. </li> <li>The following string
     * attributes are equal: <code>nodeName</code>, <code>localName</code>,
     * <code>namespaceURI</code>, <code>prefix</code>, <code>nodeValue</code> . This is: they are
     * both <code>null</code>, or they have the same length and are character for character
     * identical. </li> <li>The <code>attributes</code> <code>NamedNodeMaps</code> are equal. This
     * is: they are both <code>null</code>, or they have the same length and for each node that
     * exists in one map there is a node that exists in the other map and is equal, although not
     * necessarily at the same index. </li> <li>The <code>childNodes</code> <code>NodeLists</code>
     * are equal. This is: they are both <code>null</code>, or they have the same length and contain
     * equal nodes at the same index. Note that normalization can affect equality; to avoid this,
     * nodes should be normalized before being compared. </li> </ul> <br>For two
     * <code>DocumentType</code> nodes to be equal, the following conditions must also be satisfied:
     * <ul> <li>The following string attributes are equal: <code>publicId</code>,
     * <code>systemId</code>, <code>internalSubset</code>. </li> <li>The <code>entities</code>
     * <code>NamedNodeMaps</code> are equal. </li> <li>The <code>notations</code>
     * <code>NamedNodeMaps</code> are equal. </li> </ul> <br>On the other hand, the following do not
     * affect equality: the <code>ownerDocument</code>, <code>baseURI</code>, and
     * <code>parentNode</code> attributes, the <code>specified</code> attribute for
     * <code>Attr</code> nodes, the <code>schemaTypeInfo</code> attribute for <code>Attr</code> and
     * <code>Element</code> nodes, the <code>Text.isElementContentWhitespace</code> attribute for
     * <code>Text</code> nodes, as well as any user data or event listeners registered on the nodes.
     * <p ><b>Note:</b>  As a general rule, anything not mentioned in the description above is not
     * significant in consideration of equality checking. Note that future versions of this
     * specification may take into account more attributes and implementations conform to this
     * specification are expected to be updated accordingly.
     *
     * @param node The node to compare equality with.
     * @return Returns <code>true</code> if the nodes are equal, <code>false</code> otherwise.
     * @since DOM Level 3
     */

    //TODO : sumedha, complete
    public boolean isEqualNode(Node node) {
        final boolean equal = true;
        final boolean notEqual = false;
        if (this.getNodeType() != node.getNodeType()) {
            return notEqual;
        }
        if (checkStringAttributeEquality(node)) {
            if (checkNamedNodeMapEquality(node)) {

            } else {
                return notEqual;
            }
        } else {
            return notEqual;
        }
        return equal;
    }

    private boolean checkStringAttributeEquality(Node node) {
        final boolean equal = true;
        final boolean notEqual = false;

        // null     not-null  -> true
        // not-null null      -> true
        // null     null      -> false
        // not-null not-null  -> false

        //NodeName
        if (node.getNodeName() == null ^ this.getNodeName() == null) {
            return notEqual;
        } else {
            if (node.getNodeName() == null) {
                //This means both are null.do nothing
            } else {
                if (!(node.getNodeName().equals(this.getNodeName()))) {
                    return notEqual;
                }
            }
        }

        //localName
        if (node.getLocalName() == null ^ this.getLocalName() == null) {
            return notEqual;
        } else {
            if (node.getLocalName() == null) {
                //This means both are null.do nothing
            } else {
                if (!(node.getLocalName().equals(this.getLocalName()))) {
                    return notEqual;
                }
            }
        }

        //namespaceURI
        if (node.getNamespaceURI() == null ^ this.getNamespaceURI() == null) {
            return notEqual;
        } else {
            if (node.getNamespaceURI() == null) {
                //This means both are null.do nothing
            } else {
                if (!(node.getNamespaceURI().equals(this.getNamespaceURI()))) {
                    return notEqual;
                }
            }
        }

        //prefix
        if (node.getPrefix() == null ^ this.getPrefix() == null) {
            return notEqual;
        } else {
            if (node.getPrefix() == null) {
                //This means both are null.do nothing
            } else {
                if (!(node.getPrefix().equals(this.getPrefix()))) {
                    return notEqual;
                }
            }
        }

        //nodeValue
        if (node.getNodeValue() == null ^ this.getNodeValue() == null) {
            return notEqual;
        } else {
            if (node.getNodeValue() == null) {
                //This means both are null.do nothing
            } else {
                if (!(node.getNodeValue().equals(this.getNodeValue()))) {
                    return notEqual;
                }
            }
        }
        return equal;
    }

    private boolean checkNamedNodeMapEquality(Node node) {
        final boolean equal = true;
        final boolean notEqual = false;
        if (node.getAttributes() == null ^ this.getAttributes() == null) {
            return notEqual;
        }
        NamedNodeMap thisNamedNodeMap = this.getAttributes();
        NamedNodeMap nodeNamedNodeMap = node.getAttributes();

        // null     not-null  -> true
        // not-null null      -> true
        // null     null      -> false
        // not-null not-null  -> false

        if (thisNamedNodeMap == null ^ nodeNamedNodeMap == null) {
            return notEqual;
        } else {
            if (thisNamedNodeMap == null) {
                //This means both are null.do nothing
            } else {
                if (thisNamedNodeMap.getLength() != nodeNamedNodeMap.getLength()) {
                    return notEqual;
                } else {
                    //they have the same length and for each node that exists in one map
                    //there is a node that exists in the other map and is equal, although
                    //not necessarily at the same index.
                    int itemCount = thisNamedNodeMap.getLength();
                    for (int a = 0; a < itemCount; a++) {
                        NodeImpl thisNode = (NodeImpl) thisNamedNodeMap.item(a);
                        NodeImpl tmpNode =
                                (NodeImpl) nodeNamedNodeMap.getNamedItem(thisNode.getNodeName());
                        if (tmpNode == null) {
                            //i.e. no corresponding node
                            return notEqual;
                        } else {
                            if (!(thisNode.isEqualNode(tmpNode))) {
                                return notEqual;
                            }
                        }
                    }
                }
            }
        }
        return equal;
    }

    public Object getFeature(String feature, String version) {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    /* *
     * userData storage/hashtable will be called only when the user needs to set user data. Previously, it was done as,
     * for every node a new Hashtable created making the excution very inefficient. According to profiles, no. of method
     * invocations to setUserData() method is very low, so this implementation is better.
     * Another option:
     * TODO do a profile and check the times for hashtable initialization. If it's still higher, we have to go to second option
     * Create a separate class(UserData) to store key and value pairs. Then put those objects to a array with a reasonable size.
     * then grow it accordingly.  @ Kasun Gajasinghe 
     * @param key userData key
     * @param value userData value
     * @param userDataHandler it seems all invocations sends null for this parameter.
     *          Kept it for the moment just for being on the safe side.
     * @return previous Object if one is set before.
     */
    
    public Object setUserData(String key, Object value, UserDataHandler userDataHandler) {
        if (userData == null) {
            userData = new Hashtable();
        }
        return userData.put(key, value);
    }

    public Object getUserData(String key) {
        if (userData != null) {
            return userData.get(key);
        }
        return null;
    }

    /** Returns the <code>OMFactory</code> that created this node */
    public OMFactory getOMFactory() {
        if (factory == null) {
            factory = ((StAXSOAPModelBuilder)getBuilder()).getSOAPFactory();
        }
        return factory;
    }


    /**
     * Get the parent or the owner document of the node. The meaning of the return value depends on
     * the {@link NodeImpl#HAS_PARENT} flag.
     */
    abstract ParentNode internalGetOwnerNode();
    
    abstract void internalSetOwnerNode(ParentNode ownerNode);
    
    abstract NodeImpl internalGetPreviousSibling();
    
    abstract NodeImpl internalGetNextSibling();
    
    abstract void internalSetPreviousSibling(NodeImpl previousSibling);
    
    abstract void internalSetNextSibling(NodeImpl nextSibling);
    
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
                : ((NodeImpl)otherNode).ownerDocument())) {
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

    public final OMNode getNextOMSiblingIfAvailable() {
        return (OMNode)internalGetNextSibling();
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
        if (node instanceof NodeImpl) {
            internalSetNextSibling((NodeImpl)node);
        } else {
            throw new OMException("The node is not a " + NodeImpl.class);
        }
    }

    public final void setPreviousOMSibling(OMNode node) {
        if (node == null) {
            internalSetPreviousSibling(null);
            return;
        }
        if (node instanceof NodeImpl) {
            internalSetPreviousSibling((NodeImpl)node);
        } else {
            throw new OMException("The node is not a " + NodeImpl.class);
        }
    }

    public final OMContainer getParent() throws OMException {
        Node parent = parentNode();
        return parent instanceof OMContainer ? (OMContainer)parentNode() : null;
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
            NodeImpl previousSibling = internalGetPreviousSibling();
            NodeImpl nextSibling = internalGetNextSibling();
            if (previousSibling == null) { // This is the first child
                if (nextSibling != null) {
                    parentNode.setFirstChild((OMNode)nextSibling);
                } else {
                    parentNode.firstChild = null;
                    parentNode.lastChild = null;
                }
            } else {
                previousSibling.setNextOMSibling((OMNode)nextSibling);
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

    /** Inserts the given sibling next to this item. */
    public void insertSiblingAfter(OMNode sibling) throws OMException {
        ParentNode parentNode = parentNode();
        if (parentNode == null) {
            throw new OMException("Parent can not be null");
        } else if (this == sibling) {
            throw new OMException("Inserting self as the sibling is not allowed");
        }
        ((OMNodeEx) sibling).setParent((OMContainer)parentNode);
        if (sibling instanceof NodeImpl) {
            NodeImpl domSibling = (NodeImpl) sibling;
            domSibling.internalSetPreviousSibling(this);
            NodeImpl nextSibling = internalGetNextSibling();
            if (nextSibling == null) {
                parentNode.setLastChild(sibling);
            } else {
                nextSibling.internalSetPreviousSibling(domSibling);
            }
            domSibling.internalSetNextSibling(nextSibling);
            internalSetNextSibling(domSibling);

        } else {
            throw new OMException("The given child is not of type "
                    + NodeImpl.class);
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
        if (sibling instanceof NodeImpl) {
            // ChildNode domSibling = (ChildNode)sibling;
            // domSibling.nextSibling = this;
            // if(this.previousSibling != null) {
            // this.previousSibling.nextSibling = domSibling;
            // }
            // domSibling.previousSibling = this.previousSibling;
            // this.previousSibling = domSibling;
            NodeImpl siblingImpl = (NodeImpl) sibling;
            siblingImpl.internalSetNextSibling(this);
            NodeImpl previousSibling = internalGetPreviousSibling();
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
                    + NodeImpl.class);
        }

    }

    public abstract OMXMLParserWrapper getBuilder();
    
    public abstract void setComplete(boolean state);

    public abstract boolean isComplete();

    abstract void build();

    /**
     * Parses this node and builds the object structure in memory. AXIOM supports two levels of
     * deffered building. First is deffered building of AXIOM using StAX. Second level is the deffered
     * building of attachments. AXIOM reads in the attachements from the stream only when user asks by
     * calling getDataHandler(). build() method builds the OM without the attachments. buildAll()
     * builds the OM together with attachement data. This becomes handy when user wants to free the
     * input stream.
     */
    public void buildWithAttachments() {
        if (!this.isComplete()) {
            this.build();
        }
    }

    public void close(boolean build) {
        OMXMLParserWrapper builder = getBuilder();
        if (build) {
            this.build();
        }
        setComplete(true);
        
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
    
    public final OMInformationItem clone(OMCloneOptions options) {
        return (OMInformationItem)clone(options, null, true, true);
    }

    abstract NodeImpl clone(OMCloneOptions options, ParentNode targetParent, boolean deep, boolean namespaceRepairing);

    public Node getElementAncestor(Node currentNode) {
        Node parent = currentNode.getParentNode();
        while (parent != null) {
            short type = parent.getNodeType();
            if (type == Node.ELEMENT_NODE) {
                return parent;
            }
            parent = parent.getParentNode();
        }
        return null;
    }

}
