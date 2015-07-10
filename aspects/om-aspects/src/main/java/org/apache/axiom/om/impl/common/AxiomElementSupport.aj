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
package org.apache.axiom.om.impl.common;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.core.AttributeMatcher;
import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.IdentityMapper;
import org.apache.axiom.core.NodeMigrationException;
import org.apache.axiom.core.NodeMigrationPolicy;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.common.factory.AxiomNodeFactory;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.util.namespace.MapBasedNamespaceContext;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class with default implementations for some of the methods defined by the
 * {@link OMElement} interface.
 */
public aspect AxiomElementSupport {
    private static final Log log = LogFactory.getLog(AxiomElementSupport.class);
    
    final void AxiomElement.beforeSetLocalName() {
        forceExpand();
    }
    
    public final int AxiomElement.getType() {
        return OMNode.ELEMENT_NODE;
    }
    
    public final void AxiomElement.setNamespaceWithNoFindInCurrentScope(OMNamespace namespace) {
        forceExpand();
        internalSetNamespace(namespace);
    }

    public final void AxiomElement.setNamespace(OMNamespace namespace, boolean decl) {
        forceExpand();
        internalSetNamespace(handleNamespace(this, namespace, false, decl));
    }

    public final OMElement AxiomElement.getFirstElement() {
        OMNode node = getFirstOMChild();
        while (node != null) {
            if (node.getType() == OMNode.ELEMENT_NODE) {
                return (OMElement) node;
            } else {
                node = node.getNextOMSibling();
            }
        }
        return null;
    }

    public final Iterator AxiomElement.getChildElements() {
        return new OMChildElementIterator(getFirstElement());
    }

    public final Iterator AxiomElement.getNamespacesInScope() {
        return new NamespaceIterator(this);
    }

    public NamespaceContext AxiomElement.getNamespaceContext(boolean detached) {
        if (detached) {
            Map namespaces = new HashMap();
            for (Iterator it = getNamespacesInScope(); it.hasNext(); ) {
                OMNamespace ns = (OMNamespace)it.next();
                namespaces.put(ns.getPrefix(), ns.getNamespaceURI());
            }
            return new MapBasedNamespaceContext(namespaces);
        } else {
            return new LiveNamespaceContext(this);
        }
    }
    
    public final QName AxiomElement.resolveQName(String qname) {
        int idx = qname.indexOf(':');
        if (idx == -1) {
            OMNamespace ns = getDefaultNamespace();
            return ns == null ? new QName(qname) : new QName(ns.getNamespaceURI(), qname, "");
        } else {
            String prefix = qname.substring(0, idx);
            OMNamespace ns = findNamespace(null, prefix);
            return ns == null ? null : new QName(ns.getNamespaceURI(), qname.substring(idx+1), prefix);
        }
    }

    // TODO: this is (incorrectly) overridden by the SOAPFaultReason implementations for SOAP 1.2
    public String AxiomElement.getText() {
        String childText = null;
        StringBuffer buffer = null;
        OMNode child = getFirstOMChild();

        while (child != null) {
            final int type = child.getType();
            if (type == OMNode.TEXT_NODE || type == OMNode.CDATA_SECTION_NODE) {
                OMText textNode = (OMText) child;
                String textValue = textNode.getText();
                if (textValue != null && textValue.length() != 0) {
                    if (childText == null) {
                        // This is the first non empty text node. Just save the string.
                        childText = textValue;
                    } else {
                        // We've already seen a non empty text node before. Concatenate using
                        // a StringBuffer.
                        if (buffer == null) {
                            // This is the first text node we need to append. Initialize the
                            // StringBuffer.
                            buffer = new StringBuffer(childText);
                        }
                        buffer.append(textValue);
                    }
                }
            }
            child = child.getNextOMSibling();
        }

        if (childText == null) {
            // We didn't see any text nodes. Return an empty string.
            return "";
        } else if (buffer != null) {
            return buffer.toString();
        } else {
            return childText;
        }
    }
    
    // Note: must not be final because it is (incorrectly) overridden in the SOAPFaultCode implementation for SOAP 1.2
    public QName AxiomElement.getTextAsQName() {
        String childText = getText().trim();
        return childText.length() == 0 ? null : resolveQName(childText);
    }

    public Reader AxiomElement.getTextAsStream(boolean cache) {
        // If the element is not an OMSourcedElement and has not more than one child, then the most
        // efficient way to get the Reader is to build a StringReader
        if (!(this instanceof OMSourcedElement) && (!cache || isComplete())) {
            OMNode child = getFirstOMChild();
            if (child == null) {
                return new StringReader("");
            } else if (child.getNextOMSibling() == null) {
                return new StringReader(child instanceof OMText ? ((OMText)child).getText() : "");
            }
        }
        // In all other cases, extract the data from the XMLStreamReader
        try {
            XMLStreamReader reader = getXMLStreamReader(cache);
            if (reader.getEventType() == XMLStreamReader.START_DOCUMENT) {
                reader.next();
            }
            return XMLStreamReaderUtils.getElementTextAsStream(reader, true);
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
    }
    
    public void AxiomElement.writeTextTo(Writer out, boolean cache) throws IOException {
        try {
            XMLStreamReader reader = getXMLStreamReader(cache);
            int depth = 0;
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamReader.CHARACTERS:
                    case XMLStreamReader.CDATA:
                        if (depth == 1) {
                            out.write(reader.getText());
                        }
                        break;
                    case XMLStreamReader.START_ELEMENT:
                        depth++;
                        break;
                    case XMLStreamReader.END_ELEMENT:
                        depth--;
                }
            }
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
    }
    
    // Not final because overridden in Abdera
    public void AxiomElement.setText(String text) {
        removeChildren();
        // Add a new text node
        if (text != null && text.length() > 0) {
            getOMFactory().createOMText(this, text);
        }
    }

    public final void AxiomElement.setText(QName qname) {
        removeChildren();
        // Add a new text node
        if (qname != null) {
            OMNamespace ns = handleNamespace(qname.getNamespaceURI(), qname.getPrefix());
            getOMFactory().createOMText(this,
                    ns == null ? qname.getLocalPart() : ns.getPrefix() + ":" + qname.getLocalPart());
        }
    }

    public final void AxiomElement.discard() {
        if (getState() == CoreParentNode.INCOMPLETE && getBuilder() != null) {
            ((StAXOMBuilder)getBuilder()).discard((OMContainer)this);
        }
        detach();
    }
    
    public void AxiomElement.detachAndDiscardParent() {
        internalUnsetParent(null);
        coreSetPreviousSibling(null);
        coreSetNextSibling(null);
    }
    
    public void AxiomElement.insertChild(Class[] sequence, int pos, OMNode newChild) {
        if (!sequence[pos].isInstance(newChild)) {
            throw new IllegalArgumentException();
        }
        OMNode child = getFirstOMChild();
        while (child != null) {
            if (child instanceof OMElement) {
                if (child == newChild) {
                    // The new child is already a child of the element and it is at
                    // the right position
                    return;
                }
                if (sequence[pos].isInstance(child)) {
                    // Replace the existing child
                    child.insertSiblingAfter(newChild);
                    child.detach();
                    return;
                }
                // isAfter indicates if the new child should be inserted after the current child
                boolean isAfter = false;
                for (int i=0; i<pos; i++) {
                    if (sequence[i].isInstance(child)) {
                        isAfter = true;
                        break;
                    }
                }
                if (!isAfter) {
                    // We found the right position to insert the new child
                    child.insertSiblingBefore(newChild);
                    return;
                }
            }
            child = child.getNextOMSibling();
        }
        // Else, add the new child at the end
        addChild(newChild);
    }

    public final OMNamespace AxiomElement.handleNamespace(String namespaceURI, String prefix) {
        if (prefix.length() == 0 && namespaceURI.length() == 0) {
            OMNamespace namespace = getDefaultNamespace();
            if (namespace != null) {
                declareDefaultNamespace("");
            }
            return null;
        } else {
            OMNamespace namespace = findNamespace(namespaceURI,
                                                  prefix);
            if (namespace == null) {
                namespace = declareNamespace(namespaceURI, prefix.length() > 0 ? prefix : null);
            }
            return namespace;
        }
    }
    
    public final void AxiomElement.internalAppendAttribute(OMAttribute attr) {
        try {
            coreSetAttribute(Policies.ATTRIBUTE_MATCHER, (AxiomAttribute)attr, NodeMigrationPolicy.MOVE_ALWAYS, true, null, ReturnValue.NONE);
        } catch (NodeMigrationException ex) {
            AxiomExceptionUtil.translate(ex);
        }
    }
    
    public final OMAttribute AxiomElement.addAttribute(OMAttribute attr){
        // If the attribute already has an owner element then clone the attribute (except if it is owned
        // by the this element)
        OMElement owner = attr.getOwner();
        if (owner != null) {
            if (owner == this) {
                return attr;
            }
            attr = getOMFactory().createOMAttribute(attr.getLocalName(), attr.getNamespace(), attr.getAttributeValue());
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

        internalAppendAttribute(attr);
        return attr;
    }

    public final OMAttribute AxiomElement.addAttribute(String localName, String value, OMNamespace ns) {
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
        return addAttribute(getOMFactory().createOMAttribute(localName, namespace, value));
    }

    private static final IdentityMapper<AxiomAttribute> attributeIdentityMapper = new IdentityMapper<AxiomAttribute>();
    
    @SuppressWarnings("rawtypes")
    public final Iterator AxiomElement.getAllAttributes() {
        return coreGetAttributesByType(AxiomAttribute.class, attributeIdentityMapper);
    }
    
    public final OMAttribute AxiomElement.getAttribute(QName qname) {
        return (AxiomAttribute)coreGetAttribute(Policies.ATTRIBUTE_MATCHER, qname.getNamespaceURI(), qname.getLocalPart());
    }

    // TODO: overridden in fom-impl
    public String AxiomElement.getAttributeValue(QName qname) {
        OMAttribute attr = getAttribute(qname);
        return attr == null ? null : attr.getAttributeValue();
    }

    // TODO: complete the implementation (i.e. support value == null) and add the method to the OMElement API
    public final void AxiomElement.setAttributeValue(QName qname, String value) {
        coreSetAttribute(Policies.ATTRIBUTE_MATCHER, qname.getNamespaceURI(), qname.getLocalPart(), qname.getPrefix(), value);
    }
    
    public final void AxiomElement.removeAttribute(OMAttribute attr) {
        if (attr.getOwner() != this) {
            throw new OMException("The attribute is not owned by this element");
        }
        ((AxiomAttribute)attr).coreRemove(null);
    }

    public final OMNamespace AxiomElement.addNamespaceDeclaration(String uri, String prefix) {
        OMNamespace ns = new OMNamespaceImpl(uri, prefix);
        try {
            coreAppendAttribute(((AxiomNodeFactory)getOMFactory()).createNamespaceDeclaration(ns), NodeMigrationPolicy.MOVE_ALWAYS);
        } catch (NodeMigrationException ex) {
            throw AxiomExceptionUtil.translate(ex);
        }
        return ns;
    }
    
    public final void AxiomElement.addNamespaceDeclaration(OMNamespace ns) {
        try {
            coreSetAttribute(AttributeMatcher.NAMESPACE_DECLARATION,
                    ((AxiomNodeFactory)getOMFactory()).createNamespaceDeclaration(ns),
                    NodeMigrationPolicy.MOVE_ALWAYS, true, null, ReturnValue.NONE);
        } catch (NodeMigrationException ex) {
            throw AxiomExceptionUtil.translate(ex);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public final Iterator AxiomElement.getAllDeclaredNamespaces() {
        return coreGetAttributesByType(AxiomNamespaceDeclaration.class, NamespaceDeclarationMapper.INSTANCE);
    }

    public final OMNamespace AxiomElement.declareNamespace(OMNamespace namespace) {
        String prefix = namespace.getPrefix();
        if (prefix == null) {
            prefix = OMSerializerUtil.getNextNSPrefix();
            namespace = new OMNamespaceImpl(namespace.getNamespaceURI(), prefix);
        }
        if (prefix.length() > 0 && namespace.getNamespaceURI().length() == 0) {
            throw new IllegalArgumentException("Cannot bind a prefix to the empty namespace name");
        }
        addNamespaceDeclaration(namespace);
        return namespace;
    }

    public final OMNamespace AxiomElement.declareNamespace(String uri, String prefix) {
        if ("".equals(prefix)) {
            log.warn("Deprecated usage of OMElement#declareNamespace(String,String) with empty prefix");
            prefix = OMSerializerUtil.getNextNSPrefix();
        }
        OMNamespaceImpl ns = new OMNamespaceImpl(uri, prefix);
        return declareNamespace(ns);
    }

    public final OMNamespace AxiomElement.declareDefaultNamespace(String uri) {
        OMNamespace elementNamespace = getNamespace();
        if (elementNamespace == null && uri.length() > 0
                || elementNamespace != null && elementNamespace.getPrefix().length() == 0 && !elementNamespace.getNamespaceURI().equals(uri)) {
            throw new OMException("Attempt to add a namespace declaration that conflicts with " +
                    "the namespace information of the element");
        }
        OMNamespace namespace = new OMNamespaceImpl(uri == null ? "" : uri, "");
        addNamespaceDeclaration(namespace);
        return namespace;
    }

    public final void AxiomElement.undeclarePrefix(String prefix) {
        addNamespaceDeclaration(new OMNamespaceImpl("", prefix));
    }

    public final OMNamespace AxiomElement.findNamespace(String uri, String prefix) {

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
                namespace = ((OMElement) parent).findNamespace(uri, prefix);
                // If the prefix has been redeclared, then ignore the binding found on the ancestors
                if (prefix == null && namespace != null && findDeclaredNamespace(null, namespace.getPrefix()) != null) {
                    namespace = null;
                }
            }
        }

        return namespace;
    }

    private static final OMNamespace XMLNS = new OMNamespaceImpl(OMConstants.XMLNS_URI, OMConstants.XMLNS_PREFIX);

    /**
     * Checks for the namespace <B>only</B> in the current Element. This is also used to retrieve
     * the prefix of a known namespace URI.
     */
    private OMNamespace AxiomElement.findDeclaredNamespace(String uri, String prefix) {
        // Seems weird, but necessary for compatibility with older versions
        if (uri != null && prefix != null && prefix.length() == 0) {
            prefix = null;
        }
        
        CoreAttribute attr = coreGetFirstAttribute();
        while (attr != null) {
            if (attr instanceof AxiomNamespaceDeclaration) {
                OMNamespace namespace = ((AxiomNamespaceDeclaration)attr).getDeclaredNamespace();
                if ((prefix == null || prefix.equals(namespace.getPrefix()))
                        && (uri == null || uri.equals(namespace.getNamespaceURI()))) {
                    return namespace;
                }
            }
            attr = attr.coreGetNextAttribute();
        }

        //If the prefix is available and uri is available and its the xml namespace
        if ((prefix == null || prefix.equals(OMConstants.XMLNS_PREFIX))
                && (uri == null || uri.equals(OMConstants.XMLNS_URI))) {
            return XMLNS;
        } else {
            return null;
        }
    }

    public final OMNamespace AxiomElement.findNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        CoreAttribute attr = coreGetFirstAttribute();
        while (attr != null) {
            if (attr instanceof AxiomNamespaceDeclaration) {
                AxiomNamespaceDeclaration nsDecl = (AxiomNamespaceDeclaration)attr;
                if (nsDecl.coreGetDeclaredPrefix().equals(prefix)) {
                    OMNamespace ns = nsDecl.getDeclaredNamespace();
                    if (ns.getNamespaceURI().length() == 0) {
                        // We are either in the prefix undeclaring case (XML 1.1 only) or the namespace
                        // declaration is xmlns="". In both cases we need to return null.
                        return null;
                    } else {
                        return ns;
                    }
                }
            }
            attr = attr.coreGetNextAttribute();
        }
        OMContainer parent = getParent();
        if (parent instanceof OMElement) {
            // try with the parent
            return ((OMElement)parent).findNamespaceURI(prefix);
        } else {
            return null;
        }
    }

    public final OMNamespace AxiomElement.getDefaultNamespace() {
        return findNamespaceURI("");
    }

    public void AxiomElement.internalSerialize(Serializer serializer, OMOutputFormat format,
            boolean cache) throws OutputException {
        defaultInternalSerialize(serializer, format, cache);
    }
    
    public final void AxiomElement.defaultInternalSerialize(Serializer serializer, OMOutputFormat format,
            boolean cache) throws OutputException {
        serializer.serializeStartpart(this);
        serializer.serializeChildren(this, format, cache);
        serializer.writeEndElement();
    }

    public final String AxiomElement.toStringWithConsume() throws XMLStreamException {
        StringWriter sw = new StringWriter();
        serializeAndConsume(sw);
        return sw.toString();
    }

    public final String AxiomElement.toString() {
        StringWriter sw = new StringWriter();
        try {
            serialize(sw);
        } catch (XMLStreamException ex) {
            throw new OMException("Failed to serialize node", ex);
        }
        return sw.toString();
    }
}
