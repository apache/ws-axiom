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

import org.apache.axiom.core.AttributeMatcher;
import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.NodeMigrationException;
import org.apache.axiom.core.NodeMigrationPolicy;
import org.apache.axiom.dom.DOMAttribute;
import org.apache.axiom.dom.DOMConfigurationImpl;
import org.apache.axiom.dom.DOMElement;
import org.apache.axiom.dom.DOMExceptionUtil;
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
import org.apache.axiom.om.impl.common.AxiomAttribute;
import org.apache.axiom.om.impl.common.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomElement;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.common.Policies;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

/** Implementation of the org.w3c.dom.Element and org.apache.axiom.om.Element interfaces. */
public class ElementImpl extends ParentNode implements DOMElement, AxiomElement, NamedNode,
        OMConstants {

    private static final Log log = LogFactory.getLog(ElementImpl.class);
    
    private int lineNumber;

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
        internalSetNamespace(generateNSDecl ? handleNamespace(this, ns, false, true) : ns);
    }

    private final String checkNamespaceIsDeclared(String prefix, String namespaceURI, boolean allowDefaultNamespace, boolean declare) {
        if (prefix == null) {
            if (namespaceURI.length() == 0) {
                prefix = "";
                declare = false;
            } else {
                prefix = coreLookupPrefix(namespaceURI, true);
                if (prefix != null && (allowDefaultNamespace || prefix.length() != 0)) {
                    declare = false;
                } else {
                    prefix = OMSerializerUtil.getNextNSPrefix();
                }
            }
        } else {
            String existingNamespaceURI = coreLookupNamespaceURI(prefix, true);
            declare = declare && !namespaceURI.equals(existingNamespaceURI);
        }
        if (declare) {
            coreSetAttribute(AttributeMatcher.NAMESPACE_DECLARATION, null, prefix, null, namespaceURI);
        }
        return prefix;
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
            attr = new NSAwareAttribute(null, attr.getLocalName(), attr.getNamespace(),
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

        try {
            coreSetAttribute(Policies.ATTRIBUTE_MATCHER, (AxiomAttribute)attr, NodeMigrationPolicy.MOVE_ALWAYS, true, null, ReturnValue.NONE);
        } catch (NodeMigrationException ex) {
            DOMExceptionUtil.translate(ex);
        }
        return attr;
    }

    public final OMAttribute addAttribute(String localName, String value, OMNamespace ns) {
        try {
            String namespaceURI;
            String prefix;
            if (ns == null) {
                namespaceURI = "";
                prefix = "";
            } else {
                namespaceURI = ns.getNamespaceURI();
                prefix = ns.getPrefix();
                if (namespaceURI.length() == 0) {
                    if (prefix == null) {
                        prefix = "";
                    } else if (prefix.length() > 0) {
                        throw new IllegalArgumentException("Cannot create a prefixed attribute with an empty namespace name");
                    }
                } else {
                    if (prefix == null || prefix.length() > 0) {
                        prefix = checkNamespaceIsDeclared(prefix, namespaceURI, false, true);
                    } else {
                        throw new IllegalArgumentException("Cannot create an unprefixed attribute with a namespace");
                    }
                }
            }
            AxiomAttribute attr = (AxiomAttribute)coreGetNodeFactory().createAttribute(null, namespaceURI, localName, prefix, value, "CDATA");
            return (AxiomAttribute)coreSetAttribute(Policies.ATTRIBUTE_MATCHER, attr, NodeMigrationPolicy.MOVE_ALWAYS, true, null, ReturnValue.ADDED_ATTRIBUTE);
        } catch (CoreModelException ex) {
            throw DOMExceptionUtil.translate(ex);
        }
    }

    public OMNamespace addNamespaceDeclaration(String uri, String prefix) {
        OMNamespace ns = new OMNamespaceImpl(uri, prefix);
        addNamespaceDeclaration(ns);
        return ns;
    }
    
    public void addNamespaceDeclaration(OMNamespace ns) {
        try {
            coreAppendAttribute(new NamespaceDeclaration(null, ns, getOMFactory()), NodeMigrationPolicy.MOVE_ALWAYS);
        } catch (NodeMigrationException ex) {
            throw DOMExceptionUtil.translate(ex);
        }
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
        Attr decl = getAttributeNodeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE);
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
        CoreAttribute attr = coreGetFirstAttribute();
        while (attr != null) {
            if (attr instanceof NamespaceDeclaration) {
                NamespaceDeclaration nsDecl = (NamespaceDeclaration)attr;
                if (nsDecl.coreGetDeclaredPrefix().equals(prefix)) {
                    OMNamespace ns = nsDecl.getDeclaredNamespace();
                    if (prefix != null && prefix.length() > 0 && ns.getNamespaceURI().length() == 0) {
                        // Prefix undeclaring case (XML 1.1 only)
                        return null;
                    } else {
                        return ns;
                    }
                }
            }
            attr = attr.coreGetNextAttribute();
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
        NamedNodeMap attributes = getAttributes();
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

    public void removeAttribute(OMAttribute attr) {
        if (attr.getOwner() != this) {
            throw new OMException("The attribute is not owned by this element");
        }
        ((AttrImpl)attr).coreRemove(null);
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
        return new NSDeclIterator(getAttributes());
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
        NamedNodeMap attributes = getAttributes();
        for (int i=0, l=attributes.getLength(); i<l; i++) {
            AttrImpl attr = (AttrImpl)attributes.item(i);
            AttrImpl clonedAttr = (AttrImpl)attr.clone(options, null, true, false);
            clonedAttr.setSpecified(attr.getSpecified());
            if (namespaceRepairing && attr instanceof NSAwareAttribute) {
                NSAwareAttribute nsAwareAttr = (NSAwareAttribute)attr;
                String namespaceURI = nsAwareAttr.coreGetNamespaceURI();
                if (namespaceURI.length() != 0) {
                    clone.checkNamespaceIsDeclared(nsAwareAttr.coreGetPrefix(), namespaceURI, false, true);
                }
            }
            try {
                clone.coreAppendAttribute(clonedAttr, NodeMigrationPolicy.MOVE_ALWAYS);
            } catch (NodeMigrationException ex) {
                DOMExceptionUtil.translate(ex);
            }
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
        if (((DOMAttribute)idAttr).coreGetOwnerElement() != this) {
            throw newDOMException(DOMException.NOT_FOUND_ERR);
        }
        this.updateIsId(isId, (AttrImpl)idAttr);
    }

    /**
     * Updates the id state of the attr and notifies the document
     *
     * @param isId
     * @param tempAttr
     */
    private void updateIsId(boolean isId, AttrImpl tempAttr) {
        if (tempAttr.isId != isId) {
            tempAttr.isId = isId;
            if (isId) {
                ownerDocument().addIdAttr(tempAttr);
            } else {
                ownerDocument().removeIdAttr(tempAttr);
            }
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
