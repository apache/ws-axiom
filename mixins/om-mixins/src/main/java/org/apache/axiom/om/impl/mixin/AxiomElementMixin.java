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
package org.apache.axiom.om.impl.mixin;

import static org.apache.axiom.util.xml.NSUtils.generatePrefix;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.core.Axis;
import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.ElementAction;
import org.apache.axiom.core.ElementMatcher;
import org.apache.axiom.core.Mappers;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.common.LiveNamespaceContext;
import org.apache.axiom.om.impl.common.NSUtil;
import org.apache.axiom.om.impl.common.NamespaceDeclarationMapper;
import org.apache.axiom.om.impl.common.NamespaceIterator;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.intf.AxiomAttribute;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomNamespaceDeclaration;
import org.apache.axiom.om.impl.intf.Sequence;
import org.apache.axiom.util.namespace.MapBasedNamespaceContext;
import org.apache.axiom.util.stax.XMLStreamIOException;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.axiom.util.xml.QNameCache;
import org.apache.axiom.weaver.annotation.Mixin;

/**
 * Utility class with default implementations for some of the methods defined by the {@link
 * OMElement} interface.
 */
@Mixin
public abstract class AxiomElementMixin implements AxiomElement {
    @Override
    public final void initName(String localName, OMNamespace ns, boolean generateNSDecl) {
        internalSetLocalName(localName);
        internalSetNamespace(generateNSDecl ? NSUtil.handleNamespace(this, ns, false, true) : ns);
    }

    @Override
    public final void beforeSetLocalName() {
        forceExpand();
    }

    @Override
    public final int getType() {
        return OMNode.ELEMENT_NODE;
    }

    @Override
    public final void setNamespaceWithNoFindInCurrentScope(OMNamespace namespace) {
        forceExpand();
        internalSetNamespace(namespace);
    }

    @Override
    public final void setNamespace(OMNamespace namespace, boolean decl) {
        forceExpand();
        internalSetNamespace(NSUtil.handleNamespace(this, namespace, false, decl));
    }

