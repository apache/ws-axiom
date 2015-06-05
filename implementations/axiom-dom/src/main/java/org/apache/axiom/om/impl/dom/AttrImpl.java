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

import javax.xml.XMLConstants;

import org.apache.axiom.core.NonDeferringParentNode;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.OMAttributeEx;
import org.apache.axiom.om.impl.common.AxiomAttribute;
import org.apache.axiom.om.impl.common.AxiomText;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

/** Implementation of <code>org.w3c.dom.Attr</code> and <code>org.apache.axiom.om.OMAttribute</code> */
public class AttrImpl extends RootNode implements OMAttributeEx, AxiomAttribute, Attr, NamedNode, NonDeferringParentNode {
    private String type;

    /** Flag used to mark an attribute as per the DOM Level 3 specification */
    protected boolean isId;

    private AttrImpl(DocumentImpl ownerDocument, OMFactory factory) {
        super(factory);
        coreSetOwnerDocument(ownerDocument);
    }

    // TODO: copy isId?
    private AttrImpl(String localName, OMNamespace namespace, String type, OMFactory factory) {
        this(null, factory);
        internalSetLocalName(localName);
        internalSetNamespace(namespace);
        this.type = type;
    }
    
    public AttrImpl(DocumentImpl ownerDocument, String localName,
                    OMNamespace ns, String value, OMFactory factory) {
        this(ownerDocument, factory);
        if (ns != null) {
            if (ns.getNamespaceURI().length() == 0) {
                if (ns.getPrefix().length() > 0) {
                    throw new IllegalArgumentException("Cannot create a prefixed attribute with an empty namespace name");
                } else {
                    ns = null;
                }
            } else if (ns.getPrefix().length() == 0) {
                throw new IllegalArgumentException("Cannot create an unprefixed attribute with a namespace");
            }
        }
        internalSetLocalName(localName);
        coreAppendChild((AxiomText)factory.createOMText(value), false);
        this.type = OMConstants.XMLATTRTYPE_CDATA;
        internalSetNamespace(ns);
    }

    public AttrImpl(DocumentImpl ownerDocument, String name, String value,
                    OMFactory factory) {
        this(ownerDocument, factory);
        internalSetLocalName(name);
        coreAppendChild((AxiomText)factory.createOMText(value), false);
        this.type = OMConstants.XMLATTRTYPE_CDATA;
    }

    public AttrImpl(DocumentImpl ownerDocument, String name, OMFactory factory) {
        this(ownerDocument, factory);
        internalSetLocalName(name);
        //If this is a default namespace attr
        if (XMLConstants.XMLNS_ATTRIBUTE.equals(name)) {
            // TODO: this looks wrong; if the attribute name is "xmlns", then the prefix shouldn't be "xmlns"
            internalSetNamespace(new OMNamespaceImpl(
                    XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE));
        }
        this.type = OMConstants.XMLATTRTYPE_CDATA;
    }

    public AttrImpl(DocumentImpl ownerDocument, String localName,
                    OMNamespace namespace, OMFactory factory) {
        this(ownerDocument, factory);
        internalSetLocalName(localName);
        internalSetNamespace(namespace);
        this.type = OMConstants.XMLATTRTYPE_CDATA;
    }

    // /
    // /org.w3c.dom.Node methods
    // /

    /** Returns the name of this attribute. */
    public String getNodeName() {
        OMNamespace namespace = getNamespace();
        String localName = getLocalName();
        return (namespace != null
                && !"".equals(namespace.getPrefix()) &&
                !(XMLConstants.XMLNS_ATTRIBUTE.equals(localName)))
                ? namespace.getPrefix() + ":" + localName
                : localName;
    }

    /**
     * Returns the node type.
     *
     * @see org.w3c.dom.Node#getNodeType()
     */
    public short getNodeType() {
        return Node.ATTRIBUTE_NODE;
    }

    /**
     * Returns the value of this attribute.
     *
     * @see org.w3c.dom.Node#getNodeValue()
     */
    public String getNodeValue() throws DOMException {
        return getValue();
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        setValue(nodeValue);
    }

