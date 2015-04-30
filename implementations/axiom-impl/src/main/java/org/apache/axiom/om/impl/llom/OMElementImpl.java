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

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
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
import org.apache.axiom.om.util.StAXUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/** Class OMElementImpl */
public class OMElementImpl extends OMNodeImpl
        implements AxiomElement, OMConstants {

    private static final Log log = LogFactory.getLog(OMElementImpl.class);
    
    /** Field namespaces */
    protected HashMap namespaces = null;
    
    /** Field attributes */
    protected HashMap attributes = null;

    private int lineNumber;
    private static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

    public OMElementImpl(OMContainer parent, String localName, OMNamespace ns, OMXMLParserWrapper builder,
                    OMFactory factory, boolean generateNSDecl) {
        super(factory);
        if (localName == null || localName.trim().length() == 0) {
            throw new OMException("localname can not be null or empty");
        }
        internalSetLocalName(localName);
        coreSetBuilder(builder);
        coreSetState(builder == null ? COMPLETE : INCOMPLETE);
        if (parent != null) {
            ((AxiomContainer)parent).addChild(this, builder != null);
        }
        internalSetNamespace(generateNSDecl ? handleNamespace(this, ns, false, true) : ns);
    }

    /**
     * It is assumed that the QName passed contains, at least, the localName for this element.
     *
     * @param qname - this should be valid qname according to javax.xml.namespace.QName
     * @throws OMException
     */
    public OMElementImpl(QName qname, OMContainer parent, OMFactory factory)
            throws OMException {
        super(factory);
        coreSetState(COMPLETE);
        if (parent != null) {
            parent.addChild(this);
        }
        internalSetLocalName(qname.getLocalPart());
        internalSetNamespace(handleNamespace(qname));
    }
    
    /**
     * Constructor reserved for use by {@link OMSourcedElementImpl}.
     * 
     * @param factory
     */
    OMElementImpl(OMFactory factory) {
        super(factory);
        coreSetState(COMPLETE);
    }

    /** Method handleNamespace. */
    OMNamespace handleNamespace(QName qname) {
        OMNamespace ns = null;

        // first try to find a namespace from the scope
        String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI.length() > 0) {
            String prefix = qname.getPrefix();
            ns = findNamespace(namespaceURI, prefix);

            /**
             * What is left now is
             *  1. nsURI = null & parent != null, but ns = null
             *  2. nsURI != null, (parent doesn't have an ns with given URI), but ns = null
             */
            if (ns == null) {
                if ("".equals(prefix)) {
                    prefix = OMSerializerUtil.getNextNSPrefix();
                }
                ns = declareNamespace(namespaceURI, prefix);
            }
        } else if (qname.getPrefix().length() > 0) {
            throw new IllegalArgumentException("Cannot create a prefixed element with an empty namespace name");
        }
        return ns;
    }

    public void checkChild(OMNode child) {
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
        OMNamespace elementNamespace = getNamespace();
        if (elementNamespace == null && uri.length() > 0
                || elementNamespace != null && elementNamespace.getPrefix().length() == 0 && !elementNamespace.getNamespaceURI().equals(uri)) {
            throw new OMException("Attempt to add a namespace declaration that conflicts with " +
            		"the namespace information of the element");
        }

        OMNamespaceImpl namespace = new OMNamespaceImpl(uri == null ? "" : uri, "");

        if (namespaces == null) {
            this.namespaces = new HashMap(5);
        }
        namespaces.put("", namespace);
        return namespace;
    }

    public OMNamespace getDefaultNamespace() {
        OMNamespace defaultNS;
        if (namespaces != null && (defaultNS = (OMNamespace) namespaces.get("")) != null) {
            return defaultNS.getNamespaceURI().length() == 0 ? null : defaultNS;
        }
        OMContainer parent = getParent();
        if (parent instanceof OMElementImpl) {
            return ((OMElementImpl) parent).getDefaultNamespace();

        }
        return null;
    }

    public OMNamespace addNamespaceDeclaration(String uri, String prefix) {
        OMNamespace ns = new OMNamespaceImpl(uri, prefix);
        addNamespaceDeclaration(ns);
        return ns;
    }
    
    public void addNamespaceDeclaration(OMNamespace ns) {
        if (namespaces == null) {
            this.namespaces = new HashMap(5);
        }
        namespaces.put(ns.getPrefix(), ns);
    }

    /** @return Returns namespace. */
    public OMNamespace declareNamespace(OMNamespace namespace) {
        if (namespaces == null) {
            this.namespaces = new HashMap(5);
        }
        String prefix = namespace.getPrefix();
        if (prefix == null) {
            prefix = OMSerializerUtil.getNextNSPrefix();
            namespace = new OMNamespaceImpl(namespace.getNamespaceURI(), prefix);
        }
        if (prefix.length() > 0 && namespace.getNamespaceURI().length() == 0) {
            throw new IllegalArgumentException("Cannot bind a prefix to the empty namespace name");
        }
        namespaces.put(prefix, namespace);
        return namespace;
    }

    public void undeclarePrefix(String prefix) {
        if (namespaces == null) {
            this.namespaces = new HashMap(5);
        }
        namespaces.put(prefix, new OMNamespaceImpl("", prefix));
    }

    /**
     * Finds a namespace with the given uri and prefix, in the scope of the document. Starts to find
     * from the current element and goes up in the hiararchy until one is found. If none is found,
     * returns null.
     */
    public OMNamespace findNamespace(String uri, String prefix) {

        // check in the current element
        OMNamespace namespace = findDeclaredNamespace(uri, prefix);
        if (namespace != null) {
            return namespace;
        }

        // go up to check with ancestors
        OMContainer parent = getParent();
        if (parent != null) {
            //For the OMDocumentImpl there won't be any explicit namespace
            //declarations, so going up the parent chain till the document
            //element should be enough.
            if (parent instanceof OMElement) {
                namespace = ((OMElementImpl) parent).findNamespace(uri, prefix);
                // If the prefix has been redeclared, then ignore the binding found on the ancestors
                if (prefix == null && namespace != null && findDeclaredNamespace(null, namespace.getPrefix()) != null) {
                    namespace = null;
                }
            }
        }

        return namespace;
    }

    public OMNamespace findNamespaceURI(String prefix) {
        OMNamespace ns = this.namespaces == null ?
                null :
                (OMNamespace) this.namespaces.get(prefix);

        if (ns == null) {
            OMContainer parent = getParent();
            if (parent instanceof OMElement) {
                // try with the parent
                return ((OMElement)parent).findNamespaceURI(prefix);
            } else {
                return null;
            }
        } else if (prefix != null && prefix.length() > 0 && ns.getNamespaceURI().length() == 0) {
            // Prefix undeclaring case (XML 1.1 only)
            return null;
        } else {
            return ns;
        }
    }

    // Constant
    static final OMNamespaceImpl xmlns =
            new OMNamespaceImpl(OMConstants.XMLNS_URI,
                                OMConstants.XMLNS_PREFIX);

    /**
     * Checks for the namespace <B>only</B> in the current Element. This is also used to retrieve
     * the prefix of a known namespace URI.
     */
    private OMNamespace findDeclaredNamespace(String uri, String prefix) {
        if (uri == null) {
            return namespaces == null ? null : (OMNamespace)namespaces.get(prefix);
        }

        //If the prefix is available and uri is available and its the xml namespace
        if (prefix != null && prefix.equals(OMConstants.XMLNS_PREFIX) &&
                uri.equals(OMConstants.XMLNS_URI)) {
            return xmlns;
        }

        if (namespaces == null) {
            return null;
        }

        if (prefix == null || "".equals(prefix)) {

            OMNamespace defaultNamespace = this.getDefaultNamespace();
            if (defaultNamespace != null && uri.equals(defaultNamespace.getNamespaceURI())) {
                return defaultNamespace;
            }
            Iterator namespaceListIterator = namespaces.values().iterator();

            String nsUri;

            while (namespaceListIterator.hasNext()) {
                OMNamespace omNamespace =
                        (OMNamespace) namespaceListIterator.next();
                nsUri = omNamespace.getNamespaceURI();
                if (nsUri != null &&
                        nsUri.equals(uri)) {
                    return omNamespace;
                }
            }
        } else {
            OMNamespace namespace = (OMNamespace) namespaces.get(prefix);
            if (namespace != null && uri.equals(namespace.getNamespaceURI())) {
                return namespace;
            }
        }
        return null;
    }


    /**
     * Method getAllDeclaredNamespaces.
     *
     * @return Returns Iterator.
     */
    public Iterator getAllDeclaredNamespaces() {
        if (namespaces == null) {
            return EMPTY_ITERATOR;
        }
        return namespaces.values().iterator();
    }

    /**
     * Returns a List of OMAttributes.
     *
     * @return Returns iterator.
     */
    public Iterator getAllAttributes() {
        if (attributes == null) {
            return EMPTY_ITERATOR;
        }
        return attributes.values().iterator();
    }

    /**
     * Returns a named attribute if present.
     *
     * @param qname the qualified name to search for
     * @return Returns an OMAttribute with the given name if found, or null
     */
    public OMAttribute getAttribute(QName qname) {
        return attributes == null ? null : (OMAttribute) attributes.get(qname);
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

    /**
     * Inserts an attribute to this element. Implementor can decide as to insert this in the front
     * or at the end of set of attributes.
     *
     * <p>The owner of the attribute is set to be the particular <code>OMElement</code>.
     * If the attribute already has an owner then the attribute is cloned (i.e. its name,
     * value and namespace are copied to a new attribute) and the new attribute is added
     * to the element. It's owner is then set to be the particular <code>OMElement</code>.
     * 
     * @return The attribute that was added to the element. Note: The added attribute
     * may not be the same instance that was given to add. This can happen if the given
     * attribute already has an owner. In such case the returned attribute and the given
     * attribute are <i>equal</i> but not the same instance.
     *
     * @see OMAttributeImpl#equals(Object)
     */
    public OMAttribute addAttribute(OMAttribute attr){
        // If the attribute already has an owner element then clone the attribute (except if it is owned
        // by the this element)
        OMElement owner = attr.getOwner();
        if (owner != null) {
            if (owner == this) {
                return attr;
            }
            attr = new OMAttributeImpl(
                    attr.getLocalName(), attr.getNamespace(), attr.getAttributeValue(), attr.getOMFactory());
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

        appendAttribute(attr);
        return attr;
    }
    
    void appendAttribute(OMAttribute attr) {
        if (attributes == null) {
            this.attributes = new LinkedHashMap(5);
        }
        // Set the owner element of the attribute
        ((OMAttributeImpl)attr).internalSetOwnerElement(this);
        OMAttributeImpl oldAttr = (OMAttributeImpl)attributes.put(attr.getQName(), attr);
        // Did we replace an existing attribute?
        if (oldAttr != null) {
            oldAttr.internalUnsetOwnerElement(null);
        }
    }

    public void removeAttribute(OMAttribute attr) {
        if (attr.getOwner() != this) {
            throw new OMException("The attribute is not owned by this element");
        }
        // Remove the owner from this attribute
        ((OMAttributeImpl)attr).internalUnsetOwnerElement(null);
        attributes.remove(attr.getQName());
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
        return addAttribute(new OMAttributeImpl(localName, namespace, value, getOMFactory()));
    }

    public void build() throws OMException {
        /**
         * builder is null. Meaning this is a programatical created element but it has children which are not completed
         * Build them all.
         */
        if (getBuilder() == null && getState() == INCOMPLETE) {
            for (Iterator childrenIterator = this.getChildren(); childrenIterator.hasNext();) {
                OMNode omNode = (OMNode) childrenIterator.next();
                omNode.build();
            }
        } else {
            defaultBuild();
        }

    }

    public void setComplete(boolean complete) {
        coreSetState(complete ? COMPLETE : INCOMPLETE);
        OMContainer parent = getParent();
        if (parent != null) {
            if (!complete) {
                ((AxiomContainer)parent).setComplete(false);
            } else if (parent instanceof OMElementImpl) {
                ((OMElementImpl) parent).notifyChildComplete();
            } else if (parent instanceof OMDocumentImpl) {
                ((OMDocumentImpl) parent).notifyChildComplete();
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////

    public void internalSerialize(Serializer serializer, OMOutputFormat format, boolean cache)
            throws OutputException {

        serializer.serializeStartpart(this);
        serializer.serializeChildren(this, format, cache);
        serializer.writeEndElement();
    }

////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

    public String getNamespaceURI() {
        OMNamespace ns = getNamespace();
        if (ns == null) {
            return null;
        } else {
            String namespaceURI = ns.getNamespaceURI();
            return namespaceURI.length() == 0 ? null : namespaceURI;
        }
    }

    public void setNamespace(OMNamespace namespace) {
        setNamespace(namespace, true);
    }

    public String toStringWithConsume() throws XMLStreamException {
        StringWriter writer = new StringWriter();
        XMLStreamWriter writer2 = StAXUtils.createXMLStreamWriter(writer);
        try {
            this.serializeAndConsume(writer2);
            writer2.flush();
        } finally {
            writer2.close();
        }
        return writer.toString();
    }

    public String toString() {
        StringWriter writer = new StringWriter();
        try {
            XMLStreamWriter writer2 = StAXUtils.createXMLStreamWriter(writer);
            try {
                this.serialize(writer2);
                writer2.flush();
            } finally {
                writer2.close();
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException("Can not serialize OM Element " + this.getLocalName(), e);
        }
        return writer.toString();
    }

    public OMElement cloneOMElement() {
        
        if (log.isDebugEnabled()) {
            log.debug("cloneOMElement start");
            log.debug("  element string =" + getLocalName());
            log.debug(" isComplete = " + isComplete());
            log.debug("  builder = " + getBuilder());
        }
        return (OMElement)clone(new OMCloneOptions());
    }

    OMNode clone(OMCloneOptions options, OMContainer targetParent) {
        OMElement targetElement;
        if (options.isPreserveModel()) {
            targetElement = createClone(options, targetParent);
        } else {
            targetElement = getOMFactory().createOMElement(getLocalName(), getNamespace(), targetParent);
        }
        for (Iterator it = getAllDeclaredNamespaces(); it.hasNext(); ) {
            OMNamespace ns = (OMNamespace)it.next();
            targetElement.declareNamespace(ns);
        }
        for (Iterator it = getAllAttributes(); it.hasNext(); ) {
            OMAttribute attr = (OMAttribute)it.next();
            targetElement.addAttribute(attr);
        }
        for (Iterator it = getChildren(); it.hasNext(); ) {
            ((OMNodeImpl)it.next()).clone(options, targetElement);
        }
        return targetElement;
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return getOMFactory().createOMElement(getLocalName(), getNamespace(), targetParent);
    }
    
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
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

    /** This method will be called when one of the children becomes complete. */
    void notifyChildComplete() {
        if (getState() == INCOMPLETE && getBuilder() == null) {
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
}