    @Override
    public final OMElement getFirstElement() {
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

    @Override
    public final Iterator<OMElement> getChildElements() {
        return coreGetElements(
                Axis.CHILDREN,
                AxiomElement.class,
                ElementMatcher.ANY,
                null,
                null,
                Mappers.<OMElement>identity(),
                AxiomSemantics.INSTANCE);
    }

    @Override
    public final Iterator<OMNamespace> getNamespacesInScope() {
        return new NamespaceIterator(this);
    }

    @Override
    public NamespaceContext getNamespaceContext(boolean detached) {
        if (detached) {
            Map<String, String> namespaces = new HashMap<String, String>();
            for (Iterator<OMNamespace> it = getNamespacesInScope(); it.hasNext(); ) {
                OMNamespace ns = it.next();
                namespaces.put(ns.getPrefix(), ns.getNamespaceURI());
            }
            return new MapBasedNamespaceContext(namespaces);
        } else {
            return new LiveNamespaceContext(this);
        }
    }

    @Override
    public final QName resolveQName(String qname) {
        int idx = qname.indexOf(':');
        if (idx == -1) {
            OMNamespace ns = getDefaultNamespace();
            return QNameCache.getQName(ns == null ? "" : ns.getNamespaceURI(), qname);
        } else {
            String prefix = qname.substring(0, idx);
            OMNamespace ns = findNamespace(null, prefix);
            return ns == null
                    ? null
                    : QNameCache.getQName(ns.getNamespaceURI(), qname.substring(idx + 1), prefix);
        }
    }

    @Override
    public final String getText() {
        try {
            return coreGetCharacterData(ElementAction.SKIP).toString();
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    @Override
    public final QName getTextAsQName() {
        String childText = getText().trim();
        return childText.length() == 0 ? null : resolveQName(childText);
    }

    @Override
    public Reader getTextAsStream(boolean cache) {
        // If the element is not an OMSourcedElement and has not more than one child, then the most
        // efficient way to get the Reader is to build a StringReader
        if (!(this instanceof OMSourcedElement) && (!cache || isComplete())) {
            OMNode child = getFirstOMChild();
            if (child == null) {
                return new StringReader("");
            } else if (child.getNextOMSibling() == null) {
                return new StringReader(child instanceof OMText text ? text.getText() : "");
            }
        }
        // In all other cases, extract the data from the XMLStreamReader
        try {
            final XMLStreamReader reader = getXMLStreamReader(cache);
            if (reader.getEventType() == XMLStreamReader.START_DOCUMENT) {
                reader.next();
            }
            Reader stream = XMLStreamReaderUtils.getElementTextAsStream(reader, true);
            if (!cache) {
                // If caching is disabled, we need to close the XMLStreamReader to reenable it
                stream =
                        new FilterReader(stream) {
                            @Override
                            public void close() throws IOException {
                                try {
                                    reader.close();
                                } catch (XMLStreamException ex) {
                                    throw new XMLStreamIOException(ex);
                                }
                            }
                        };
            }
            return stream;
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
    }

    @Override
    public void writeTextTo(Writer out, boolean cache) throws IOException {
        try {
            XMLStreamReader reader = getXMLStreamReader(cache);
            int depth = 0;
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamReader.CHARACTERS, XMLStreamReader.CDATA -> {
                        if (depth == 1) {
                            out.write(reader.getText());
                        }
                    }
                    case XMLStreamReader.START_ELEMENT -> {
                        depth++;
                    }
                    case XMLStreamReader.END_ELEMENT -> {
                        depth--;
                    }
                }
            }
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
    }

    @Override
    public final void setText(String text) {
        try {
            coreSetCharacterData(text, AxiomSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    @Override
    public final void setText(QName qname) {
        removeChildren();
        // Add a new text node
        if (qname != null) {
            OMNamespace ns = handleNamespace(qname.getNamespaceURI(), qname.getPrefix());
            getOMFactory()
                    .createOMText(
                            this,
                            ns == null
                                    ? qname.getLocalPart()
                                    : ns.getPrefix() + ":" + qname.getLocalPart());
        }
    }

    @Override
    public final void discard() {
        try {
            coreDiscard(true);
            detach();
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    @Override
    public <T extends OMElement> void insertChild(
            Sequence sequence, int pos, T newChild, boolean allowReplace) {
        if (!sequence.item(pos).isInstance(newChild)) {
            throw new IllegalArgumentException();
        }
        OMNode child = getFirstOMChild();
        Class<? extends OMElement> type = sequence.item(pos);
        while (child != null) {
            if (child instanceof OMElement) {
                if (child == newChild) {
                    // The new child is already a child of the element and it is at
                    // the right position
                    return;
                }
                if (type.isInstance(child)) {
                    // Replace the existing child
                    if (allowReplace) {
                        child.insertSiblingAfter(newChild);
                        child.detach();
                    } else {
                        throw new OMException(
                                "The element already has a child of type " + type.getName());
                    }
                    return;
                }
                // isAfter indicates if the new child should be inserted after the current child
                boolean isAfter = false;
                for (int i = 0; i < pos; i++) {
                    if (sequence.item(i).isInstance(child)) {
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

    @Override
    public final OMNamespace handleNamespace(String namespaceURI, String prefix) {
        if (prefix.length() == 0 && namespaceURI.length() == 0) {
            OMNamespace namespace = getDefaultNamespace();
            if (namespace != null) {
                declareDefaultNamespace("");
            }
            return null;
        } else {
            OMNamespace namespace = findNamespace(namespaceURI, prefix);
            if (namespace == null) {
                namespace = declareNamespace(namespaceURI, prefix.length() > 0 ? prefix : null);
            }
            return namespace;
        }
    }

    public final void internalAppendAttribute(OMAttribute attr) {
        coreSetAttribute(
                AxiomSemantics.ATTRIBUTE_MATCHER, (AxiomAttribute) attr, AxiomSemantics.INSTANCE);
    }

    @Override
    public final OMAttribute addAttribute(OMAttribute attr) {
        // If the attribute already has an owner element then clone the attribute (except if it is
        // owned
        // by the this element)
        OMElement owner = attr.getOwner();
        if (owner != null) {
            if (owner == this) {
                return attr;
            }
            attr =
                    getOMFactory()
                            .createOMAttribute(
                                    attr.getLocalName(),
                                    attr.getNamespace(),
                                    attr.getAttributeValue());
        }
        NSUtil.handleNamespace(this, attr.getNamespace(), true, true);
        internalAppendAttribute(attr);
        return attr;
    }

    @Override
    public final OMAttribute addAttribute(String localName, String value, OMNamespace ns) {
        OMNamespace namespace = null;
        if (ns != null) {
            String namespaceURI = ns.getNamespaceURI();
            String prefix = ns.getPrefix();
            if (namespaceURI.length() > 0 || prefix != null) {
                namespace = findNamespace(namespaceURI, prefix);
                if (namespace == null || prefix == null && namespace.getPrefix().length() == 0) {
                    namespace =
                            new OMNamespaceImpl(
                                    namespaceURI,
                                    prefix != null ? prefix : generatePrefix(namespaceURI));
                }
            }
        }
        return addAttribute(getOMFactory().createOMAttribute(localName, namespace, value));
    }

    @Override
    public final Iterator<OMAttribute> getAllAttributes() {
        return coreGetAttributesByType(
                AxiomAttribute.class, Mappers.<OMAttribute>identity(), AxiomSemantics.INSTANCE);
    }

    @Override
    public final OMAttribute getAttribute(QName qname) {
        return (AxiomAttribute)
                coreGetAttribute(
                        AxiomSemantics.ATTRIBUTE_MATCHER,
                        qname.getNamespaceURI(),
                        qname.getLocalPart());
    }

    @Override
    public final String getAttributeValue(QName qname) {
        OMAttribute attr = getAttribute(qname);
        return attr == null ? null : attr.getAttributeValue();
    }

    // TODO: complete the implementation (i.e. support value == null and the no namespace case) and
    // add the method to the OMElement API
    @Override
    public final void _setAttributeValue(QName qname, String value) {
        OMAttribute attr = getAttribute(qname);
        if (attr != null) {
            attr.setAttributeValue(value);
        } else {
            addAttribute(
                    qname.getLocalPart(),
                    value,
                    new OMNamespaceImpl(qname.getNamespaceURI(), qname.getLocalPart()));
        }
    }

    @Override
    public final void removeAttribute(OMAttribute attr) {
        if (attr.getOwner() != this) {
            throw new OMException("The attribute is not owned by this element");
        }
        ((AxiomAttribute) attr).coreRemove(AxiomSemantics.INSTANCE);
    }

    @Override
    public final void addNamespaceDeclaration(OMNamespace ns) {
        AxiomNamespaceDeclaration decl = getNodeFactory().createNamespaceDeclaration();
        decl.setDeclaredNamespace(ns);
        coreSetAttribute(
                AxiomSemantics.NAMESPACE_DECLARATION_MATCHER, decl, AxiomSemantics.INSTANCE);
    }

    @Override
    public final Iterator<OMNamespace> getAllDeclaredNamespaces() {
        return coreGetAttributesByType(
                AxiomNamespaceDeclaration.class,
                NamespaceDeclarationMapper.INSTANCE,
                AxiomSemantics.INSTANCE);
    }

    @Override
    public final OMNamespace declareNamespace(OMNamespace namespace) {
        String prefix = namespace.getPrefix();
        String namespaceURI = namespace.getNamespaceURI();
        if (prefix == null) {
            prefix = generatePrefix(namespaceURI);
            namespace = new OMNamespaceImpl(namespaceURI, prefix);
        }
        if (prefix.length() > 0 && namespaceURI.length() == 0) {
            throw new IllegalArgumentException("Cannot bind a prefix to the empty namespace name");
        }
        addNamespaceDeclaration(namespace);
        return namespace;
    }

    @Override
    public final OMNamespace declareNamespace(String uri, String prefix) {
        return declareNamespace(new OMNamespaceImpl(uri, prefix));
    }

    @Override
    public final OMNamespace declareDefaultNamespace(String uri) {
        OMNamespace elementNamespace = getNamespace();
        if (elementNamespace == null && uri.length() > 0
                || elementNamespace != null
                        && elementNamespace.getPrefix().length() == 0
                        && !elementNamespace.getNamespaceURI().equals(uri)) {
            throw new OMException(
                    "Attempt to add a namespace declaration that conflicts with the namespace information of the element");
        }
        OMNamespace namespace = new OMNamespaceImpl(uri == null ? "" : uri, "");
        addNamespaceDeclaration(namespace);
        return namespace;
    }

    @Override
    public final void undeclarePrefix(String prefix) {
        addNamespaceDeclaration(new OMNamespaceImpl("", prefix));
    }

    @Override
    public final OMNamespace findNamespace(String uri, String prefix) {

        // check in the current element
        OMNamespace namespace = findDeclaredNamespace(uri, prefix);
        if (namespace != null) {
            return namespace;
        }

        // go up to check with ancestors
        OMContainer parent = getParent();
        if (parent != null) {
            // For the OMDocumentImpl there won't be any explicit namespace
            // declarations, so going up the parent chain till the document
            // element should be enough.
            if (parent instanceof OMElement element) {
                namespace = element.findNamespace(uri, prefix);
                // If the prefix has been redeclared, then ignore the binding found on the ancestors
                if (namespace != null
                        && findDeclaredNamespace(null, namespace.getPrefix()) != null) {
                    namespace = null;
                }
            }
        }

        return namespace;
    }

    private static final OMNamespace XMLNS =
            new OMNamespaceImpl(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);

    /**
     * Checks for the namespace <B>only</B> in the current Element. This is also used to retrieve
     * the prefix of a known namespace URI.
     */
    private OMNamespace findDeclaredNamespace(String uri, String prefix) {
        CoreAttribute attr = coreGetFirstAttribute();
        while (attr != null) {
            if (attr instanceof AxiomNamespaceDeclaration) {
                OMNamespace namespace = ((AxiomNamespaceDeclaration) attr).getDeclaredNamespace();
                if ((prefix == null || prefix.equals(namespace.getPrefix()))
                        && (uri == null || uri.equals(namespace.getNamespaceURI()))) {
                    return namespace;
                }
            }
            attr = attr.coreGetNextAttribute();
        }

        // If the prefix is available and uri is available and its the xml namespace
        if ((prefix == null || prefix.equals(XMLConstants.XML_NS_PREFIX))
                && (uri == null || uri.equals(XMLConstants.XML_NS_URI))) {
            return XMLNS;
        } else {
            return null;
        }
    }

    @Override
    public final OMNamespace findNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        CoreAttribute attr = coreGetFirstAttribute();
        while (attr != null) {
            if (attr instanceof AxiomNamespaceDeclaration) {
                AxiomNamespaceDeclaration nsDecl = (AxiomNamespaceDeclaration) attr;
                if (nsDecl.coreGetDeclaredPrefix().equals(prefix)) {
                    OMNamespace ns = nsDecl.getDeclaredNamespace();
                    if (ns.getNamespaceURI().length() == 0) {
                        // We are either in the prefix undeclaring case (XML 1.1 only) or the
                        // namespace
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
        if (parent instanceof OMElement element) {
            // try with the parent
            return element.findNamespaceURI(prefix);
        } else {
            return null;
        }
    }

    @Override
    public final OMNamespace getDefaultNamespace() {
        return findNamespaceURI("");
    }

    @Override
    public final String toStringWithConsume() throws XMLStreamException {
        StringWriter sw = new StringWriter();
        serializeAndConsume(sw);
        return sw.toString();
    }

    @Override
    public final String toString() {
        StringWriter sw = new StringWriter();
        try {
            serialize(sw);
        } catch (XMLStreamException ex) {
            throw new OMException("Failed to serialize node", ex);
        }
        return sw.toString();
    }

    @Override
    public final OMElement cloneOMElement() {
        return (OMElement) clone(null);
    }

    @Override
    public final void buildWithAttachments() {
        if (getState() == INCOMPLETE) {
            build();
        }
        if (isExpanded()) {
            OMNode child = getFirstOMChild();
            while (child != null) {
                child.buildWithAttachments();
                child = child.getNextOMSibling();
            }
        }
    }

    @Override
    public void checkChild(OMNode child) {}

    @Override
    public final void setNamespace(OMNamespace namespace) {
        setNamespace(namespace, true);
    }

    @Override
    public final void setLineNumber(int lineNumber) {}

    @Override
    public final int getLineNumber() {
        return 0;
    }

    @Override
    public final CoreElement getContextElement() {
        CoreParentNode parent = coreGetParent();
        return parent instanceof CoreElement element ? element : null;
    }

    @Override
    public Iterator<OMNode> getDescendants(boolean includeSelf) {
        return coreGetNodes(
                includeSelf ? Axis.DESCENDANTS_OR_SELF : Axis.DESCENDANTS,
                AxiomChildNode.class,
                Mappers.<OMNode>identity(),
                AxiomSemantics.INSTANCE);
    }
}