    /**
     * Returns the value of this attribute.
     *
     * @see org.w3c.dom.Attr#getValue()
     */
    public String getValue() {
        String value = null;
        StringBuffer buffer = null;
        Node child = getFirstChild();

        while (child != null) {
            String textValue = ((Text)child).getData();
            if (textValue != null && textValue.length() != 0) {
                if (value == null) {
                    // This is the first non empty text node. Just save the string.
                    value = textValue;
                } else {
                    // We've already seen a non empty text node before. Concatenate using
                    // a StringBuffer.
                    if (buffer == null) {
                        // This is the first text node we need to append. Initialize the
                        // StringBuffer.
                        buffer = new StringBuffer(value);
                    }
                    buffer.append(textValue);
                }
            }
            child = child.getNextSibling();
        }

        if (value == null) {
            // We didn't see any text nodes. Return an empty string.
            return "";
        } else if (buffer != null) {
            return buffer.toString();
        } else {
            return value;
        }
    }

    // /
    // /org.w3c.dom.Attr methods
    // /
    public String getName() {
        OMNamespace namespace = getNamespace();
        String localName = getLocalName();
        if (namespace != null) {
            if ((XMLConstants.XMLNS_ATTRIBUTE.equals(localName))) {
                return localName;
            } else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespace.getNamespaceURI())) {
                return XMLConstants.XMLNS_ATTRIBUTE + ":" + localName;
            } else if (namespace.getPrefix().equals("")) {
                return localName;
            } else {
                return namespace.getPrefix() + ":" + localName;
            }
        } else {
            return localName;
        }
    }

    /**
     * Returns the owner element.
     *
     * @see org.w3c.dom.Attr#getOwnerElement()
     */
    public Element getOwnerElement() {
        return (Element)coreGetOwnerElement();
    }

    void setOwnerElement(ElementImpl element, boolean useDomSemantics) {
        if (element == null) {
            internalUnsetOwnerElement(useDomSemantics ? coreGetOwnerDocument(true) : null);
        } else {
            internalSetOwnerElement(element);
        }
    }
    
    public boolean getSpecified() {
        return coreGetSpecified();
    }

    public void setSpecified(boolean specified) {
        coreSetSpecified(specified);
    }

    public String getAttributeValue() {
        return getValue();
    }

    public String getAttributeType() {
        return type;
    }

    /**
     * Sets the attribute value.
     *
     * @see org.apache.axiom.om.OMAttribute#setAttributeValue(String)
     */
    public void setAttributeValue(String value) {
        setValue(value);
    }

    /**
     * Sets the attribute value.
     *
     * @see org.apache.axiom.om.OMAttribute#setAttributeType(String)
     */
    public void setAttributeType(String attrType) {    
    	this.type = attrType;
    }

    final void checkInUse() {
        if (coreGetOwnerElement() != null) {
            throw newDOMException(DOMException.INUSE_ATTRIBUTE_ERR);
        }
    }

    /**
     * Sets the value of the attribute.
     *
     * @see org.w3c.dom.Attr#setValue(String)
     */
    public void setValue(String value) throws DOMException {
        setTextContent(value);
    }

    /**
     * Returns the namespace URI of this attr node.
     *
     * @see org.w3c.dom.Node#getNamespaceURI()
     */
    public String getNamespaceURI() {
        OMNamespace namespace = getNamespace();
        return namespace != null ? namespace.getNamespaceURI() : null;
    }

    /*
     * DOM-Level 3 methods
     */
    public TypeInfo getSchemaTypeInfo() {
        // TODO TODO
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isId() {
        return isId;
    }

    public String toString() {
        OMNamespace namespace = getNamespace();
        String localName = getLocalName();
        return (namespace == null) ? localName : namespace
                .getPrefix()
                + ":" + localName;
    }

    /**
     * An instance of <code>AttrImpl</code> can act as an <code>OMAttribute</code> and as well as an
     * <code>org.w3c.dom.Attr</code>. So we first check if the object to compare with (<code>obj</code>)
     * is of type <code>OMAttribute</code> (this includes instances of <code>OMAttributeImpl</code> or
     * <code>AttrImpl</code> (instances of this class)). If so we check for the equality
     * of namespaces first (note that if the namespace of this instance is null then for the <code>obj</code>
     * to be equal its namespace must also be null). This condition solely doesn't determine the equality.
     * So we check for the equality of names and values (note that the value can also be null in which case
     * the same argument holds as that for the namespace) of the two instances. If all three conditions are
     * met then we say the two instances are equal.
     *
     * <p>If <code>obj</code> is of type <code>org.w3c.dom.Attr</code> then we perform the same equality check
     * as before. Note that, however, the implementation of the test for equality in this case is little different
     * than before.
     *
     * <p>If <code>obj</code> is neither of type <code>OMAttribute</code> nor of type <code>org.w3c.dom.Attr</code>
     * then we return false.
     *
     * @param obj The object to compare with this instance
     * @return True if the two objects are equal or else false. The equality is checked as explained above.
     */
    public boolean equals(Object obj) {
        OMNamespace namespace = getNamespace();
        String localName = getLocalName();
        String attrValue = getValue();
        if (obj instanceof OMAttribute) { // Checks equality of an OMAttributeImpl or an AttrImpl with this instance
            OMAttribute other = (OMAttribute) obj;
            return (namespace == null ? other.getNamespace() == null :
                    namespace.equals(other.getNamespace()) &&
                    localName.equals(other.getLocalName()) &&
                    (attrValue == null ? other.getAttributeValue() == null :
                            attrValue.toString().equals(other.getAttributeValue())));
        } else if (obj instanceof Attr) {// Checks equality of an org.w3c.dom.Attr with this instance
            Attr other = (Attr)obj;
            String otherNs = other.getNamespaceURI();
            if (namespace == null) { // I don't have a namespace
                if (otherNs != null) {
                    return false; // I don't have a namespace and the other has. So return false
                } else {
                    // Both of us don't have namespaces. So check for name and value equality only
                    return (localName.equals(other.getLocalName()) &&
                            (attrValue == null ? other.getValue() == null :
                                    attrValue.toString().equals(other.getValue())));
                }
            } else { // Ok, now I've a namespace
                String ns = namespace.getNamespaceURI();
                String prefix = namespace.getPrefix();
                String otherPrefix = other.getPrefix();
                // First check for namespaceURI equality. Then check for prefix equality.
                // Then check for name and value equality
                return (ns.equals(otherNs) && (prefix == null ? otherPrefix == null : prefix.equals(otherPrefix)) &&
                        (localName.equals(other.getLocalName())) &&
                        (attrValue == null ? other.getValue() == null :
                                attrValue.toString().equals(other.getValue())));
            }
        }
        return false;
    }

    public int hashCode() {
        OMNamespace namespace = getNamespace();
        String localName = getLocalName();
        String attrValue = getValue();
        return localName.hashCode() ^ (attrValue != null ? attrValue.toString().hashCode() : 0) ^
                (namespace != null ? namespace.hashCode() : 0);
    }

    ParentNode shallowClone(OMCloneOptions options, ParentNode targetParent, boolean namespaceRepairing) {
        // Note: targetParent is always null here
        return new AttrImpl(getLocalName(), getNamespace(), type, getOMFactory());
    }

    public final boolean isComplete() {
        return true;
    }

    public final void setComplete(boolean state) {
        if (state != true) {
            throw new IllegalStateException();
        }
    }

    public final void build() {
        // An attribute node doesn't have a builder
    }

    public final String lookupNamespaceURI(String specifiedPrefix) {
        Element ownerElement = getOwnerElement();
        return ownerElement == null ? null : ownerElement.lookupNamespaceURI(specifiedPrefix);
    }

    public final String lookupPrefix(String namespaceURI) {
        Element ownerElement = getOwnerElement();
        return ownerElement == null ? null : ownerElement.lookupPrefix(namespaceURI);
    }
}
