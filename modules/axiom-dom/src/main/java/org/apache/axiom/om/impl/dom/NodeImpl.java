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

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

import java.util.Hashtable;

public abstract class NodeImpl implements Node, NodeList, Cloneable {

    /** Holds the user data objects */
    private Hashtable userData; // Will be initialized in setUserData()

    /** Field builder */
    public OMXMLParserWrapper builder;

    /** Field done */
    protected boolean done = false;

    private DocumentImpl ownerNode;

    /** Factory that created this node */
    protected final OMFactory factory;

    // data

    protected short flags;

    protected final static short OWNED = 0x1 << 1;

    protected final static short FIRSTCHILD = 0x1 << 2;

    protected final static short READONLY = 0x1 << 3;

    protected final static short SPECIFIED = 0x1 << 4;

    //
    // Constructors
    //

    protected NodeImpl(DocumentImpl ownerDocument, OMFactory factory) {
        //this(factory);
        this.factory = factory;
        setOwnerDocument(ownerDocument);
        // this.isOwned(true);

    }

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
     * Finds the document that this Node belongs to (the document in whose context the Node was
     * created). The Node may or may not
     */
    public Document getOwnerDocument() {
        return ownerDocument();
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

    /** Returns the next child of this node's parent, or null if none. */
    public Node getNextSibling() {
        return null; // default behavior, overriden in ChildNode
    }

    /** Returns the previous child of this node's parent, or null if none. */
    public Node getPreviousSibling() {
        return null; // default behavior, overriden in ChildNode
    }

    // public Node cloneNode(boolean deep) {
    // if(this instanceof OMElement) {
    // return (Node)((OMElement)this).cloneOMElement();
    // } else if(this instanceof OMText ){
    // return ((TextImpl)this).cloneText();
    // } else {
    // throw new UnsupportedOperationException("Only elements can be cloned
    // right now");
    // }
    // }
    //    
    public Node cloneNode(boolean deep) {
        NodeImpl newnode;
        try {
            newnode = (NodeImpl) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("**Internal Error**" + e);
        }
        newnode.setOwnerDocument(ownerDocument());
        newnode.isOwned(false);

        newnode.isReadonly(false);

        return newnode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.w3c.dom.Node#getChildNodes()
     */
    public NodeList getChildNodes() {
        return this;
    }

    public boolean isSupported(String feature, String version) {
        throw new UnsupportedOperationException();
        // TODO
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.w3c.dom.Node#appendChild(org.w3c.dom.Node)
     */
    public Node appendChild(Node newChild) throws DOMException {
        return insertBefore(newChild, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.w3c.dom.Node#removeChild(org.w3c.dom.Node)
     */
    public Node removeChild(Node oldChild) throws DOMException {
        throw new DOMException(DOMException.NOT_FOUND_ERR, DOMMessageFormatter
                .formatMessage(DOMMessageFormatter.DOM_DOMAIN, DOMException.NOT_FOUND_ERR,
                               null));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.w3c.dom.Node#insertBefore(org.w3c.dom.Node, org.w3c.dom.Node)
     */
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        // Overridden in ParentNode
        throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
                               DOMMessageFormatter.formatMessage(
                                       DOMMessageFormatter.DOM_DOMAIN,
                                       DOMException.HIERARCHY_REQUEST_ERR, null));

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.w3c.dom.Node#replaceChild(org.w3c.dom.Node, org.w3c.dom.Node)
     */
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
                               DOMMessageFormatter.formatMessage(
                                       DOMMessageFormatter.DOM_DOMAIN,
                                       DOMException.HIERARCHY_REQUEST_ERR, null));
    }

    //
    // NodeList methods
    //

    /**
     * NodeList method: Returns the number of immediate children of this node.
     * <p/>
     * By default we do not have any children, ParentNode overrides this.
     *
     * @return Returns int.
     * @see ParentNode
     */
    public int getLength() {
        return 0;
    }

    /**
     * NodeList method: Returns the Nth immediate child of this node, or null if the index is out of
     * bounds.
     * <p/>
     * By default we do not have any children, ParentNode overrides this.
     *
     * @param index
     * @return Returns org.w3c.dom.Node
     * @see ParentNode
     */
    public Node item(int index) {
        return null;
    }

    /*
     * Flags setters and getters
     */

    final boolean isOwned() {
        return (flags & OWNED) != 0;
    }

    final void isOwned(boolean value) {
        flags = (short) (value ? flags | OWNED : flags & ~OWNED);
    }

    final boolean isFirstChild() {
        return (flags & FIRSTCHILD) != 0;
    }

    final void isFirstChild(boolean value) {
        flags = (short) (value ? flags | FIRSTCHILD : flags & ~FIRSTCHILD);
    }

    final boolean isReadonly() {
        return (flags & READONLY) != 0;
    }

    final void isReadonly(boolean value) {
        flags = (short) (value ? flags | READONLY : flags & ~READONLY);
    }

    final boolean isSpecified() {
        return (flags & SPECIFIED) != 0;
    }

    final void isSpecified(boolean value) {
        flags = (short) (value ? flags | SPECIFIED : flags & ~SPECIFIED);
    }

    DocumentImpl ownerDocument() {
        return ownerNode;
    }
    
    /**
     * Sets the owner document.
     *
     * @param document
     */
    void setOwnerDocument(DocumentImpl document) {
        this.ownerNode = document;
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

    public String lookupNamespaceURI(String prefix) {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
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
        return this.factory;
    }
}
