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

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import org.apache.axiom.dom.DOMConfigurationImpl;
import org.apache.axiom.dom.DOMElement;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomElement;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.impl.util.EmptyIterator;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

/** Implementation of the org.w3c.dom.Element and org.apache.axiom.om.Element interfaces. */
public class ElementImpl extends ParentNode implements DOMElement, AxiomElement, NamedNode,
        OMConstants {

    private static final Log log = LogFactory.getLog(ElementImpl.class);
    
    private int lineNumber;

    private AttributeMap attributes;

    private static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    public ElementImpl(ParentNode parentNode, String localName, OMNamespace ns, OMXMLParserWrapper builder,
                       OMFactory factory, boolean generateNSDecl) {
        super(factory);
        internalSetLocalName(localName);
        coreSetBuilder(builder);
        coreSetState(builder == null ? COMPLETE : INCOMPLETE);
        if (parentNode != null) {
            // TODO: dirty hack to get the correct semantics (reordering) if the parent is a SOAP envelope
            if (parentNode instanceof AxiomContainer) {
                ((AxiomContainer)parentNode).addChild(this, builder != null);
            } else {
                parentNode.coreAppendChild(this, builder != null);
            }
        }
        this.attributes = new AttributeMap(this);
        internalSetNamespace(generateNSDecl ? handleNamespace(this, ns, false, true) : ns);
    }

    // /
    // /org.w3c.dom.Node methods
    // /

    public String getTagName() {
        OMNamespace namespace = getNamespace();
        String localName = getLocalName();
        if (namespace != null) {
            if (namespace.getPrefix() == null
                    || "".equals(namespace.getPrefix())) {
                return localName;
            } else {
                return namespace.getPrefix() + ":" + localName;
            }
        } else {
            return localName;
        }
    }

    /** Returns the value of the namespace URI. */
    public String getNamespaceURI() {
        OMNamespace namespace = getNamespace();
        if (namespace == null) {
            return null;
        } else {
            // If the element has no namespace, the result should be null, not
            // an empty string.
            String uri = namespace.getNamespaceURI();
            return uri.length() == 0 ? null : uri.intern();
        }
    }

    // /
    // / org.w3c.dom.Element methods
    // /

    /**
     * Removes an attribute by name.
     *
     * @param name The name of the attribute to remove
     * @see org.w3c.dom.Element#removeAttribute(String)
     */
    public void removeAttribute(String name) throws DOMException {
        if (this.attributes != null) {
            this.attributes.removeNamedItem(name);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.w3c.dom.Element#removeAttributeNS(java.lang.String,
     *      java.lang.String)
     */
    public void removeAttributeNS(String namespaceURI, String localName)
            throws DOMException {
        if (this.attributes != null) {
            this.attributes.removeNamedItemNS(namespaceURI, localName);
        }
    }

    /**
     * Removes the specified attribute node.
     *
     * @see org.w3c.dom.Element#removeAttributeNode(org.w3c.dom.Attr)
     */
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        if (oldAttr.getOwnerElement() != this) {
            throw newDOMException(DOMException.NOT_FOUND_ERR);
        }
        attributes.remove((AttrImpl)oldAttr, true);
        return oldAttr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.w3c.dom.Element#hasAttribute(java.lang.String)
     */
    public boolean hasAttribute(String name) {
        return this.getAttributeNode(name) != null;
    }

    /**
     * Returns whether the given attribute is available or not.
     *
     * @see org.w3c.dom.Element#hasAttributeNS(String, String)
     */
    public boolean hasAttributeNS(String namespaceURI, String localName) {
        return this.getAttributeNodeNS(namespaceURI, localName) != null;
    }

    /**
     * Looks in the local list of attributes and returns if found. If the local list is null,
     * returns "".
     *
     * @see org.w3c.dom.Element#getAttribute(String)
     */
    public String getAttribute(String name) {
        if (attributes == null) {
            return "";
        } else {
            Attr attr = ((Attr) attributes.getNamedItem(name));
            return (attr != null) ? attr.getValue() : "";
        }
    }

    /**
     * Retrieves an attribute node by name.
     *
     * @see org.w3c.dom.Element#getAttributeNode(String)
     */
    public Attr getAttributeNode(String name) {
        return (this.attributes == null) ? null : (AttrImpl) this.attributes
                .getNamedItem(name);
    }

    /**
     * Retrieves an attribute value by local name and namespace URI.
     *
     * @see org.w3c.dom.Element#getAttributeNS(String, String)
     */
    public String getAttributeNS(String namespaceURI, String localName) {
        if (this.attributes == null) {
            return "";
        }
        Attr attributeNodeNS = this.getAttributeNodeNS(namespaceURI, localName);
        return attributeNodeNS == null ? "" : attributeNodeNS.getValue();
    }

    /**
     * Retrieves an attribute node by local name and namespace URI.
     *
     * @see org.w3c.dom.Element#getAttributeNodeNS(String, String)
     */
    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        return (this.attributes == null) ? null : (Attr) this.attributes
                .getNamedItemNS(namespaceURI, localName);
    }

    /**
     * Adds a new attribute node.
     *
     * @see org.w3c.dom.Element#setAttributeNode(org.w3c.dom.Attr)
     */
    public Attr setAttributeNode(Attr attr) throws DOMException {
        AttrImpl attrImpl = (AttrImpl) attr;

        checkSameOwnerDocument(attr);

        // check whether the attr is in use
        attrImpl.checkInUse();

        if (attr.getNodeName().startsWith(XMLConstants.XMLNS_ATTRIBUTE + ":")) {
            // This is a ns declaration
            this.declareNamespace(attr.getNodeValue(), DOMUtil
                    .getLocalName(attr.getName()));

            //Don't add this to attr list, since its a namespace
            return attr;
        } else if (attr.getNodeName().equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            this.declareDefaultNamespace(attr.getValue());

            //Don't add this to attr list, since its a namespace
            return attr;
        }
        if (this.attributes == null) {
            this.attributes = new AttributeMap(this);
        }

        return (Attr) this.attributes.setNamedItem(attr);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.w3c.dom.Element#setAttribute(java.lang.String, java.lang.String)
     */
    public void setAttribute(String name, String value) throws DOMException {
        // Check for invalid charaters
        if (!DOMUtil.isQualifiedName(name)) {
            throw newDOMException(DOMException.INVALID_CHARACTER_ERR);
        }
        if (name.startsWith(XMLConstants.XMLNS_ATTRIBUTE + ":")) {
            // This is a ns declaration
            this.declareNamespace(value, DOMUtil.getLocalName(name));
        } else if (name.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            this.declareDefaultNamespace(value);
        } else {
            this.setAttributeNode(new AttrImpl(ownerDocument(), name, value,
                                               getOMFactory()));
        }

    }

    public Attr setAttributeNodeNS(Attr attr) throws DOMException {
        return setAttributeNodeNS(attr, true, false);
    }
    
    private Attr setAttributeNodeNS(Attr attr, boolean useDomSemantics, boolean generateNSDecl) throws DOMException {
        AttrImpl attrImpl = (AttrImpl) attr;

        if (useDomSemantics) {
            checkSameOwnerDocument(attr);
        }

        // check whether the attr is in use
        attrImpl.checkInUse();

        if (this.attributes == null) {
            this.attributes = new AttributeMap(this);
        }

        // handle the namespaces
        if (generateNSDecl && attr.getNamespaceURI() != null
                && findNamespace(attr.getNamespaceURI(), attr.getPrefix())
                == null) {
            // TODO checkwhether the same ns is declared with a different
            // prefix and remove it
            this.declareNamespace(new OMNamespaceImpl(attr.getNamespaceURI(),
                                                    attr.getPrefix()));
        }

        return (Attr) this.attributes.setAttribute(attr, useDomSemantics);
    }

    /**
     * Adds a new attribute.
     *
     * @see org.w3c.dom.Element#setAttributeNS(String, String, String)
     */
    public void setAttributeNS(String namespaceURI, String qualifiedName,
                               String value) throws DOMException {
        
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        String localName = DOMUtil.getLocalName(qualifiedName);
        String prefix = DOMUtil.getPrefix(qualifiedName);
        DOMUtil.validateAttrName(namespaceURI, localName, prefix);
        
        AttrImpl attr = (AttrImpl)getAttributeNodeNS(namespaceURI, localName);
        if (attr != null) {
            attr.setPrefix(prefix);
            attr.setValue(value);
        } else {
            if (namespaceURI != null) {
                attr = new AttrImpl(ownerDocument(), localName, value, getOMFactory());
                attr.internalSetNamespace(new OMNamespaceImpl(namespaceURI, prefix == null ? "" : prefix));
    
                this.setAttributeNodeNS(attr);
            } else {
                // When the namespace is null, the attr name given better not be
                // a qualified name
                // But anyway check and set it
                this.setAttribute(localName, value);
            }
        }

    }

    /** Returns whether this element contains any attribute or not. */
    public boolean hasAttributes() {
        return attributes != null && attributes.getLength() > 0;
    }

    // /
    // /OmElement methods
    // /

    /** @see org.apache.axiom.om.OMElement#addAttribute (org.apache.axiom.om.OMAttribute) */
    public OMAttribute addAttribute(OMAttribute attr) {
        // If the attribute already has an owner element then clone the attribute (except if it is owned
        // by the this element)
        OMElement owner = attr.getOwner();
        if (owner != null) {
            if (owner == this) {
                return attr;
            }
            attr = new AttrImpl(null, attr.getLocalName(), attr.getNamespace(),
                    attr.getAttributeValue(), attr.getOMFactory());
        }
        
        OMNamespace namespace = attr.getNamespace();
        if (namespace != null) {
            String uri = namespace.getNamespaceURI();
            if (uri.length() > 0) {
                String prefix = namespace.getPrefix();
                OMNamespace ns2 = findNamespaceURI(prefix);
                if (ns2 == null || !uri.equals(ns2.getNamespaceURI())) {
                    declareNamespace(uri, prefix);
                }
            }
        }

        this.setAttributeNodeNS((Attr) attr, false, true);
        return attr;
    }

    public OMAttribute addAttribute(String localName, String value,
                                    OMNamespace ns) {
        OMNamespace namespace = null;
        if (ns != null) {
            String namespaceURI = ns.getNamespaceURI();
            String prefix = ns.getPrefix();
            if (namespaceURI.length() > 0 || prefix != null) {
                namespace = findNamespace(namespaceURI, prefix);
                if (namespace == null || prefix == null && namespace.getPrefix().length() == 0) {
                    namespace = new OMNamespaceImpl(namespaceURI, prefix != null ? prefix : OMSerializerUtil.getNextNSPrefix());
                }
            }
        }
        return addAttribute(new AttrImpl(null, localName, namespace, value, getOMFactory()));
    }

    public OMNamespace addNamespaceDeclaration(String uri, String prefix) {
        OMNamespace ns = new OMNamespaceImpl(uri, prefix);
        addNamespaceDeclaration(ns);
        return ns;
    }
    
    public void addNamespaceDeclaration(OMNamespace ns) {
        String prefix = ns.getPrefix();
        setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix.length() == 0 ? XMLConstants.XMLNS_ATTRIBUTE : XMLConstants.XMLNS_ATTRIBUTE + ":" + prefix, ns.getNamespaceURI());
    }

    /**
     * Allows overriding an existing declaration if the same prefix was used.
     *
     * @see org.apache.axiom.om.OMElement#declareNamespace (org.apache.axiom.om.OMNamespace)
     */
    public OMNamespace declareNamespace(OMNamespace namespace) {
        if (namespace != null) {
            String prefix = namespace.getPrefix();
            if (prefix == null) {
                prefix = OMSerializerUtil.getNextNSPrefix();
                namespace = new OMNamespaceImpl(namespace.getNamespaceURI(), prefix);
            }
            if (prefix.length() > 0 && namespace.getNamespaceURI().length() == 0) {
                throw new IllegalArgumentException("Cannot bind a prefix to the empty namespace name");
            }

            if (!namespace.getPrefix().startsWith(XMLConstants.XMLNS_ATTRIBUTE)) {
                setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix.length() == 0 ? XMLConstants.XMLNS_ATTRIBUTE : XMLConstants.XMLNS_ATTRIBUTE + ":" + prefix, namespace.getNamespaceURI());
            }
        }
        return namespace;
    }

    public void undeclarePrefix(String prefix) {
        setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix.length() == 0 ? XMLConstants.XMLNS_ATTRIBUTE : XMLConstants.XMLNS_ATTRIBUTE + ":" + prefix, "");
    }

    public OMNamespace declareNamespace(String uri, String prefix) {
        if ("".equals(prefix)) {
            log.warn("Deprecated usage of OMElement#declareNamespace(String,String) with empty prefix");
            prefix = OMSerializerUtil.getNextNSPrefix();
        }
        
        OMNamespaceImpl ns = new OMNamespaceImpl(uri, prefix);
        return declareNamespace(ns);
    }

    public OMNamespace declareDefaultNamespace(String uri) {
        OMNamespace namespace = getNamespace();
        if (namespace == null && uri.length() > 0
                || namespace != null && namespace.getPrefix().length() == 0 && !namespace.getNamespaceURI().equals(uri)) {
            throw new OMException("Attempt to add a namespace declaration that conflicts with " +
                    "the namespace information of the element");
        }

        setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE, uri);
        return new OMNamespaceImpl(uri, "");
    }

    public OMNamespace getDefaultNamespace() {
        Attr decl = (Attr)attributes.getNamedItemNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE);
        if (decl != null) {
            String uri = decl.getValue();
            return uri.length() == 0 ? null : new OMNamespaceImpl(uri, "");
        }

        ParentNode parentNode = (ParentNode)coreGetParent();
        if (parentNode instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) parentNode;
            return element.getDefaultNamespace();
        }
        return null;
    }

    /** @see org.apache.axiom.om.OMElement#findNamespace(String, String) */
    public OMNamespace findNamespace(String uri, String prefix) {

        // check in the current element
        OMNamespace namespace = findDeclaredNamespace(uri, prefix);
        if (namespace != null) {
            return namespace;
        }

        // go up to check with ancestors
        ParentNode parentNode = (ParentNode)coreGetParent();
        if (parentNode != null) {
            // For the OMDocumentImpl there won't be any explicit namespace
            // declarations, so going up the parent chain till the document
            // element should be enough.
            if (parentNode instanceof OMElement) {
                namespace = ((ElementImpl) parentNode).findNamespace(uri,
                                                                     prefix);
                // If the prefix has been redeclared, then ignore the binding found on the ancestors
                if (prefix == null && namespace != null && findDeclaredNamespace(null, namespace.getPrefix()) != null) {
                    namespace = null;
                }
            }
        }

        if (namespace == null && uri != null && prefix != null
                && prefix.equals(OMConstants.XMLNS_PREFIX)
                && uri.equals(OMConstants.XMLNS_URI)) {
            declareNamespace(OMConstants.XMLNS_URI, OMConstants.XMLNS_PREFIX);
            namespace = findNamespace(uri, prefix);
        }
        return namespace;
    }

    public OMNamespace findNamespaceURI(String prefix) {
        if (attributes != null) {
            Attr decl = (Attr)attributes.getNamedItemNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix.length() == 0 ? XMLConstants.XMLNS_ATTRIBUTE : prefix);
            if (decl != null) {
                String namespaceURI = decl.getValue();
                if (prefix != null && prefix.length() > 0 && namespaceURI.length() == 0) {
                    // Prefix undeclaring case (XML 1.1 only)
                    return null;
                } else {
                    return new OMNamespaceImpl(namespaceURI, prefix);
                }
            }
        }
        ParentNode parentNode = (ParentNode)coreGetParent();
        if (parentNode instanceof OMElement) {
            // try with the parent
            return ((OMElement)parentNode).findNamespaceURI(prefix);
        } else {
            return null;
        }
    }

    /**
     * Checks for the namespace <B>only</B> in the current Element. This can also be used to
     * retrieve the prefix of a known namespace URI.
     */
    private OMNamespace findDeclaredNamespace(String uri, String prefix) {

        if (uri == null) {
            Attr decl = (Attr)attributes.getNamedItemNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
                    prefix.length() == 0 ? XMLConstants.XMLNS_ATTRIBUTE : prefix);
            return decl == null ? null : new OMNamespaceImpl(decl.getValue(), prefix);
        }
        // If the prefix is available and uri is available and its the xml
        // namespace
        if (prefix != null && prefix.equals(OMConstants.XMLNS_PREFIX)
                && uri.equals(OMConstants.XMLNS_URI)) {
            return new OMNamespaceImpl(uri, prefix);
        }

        if (prefix == null || "".equals(prefix)) {
            for (int i=0; i<attributes.getLength(); i++) {
                Attr attr = (Attr)attributes.item(i);
                if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI())) {
                    String declaredUri = attr.getValue();
                    if (declaredUri.equals(uri)) {
                        return new OMNamespaceImpl(uri, attr.getPrefix() == null ? "" : attr.getLocalName());
                    }
                }
            }
        } else {
            Attr decl = (Attr)attributes.getNamedItemNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, prefix);
            if (decl != null) {
                String declaredUri = decl.getValue();
                if (declaredUri.equals(uri)) {
                    return new OMNamespaceImpl(uri, prefix);
                }
            }
        }

        return null;
    }

    /**
     * Returns a named attribute if present.
     *
     * @see org.apache.axiom.om.OMElement#getAttribute (javax.xml.namespace.QName)
     */
    public OMAttribute getAttribute(QName qname) {
        if (this.attributes == null) {
            return null;
        }

        if (qname.getNamespaceURI().equals("")) {
            return (AttrImpl) this.getAttributeNode(qname.getLocalPart());
        } else {
            return (AttrImpl) this.getAttributeNodeNS(qname.getNamespaceURI(),
                                                      qname.getLocalPart());
        }
    }

    /**
     * Returns a named attribute's value, if present.
     *
     * @param qname the qualified name to search for
     * @return Returns a String containing the attribute value, or null.
     */
    public String getAttributeValue(QName qname) {
        OMAttribute attr = getAttribute(qname);
        return (attr == null) ? null : attr.getAttributeValue();
    }

    public void removeAttribute(OMAttribute attr) {
        if (attr.getOwner() != this) {
            throw new OMException("The attribute is not owned by this element");
        }
        attributes.remove((AttrImpl)attr, false);
    }

    public void setNamespace(OMNamespace namespace) {
        setNamespace(namespace, true);
    }

    public void internalSerialize(Serializer serializer,
                                     OMOutputFormat format, boolean cache) throws OutputException {

        serializer.serializeStartpart(this);
        serializer.serializeChildren(this, format, cache);
        serializer.writeEndElement();
    }

    public String toStringWithConsume() throws XMLStreamException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.serializeAndConsume(baos);
        return new String(baos.toByteArray());
    }

    /**
     * Overridden toString() for ease of debugging.
     *
     * @see Object#toString()
     */
    public String toString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
