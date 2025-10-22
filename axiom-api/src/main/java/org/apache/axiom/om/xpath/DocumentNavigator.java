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

package org.apache.axiom.om.xpath;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.jaxen.BaseXPath;
import org.jaxen.DefaultNavigator;
import org.jaxen.FunctionCallException;
import org.jaxen.JaxenConstants;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.util.SingleObjectIterator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DocumentNavigator extends DefaultNavigator {

    private static final long serialVersionUID = 7325116153349780805L;

    /**
     * Returns a parsed form of the given xpath string, which will be suitable for queries on
     * documents that use the same navigator as this one.
     *
     * @param xpath the XPath expression
     * @return Returns a new XPath expression object.
     * @throws SAXPathException if the string is not a syntactically correct XPath expression
     * @see XPath
     */
    @Override
    public XPath parseXPath(String xpath) throws SAXPathException {
        return new BaseXPath(xpath, this);
    }

    /**
     * Retrieves the namespace URI of the given element node.
     *
     * @param object the context element node
     * @return Returns the namespace URI of the element node.
     */
    @Override
    public String getElementNamespaceUri(Object object) {
        OMElement attr = (OMElement) object;
        return attr.getQName().getNamespaceURI();
    }

    /**
     * Retrieves the name of the given element node.
     *
     * @param object the context element node
     * @return Returns the name of the element node.
     */
    @Override
    public String getElementName(Object object) {
        return ((OMElement) object).getLocalName();
    }

    /**
     * Retrieves the QName of the given element node.
     *
     * @param object the context element node
     * @return Returns the QName of the element node.
     */
    @Override
    public String getElementQName(Object object) {
        OMElement attr = (OMElement) object;
        String prefix = null;
        OMNamespace namespace = attr.getNamespace();
        if (namespace != null) {
            prefix = namespace.getPrefix();
        }
        if (prefix == null || "".equals(prefix)) {
            return attr.getLocalName();
        }
        return prefix + ":" + namespace.getNamespaceURI();
    }

    /**
     * Retrieves the namespace URI of the given attribute node.
     *
     * @param object the context attribute node
     * @return Returns the namespace URI of the attribute node.
     */
    @Override
    public String getAttributeNamespaceUri(Object object) {
        OMAttribute attr = (OMAttribute) object;
        return attr.getQName().getNamespaceURI();
    }

    /**
     * Retrieves the name of the given attribute node.
     *
     * @param object the context attribute node
     * @return Returns the name of the attribute node.
     */
    @Override
    public String getAttributeName(Object object) {
        return ((OMAttribute) object).getLocalName();
    }

    /**
     * Retrieves the QName of the given attribute node.
     *
     * @param object the context attribute node
     * @return Returns the qualified name of the attribute node.
     */
    @Override
    public String getAttributeQName(Object object) {
        OMAttribute attr = (OMAttribute) object;
        String prefix = attr.getNamespace().getPrefix();
        if (prefix == null || "".equals(prefix)) {
            return attr.getLocalName();
        }
        return prefix + ":" + attr.getLocalName();
    }

    /**
     * Returns whether the given object is a document node. A document node is the node that is
     * selected by the xpath expression <code>/</code>.
     *
     * @param object the object to test
     * @return Returns <code>true</code> if the object is a document node, else <code>false</code> .
     */
    @Override
    public boolean isDocument(Object object) {
        return object instanceof OMDocument;
    }

    /**
     * Returns whether the given object is an element node.
     *
     * @param object the object to test
     * @return Returns <code>true</code> if the object is an element node, else <code>false</code> .
     */
    @Override
    public boolean isElement(Object object) {
        return object instanceof OMElement;
    }

    /**
     * Returns whether the given object is an attribute node.
     *
     * @param object the object to test
     * @return Returns <code>true</code> if the object is an attribute node, else <code>false</code>
     *     .
     */
    @Override
    public boolean isAttribute(Object object) {
        return object instanceof OMAttribute;
    }

    /**
     * Returns whether the given object is a namespace node.
     *
     * @param object the object to test
     * @return Returns <code>true</code> if the object is a namespace node, else <code>false</code>
     *     .
     */
    @Override
    public boolean isNamespace(Object object) {
        return object instanceof OMNamespace;
    }

    /**
     * Returns whether the given object is a comment node.
     *
     * @param object the object to test
     * @return Returns <code>true</code> if the object is a comment node, else <code>false</code> .
     */
    @Override
    public boolean isComment(Object object) {
        return (object instanceof OMComment);
    }

    /**
     * Returns whether the given object is a text node.
     *
     * @param object the object to test
     * @return Returns <code>true</code> if the object is a text node, else <code>false</code> .
     */
    @Override
    public boolean isText(Object object) {
        return (object instanceof OMText);
    }

    /**
     * Returns whether the given object is a processing-instruction node.
     *
     * @param object the object to test
     * @return Returns <code>true</code> if the object is a processing-instruction node, else <code>
     *     false</code> .
     */
    @Override
    public boolean isProcessingInstruction(Object object) {
        return (object instanceof OMProcessingInstruction);
    }

    /**
     * Retrieves the string-value of a comment node. This may be the empty string if the comment is
     * empty, but must not be null.
     *
     * @param object the comment node
     * @return Returns the string-value of the node.
     */
    @Override
    public String getCommentStringValue(Object object) {
        return ((OMComment) object).getValue();
    }

    /**
     * Retrieves the string-value of an element node. This may be the empty string if the element is
     * empty, but must not be null.
     *
     * @param object the comment node.
     * @return Returns the string-value of the node.
     */
    @Override
    public String getElementStringValue(Object object) {
        if (isElement(object)) {
            return getStringValue((OMElement) object, new StringBuffer()).toString();
        }
        return null;
    }

    private StringBuffer getStringValue(OMNode node, StringBuffer buffer) {
        if (isText(node)) {
            buffer.append(((OMText) node).getText());
        } else if (node instanceof OMElement) {
            Iterator<OMNode> children = ((OMElement) node).getChildren();
            while (children.hasNext()) {
                getStringValue(children.next(), buffer);
            }
        }
        return buffer;
    }

    /**
     * Retrieves the string-value of an attribute node. This should be the XML 1.0 normalized
     * attribute value. This may be the empty string but must not be null.
     *
     * @param object the attribute node
     * @return Returns the string-value of the node.
     */
    @Override
    public String getAttributeStringValue(Object object) {
        return ((OMAttribute) object).getAttributeValue();
    }

    /**
     * Retrieves the string-value of a namespace node. This is generally the namespace URI. This may
     * be the empty string but must not be null.
     *
     * @param object the namespace node
     * @return Returns the string-value of the node.
     */
    @Override
    public String getNamespaceStringValue(Object object) {
        return ((OMNamespace) object).getNamespaceURI();
    }

    /**
     * Retrieve the string-value of a text node. This must not be null and should not be the empty
     * string. The XPath data model does not allow empty text nodes.
     *
     * @param object the text node
     * @return Returns the string-value of the node.
     */
    @Override
    public String getTextStringValue(Object object) {
        return ((OMText) object).getText();
    }

    /**
     * Retrieves the namespace prefix of a namespace node.
     *
     * @param object the namespace node
     * @return Returns the prefix associated with the node.
     */
    @Override
    public String getNamespacePrefix(Object object) {
        return ((OMNamespace) object).getPrefix();
    }

    /**
     * Retrieves an <code>Iterator</code> matching the <code>child</code> XPath axis.
     *
     * @param contextNode the original context node
     * @return Returns an Iterator capable of traversing the axis, not null.
     * @throws UnsupportedAxisException if the semantics of the child axis are not supported by this
     *     object model
     */
    @Override
    public Iterator<?> getChildAxisIterator(Object contextNode) throws UnsupportedAxisException {
        if (contextNode instanceof OMContainer) {
            return ((OMContainer) contextNode).getChildren();
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }

    @Override
    public Iterator<?> getDescendantAxisIterator(Object object) throws UnsupportedAxisException {
        // TODO: Fix this better?
        return super.getDescendantAxisIterator(object);
    }

    /**
     * Retrieves an <code>Iterator</code> matching the <code>attribute</code> XPath axis.
     *
     * @param contextNode the original context node
     * @return Returns an Iterator capable of traversing the axis, not null.
     * @throws UnsupportedAxisException if the semantics of the attribute axis are not supported by
     *     this object model
     */
    @Override
    public Iterator<?> getAttributeAxisIterator(Object contextNode)
            throws UnsupportedAxisException {
        if (isElement(contextNode)) {
            return ((OMElement) contextNode).getAllAttributes();
        } else {
            return JaxenConstants.EMPTY_ITERATOR;
        }
    }

    /**
     * Retrieves an <code>Iterator</code> matching the <code>namespace</code> XPath axis.
     *
     * @param contextNode the original context node
     * @return Returns an Iterator capable of traversing the axis, not null.
     * @throws UnsupportedAxisException if the semantics of the namespace axis are not supported by
     *     this object model
     */
    @Override
    public Iterator<?> getNamespaceAxisIterator(Object contextNode)
            throws UnsupportedAxisException {
        if (!(contextNode instanceof OMContainer && contextNode instanceof OMElement)) {
            return JaxenConstants.EMPTY_ITERATOR;
        }
        OMContainer omContextNode = (OMContainer) contextNode;
        List<OMNamespaceEx> nsList = new ArrayList<OMNamespaceEx>();
        Set<String> prefixes = new HashSet<String>();
        for (OMContainer context = omContextNode;
                context != null && !(context instanceof OMDocument);
                context = ((OMElement) context).getParent()) {
            OMElement element = (OMElement) context;
            List<OMNamespace> declaredNS = new ArrayList<OMNamespace>();
            Iterator<OMNamespace> i = element.getAllDeclaredNamespaces();
            while (i != null && i.hasNext()) {
                declaredNS.add(i.next());
            }
            declaredNS.add(element.getNamespace());
            for (Iterator<OMAttribute> iter = element.getAllAttributes();
                    iter != null && iter.hasNext(); ) {
                OMAttribute attr = iter.next();
                OMNamespace namespace = attr.getNamespace();
                if (namespace != null) {
                    declaredNS.add(namespace);
                }
            }
            for (OMNamespace namespace : declaredNS) {
                if (namespace != null) {
                    String prefix = namespace.getPrefix();
                    if (prefix != null && !prefixes.contains(prefix)) {
                        prefixes.add(prefix);
                        nsList.add(new OMNamespaceEx(namespace, context));
                    }
                }
            }
        }
        nsList.add(
                new OMNamespaceEx(
                        omContextNode
                                .getOMFactory()
                                .createOMNamespace("http://www.w3.org/XML/1998/namespace", "xml"),
                        omContextNode));
        return nsList.iterator();
    }

    /**
     * Retrieves an <code>Iterator</code> matching the <code>self</code> xpath axis.
     *
     * @param contextNode the original context node
     * @return Returns an Iterator capable of traversing the axis, not null.
     * @throws UnsupportedAxisException if the semantics of the self axis are not supported by this
     *     object model
     */
    @Override
    public Iterator<?> getSelfAxisIterator(Object contextNode) throws UnsupportedAxisException {
        // TODO: Fix this better?
        return super.getSelfAxisIterator(contextNode);
    }

    /**
     * Retrieves an <code>Iterator</code> matching the <code>descendant-or-self</code> XPath axis.
     *
     * @param contextNode the original context node
     * @return Returns an Iterator capable of traversing the axis, not null.
     * @throws UnsupportedAxisException if the semantics of the descendant-or-self axis are not
     *     supported by this object model
     */
    @Override
    public Iterator<?> getDescendantOrSelfAxisIterator(Object contextNode)
            throws UnsupportedAxisException {
        // TODO: Fix this better?
        return super.getDescendantOrSelfAxisIterator(contextNode);
    }

    /**
     * Retrieves an <code>Iterator</code> matching the <code>ancestor-or-self</code> XPath axis.
     *
     * @param contextNode the original context node
     * @return Returns an Iterator capable of traversing the axis, not null.
     * @throws UnsupportedAxisException if the semantics of the ancestor-or-self axis are not
     *     supported by this object model
     */
    @Override
    public Iterator<?> getAncestorOrSelfAxisIterator(Object contextNode)
            throws UnsupportedAxisException {
        // TODO: Fix this better?
        return super.getAncestorOrSelfAxisIterator(contextNode);
    }

    /**
     * Retrieves an <code>Iterator</code> matching the <code>parent</code> XPath axis.
     *
     * @param contextNode the original context node
     * @return Returns an Iterator capable of traversing the axis, not null.
     * @throws UnsupportedAxisException if the semantics of the parent axis are not supported by
     *     this object model
     */
    @Override
    public Iterator<?> getParentAxisIterator(Object contextNode) throws UnsupportedAxisException {
        if (contextNode instanceof OMNode) {
            return new SingleObjectIterator(((OMNode) contextNode).getParent());
        } else if (contextNode instanceof OMNamespaceEx) {
            return new SingleObjectIterator(((OMNamespaceEx) contextNode).getParent());
        } else if (contextNode instanceof OMAttribute) {
            return new SingleObjectIterator(((OMAttribute) contextNode).getOwner());
        }
        return JaxenConstants.EMPTY_ITERATOR;
    }

    /**
     * Retrieves an <code>Iterator</code> matching the <code>ancestor</code> XPath axis.
     *
     * @param contextNode the original context node
     * @return Returns an Iterator capable of traversing the axis, not null.
     * @throws UnsupportedAxisException if the semantics of the ancestor axis are not supported by
     *     this object model
     */
    @Override
    public Iterator<?> getAncestorAxisIterator(Object contextNode) throws UnsupportedAxisException {
        // TODO: Fix this better?
        return super.getAncestorAxisIterator(contextNode);
    }

    /**
     * Retrieves an <code>Iterator</code> matching the <code>following-sibling</code> XPath axis.
     *
     * @param contextNode the original context node
     * @return Returns an Iterator capable of traversing the axis, not null.
     * @throws UnsupportedAxisException if the semantics of the following-sibling axis are not
     *     supported by this object model
     */
    @Override
    public Iterator<?> getFollowingSiblingAxisIterator(Object contextNode)
            throws UnsupportedAxisException {
        List<OMNode> list = new ArrayList<OMNode>();
        if (contextNode != null && contextNode instanceof OMNode) {
            OMNode node = (OMNode) contextNode;
            while (true) {
                node = node.getNextOMSibling();
                if (node != null) {
                    list.add(node);
                } else {
                    break;
                }
            }
        }
        return list.iterator();
    }

    /**
     * Retrieves an <code>Iterator</code> matching the <code>preceding-sibling</code> XPath axis.
     *
     * @param contextNode the original context node
     * @return Returns an Iterator capable of traversing the axis, not null.
     * @throws UnsupportedAxisException if the semantics of the preceding-sibling axis are not
     *     supported by this object model
     */
    @Override
    public Iterator<?> getPrecedingSiblingAxisIterator(Object contextNode)
            throws UnsupportedAxisException {
        List<OMNode> list = new ArrayList<OMNode>();
        if (contextNode != null && contextNode instanceof OMNode) {
            OMNode node = (OMNode) contextNode;
            while (true) {
                node = node.getPreviousOMSibling();
                if (node != null) {
                    list.add(node);
                } else {
                    break;
                }
            }
        }
        return list.iterator();
    }

    /**
     * Retrieves an <code>Iterator</code> matching the <code>following</code> XPath axis.
     *
     * @param contextNode the original context node
     * @return Returns an Iterator capable of traversing the axis, not null.
     * @throws UnsupportedAxisException if the semantics of the following axis are not supported by
     *     this object model
     */
    @Override
    public Iterator<?> getFollowingAxisIterator(Object contextNode)
            throws UnsupportedAxisException {
        // TODO: Fix this better?
        return super.getFollowingAxisIterator(contextNode);
    }

    /**
     * Retrieves an <code>Iterator</code> matching the <code>preceding</code> XPath axis.
     *
     * @param contextNode the original context node
     * @return Returns an Iterator capable of traversing the axis, not null.
     * @throws UnsupportedAxisException if the semantics of the preceding axis are not supported by
     *     this object model
     */
    @Override
    public Iterator<?> getPrecedingAxisIterator(Object contextNode)
            throws UnsupportedAxisException {
        // TODO: Fix this better?
        return super.getPrecedingAxisIterator(contextNode);
    }

    /**
     * Loads a document from the given URI.
     *
     * @param uri the URI of the document to load
     * @return Returns the document.
     * @throws FunctionCallException if the document could not be loaded
     */
    @Override
    public Object getDocument(String uri) throws FunctionCallException {
        InputStream in = null;
        try {
            if (uri.indexOf(':') == -1) {
                in = new FileInputStream(uri);
            } else {
                URL url = new URL(uri);
                in = url.openStream();
            }
            return OMXMLBuilderFactory.createOMBuilder(in).getDocument();
        } catch (Exception e) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    // Ignore
                }
            }
            throw new FunctionCallException(e);
        }
    }

    /**
     * Returns the element whose ID is given by elementId. If no such element exists, returns null.
     * Attributes with the name "ID" are not of type ID unless so defined. Implementations that do
     * not know whether attributes are of type ID or not are expected to return null.
     *
     * @param contextNode a node from the document in which to look for the id
     * @param elementId id to look for
     * @return Returns element whose ID is given by elementId, or null if no such element exists in
     *     the document or if the implementation does not know about attribute types.
     */
    @Override
    public Object getElementById(Object contextNode, String elementId) {
        // TODO: Fix this better?
        return super.getElementById(contextNode, elementId);
    }

    /**
     * Returns the document node that contains the given context node.
     *
     * @param contextNode the context node
     * @return Returns the document of the context node.
     * @see #isDocument(Object)
     */
    @Override
    public Object getDocumentNode(Object contextNode) {
        if (contextNode instanceof OMDocument) {
            return contextNode;
        }
        OMContainer parent = ((OMNode) contextNode).getParent();
        if (parent == null) {
            // this node doesn't have a parent Document. So return the document element itself
            return contextNode;
        } else {
            return getDocumentNode(parent);
        }
    }

    /**
     * Translates a namespace prefix to a namespace URI, <b>possibly</b> considering a particular
     * element node.
     *
     * <p>Strictly speaking, prefix-to-URI translation should occur irrespective of any element in
     * the document. This method is provided to allow a non-conforming ease-of-use enhancement.
     *
     * @param prefix the prefix to translate
     * @param element the element to consider during translation
     * @return Returns the namespace URI associated with the prefix.
     */
    @Override
    public String translateNamespacePrefixToUri(String prefix, Object element) {
        // TODO: Fix this better?
        return super.translateNamespacePrefixToUri(prefix, element);
    }

    /**
     * Retrieves the target of a processing-instruction.
     *
     * @param object the context processing-instruction node
     * @return Returns the target of the processing-instruction node.
     */
    @Override
    public String getProcessingInstructionTarget(Object object) {
        return ((OMProcessingInstruction) object).getTarget();
    }

    /**
     * Retrieves the data of a processing-instruction.
     *
     * @param object the context processing-instruction node
     * @return Returns the data of the processing-instruction node.
     */
    @Override
    public String getProcessingInstructionData(Object object) {
        return ((OMProcessingInstruction) object).getValue();
    }

    /**
     * Returns a number that identifies the type of node that the given object represents in this
     * navigator. See org.jaxen.pattern.Pattern
     *
     * @param node ????
     * @return Returns short.
     */
    @Override
    public short getNodeType(Object node) {
        // TODO: Fix this better?
        return super.getNodeType(node);
    }

    /**
     * Returns the parent of the given context node.
     *
     * <p>The parent of any node must either be a document node or an element node.
     *
     * @param contextNode the context node
     * @return Returns the parent of the context node, or null if this is a document node.
     * @throws UnsupportedAxisException if the parent axis is not supported by the model
     * @see #isDocument
     * @see #isElement
     */
    @Override
    public Object getParentNode(Object contextNode) throws UnsupportedAxisException {
        if (contextNode == null || contextNode instanceof OMDocument) {
            return null;
        } else if (contextNode instanceof OMAttribute) {
            return ((OMAttribute) contextNode).getOwner();
        } else if (contextNode instanceof OMNamespaceEx) {
            return ((OMNamespaceEx) contextNode).getParent();
        }
        return ((OMNode) contextNode).getParent();
    }

    class OMNamespaceEx implements OMNamespace {
        final OMNamespace originalNsp;
        final OMContainer parent;

        OMNamespaceEx(OMNamespace nsp, OMContainer parent) {
            originalNsp = nsp;
            this.parent = parent;
        }

        @Override
        public boolean equals(String uri, String prefix) {
            return originalNsp.equals(uri, prefix);
        }

        @Override
        public String getPrefix() {
            return originalNsp.getPrefix();
        }

        @Override
        public String getName() {
            return originalNsp.getNamespaceURI();
        }

        @Override
        public String getNamespaceURI() {
            return originalNsp.getNamespaceURI();
        }

        public OMContainer getParent() {
            return parent;
        }
    }
}