//            this.build();
            this.serialize(baos);
        } catch (XMLStreamException e) {
            throw new RuntimeException("Can not serialize OM Element " + this.getLocalName(), e);
        }
        return new String(baos.toByteArray());
    }

    /** @see org.apache.axiom.om.OMElement#getAllDeclaredNamespaces() */
    public Iterator getAllDeclaredNamespaces() throws OMException {
        return new NSDeclIterator(attributes);
    }

    /** @see org.apache.axiom.om.OMElement#getAllAttributes() */
    public Iterator getAllAttributes() {
        if (attributes == null) {
            return EMPTY_ITERATOR;
        }
        ArrayList list = new ArrayList();
        for (int i = 0; i < attributes.getLength(); i++) {
            OMAttribute item = (OMAttribute) attributes.getItem(i);
            if (item.getNamespace() == null
                    || !(item.getNamespace() != null && XMLConstants.XMLNS_ATTRIBUTE_NS_URI
                    .equals(item.getNamespace().getNamespaceURI()))) {
                list.add(item);
            }
        }

        return list.iterator();
    }

    public OMElement cloneOMElement() {
        return (OMElement)clone(new OMCloneOptions());
    }

    final ParentNode shallowClone(OMCloneOptions options, ParentNode targetParent, boolean namespaceRepairing) {
        ElementImpl clone;
        if (options.isPreserveModel()) {
            clone = (ElementImpl)createClone(options, targetParent, namespaceRepairing);
        } else {
            clone = new ElementImpl(targetParent, getLocalName(), getNamespace(), null, getOMFactory(), namespaceRepairing);
        }
        for (int i=0, l=attributes.getLength(); i<l; i++) {
            AttrImpl attr = (AttrImpl)attributes.item(i);
            AttrImpl clonedAttr = (AttrImpl)attr.clone(options, null, true, false);
            clonedAttr.setSpecified(attr.getSpecified());
            clone.setAttributeNodeNS(clonedAttr, false, namespaceRepairing && !XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI()));
        }
        return clone;
    }

    protected OMElement createClone(OMCloneOptions options, ParentNode targetParent, boolean generateNSDecl) {
        return new ElementImpl(targetParent, getLocalName(), getNamespace(), null, getOMFactory(), generateNSDecl);
    }
    
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    /** Returns the set of attributes of this node and the namespace declarations available. */
    public NamedNodeMap getAttributes() {
        return attributes;
    }

    /**
     * Returns the namespace uri, given the prefix. If it is not found at this element, searches the
     * parent.
     *
     * @param prefix
     * @return Returns namespace.
     */
    public String getNamespaceURI(String prefix) {
        OMNamespace ns = this.findNamespaceURI(prefix);
        return (ns != null) ? ns.getNamespaceURI() : null;
    }

    /*
     * DOM-Level 3 methods
     */

    public void setIdAttribute(String name, boolean isId) throws DOMException {
        //find the attr
        AttrImpl tempAttr = (AttrImpl) this.getAttributeNode(name);
        if (tempAttr == null) {
            throw newDOMException(DOMException.NOT_FOUND_ERR);
        }

        this.updateIsId(isId, tempAttr);
    }

    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId)
            throws DOMException {
        //find the attr
        AttrImpl tempAttr = (AttrImpl) this.getAttributeNodeNS(namespaceURI, localName);
        if (tempAttr == null) {
            throw newDOMException(DOMException.NOT_FOUND_ERR);
        }

        this.updateIsId(isId, tempAttr);
    }

    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        //find the attr
        Iterator attrIter = this.getAllAttributes();
        AttrImpl tempAttr = null;
        while (attrIter.hasNext()) {
            AttrImpl attr = (AttrImpl) attrIter.next();
            if (attr.equals(idAttr)) {
                tempAttr = attr;
                break;
            }
        }

        if (tempAttr == null) {
            throw newDOMException(DOMException.NOT_FOUND_ERR);
        }

        this.updateIsId(isId, tempAttr);
    }

    /**
     * Updates the id state of the attr and notifies the document
     *
     * @param isId
     * @param tempAttr
     */
    private void updateIsId(boolean isId, AttrImpl tempAttr) {
        tempAttr.isId = isId;
        if (isId) {
            ownerDocument().addIdAttr(tempAttr);
        } else {
            ownerDocument().removeIdAttr(tempAttr);
        }
    }

    /* (non-Javadoc)
      * @see org.apache.axiom.om.OMNode#buildAll()
      */
    public void buildWithAttachments() {
        if (getState() == INCOMPLETE) {
            this.build();
        }
        Iterator iterator = getChildren();
        while (iterator.hasNext()) {
            OMNode node = (OMNode) iterator.next();
            node.buildWithAttachments();
        }
    }

    public void normalize(DOMConfigurationImpl config) {
        if (config.isEnabled(DOMConfigurationImpl.NAMESPACES)) {
            OMNamespace namespace = getNamespace();
            if (namespace == null) {
                if (getDefaultNamespace() != null) {
                    declareDefaultNamespace("");
                }
            } else {
                OMNamespace namespaceForPrefix = findNamespaceURI(namespace.getPrefix());
                if (namespaceForPrefix == null || !namespaceForPrefix.getNamespaceURI().equals(namespace.getNamespaceURI())) {
                    declareNamespace(namespace);
                }
            }
        }
        super.normalize(config);
    }

    public final void setComplete(boolean complete) {
        coreSetState(complete ? COMPLETE : INCOMPLETE);
        ParentNode parentNode = (ParentNode)coreGetParent();
        if (parentNode != null) {
            if (!complete) {
                ((DOMContainer)parentNode).setComplete(false);
            } else {
                ((DOMContainer)parentNode).notifyChildComplete();
            }
        }
    }

    public final void build() {
        defaultBuild();
    }

    public final void checkChild(OMNode child) {
    }
}
